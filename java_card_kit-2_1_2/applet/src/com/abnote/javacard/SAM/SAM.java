//Thomas Bellenger - PCSC Smartcard Library for Mono/C#
//Copyright (C) 2003 - 2008 Thomas Bellenger

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.



package com.abnote.javacard.SAM;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

/**
 */

public class SAM extends Applet
{
	
		// Applet Version
		private static final byte[] VERSION = {0x0,0x0,0x1};
    
    // all APDU's for this applet use the CLA = 0x80
    private static final byte CLA_SAM = (byte)0x80;
    
    // all available INS for this applet
    private static final byte INS_LOAD_ZMK_COMPONENT = 0x40;
    private static final byte INS_LOAD_KEY = 0x42;
    private static final byte INS_LOCK_ZMK_LOADING = 0x44;
    private static final byte INS_DIVERSIFY_KEY = 0x46;
    private static final byte INS_CALCULATE_SESSION_KEY = 0x48;
    private static final byte INS_CALCULATE_SESSION_KEY_SCP02 = 0x49;
    private static final byte INS_CALCULATE_CRYPTOGRAM = 0x51;
    private static final byte INS_CALCULATE_MAC = 0x52;
    private static final byte INS_ENCRYPT_UNDER_KEK = 0x53;
    private static final byte INS_GET_STATUS = 0x54;
    private static final byte INS_VERIFY_PIN = 0x20;
    private static final byte INS_UPDATE_PIN = 0x22;
    private static final byte INS_STORE_PASSWORD = (byte)0x81;
    private static final byte INS_RETR_PASSWORD = (byte)0x82;
    private static final byte INS_VERSION = (byte)0x90;
    private static final byte INS_DEBUG1 = (byte)0x80;
    
    // MAX constants
    private static final byte MAX_ZMK_KEYS = 0x0A;
    private static final byte MAX_KEYS = 0x06;
    private static final byte MAX_PIN_SIZE = 0x08;
    
    // LENGTH constants
    private static final short LENGTH_ZMK_STORAGE = (short)(0xbe); 
    private static final byte LENGTH_KEY_STORAGE = (byte)(MAX_KEYS * 20);
    private static final byte LENGTH_PASSWORD_STORAGE = 0x20;
    private static final byte LENGTH_CRYPTO_AREA = 0x10;
    private static final byte LENGTH_TEMP_STORAGE = 0x20;
    private static final byte LENGTH_ZMK = 0x10;
    private static final byte LENGTH_ZMK_KCV = 0x03;
    private static final byte LENGTH_KEY = 0x10;
    private static final byte LENGTH_KCV = 0x03;
    private static final byte LENGTH_CRYPTOGRAM = 0x08;
    private static final byte LENGTH_KEYTYPE = 0x01;
    private static final byte LENGTH_KEYBLOCK = (byte)(LENGTH_KEY + LENGTH_KCV + LENGTH_KEYTYPE);
    
    private static final byte[] ZERO_8_ARRAY = {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0};
    
    // OFFSET constants
    private static final byte OFFSET_ZMK = 0x00;
    private static final byte OFFSET_ZMK_KCV = 0x10;
    private static final byte OFFSET_KEYTYPE = 0x00;
    private static final byte OFFSET_KEY = 0x01;
    private static final byte OFFSET_KCV = 0x11;
    
    // KEYTYPE constants
    private static final byte KEYTYPE_ENC = 0x00;
    private static final byte KEYTYPE_MAC = 0x01;
    private static final byte KEYTYPE_KEK = 0x02;
    private static final byte KEYTYPE_ZMK = 0x0F;
    
    // KEYTYPE MASK constants
    private static final byte KEYTYPE_MASK_SESSION = 0x10;
    private static final byte KEYTYPE_MASK_DERIVED = 0x20;
    
    // PINTRYLIMIT constant
    private static final byte PIN_TRY_LIMIT = 0x0F;
    
    // SW constants
    private static final short SW_PIN_VERIFICATION_FAILED = 0x6300;
    private static final short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    private static final short SW_KCV_INCORRECT = 0x6988;
    private static final short SW_NOT_IMPLEMENTED = 0x69FF;
    
