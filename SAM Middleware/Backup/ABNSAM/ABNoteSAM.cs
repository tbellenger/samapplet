using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Common;
using MonoPcsc;
using MonoPcsc.Smartcard;
using MonoPcsc.Smartcard.StatusWord;
using System.Threading;

namespace ABNSAM
{
    struct InfoHolder
    {
        public string readerName;
        public ABnoteSAMFinder sam;
    }

    public delegate void SAMFoundEventHandler(object sender, ABnoteSAM sam);

    public class ABnoteSAMFinder
    {
        private Thread[] cardFinders;
        private bool _stopFinder = false;
        public string ReaderName { get; set; }
        public bool StopFinder { 
            get { return _stopFinder; } 
            set { _stopFinder = value; Smart.Cancel(); } 
        }
        public bool FindAnyCard { get; set; }
        private static object stateLock = new object();
        public event SAMFoundEventHandler SAMFound;

        public void FindCard()
        {
            string[] readers = Smart.ListReaders();
            if (readers != null)
            {
                cardFinders = new Thread[readers.Length];
                for (int i = 0; i < readers.Length; i++)
                {
                    cardFinders[i] = new Thread(new ParameterizedThreadStart(ABnoteSAMFinder.SAMFinder));
                    InfoHolder ih = new InfoHolder();
                    ih.readerName = readers[i];
                    ih.sam = this;
                    cardFinders[i].Start(ih);
                }
            }
        }

        private void OnSAMFound(ABnoteSAM card)
        {
            if (SAMFound != null)
            {
                SAMFound(this, card);
            }
        }

        private static void SAMFinder(object args)
        {
            bool stopThread = false;
            string readerName = ((InfoHolder)args).readerName;
            ABnoteSAMFinder sam = ((InfoHolder)args).sam;
            SCARD_READERSTATE stat = Smart.GetStatusChangeEx(readerName, 10, SCardState.SCARD_STATE_UNAWARE);
            while (!stopThread)
            {
                if ((stat.dwEventState & SCardState.SCARD_STATE_UNKNOWN) == SCardState.SCARD_STATE_UNKNOWN)
                {
                    // reader removed?
                    return;
                }
                if (((stat.dwEventState & SCardState.SCARD_STATE_PRESENT) == SCardState.SCARD_STATE_PRESENT) &&
                    !((stat.dwEventState & SCardState.SCARD_STATE_INUSE) == SCardState.SCARD_STATE_INUSE))
                {
                    // Card inserted - check for SAM applet
                    ABnoteSAM card = new ABnoteSAM(Smart.Connect(readerName), new RelaxedStatusWordFilter());
                    string resp = "";
                    try
                    {
                        resp = card.SelectApplet();
                    }
                    catch (Exception) { }
                    if (resp.EndsWith("9000"))
                    {
                        sam.ReaderName = readerName;
                        sam.OnSAMFound(card);
                    }
                    else if (sam.FindAnyCard)
                    {
                        sam.ReaderName = readerName;
                        sam.OnSAMFound(card);
                    }
                }
                lock (stateLock)
                {
                    stopThread = sam.StopFinder;
                }
                try
                {
                    stat = Smart.GetStatusChangeEx((string)readerName, -1, stat.dwEventState);
                }
                catch (Exception) {
                    break;
                }
            }
        }
    }

    public class ABnoteSAM : GPCard
    {
        private string appletVersion = "";
        public string Version { get { return "000001"; } }
        
        public ABnoteSAM(int hCard, IStatusWordFilter sw) : base(hCard, sw) { }

        public ABnoteSAM(int hCard) : base(hCard) { }

        public ABnoteSAM(Card card) : base(card) { }

        public string SelectApplet()
        {
            return SelectAID("a00000006203010c0101");
        }

        public bool CheckAppletVersion()
        {
            if (appletVersion == "")
            {
                appletVersion = Send("8090000003").Substring(0,6);
            }
            return appletVersion == Version;
        }

        public string VerifyPIN(string PIN)
        {
            return Send("80200000" + Util.AddHexLength(PIN));
        }

        public string UpdatePIN(string PIN)
        {
            return Send("80220000" + Util.AddHexLength(PIN));
        }

        public string StoreData(string data, byte slot, bool overwrite)
        {
            if (data.Length > 12)
            {
                throw new Exception("Data stored in applet may not be more than 12 chars length");
            }

            string cmd = ("8081" + Util.BinToHex(slot) + (overwrite ? "01":"00") + "19" + Util.AddHexLength(Util.AsciiToHex(data))).PadRight(60, '0');
            return Send(cmd);
        }

        public string RetreiveData(byte slot)
        {
            string cmd = "8082" + Util.BinToHex(slot) + "0019";
            string resp = Send(cmd);
            if (resp.EndsWith("9000"))
            {
                int len = int.Parse(resp.Substring(0, 2));
                string hex = resp.Substring(2, len * 2);
                return Util.HexToAscii(hex);
            }
            else
            {
                return null;
            }
        }

        public string LoadZMKComponent(byte[] comp, byte compIndex, byte zmkSlot, byte[] kcv)
        {
            return Send("8040" + Util.BinToHex(compIndex) + Util.BinToHex(zmkSlot) + "14" + Util.BinToHex(comp) + "03" + Util.BinToHex(kcv));
        }

        public string LockZMKLoading()
        {
            return Send("8044000000");
        }

        public string LoadCryptogram(byte[] cryptogram, byte keySlot, byte zmkSlot, byte[] kcv)
        {
            return Send("8042" + Util.BinToHex(keySlot) + Util.BinToHex(zmkSlot) + "1400" + Util.BinToHex(cryptogram) + Util.BinToHex(kcv));
        }
    }
}
