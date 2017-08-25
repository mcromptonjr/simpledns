package com.zorben.dns.message;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;
import com.zorben.dns.util.DNParser;

/**
 * Used for parsing and storing DNS Message Question data.
 * <br>
 * The format is visualized below:
 * <pre>
 *                                  1  1  1  1  1  1
 *    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                                               |
 *  /                     QNAME                     /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                     QTYPE                     |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                     QCLASS                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * @author Mac Crompton
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.2">RFC 1035 Section 4.1.2 - Question Section Format</a>
 *
 */
public class MessageQuestion implements MessageParser {
	
	/**
	 * A domain name represented as a sequence of labels, where
     * each label consists of a length octet followed by that
     * number of octets.  The domain name terminates with the
     * zero length octet for the null label of the root.  Note
     * that this field may be an odd number of octets; no
     * padding is used.
     * <br>
     * <br>
     * Look to {@link DNParser} for details of how domain names are parsed.
	 */
	private String qname;
	
	/**
	 * A two octet (byte) code which specifies the type of the query.
     * The values for this field include all codes valid for a
     * TYPE field, together with some more general codes which
     * can match more than one type of {@link ResourceRecord}.
     * 
     * <pre>
     * 
     * A               1 a host address
     * 
     * NS              2 an authoritative name server
     * 
     * CNAME           5 the canonical name for an alias
     * 
     * SOA             6 marks the start of a zone of authority
     * 
     * WKS             11 a well known service description
     * 
     * PTR             12 a domain name pointer
     * 
     * HINFO           13 host information
     * 
     * MINFO           14 mailbox or mail list information
     * 
     * MX              15 mail exchange
     * 
     * TXT             16 text strings
     * 
     * AXFR            252 A request for a transfer of an entire zone
	 *
	 * MAILB           253 A request for mailbox-related records (MB, MG or MR)
	 *
	 * MAILA           254 A request for mail agent RRs (Obsolete - see MX)
	 *
	 * *               255 A request for all records
     * </pre>
     * 
     * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.2.2">RFC 1035 Section 3.2.2 - TYPE values</a>
     * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.2.3">RFC 1035 Section 3.2.3 - QTYPE values</a>
	 */
	private short qtype;
	
	/**
	 * A two octet code that specifies the class of the query.
     * For example, the QCLASS field is IN for the Internet.
     * 
     * <pre>
     * IN              1 the Internet
	 *
	 * CS              2 the CSNET class (Obsolete - used only for examples in
     *          	   some obsolete RFCs)
	 *
	 * CH              3 the CHAOS class
	 *
	 * HS              4 Hesiod [Dyer 87]
	 * 
	 * *               255 any class
     * </pre>
	 */
	private short qclass;
	
	/**
	 * The total length in bytes of this {@link MessageQuestion} in raw byte format.
	 */
	private int length;

	/**
	 * Constructs a DNS Message Question from raw byte data.
	 * <br>
	 * Assumes the input {@code data} is encoded correctly with a {@link MessageQuestion} at index {@code start}.
	 * 
	 * @param data The raw byte data to parse through
	 * @param start The index at which to start parsing
	 */
	public MessageQuestion(byte[] data, int start) {
		int pos = start;	// Set start position
		StringBuilder sb = new StringBuilder();
		pos = DNParser.parseDomainName(data, start, sb);	// Parses through the {@code qname} section of the Message Question, returning the fully parsed out domain name and ending position
		qname = sb.toString();
		qtype = (short) (((data[pos] & 0xFF) << 8) | (data[pos+1] & 0xFF));	// Parses the next two bytes as the question type
		qclass = (short) (((data[pos+2] & 0xFF) << 8) | (data[pos+3] & 0xFF));	// Parses the remaining two bytes as the question class
		
		length = pos + 4 - start;	// Calculate the length of this {@link MessageQuestion}
	}
	
	/**
	 * Creates a {@link MessageQuestion} directly from specified parameters.
	 * @param qname The domain name to query
	 * @param qtype The question type, 1-byte integer
	 * @param qclass The question class 1-byte integer
	 */
	public MessageQuestion(String qname, short qtype, short qclass) {
		this.qname = qname;
		this.qtype = qtype;
		this.qclass = qclass;
	}

	/**
	 * Retrieves the length of this {@link MessageQuestion} in number of bytes when encoded in byte format.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Converts this object into the appropriate byte array for network transmission.
	 * @return A byte array representing this object.
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<Byte>(getLength());
		array.addAll(DNParser.toByteArray(qname));
		array.add((byte) ((qtype & 0xFF00) >> 8));
		array.add((byte) (qtype & 0x00FF));
		array.add((byte) ((qclass & 0xFF00) >> 8));
		array.add((byte) (qclass & 0x00FF));
		return array;
	}
	
	/**
	 * Converts this object into an easy to read string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("QNAME: " + qname + "\n");
		sb.append("QTYPE: " + qtype + "\n");
		sb.append("QCLASS: " + qclass + "\n");
		return sb.toString();
	}

	/**
	 * @return The domain name being queried by this {@link MessageQuestion}.
	 */
	public String getQname() {
		return qname;
	}

	/**
	 * @return The query type of this {@link MessageQuestion} as described above.
	 */
	public short getQtype() {
		return qtype;
	}

	/**
	 * @return The query class of this {@link MessageQuestion} as described above.
	 */
	public short getQclass() {
		return qclass;
	}
}
