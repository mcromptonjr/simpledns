package com.zorben.dns.message;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;

/**
 * Used for parsing and storing DNS Message Header data.
 * <br>
 * Format can be described as below:
 * <pre>
 *                                  1  1  1  1  1  1 
 *    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5 
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                      ID                       |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    QDCOUNT                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    ANCOUNT                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    NSCOUNT                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                    ARCOUNT                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1.1">RFC 1035 Section 4.1.1 - Message Header</a>
 *
 */
public class MessageHeader implements MessageParser {
	
	/**
	 * A 16 bit identifier assigned by the program that
     * generates any kind of query.  This identifier is copied
     * the corresponding reply and can be used by the requester
     * to match up replies to outstanding queries.
	 */
	private short id;
	
	/**
	 * A one bit field that specifies whether this message is a
     * query (0), or a response (1).
	 */
	private boolean qr;
	
	/**
	 * A four bit field that specifies kind of query in this
     * message.  This value is set by the originator of a query
     * and copied into the response.  The values are:
	 * <pre>
     * 0               a standard query (QUERY)
	 * 
     * 1               an inverse query (IQUERY)
	 * 
     * 2               a server status request (STATUS)
	 * 
     * 3-15            reserved for future use
     * </pre>
	 */
	private byte opcode;
	
	/**
	 * Authoritative Answer - this bit is valid in responses,
     * and specifies that the responding name server is an
     * authority for the domain name in question section.
	 * <br>
     * Note that the contents of the answer section may have
     * multiple owner names because of aliases.  The AA bit
     * corresponds to the name which matches the query name, or
     * the first owner name in the answer section.
	 */
	private boolean aa;
	
	/**
	 * TrunCation - specifies that this message was truncated
     * due to length greater than that permitted on the
     * transmission channel.
	 */
	private boolean tc;
	
	/**
	 * Recursion Desired - this bit may be set in a query and
     * is copied into the response.  If RD is set, it directs
     * the name server to pursue the query recursively.
     * Recursive query support is optional.
	 */
	private boolean rd;
	
	/**
	 * Recursion Available - this be is set or cleared in a
     * response, and denotes whether recursive query support is
     * available in the name server.
	 */
	private boolean ra;
	
	/**
	 * Reserved for future use.  Must be zero in all queries
     * and responses.
	 */
	private byte z;
	
	/**
	 * Response code - this 4 bit field is set as part of
     * responses.  The values have the following
     * interpretation:
	 * <pre>
     * 0               No error condition
	 *
     * 1               Format error - The name server was
     *                 unable to interpret the query.
	 *
     * 2               Server failure - The name server was
     *                 unable to process this query due to a
     *                 problem with the name server.
	 *
     * 3               Name Error - Meaningful only for
     *                 responses from an authoritative name
     *                 server, this code signifies that the
     *                 domain name referenced in the query does
     *                 not exist.
	 *
     * 4               Not Implemented - The name server does
     *                 not support the requested kind of query.
	 *
     * 5               Refused - The name server refuses to
     *                 perform the specified operation for
     *                 policy reasons.  For example, a name
     *                 server may not wish to provide the
     *                 information to the particular requester,
     *                 or a name server may not wish to perform
     *                 a particular operation (e.g., zone
     *                 transfer) for particular data.
	 *
     * 6-15            Reserved for future use.
     * </pre>
	 */
	private byte rcode;
	
	/**
	 * An unsigned 16 bit integer specifying the number of
     * entries in the {@link MessageQuestion} section.
	 */
	private short qdcount;
	
	/**
	 * An unsigned 16 bit integer specifying the number of
     * {@link ResourceRecord}s in the answer section.
	 */
	private short ancount;
	
	/**
	 * An unsigned 16 bit integer specifying the number of name
     * server {@link ResourceRecord}s in the authority records
     * section.
	 */
	private short nscount;
	
	/**
	 * An unsigned 16 bit integer specifying the number of
     * {@link ResourceRecord}s in the additional records section.
	 */
	private short arcount;

