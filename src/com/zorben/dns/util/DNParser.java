package com.zorben.dns.util;

import java.util.ArrayList;

/**
 * Static class with utility functions for parsing and writing DNS encoded domain names.
 * 
 * @author Mac Crompton
 *
 */
public class DNParser {
	
	/**
	 * Parses a domain name in message format, into a domain in a readable format.
	 * Places the result into sb.
	 * @param data - The raw data from the dns message
	 * @param start - The starting index of the domain name
	 * @param sb - The StringBuilder to place the resultant domain name into.
	 * @return - The index immediately after the 0 for the specified domain name.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.1">RFC 1035 Section 3.1 - Name Space Definitions</a>
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-2.3.1">RFC 1035 Section 2.3.1 - Preferred Name Syntax</a>
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.4">RFC 1035 Section 4.1.4 - Message Compression</a>
	 */
	public static int parseDomainName(byte[] data, int start, StringBuilder sb) {
		int pos = start;
		// We are done if we find the zero byte
		while(data[pos] != 0) {
			// This byte represents a "Domain Pointer" if the left 2 most bits are 1 1 (which is 3).
			boolean isDomainPtr = ((data[pos] & 0xC0) >> 6) == 3;
			if(isDomainPtr) {
				// If this is a "Domain Pointer", use the next two bytes (6 bits and 1 byte) as the offset and parse the domain at that location
				int offset = ((data[pos] & 0x3F) << 8) | (data[pos+1] & 0xFF);
				parseDomainName(data, offset, sb);
				pos += 2;
				return pos;
			} else {
				// If this is not a "Domain Pointer", calculate the length of the domain and interpret each of the following bytes as an ascii character
				int length = (data[pos] & 0xFF);
				pos ++;
				for(int i = 0; i < length; i++) {
					sb.append((char) data[i+pos]);
				}
				sb.append('.');
				pos += length;
			}
		}
		
		// Return the index proceeding the zero byte
		return pos + 1;
	}
	
	/**
	 * Returns a byte ArrayList that contains the domain name in DNS Protocol domain name format.
	 * No compression, as described in RFC 1035 is done.
	 * @param domainName The domain name to be converted
	 * @return The ArrayList of bytes containing the formatted domain name.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.1">RFC 1035 Section 3.1 - Name Space Definitions</a>
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.4">RFC 1035 Section 4.1.4 - Message Compression</a>
	 */
	public static ArrayList<Byte> toByteArray(String domainName) {
		// TODO: Add compression as described in the documentation
		ArrayList<Byte> array = new ArrayList<Byte>();
		
		// Iterate over each domain label
		String[] labels = domainName.split("\\.");
		for(String label : labels) {
			// Encode the length of the label as a byte (can be no larger than 255 characters)
			byte length = (byte) (label.length() & 0x000000FF);		// TODO: Should check that the length is not larger than 255 characters
			array.add(length);
			// Encode each character of the label as a byte and add it to our array
			for(int i = 0; i < label.length(); i++) {
				byte b = (byte)(label.charAt(i) & 0xFF);
				array.add(b);
			}
		}
		// Add the zero byte to the end
		array.add((byte) 0);
		return array;
	}
}
