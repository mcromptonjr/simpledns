package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Parses and stores data pertaining to a domain pointer.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                   PTRDNAME                    /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * PTR records cause no additional section processing.  These RRs are used
 * in special domains to point to some other location in the domain space.
 * These records are simple data, and don't imply any special processing
 * similar to that performed by CNAME, which identifies aliases.  See the
 * description of the IN-ADDR.ARPA domain for an example.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.12">RFC 1035 Section 3.3.12 - PTR RDATA Format</a>
 */
public class Ptr extends RData {
	
	/**
	 * A domain-name which points to some location in the
     * domain name space.
	 */
	private String ptrdname;

	/**
	 * Constructs a Domain Pointer {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Domain Pointer {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the Domain Pointer {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Ptr(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		StringBuilder sb = new StringBuilder();
		DNParser.parseDomainName(rawData, start, sb);
		ptrdname = sb.toString();
	}

	/**
	 * Convert this Domain Pointer {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.addAll(DNParser.toByteArray(ptrdname));
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA PTR:\n");
		sb.append("PTRDNAME: ").append(ptrdname);
		return sb.toString(); 
	}

	/**
	 * @return A domain name which points to some location in the domain name space.
	 */
	public String getPtrdname() {
		return ptrdname;
	}
}