	/**
	 * Constructs a DNS {@link MessageHeader} from raw byte data and a start index.
	 * <br>
	 * Assumes a properly encoded {@link MessageHeader} according to RFC 1035 Section 4.1.1.
	 * <br>
	 * For a visual description of how this data is parsed, look at the diagram in the {@link MessageHeader} class description.
	 * 
	 * @param data The raw byte data containing the {@link MessageHeader}.
	 * @param start The index to start reading the {@link MessageHeader} from the {@code data} array.
	 */
	public MessageHeader(byte[] data, int start) {
		// NOTE: Bytes are being referred to as "first, second, third" meaning that the byte at {@code start} is the "first" byte.
		// 		 However, bits are referred to starting at 0. Meaning bits are indexed from 0 to 7.
		id = (short) (((data[start] & 0xFF) << 8) | (data[start+1] & 0xFF));		// Combines the first two bytes to represent the ID of the header
		qr = ((data[start+2] & 0xFF) >> 7) == 1;									// Grabs the third byte and checks the 0th bit to determine if this is a query
		opcode = (byte) ((data[start+2] & 0x78) >> 3);								// Grabs the third byte and combines bits 1-4 to form the opcode
		aa = ((data[start+2] & 0x04) >> 2) == 1;									// Grabs the third byte and checks the 5th bit to determine if this is an authoritative answer
		tc = ((data[start+2] & 0x02) >> 1) == 1;									// Grabs the third byte and checks the 6th bit to determine if this message is truncated
		rd = ((data[start+2] & 0x01) >> 0) == 1;									// Grabs the third byte and checks the 7th bit to determine if recursion is desired
		ra = (data[start+3] >> 7) == 1;												// Grabs the fourth byte and checks the 0th bit to determine if recursion is available
		z = (byte) ((data[start+3] & 0x70) >> 4);									// Grabs the fourth byte and checks bits 1-3 for the {@code z} value. They should always represent the number 0.
		rcode = (byte) ((data[start+3] & 0x0F) >> 0);								// Grabs the fourth byte and checks bits 4-7 representing the response code.
		qdcount = (short) (((data[start+4] & 0xFF) << 8) | (data[start+5] & 0xFF));	// Grabs the fifth and sixth bytes and combines them to represent the number of {@link MessageQuestion}s
		ancount = (short) (((data[start+6] & 0xFF) << 8) | (data[start+7] & 0xFF));	// Grabs the seventh and eighth bytes and combines them to represent the number of Answer {@link ResourceRecord}s
		nscount = (short) (((data[start+8] & 0xFF) << 8) | (data[start+9] & 0xFF));	// Grabs the ninth and tenth bytes and combines them to represent the number of Authority {@link ResourceRecord}s
		arcount = (short) (((data[start+10] & 0xFF) << 8) | (data[start+11] & 0xFF));//Grabs the eleventh and twelth bytes and combines them to represent the number of Additional {@link ResourceRecord}s
	}
	
	/**
	 * Constructs a DNS Message Header
	 * @param id The ID of the message being sent. The response will carry the same ID. This is used for keeping track of outstanding requests.
	 * @param qr Is this message a query (0), or a response (1)
	 * @param opcode 4 bit field that specifies the type of query:
	 * 					0 - a standard query (QUERY)
	 * 					1 - an inverse query (IQUERY)
	 * 					2 - a server status request (STATUS)
	 * 				 3-15 - reserved for future use
	 * @param aa Set if the responding name server is an authoritative server for the domain name
	 * @param tc Set if the message was truncated, due to length restrictions on the transmission channel.
	 * @param rd Set if recursion is desired by the name server
	 * @param ra Set if recursion is available by the responding name server.
	 * @param rcode Response Code - 4 bit field set with the following values:
	 * 					0 - No error condition
	 * 					1 - Format error - The name server was unable to interpret the query
	 * 					2 - Server failure - The name server was unable to process this query due to a problem with the name server
	 * 					3 - Name Error - Meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist
	 * 					4 - Not Implemented - The name server does not support the requested kind of query.
	 * 					5 - Refused - The name server refuses to perform the specified operation for policy reasons.  For example, a name server may not wish to provide the information to the particular requester, or a name server may not wish to perform a particular operation (e.g., zone transfer) for particular data.
	 * 				 6-15 - Reserved for future use.
	 * @param qdcount Unsigned 16 bit integer specifying the number of entries in the question section.
	 * @param ancount Unsigned 16 bit integer specifying the number of answers in the answer section.
	 * @param nscount Unsigned 16 bit integer specifying the number of authority records in the authority section.
	 * @param arcount Unsigned 16 bit integer specifying the number of additional records in the additional section.
	 */
	public MessageHeader(int id, boolean qr, int opcode, boolean aa,
			boolean tc, boolean rd, boolean ra, int rcode, int qdcount,
			int ancount, int nscount, int arcount) {
		super();
		this.id = (short) (id & 0xFFFF);
		this.qr = qr;
		this.opcode = (byte) (opcode & 0xFF);
		this.aa = aa;
		this.tc = tc;
		this.rd = rd;
		this.ra = ra;
		this.rcode = (byte) (rcode & 0xFF);
		this.qdcount = (short) (qdcount & 0xFFFF);
		this.ancount = (short) (ancount & 0xFFFF);
		this.nscount = (short) (nscount & 0xFFFF);
		this.arcount = (short) (arcount & 0xFFFF);
	}

	/**
	 * Retrieves the length in bytes of this {@link MessageHeader} in raw byte form.
	 * <br>
	 * This is always 12 bytes.
	 */
	public int getLength() {
		return 12;
	}
	
	/**
	 * Retrieves the number of {@link MessageQuestion}s as specified in this {@link MessageHeader}
	 * @return The number of questions as an unsigned 16-bit integer.
	 */
	public int getNumQuestions() {
		return (int) (qdcount & 0xFFFF);
	}
	