    // transient
    private boolean[] transientBools;
    private byte[] transientKeyStorage;
    private byte[] transientCryptoArea;
    private byte[] transientTemporaryStorage;
    private DESKey transientDESKey;
    
    // Crypto
    private Cipher cipherTDES_CBC;
    private Cipher cipherTDES_ECB;
    
    // PIN
    private OwnerPIN pin;
    
    // Others
    private boolean zmkLoadingLocked = false;
    private byte[] zmkStorage;
    private Password[] passwordStorage;

    /**
     * Only this class's install method should create the applet object.
     */
    protected SAM(byte[] bArray,short bOffset,byte bLength)
    {
             
        transientBools = JCSystem.makeTransientBooleanArray((short)0x03, JCSystem.CLEAR_ON_DESELECT);
        transientCryptoArea = JCSystem.makeTransientByteArray(LENGTH_CRYPTO_AREA, JCSystem.CLEAR_ON_DESELECT);
        transientTemporaryStorage = JCSystem.makeTransientByteArray(LENGTH_TEMP_STORAGE, JCSystem.CLEAR_ON_DESELECT);
        transientKeyStorage = JCSystem.makeTransientByteArray(LENGTH_KEY_STORAGE, JCSystem.CLEAR_ON_DESELECT);
        transientDESKey = (DESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_DES_TRANSIENT_DESELECT, KeyBuilder.LENGTH_DES3_2KEY, false);
        
        cipherTDES_CBC = Cipher.getInstance(Cipher.ALG_DES_CBC_NOPAD, false);
        cipherTDES_ECB = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
        
        pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);
        
        byte iLen = bArray[bOffset]; // aid length
        bOffset = (short) (bOffset+iLen+1);
        byte cLen = bArray[bOffset]; // info length
        bOffset = (short) (bOffset+cLen+1);
        byte aLen = bArray[bOffset]; // applet data length
        
        // The installation parameters contain the PIN
        // initialization value
        pin.update(bArray, (short)(bOffset+1), aLen);
        
        zmkStorage = new byte[LENGTH_ZMK_STORAGE];
        
        passwordStorage = new Password[LENGTH_PASSWORD_STORAGE];
        for (byte i = 0; i < passwordStorage.length; i++) {
        	passwordStorage[i] = new Password();
        }
        
