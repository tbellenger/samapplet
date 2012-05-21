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

/**
 */

public class Password {
	private static final byte LENGTH_BUFFER = 0x19;
	private byte[] password;
	private boolean used;
	
	public Password() {
		password = new byte[LENGTH_BUFFER];
		used = false;
	}
	
	public void store(byte[] pass, byte offset) {
		if (used) { ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED); }
		Util.arrayCopyNonAtomic(pass,(short)offset,password,(short)0,(short)LENGTH_BUFFER);
		used = true;
	}
	
	public void delete() {
		used = false;
	}
	
	public byte[] getPassword() {
		if (!used) { ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED); }
		return password;
	}
}