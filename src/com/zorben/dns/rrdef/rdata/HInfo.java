package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.CSParser;

/**
 * Parses and stores data pertaining to the information about a specific host.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                      CPU                      /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                       OS                      /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.2">RFC 1035 Section 3.3.2 - HINFO RDATA Format</a>
 */
public class HInfo extends RData {
	
	/**
	 * A character-string which specifies the CPU type.
	 */
	private String cpu;
	
	/**
	 * A character-string which specifies the operating system type.
	 */
	private String os;
	
	/**
	 * Constructs a host information {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded host information {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param data Raw byte data containing the host information {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public HInfo(byte[] data, int start, int length) {
		super(data, start, length);
		StringBuilder sb = new StringBuilder();
		int endPos = CSParser.parseCharString(data, start, sb);
		cpu = sb.toString();
		sb = new StringBuilder();
		endPos = CSParser.parseCharString(data, endPos, sb);
		os = sb.toString();
	}

	/**
	 * Convert this Host Information {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.addAll(CSParser.toByteArray(cpu));
		array.addAll(CSParser.toByteArray(os));
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA HINFO:\n");
		sb.append("CPU: ").append(cpu).append("\n");
		sb.append("OS: ").append(os);
		return sb.toString(); 
	}

	/**
	 * @return A string containing information about the host's cpu. Formatting is up to the responding server.
	 */
	public String getCpu() {
		return cpu;
	}

	/**
	 * @return A string containing information about the host's operating system. Formatting is up to the responding server.
	 */
	public String getOs() {
		return os;
	}
}
