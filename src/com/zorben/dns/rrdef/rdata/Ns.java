package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Parses and stores data pertaining to the name server authority.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                   NSDNAME                     /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.11">RFC 1035 Section 3.3.11 - NS RDATA Format</a>
 */
public class Ns extends RData {
	
	/**
	 * A domain-name which specifies a host which should be
     * authoritative for the specified class and domain.
	 */
	private String nsdname;

	/**
	 * Constructs a Name Server {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Name Server {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the Name Server {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Ns(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		StringBuilder sb = new StringBuilder();
		DNParser.parseDomainName(rawData, start, sb);
		nsdname = sb.toString();
	}

	/**
	 * Convert this Name Server {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.addAll(DNParser.toByteArray(nsdname));
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA NS:\n");
		sb.append("NSDNAME: ").append(nsdname);
		return sb.toString(); 
	}

	/**
	 * @return The server acting as an authority name server for the domain.
	 */
	public String getNsdname() {
		return nsdname;
	}
}