	/**
	 * Retrieves the number of Answer {@link ResourceRecord}s as specified in this {@link MessageHeader}
	 * @return The number of answer RRs as an unsigned 16-bit integer.
	 */
	public int getNumAnswers() {
		return (int) (ancount & 0xFFFF);
	}
	
	/**
	 * Retrieves the number of Authority {@link ResourceRecord}s as specified in this {@link MessageHeader}
	 * @return The number of authority RRs as an unsigned 16-bit integer.
	 */
	public int getNumAuthorities() {
		return (int) (nscount & 0xFFFF);
	}
	
	/**
	 * Retrieves the number of Additional {@link ResourceRecord}s as specified in this {@link MessageHeader}
	 * @return The number of additional RRs as an unsigned 16-bit integer.
	 */
	public int getNumAdditional() {
		return (int) (arcount & 0xFFFF);
	}
	
	/**
	 * Converts this {@link MessageHeader} object into an array of bytes for network transmission.
	 * @return The byte array representing this object.
	 */
	public ArrayList<Byte> toByteArray() {
		// TODO: This method needs light commenting. This is essentially the inverse of the constructor.
		ArrayList<Byte> array = new ArrayList<Byte>(getLength());
		array.add((byte) ((id & 0xFF00) >> 8));
		array.add((byte) (id & 0x00FF));
		byte byte3 = (byte) ((qr?1:0) << 4);
		byte3 = (byte) (((opcode & 0x0F) | byte3) << 1);
		byte3 = (byte) (((aa?1:0) | byte3) << 1);
		byte3 = (byte) (((tc?1:0) | byte3) << 1);
		byte3 = (byte) ((rd?1:0) | byte3);
		array.add(byte3);
		byte byte4 = (byte) ((ra?1:0) << 7);
		byte4 = (byte) ((rcode & 0x0F) | byte4);
		array.add(byte4);
		array.add((byte) ((qdcount & 0xFF00) >> 8));
		array.add((byte) (qdcount & 0x00FF));
		array.add((byte) ((ancount & 0xFF00) >> 8));
		array.add((byte) (ancount & 0x00FF));
		array.add((byte) ((nscount & 0xFF00) >> 8));
		array.add((byte) (nscount & 0x00FF));
		array.add((byte) ((arcount & 0xFF00) >> 8));
		array.add((byte) (arcount & 0x00FF));
		return array;
	}
	
	/**
	 * Converts this object into an easy to read String for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + id + "\n");
		sb.append("QR: " + qr + "\n");
		sb.append("OPCODE: " + opcode + "\n");
		sb.append("AA: " + aa + "\n");
		sb.append("TC: " + tc + "\n");
		sb.append("RD: " + rd + "\n");
		sb.append("RA: " + ra + "\n");
		sb.append("Z: " + z + "\n");
		sb.append("RCODE: " + rcode + "\n");
		sb.append("QDCOUNT: " + qdcount + "\n");
		sb.append("ANCOUNT: " + ancount + "\n");
		sb.append("NSCOUNT: " + nscount + "\n");
		sb.append("ARCOUNT: " + arcount + "\n");
		return sb.toString();
	}
	
	/**
	 * Increments the question counter represented in this {@link MessageHeader}.
	 * <br>
	 * This is for encoding this {@link MessageHeader} later for network transmission.
	 */
	public void addQuestion() {
		qdcount++;
	}

	/**
	 * @return The unique ID identifying this {@link Message}.
	 */
	public short getId() {
		return id;
	}

	/**
	 * @return Flag indicating if this is a query (false), or a response (true).
	 */
	public boolean isQr() {
		return qr;
	}

	/**
	 * @return An 8-bit integer representing the operation being performed by this {@link Message}.
	 */
	public byte getOpcode() {
		return opcode;
	}

	/**
	 * @return Flag indicating if this is an authoritative answer.
	 */
	public boolean isAa() {
		return aa;
	}

	/**
	 * @return Flag indicating if this {@link Message} has been truncated.
	 */
	public boolean isTc() {
		return tc;
	}

	/**
	 * @return Flag indicating if recursion is desired.
	 */
	public boolean isRd() {
		return rd;
	}

	/**
	 * @return Flag indicating if recursion is available.
	 */
	public boolean isRa() {
		return ra;
	}

	/**
	 * @return Always returns 0.
	 */
	public byte getZ() {
		return z;
	}

	/**
	 * @return An 8-bit integer representing the response code pertaining to this {@link Message}.
	 */
	public byte getRcode() {
		return rcode;
	}

	/**
	 * @return The number of queries contained in this {@link Message}.
	 */
	public short getQdcount() {
		return qdcount;
	}

	/**
	 * @return The number of answers contained in this {@link Message}.
	 */
	public short getAncount() {
		return ancount;
	}

	/**
	 * @return The number of authoritative name server records contained in this {@link Message}.
	 */
	public short getNscount() {
		return nscount;
	}

	/**
	 * @return The number of additional records contained in this {@link Message}.
	 */
	public short getArcount() {
		return arcount;
	}
}
