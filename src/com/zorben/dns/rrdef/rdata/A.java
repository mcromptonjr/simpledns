package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;

/**
 * Parses and stores {@link ResourceRecord} Internet Address data.
 * <br>
 * Internet Address format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    ADDRESS                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *
 *  where:
 *
 *  ADDRESS         A 32 bit Internet address.
 * </pre>
 * 
 * @author Mac Crompton
 * 
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.4.1">RFC 1035 Section 3.4.1 - A RDATA Format</a>
 */
public class A extends RData {
	
	/**
	 * A 32-bit integer representing the 4 bytes of the IPv4 internet address.
	 * <br>
	 * An IPv4 address in standard form X1.X2.X3.X4 will be stored in the 32-bit integer where X1 is the most significant byte, and X4 is the least significant byte.
	 */
	private int address;

	/**
	 * Constructs an internet address {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded internet address {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the internet address {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public A(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		
		// Concatenate all 4 bytes of the IPv4 address into the {@code address} variable.
		for(int i = 0; i < 4; i++) {
			address = ((address << 8) | (rawData[start+i] & 0xFF));
		}
	}

	/**
	 * Convert this internet address {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			// Grab the correct byte from the 32-bit integer
			int mask = 0xFF << ((4-i-1) * 8);
			// Shift this byte over so that it can be represented by a single byte
			byte b = (byte) ((address & mask) >>> ((4-i-1) * 8));
			array.add(b);
		}
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 * <br>
	 * Displays the IPv4 address in the following format: X.X.X.X
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA TYPE A:\n");
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			int part = ((address & mask) >>> ((4-i-1) * 8));
			sb.append(part);
			sb.append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	/**
	 * @return The 32-bit integer representing an IPv4 internet address.
	 */
	public int getAddress() {
		return address;
	}
}
