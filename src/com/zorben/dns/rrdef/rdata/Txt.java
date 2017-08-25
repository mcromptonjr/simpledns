package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.CSParser;

/**
 * Parses and stores data pertaining to the text data record.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                   TXT-DATA                    /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * TXT RRs are used to hold descriptive text.  The semantics of the text
 * depends on the domain where it is found.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.14">RFC 1035 Section 3.3.14 - TEXT RDATA Format</a>
 */
public class Txt extends RData {
	
	/**
	 * One or more character-strings.
	 */
	private ArrayList<String> txtdata;

	/**
	 * Constructs a Text Data {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Text {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param rawData Raw byte data containing the Text {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Txt(byte[] rawData, int start, int length) {
		super(rawData, start, length);
		txtdata = new ArrayList<String>();
		int endPos = start;
		while(endPos - start < length) {
			StringBuilder sb = new StringBuilder();
			// Parses the DNS character string into a Java string
			endPos = CSParser.parseCharString(rawData, endPos, sb);
			txtdata.add(sb.toString());
		}
	}

	/**
	 * Convert this Text {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		for(String txt : txtdata) {
			array.addAll(CSParser.toByteArray(txt));
		}
		return array;
	}

	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA TXT:\n");
		sb.append("TXTDATA: ").append(txtdata);
		return sb.toString(); 
	}

	/**
	 * @return The set of strings returned by the responding name server. This text can be formatted in any way.
	 */
	public ArrayList<String> getTxtdata() {
		return txtdata;
	}
}
