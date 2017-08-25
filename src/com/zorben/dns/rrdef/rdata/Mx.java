package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Parses and stores data pertaining to the information about a specific host.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                  PREFERENCE                   |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                   EXCHANGE                    /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.9">RFC 1035 Section 3.3.9 - MX RDATA Format</a>
 */
public class Mx extends RData {
	
	/**
	 * A 16 bit integer which specifies the preference given to
     * this RR among others at the same owner.  Lower values
     * are preferred.
	 */
	private short preference;
	
	/**
	 * A domain-name which specifies a host willing to act as
     * a mail exchange for the owner name.
	 */
	private String exchange;

	/**
	 * Constructs a Mail Exchange {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Mail Exchange {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the Mail Exchange {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Mx(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		preference = (short) (((preference | rawData[0]) << 8) | (rawData[1] & 0xFF));
		StringBuilder sb = new StringBuilder();
		DNParser.parseDomainName(rawData, start+2, sb);
		exchange = sb.toString();
	}

	/**
	 * Convert this Mail Exchange {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.add((byte) ((preference & 0xFF00) >> 8));
		array.add((byte) (preference & 0x00FF));
		array.addAll(DNParser.toByteArray(exchange));
		return array;
	}

	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA MX:\n");
		sb.append("PREFERENCE: ").append(preference).append("\n");
		sb.append("EXCHANGE: ").append(exchange);
		return sb.toString(); 
	}

	/**
	 * @return Preference that this record be used over others with respect to the server owner. Lower is more preferred.
	 */
	public short getPreference() {
		return preference;
	}

	/**
	 * @return The host domain name willing to act as a mail exchange server.
	 */
	public String getExchange() {
		return exchange;
	}
}
