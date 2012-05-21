using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using MonoPcsc;
using MonoPcsc.Smartcard.StatusWord;
using MonoPcsc.Smartcard;
using Common;
using ABNSAM;
using System.IO;

namespace SAMManager
{
    public partial class MainForm : Form
    {
        private ABnoteSAM card;
        private ABnoteSAMFinder finder;

        public MainForm()
        {
            InitializeComponent();
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            finder = new ABnoteSAMFinder();
            finder.SAMFound += new SAMFoundEventHandler(finder_SAMFound);
            finder.FindCard();
        }

        void finder_SAMFound(object sender, ABnoteSAM card)
        {
            DialogResult dr = MessageBox.Show("Card found in " + ((ABnoteSAMFinder)sender).ReaderName + ". Connect to this card?", "SAM Found", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (dr == DialogResult.Yes)
            {
                this.card = card;
                if (InvokeRequired)
                {
                    Invoke((MethodInvoker)delegate()
                    {
                        btn_Connect.Text = "Connected";
                    });
                }
                else
                {
                    btn_Connect.Text = "Connected";
                }
            }
        }

        private void btn_Load_Click(object sender, EventArgs e)
        {
            finder.FindAnyCard = true;
            DialogResult dr = MessageBox.Show("The SAM applet is about to be loaded on to your card. " + 
                                            "If it is already loaded on the card it will be deleted first " + 
                                            "and all data stored in the applet will be lost. If you would like " + 
                                            "to continue please insert the card in any reader then click 'Yes'.",
                                            "Load Applet", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
            if (dr == DialogResult.Yes)
            {
                this.Refresh();
                if (card == null)
                {
                    MessageBox.Show("Connect to card before loading applet");
                }

                try
                {
                    GPKeySet keys = new GPKeySet();
                    keys.AutKey = Util.HexToBin("404142434445464748494a4b4c4d4e4f");
                    keys.MacKey = Util.HexToBin("404142434445464748494a4b4c4d4e4f");
                    keys.KekKey = Util.HexToBin("404142434445464748494a4b4c4d4e4f");
                    keys.KeyVersion = 0;

                    string capFile;

                    string[] capFiles = Directory.GetFiles(Environment.CurrentDirectory, "*.cap");
                    if (capFiles.Length < 1)
                    {
                        MessageBox.Show("No .CAP files found");
                        return;
                    }
                    else
                    {
                        if (capFiles.Length == 1)
                        {
                            capFile = capFiles[0];
                            GPLoadFile loadFile = new GPLoadFile(capFile, "1234");
                            if (!loadFile.ReadCapFile()) { MessageBox.Show("Error reading load file."); return; }
                            loadFile.GenerateLoadCommands("");

                            card.Keys = keys;
                            card.SelectAID("");
                            string ret = card.OpenSecureChannel(0, false);
                            if (ret == "9000")
                            {
                                Cursor.Current = Cursors.WaitCursor;
                                card.Send("80E40080" + Util.AddHexLength("4F" + Util.AddHexLength(loadFile.PackageAID)));
                                if (card.InstallCapFile(loadFile))
                                {
                                    MessageBox.Show("Applet Installed");
                                    card.SelectApplet();
                                }
                                else
                                {
                                    MessageBox.Show("Error installing applet");
                                }
                                Cursor.Current = Cursors.Arrow;
                            }
                            else
                            {
                                MessageBox.Show("Error opening secure channel");
                            }
                        }
                    }
                }
                catch (Exception exp)
                {
                    MessageBox.Show(exp.Message);
                    Cursor.Current = Cursors.Arrow;
                }
            }
            finder.FindAnyCard = false;
        }

        private void btn_Verify_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    if (card.VerifyPIN(mskTxtPIN.Text) == "9000")
                    {
                        MessageBox.Show("PIN Verified OK");
                    }
                    else
                    {
                        MessageBox.Show("PIN Verification Failed");
                    }
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private void btn_Update_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    if (card.UpdatePIN(mskTxtUpdate.Text) == "9000")
                    {
                        MessageBox.Show("PIN Updated OK");
                    }
                    else
                    {
                        MessageBox.Show("PIN Update Failed");
                    }
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }

        }

        private void btn_Store_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    string resp = card.StoreData(mskTxtPass.Text, byte.Parse(txtSlot.Text), chkOverwrite.Checked);
                    if (resp == "9000")
                    {
                        MessageBox.Show("Password Stored OK");
                    }
                    else
                    {
                        MessageBox.Show("Password Storage Failed [" + resp + "]");
                    }

                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private void btn_RetrPass_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    string retr = card.RetreiveData(byte.Parse(txtSlotRetr.Text));
                    if (retr != null)
                    {
                        txtRetrPass.Text = retr;
                    }
                    else
                    {
                        txtRetrPass.Text = "Password Retrieval Failed";
                    }
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private bool VersionCheck()
        {
            try
            {
                DialogResult dr = DialogResult.Yes;
                if (!card.CheckAppletVersion())
                {
                    dr = MessageBox.Show("Applet version does not match middleware version. Continue?", "Version Mismatch", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
                }
                return (dr == DialogResult.Yes);
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
            return false;
        }

        private void btn_ComponentLoad_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    string resp = card.LoadZMKComponent(Util.HexToBin(mskTxtZMKComp.Text), byte.Parse(txtCompIndex.Text), byte.Parse(txtZMKSlot.Text), Util.HexToBin(txtCompKCV.Text));
                    MessageBox.Show(resp, "Card Response", MessageBoxButtons.OK, (resp.EndsWith("9000") ? MessageBoxIcon.Information : MessageBoxIcon.Error));
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private void btnLockLoad_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    string resp = card.LockZMKLoading();
                    MessageBox.Show(resp, "Card Response", MessageBoxButtons.OK, (resp.EndsWith("9000") ? MessageBoxIcon.Information : MessageBoxIcon.Error));
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private void btn_LoadCryptogram_Click(object sender, EventArgs e)
        {
            try
            {
                if (VersionCheck())
                {
                    string resp = card.LoadCryptogram(Util.HexToBin(txtCryptogram.Text), byte.Parse(txtKeySlot.Text), byte.Parse(txtZMKSlot2.Text), Util.HexToBin(txtKeyKCV.Text));
                    MessageBox.Show(resp, "Card Response", MessageBoxButtons.OK, (resp.EndsWith("9000") ? MessageBoxIcon.Information : MessageBoxIcon.Error));
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
            }
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            finder.StopFinder = true;
        }
    }
}
