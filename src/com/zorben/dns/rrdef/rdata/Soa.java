package com.zorben.dns.rrdef.rdata;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Parses and stores data pertaining to the start of authority record.
 * <br>
 * Format is described below:
 * <pre>
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                     MNAME                     /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  /                     RNAME                     /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    SERIAL                     |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    REFRESH                    |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                     RETRY                     |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    EXPIRE                     |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    MINIMUM                    |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * SOA records cause no additional section processing.
 * <br>
 * All times are in units of seconds.
 * <br>
 * Most of these fields are pertinent only for name server maintenance
 * operations.  However, MINIMUM is used in all query operations that
 * retrieve RRs from a zone.  Whenever a RR is sent in a response to a
 * query, the TTL field is set to the maximum of the TTL field from the RR
 * and the MINIMUM field in the appropriate SOA.  Thus MINIMUM is a lower
 * bound on the TTL field for all RRs in a zone.  Note that this use of
 * MINIMUM should occur when the RRs are copied into the response and not
 * when the zone is loaded from a master file or via a zone transfer.  The
 * reason for this provison is to allow future dynamic update facilities to
 * change the SOA RR with known semantics.
 * 
 * @author Mac Crompton
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.3.13">RFC 1035 Section 3.3.13 - SOA RDATA Format</a>
 */
public class Soa extends RData {
	
	/**
	 * The domain-name of the name server that was the
     * original or primary source of data for this zone.
	 */
	private String mname;
	
	/**
	 * A domain-name which specifies the mailbox of the
     * person responsible for this zone.
	 */
	private String rname;
	
	/**
	 * The unsigned 32 bit version number of the original copy
     * of the zone.  Zone transfers preserve this value.  This
     * value wraps and should be compared using sequence space
     * arithmetic.
	 */
	private int serial;
	
	/**
	 * A 32 bit time interval before the zone should be
     * refreshed.
     * <br>
     * Time is measured in seconds.
	 */
	private int refresh;
	
	/**
	 * A 32 bit time interval that should elapse before a
     * failed refresh should be retried.
     * <br>
     * Time is measured in seconds.
	 */
	private int retry;
	
	/**
	 * A 32 bit time value that specifies the upper limit on
     * the time interval that can elapse before the zone is no
     * longer authoritative.
     * <br>
     * Time is measured in seconds.
	 */
	private int expire;
	
	/**
	 * The unsigned 32 bit minimum TTL field that should be
     * exported with any RR from this zone.
     * <br>
     * Time is measured in seconds.
	 */
	private int minimum;
	
	/**
	 * Constructs a Start of Authority {@link RData} object from raw byte data.
	 * <br>
	 * Assumes that a properly encoded Start of Authority {@link ResourceRecord} data object is stored in the {@code rawData} variable, at the {@code start} index, of length {@code length}.
	 * 
	 * @param data Raw byte data containing the Start of Authority {@link ResourceRecord} data.
	 * @param start Byte index indicating the beginning of this data object.
	 * @param length Length in bytes indicating the size of this data object.
	 */
	public Soa(byte[] data, int start, int length) {
		super(data, start, length);
		int pos = start;
		StringBuilder mnameSb = new StringBuilder();
		pos = DNParser.parseDomainName(data, pos, mnameSb);
		mname = mnameSb.toString();
		StringBuilder rnameSb = new StringBuilder();
		pos = DNParser.parseDomainName(data, pos, rnameSb);
		rname = rnameSb.toString();
		for(int i = 0; i < 4; i++) {
			serial = ((serial << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
		for(int i = 0; i < 4; i++) {
			refresh = ((refresh << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
		for(int i = 0; i < 4; i++) {
			retry = ((retry << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
		for(int i = 0; i < 4; i++) {
			expire = ((expire << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
		for(int i = 0; i < 4; i++) {
			minimum = ((minimum << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
	}

	/**
	 * Convert this Start of Authority {@link ResourceRecord} data object into raw byte format.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>();
		array.addAll(DNParser.toByteArray(mname));
		array.addAll(DNParser.toByteArray(rname));
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((serial & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((refresh & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((retry & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((expire & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << ((4-i-1) * 8);
			byte b = (byte) ((minimum & mask) >> ((4-i-1) * 8));
			array.add(b);
		}
		return array;
	}
	
	/**
	 * Convert this object into a nicely formatted string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RDATA SOA:\n");
		sb.append("MNAME: ").append(mname).append("\n");
		sb.append("RNAME: ").append(rname).append("\n");
		sb.append("SERIAL: ").append(serial).append("\n");
		sb.append("REFRESH: ").append(refresh).append("\n");
		sb.append("RETRY: ").append(retry).append("\n");
		sb.append("EXPIRE: ").append(expire).append("\n");
		sb.append("MINIMUM: ").append(minimum);
		return sb.toString(); 
	}

	/**
	 * @return The domain name representing the primary or original owner of data for this zone.
	 */
	public String getMname() {
		return mname;
	}

	/**
	 * @return A domain name representing the mailbox of the person responsible for this zone.
	 */
	public String getRname() {
		return rname;
	}

	/**
	 * @return An unsigned 32-bit integer representing the serial number for this zone.
	 */
	public int getSerial() {
		return serial;
	}

	/**
	 * @return The time in seconds before this zone should be refreshed.
	 */
	public int getRefresh() {
		return refresh;
	}

	/**
	 * @return The time in seconds before a failed refresh should be retried.
	 */
	public int getRetry() {
		return retry;
	}

	/**
	 * @return The time in seconds until this zone is no longer authoritative.
	 */
	public int getExpire() {
		return expire;
	}

	/**
	 * @return The minimum {@link ResourceRecord} TTL value in seconds that this server will respond with.
	 */
	public int getMinimum() {
		return minimum;
	}
}
