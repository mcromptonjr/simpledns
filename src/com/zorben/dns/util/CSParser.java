package com.zorben.dns.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static class with utility functions for parsing and writing DNS encoded character strings.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3">RFC 1035 Section 3.3 - Standard RRs</a>
 */
public class CSParser {
	
	/**
	 * Parses a DNS encoded character string, into a Java string.
	 * Places the result into sb.
	 * @param data - The raw data from the dns message
	 * @param start - The starting index of the character string
	 * @param sb - The StringBuilder to place the resultant string into.
	 * @return - The index immediately after the 0 for the specified character string.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3">RFC 1035 Section 3.3 - Standard RRs</a>
	 */
	public static int parseCharString(byte[] data, int start, StringBuilder sb) {
		int length = data[start] & 0xFF;
		sb.append(new String(Arrays.copyOfRange(data, start+1, length)));
		return start+length+1;
	}
	
	/**
	 * Returns a byte ArrayList that contains the character string in DNS Protocol character string format.
	 * 
	 * @param string The string to be converted
	 * @return The ArrayList of bytes containing the formatted character string.
	 * 
	 */
	public static ArrayList<Byte> toByteArray(String string) {
		ArrayList<Byte> array = new ArrayList<Byte>();
		byte length = (byte)(string.length() & 0x000000FF);
		array.add(length);
		for(int i = 0; i < string.length(); i++) {
			array.add((byte) (string.charAt(i) & 0xFF));
		}
		
		return array;
	}
}
