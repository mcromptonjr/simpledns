package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;

/**
 * Abstract structure for how {@link ResourceRecord} data should be stored and parsed.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3">RFC 1035 Section 3.3 - Standard RRs</a>
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.4">RFC 1035 Section 3.3 - Internet Specific RRs</a>
 */
public abstract class RData {
	

	/**
	 * Construct this Resource Record data object with the provided raw byte data, start index, and length in bytes.
	 * <br>
	 * Assumes the input {@code data} is encoded correctly with an {@link RData} object at index {@code start}.
	 * 
	 * @param rawData The raw data to parse as an {@link RData} object.
	 * @param start The byte index to start parsing at.
	 * @param length The length in bytes of this {@link RData} object in raw byte format.
	 */
	public RData(byte[] rawData, int start, int length) {
		
	}
	
	/**
	 * Convert this object into raw byte format for transmission.
	 * 
	 * @return This {@link RData} in raw byte format.
	 */
	public abstract ArrayList<Byte> toByteArray();
}
