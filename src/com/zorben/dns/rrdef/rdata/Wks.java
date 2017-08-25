package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;
import java.util.Arrays;

import com.zorben.dns.rrdef.ResourceRecord;

/**
 * Parses and stores data pertaining to the well known services record.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    ADDRESS                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |       PROTOCOL        |                       |
 *  +--+--+--+--+--+--+--+--+                       |
 *  |                                               |
 *  /                    BIT MAP                    /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * The WKS record is used to describe the well known services supported by
 * a particular protocol on a particular internet address.  The PROTOCOL
 * field specifies an IP protocol number, and the bit map has one bit per
 * port of the specified protocol.  The first bit corresponds to port 0,
 * the second to port 1, etc.  If the bit map does not include a bit for a
 * protocol of interest, that bit is assumed zero.  The appropriate values
 * and mnemonics for ports and protocols are specified in [<a href="https://tools.ietf.org/html/rfc1010">RFC-1010</a>].
 * <br>
 * For example, if PROTOCOL=TCP (6), the 26th bit corresponds to TCP port
 * <strong>25 (SMTP)</strong>.  If this bit is set, a SMTP server should be listening on TCP
 * port 25; if zero, SMTP service is not supported on the specified
 * address.
 * <br>
 * The purpose of WKS RRs is to provide availability information for
 * servers for TCP and UDP.  If a server supports both TCP and UDP, or has
 * multiple Internet addresses, then multiple WKS RRs are used.
 * <br>
 * WKS RRs cause no additional section processing.
 * <br>
 * In master files, both ports and protocols are expressed using mnemonics
 * or decimal numbers.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.4.2">RFC 1035 Section 3.4.2 - WKS RDATA Format</a>
 */
public class Wks extends RData {
	
	/**
	 * An 32 bit Internet address supporting the specified services.
	 */
	private int address;
	
	/**
	 * An 8 bit IP protocol number
	 */
	private byte protocol;
	
	/**
	 * A variable length bit map.  The bit map must be a multiple of 8 bits long.
	 */
	private byte[] bitmap;

	/**
	 * Constructs a Well Known Services {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Well Known Services {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the Well Known Services {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Wks(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		
		// Grab the IPv4 address for which the well known services are being hosted
		for(int i = 0; i < 4; i++) {
			address = ((address << 8) | (rawData[i] & 0xFF));
		}
		
		// Grab the fifth byte indicating the protocol being used
		protocol = rawData[4];
		
		// Grab the remaining bits indicating which ports are available at this location
		bitmap = Arrays.copyOfRange(bitmap, 5, length-5);
	}

	/**
	 * Convert this Well Known Services {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((address & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		array.add(protocol);
		for(byte b : bitmap) {
			array.add(b);
		}
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA HINFO:\n");
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			int part = ((address & mask) >>> ((4-i-1) * 8));
			sb.append(part);
			sb.append(".");
		}
		sb.append("\n");
		sb.append("PROTOCOL: ").append(protocol);
		return sb.toString(); 
	}

	/**
	 * @return The IPv4 address supporting the specified well known services.
	 */
	public int getAddress() {
		return address;
	}

	/**
	 * @return An 8-bit integer representing the protocol running the well known services.
	 */
	public byte getProtocol() {
		return protocol;
	}

	/**
	 * @return The bitmap, in sets of 8 bits, indicating the ports for which well known services are being supported on.
	 */
	public byte[] getBitmap() {
		return bitmap;
	}
}
