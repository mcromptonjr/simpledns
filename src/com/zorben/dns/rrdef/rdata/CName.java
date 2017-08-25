package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Parses and stores {@link ResourceRecord} Canonical Name data.
 * <br>
 * The format for Canonical Name data can be found below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                     CNAME                     /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.1">RFC 1035 Section 3.3.1 - CNAME RDATA Format</a>
 */
public class CName extends RData {
	
	/**
	 * A domain-name which specifies the canonical or primary
     * name for the owner.  The owner name is an alias.
	 */
	private String cname;

	/**
	 * Constructs a Canonical Name data object using raw byte data
	 * <br>
	 * Assumes that a Canonical Name data object is encoded in the {@code rawData} at the byte indicated by {@code start} with length in bytes {@code length}.
	 * @param rawData The byte array containing the Canonical Name object.
	 * @param start The byte index indicating the start of the Canonical Name object.
	 * @param length The length in bytes of the Canonical Name object.
	 */
	public CName(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		StringBuilder sb = new StringBuilder();
		DNParser.parseDomainName(rawData, start, sb);
		cname = sb.toString();
	}

	/**
	 * Converts this Canonical Name object into byte format for transmission.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.addAll(DNParser.toByteArray(cname));
		return array;
	}
	
	/**
	 * Converts this Canonical Name object into an easy to read string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA CNAME:\n");
		sb.append("CNAME: ").append(cname);
		return sb.toString(); 
	}

	/**
	 * @return The canonical or primary name for the owner of this record.
	 */
	public String getCname() {
		return cname;
	}
}