        register();
    }

    /**
     * Installs this applet.
     * @param bArray the array containing installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the parameter data in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength)
    {
        new SAM(bArray, bOffset, bLength);
    }
    
    public boolean select() {
        return true;
    }
    
    public void deselect() {
        pin.reset();
    }

    /**
     * Processes an incoming APDU.
     * @see APDU
     * @param apdu the incoming APDU
     * @exception ISOException with the response bytes per ISO 7816-4
     */
    public void process(APDU apdu)
    {
        byte buffer[] = apdu.getBuffer();
        
        buffer[ISO7816.OFFSET_CLA] = (byte)(buffer[ISO7816.OFFSET_CLA] & (byte)0xFC);
    
        if ((buffer[ISO7816.OFFSET_CLA] == ISO7816.CLA_ISO7816) && (buffer[ISO7816.OFFSET_INS] == ISO7816.INS_SELECT)) {
          return;
        }
        
        if (buffer[ISO7816.OFFSET_CLA] != CLA_SAM) { ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED); }
        
        switch (buffer[ISO7816.OFFSET_INS]) {
          case INS_LOAD_ZMK_COMPONENT:      		loadZMKComponent(apdu); return;
          case INS_LOAD_KEY:                		loadKey(apdu); return;
          case INS_LOCK_ZMK_LOADING:        		lockZMKLoading(apdu); return;
          case INS_DIVERSIFY_KEY:           		diversifyKey(apdu); return;
          case INS_CALCULATE_SESSION_KEY:   		calculateSessionKey(apdu); return;
          case INS_CALCULATE_SESSION_KEY_SCP02: 	calculateSessionKeySCP02(apdu); return;
          case INS_CALCULATE_CRYPTOGRAM:    		calculateCryptogram(apdu); return;
          case INS_CALCULATE_MAC:           		calculateMAC(apdu); return;
          case INS_ENCRYPT_UNDER_KEK:	    		encryptUnderKek(apdu); return;
          case INS_GET_STATUS:              		getStatus(apdu); return;
          case INS_VERIFY_PIN:              		verifyPIN(apdu); return;
          case INS_UPDATE_PIN:						updatePIN(apdu); return;
          case INS_STORE_PASSWORD:					storePassword(apdu); return;
    	  case INS_RETR_PASSWORD:					retrPassword(apdu); return;
          case INS_VERSION:							version(apdu); return;
          case INS_DEBUG1:                  		debug1(apdu); return;
          default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);     
        }
    }
    
    //--------------------------------------------------------------------------
    // Processing Methods ------------------------------------------------------
    // -------------------------------------------------------------------------
    
    // Returns the version of the applet over 3 bytes.
    private void version(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	
    	Util.arrayCopyNonAtomic(VERSION,(byte)0,buffer,(byte)0,(byte)3);
    	
    	apdu.setOutgoingAndSend((byte)0,(byte)3);
    }
    
    // In this command a password is stored on the card
    // and are accessible once the PIN has been verified
    // CLA = 0x80, INS = 0x81, P1 = Storage Slot, P2 = 00 - don't overwrite/01 - overwrite, LC = 0x19
    private void storePassword(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	short bytesRead = apdu.setIncomingAndReceive();
    	
    	byte P1 = buffer[ISO7816.OFFSET_P1];
    	byte P2 = buffer[ISO7816.OFFSET_P2];
    	byte LC = buffer[ISO7816.OFFSET_LC];
    	
    	if (!pin.isValidated()) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
    	if (LC != 0x19) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
    	if (P1 < 0x00 || P1 > (byte)(LENGTH_PASSWORD_STORAGE - 1)) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
    	if (P2 < 0x00 || P2 > 0x01) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
    	
    	if (P2 == 0x01) {
    		passwordStorage[P1].delete();
    	}
    	passwordStorage[P1].store(buffer,ISO7816.OFFSET_CDATA);
    }
    
    private void retrPassword(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	
    	byte P1 = buffer[ISO7816.OFFSET_P1];
    	
    	if (!pin.isValidated()) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
    	
    	byte[] pass = passwordStorage[P1].getPassword();
    	Util.arrayCopyNonAtomic(pass,(short)0,buffer,(short)0,(short)0x19);
    	
    	apdu.setOutgoingAndSend((short)0,(short)0x19);
    }
    	
    	
    
    // In this command a ZMK component is loaded on to the card
    // the KCV of the complete key should be loaded with the all components
    // CLA = 0x80, INS = 0x40, P1 = 01|02|03, P2 = ZMKStorageIndex(00 - 0F), LC = 0x14 or 0x11
    // Data = KeyOver16Bytes | KCVLength = 0x03 or 0x00 | KCV
    private void loadZMKComponent(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == true) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x01 || P1 > 0x03) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 < 0x00 || P2 >= MAX_ZMK_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC < 0x11 || LC > 0x19) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        byte kcvLength = buffer[(short)(ISO7816.OFFSET_CDATA + LENGTH_ZMK)];
        
        if (P1 == 0x01) {
          // load ZMK component 1
          Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, transientTemporaryStorage, OFFSET_ZMK, LENGTH_ZMK);
          transientBools[0x00] = true;
          transientBools[0x01] = false;
        } else if (P1 == 0x02) {
          // load ZMK component 2
          if (transientBools[0x00] != true) { ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED); }
          xorArray(transientTemporaryStorage, OFFSET_ZMK, buffer, ISO7816.OFFSET_CDATA, transientTemporaryStorage, (short)(OFFSET_ZMK + LENGTH_ZMK), LENGTH_ZMK);
          transientBools[0x01] = true;
        } else if (P1 == 0x03) {
          // load ZMK component 3
          if (transientBools[0x01] != true) { ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED); }
          xorArray(transientTemporaryStorage, (short)(OFFSET_ZMK + LENGTH_ZMK), buffer, ISO7816.OFFSET_CDATA, transientTemporaryStorage, OFFSET_ZMK, LENGTH_ZMK);
          Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA + LENGTH_ZMK + 1), transientTemporaryStorage, OFFSET_ZMK_KCV, kcvLength);
          transientBools[0x00] = false;
          transientBools[0x01] = false;
          // calculate KCV
          // ZMK is now in offset 0 of crypto area
          transientDESKey.setKey(transientTemporaryStorage, OFFSET_ZMK);
          cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
          cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, buffer, (byte)0);
          // compare KCV with calculated KCV
          for (short i = 0; i < kcvLength; i++) {
            if(buffer[i] != transientTemporaryStorage[(short)(OFFSET_ZMK_KCV + i)]) { ISOException.throwIt(SW_KCV_INCORRECT); }
          }
          // KCV is good so copy ZMK to persistant storage
          Util.arrayCopyNonAtomic(transientTemporaryStorage, OFFSET_ZMK, zmkStorage, (byte)(P2 * (byte)(LENGTH_ZMK + LENGTH_ZMK_KCV)),LENGTH_ZMK);
          Util.arrayCopyNonAtomic(buffer,(byte)0,zmkStorage,(byte)((P2 * (byte)(LENGTH_ZMK + LENGTH_ZMK_KCV) + LENGTH_ZMK)),LENGTH_ZMK_KCV);
          apdu.setOutgoingAndSend((byte)0,LENGTH_ZMK_KCV);
        }
    }
    // CLA = 0x80, INS = 0x42, P1 = 00-0F (key store), P2 = 00-0F (zmk store), LC = 0x14
    // Data = Key Type + Cryptogram + KCV
    private void loadKey(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 < 0x00 || P2 >= MAX_ZMK_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x14) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // Check that the KCV is correct when decrypted
        // Check ZMK is valid
        
        Util.arrayCopyNonAtomic(zmkStorage,(byte)(P2 * (byte)(LENGTH_ZMK + LENGTH_ZMK_KCV)), transientCryptoArea, (byte)0, LENGTH_ZMK);
        Util.arrayCopyNonAtomic(zmkStorage, (byte)(P2 * (byte)(LENGTH_ZMK + LENGTH_ZMK_KCV) + LENGTH_ZMK), transientTemporaryStorage, (byte)0, LENGTH_ZMK_KCV);
        // ZMK is now in offset 0 of crypto area
        transientDESKey.setKey(transientCryptoArea, (byte)0);
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, LENGTH_ZMK_KCV);
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientTemporaryStorage[(short)(i + LENGTH_ZMK_KCV)]) { ISOException.throwIt(SW_KCV_INCORRECT); }
        }
        // ZMK is already set as key and KCV matches so decrypt cryptogram into memory
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_DECRYPT);
        cipherTDES_ECB.doFinal(buffer, (short)(ISO7816.OFFSET_CDATA + OFFSET_KEY), LENGTH_KEY, buffer, (short)(ISO7816.OFFSET_CDATA + OFFSET_KEY));
        // Clear key now replaces cryptogram, otherwise input buffer remains as before
        transientDESKey.setKey(buffer, (short)(ISO7816.OFFSET_CDATA + OFFSET_KEY));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_KCV; i++) {
          if(transientTemporaryStorage[i] != buffer[(short)(i + ISO7816.OFFSET_CDATA + OFFSET_KCV)]) { ISOException.throwIt(SW_KCV_INCORRECT); }
        }
        // Calculated KCV matches input KCV so copy whole of buffer to key store
        Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, transientKeyStorage, (byte)(P1 * (LENGTH_KEYTYPE + LENGTH_KEY + LENGTH_KCV)), (byte)(LENGTH_KEYTYPE + LENGTH_KEY + LENGTH_KCV)); 
    }
    
    // CLA = 0x80, INS = 0x44, P1P2 = Any
    // Condition is that PIN has been verified
    private void lockZMKLoading(APDU apdu) {
        if (pin.isValidated()) {
          zmkLoadingLocked = true;
        } else {
          ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
    }
    
    // CLA = 0x80, INS = 0x46, P1 = 00-0F (key store), P2 = 00-0F (key store), LC = 0x10
    // Data = div data
    // conditions: zmk loading locked, key type is not diversified or session, kcv of key is correct
    private void diversifyKey(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 < 0x00 || P2 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x10) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are using a non diversified key and not using a session key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & 0x30) != 0x00) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        
        // now encrypt div data and store new key
        cipherTDES_ECB.doFinal(buffer, ISO7816.OFFSET_CDATA, LC, transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        // store new KCV
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, buffer, (byte)0);
        Util.arrayCopyNonAtomic(buffer, (byte)0, transientKeyStorage, (byte)(OFFSET_KCV + (byte)(P2 * LENGTH_KEYBLOCK)),LENGTH_KCV);
        // store key type
        transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P2 * LENGTH_KEYBLOCK))] = (byte)(KEYTYPE_MASK_DERIVED | transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P1 * LENGTH_KEYBLOCK))]);
        apdu.setOutgoingAndSend((byte)0,LENGTH_KCV);
    }
    
    // CLA = 0x80, INS = 0x48, P1 = 00-0F (key store), P2 = 00-0F (key store), LC = 0x10
    // Data = div data
    private void calculateSessionKey(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 < 0x00 || P2 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x10) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are  not using a session key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_MASK_SESSION) != 0x00) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        
        // now encrypt div data and store new key
        cipherTDES_ECB.doFinal(buffer, ISO7816.OFFSET_CDATA, LC, transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        // store new KCV
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, buffer, (byte)0);
        Util.arrayCopyNonAtomic(buffer, (byte)0, transientKeyStorage, (byte)(OFFSET_KCV + (byte)(P2 * LENGTH_KEYBLOCK)),LENGTH_KCV);
        // store key type
        transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P2 * LENGTH_KEYBLOCK))] = (byte)(KEYTYPE_MASK_SESSION | transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P1 * LENGTH_KEYBLOCK))]);
        apdu.setOutgoingAndSend((byte)0,LENGTH_KCV);
    }
    
    // CLA = 0x80, INS = 0x49, P1 = 00-0F (key store), P2 = 00-0F (key store), LC = 0x10
    // Data = div data
    private void calculateSessionKeySCP02(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 < 0x00 || P2 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x10) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are  not using a session key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_MASK_SESSION) != 0x00) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        
        // now encrypt div data and store new key
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_CBC.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_CBC.doFinal(buffer, ISO7816.OFFSET_CDATA, LC, transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        // store new KCV
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P2 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, buffer, (byte)0);
        Util.arrayCopyNonAtomic(buffer, (byte)0, transientKeyStorage, (byte)(OFFSET_KCV + (byte)(P2 * LENGTH_KEYBLOCK)),LENGTH_KCV);
        // store key type
        transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P2 * LENGTH_KEYBLOCK))] = (byte)(KEYTYPE_MASK_SESSION | transientKeyStorage[(byte)(OFFSET_KEYTYPE + (byte)(P1 * LENGTH_KEYBLOCK))]);
        apdu.setOutgoingAndSend((byte)0,LENGTH_KCV);
    }
    
    // CLA = 0x80, INS = 0x51, P1 = 00-0F (key store), P2 = 0x00, LC = 0x18
    // Data = Host Random | Card random | 80Padding
    // key must be session
    private void calculateCryptogram(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 != 0x00) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x18) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are using a session key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_ENC) != KEYTYPE_ENC) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_MASK_SESSION) != KEYTYPE_MASK_SESSION) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        
        // now encrypt div data and store new key
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_CBC.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_CBC.doFinal(buffer, ISO7816.OFFSET_CDATA, LC, buffer, (byte)(ISO7816.OFFSET_CDATA + LC));
        
        // output the resulting cryptogram (last 8 bytes only)
        apdu.setOutgoingAndSend((byte)(ISO7816.OFFSET_CDATA + LC + 0x10),LENGTH_CRYPTOGRAM);
    }
    
    
    private void calculateMAC(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 != 0x00) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x18) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are using a session key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_MAC) != KEYTYPE_MAC) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_MASK_SESSION) != KEYTYPE_MASK_SESSION) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }
        
        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        	
    }
    
    // P1 = kek in store, P2 = target key in store, LE = 0x10
    // no data
    private void encryptUnderKek(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
        
        byte P1 = buffer[ISO7816.OFFSET_P1];
        byte P2 = buffer[ISO7816.OFFSET_P2];
        byte LC = buffer[ISO7816.OFFSET_LC];
        
        if (zmkLoadingLocked == false) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }
        if (P1 < 0x00 || P1 >= MAX_KEYS) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (P2 != 0x00) { ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2); }
        if (LC != 0x10) { ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); }
        
        // check that we are using a kek key
        if ((transientKeyStorage[(byte)(OFFSET_KEYTYPE + (P1 * LENGTH_KEYBLOCK))] & KEYTYPE_KEK) != KEYTYPE_KEK) { ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); }

        transientDESKey.setKey(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)));
        cipherTDES_ECB.init(transientDESKey, Cipher.MODE_ENCRYPT);
        cipherTDES_ECB.doFinal(ZERO_8_ARRAY, (byte)0, (byte)8, transientTemporaryStorage, (byte)0);
        
        // compare KCV with calculated KCV
        for (short i = 0; i < LENGTH_ZMK_KCV; i++) {
          if(transientTemporaryStorage[i] != transientKeyStorage[(byte)(OFFSET_KCV + i + (byte)(P1 * LENGTH_KEYBLOCK))]) { ISOException.throwIt(ISO7816.SW_WRONG_DATA); }
        }
        cipherTDES_ECB.doFinal(transientKeyStorage, (byte)(OFFSET_KEY + (byte)(P1 * LENGTH_KEYBLOCK)), LENGTH_KEY, buffer, (byte)0);
        apdu.setOutgoingAndSend((byte)0,LENGTH_KEY); 
    }
    
    private void getStatus(APDU apdu) {ISOException.throwIt(SW_NOT_IMPLEMENTED);}
    
    // CLA = 0x80, INS = 0x20, P1P2 = Any
    // Verifies the PIN set at instanciation
    private void verifyPIN(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        
        if ( pin.check(buffer, ISO7816.OFFSET_CDATA, (byte)bytesRead) == false ) {
          ISOException.throwIt(SW_PIN_VERIFICATION_FAILED);
        }  
    }
    
    private void updatePIN(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	short bytesRead = apdu.setIncomingAndReceive();
    	
    	byte LC = buffer[ISO7816.OFFSET_LC];
    
    	if (!pin.isValidated()) { ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED); }

    	pin.update(buffer, ISO7816.OFFSET_CDATA, LC);
    }
    		
    
    // WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
    // debug function to remove before release to production
    // WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
    private void debug1(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte P1 = buffer[ISO7816.OFFSET_P1];
        
        if (P1 == 0x10) {
          Util.arrayCopyNonAtomic(transientKeyStorage, (byte)0, buffer, (byte)0, LENGTH_KEY_STORAGE);
          apdu.setOutgoingAndSend((byte)0, LENGTH_KEY_STORAGE);
        } else if (P1 == 0x20) {
          Util.arrayCopyNonAtomic(zmkStorage, (byte)0, buffer, (byte)0, LENGTH_ZMK_STORAGE);
          apdu.setOutgoingAndSend((byte)0, LENGTH_ZMK_STORAGE);
        } else {
          Util.arrayCopyNonAtomic(transientTemporaryStorage, (byte)0, buffer, (byte)0, LENGTH_TEMP_STORAGE);
          apdu.setOutgoingAndSend((byte)0, LENGTH_TEMP_STORAGE);
        }
    }
    
    // -------------------------------------------------------------------------
    // Other methods -----------------------------------------------------------
    //--------------------------------------------------------------------------
    
    private void xorArray(byte[] a, short offa, byte[] b, short offb, byte[] c, short offc, short len) {
        Util.arrayCopyNonAtomic(a,offa,c,offc,len);
        for (short i = 0; i < len; i++) {
          c[(short)(offc + i)] = (byte)(c[(short)(offc + i)] ^ b[(short)(offb + i)]);
        }
    }
}
