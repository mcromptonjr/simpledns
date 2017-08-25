package com.zorben.dns.rrdef;

import java.util.ArrayList;

import com.zorben.dns.message.MessageParser;
import com.zorben.dns.rrdef.rdata.RData;
import com.zorben.dns.rrdef.rdata.RDataFactory;
import com.zorben.dns.rrdef.rdata.RDataNotSupportedException;
import com.zorben.dns.util.DNParser;

/**
 * Used for parsing and storing DNS Resource Record data.
 * <br>
 * The format is visualized below:
 * <pre>
 * 
 *                                  1  1  1  1  1  1
 *    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                                               |
 *  /                                               /
 *  /                      NAME                     /
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                      TYPE                     |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                     CLASS                     |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                      TTL                      |
 *  |                                               |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *  |                   RDLENGTH                    |
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
 *  /                     RDATA                     /
 *  /                                               /
 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author Mac Crompton
 * 
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.2">RFC 1035 Section 3.2 - Resource Records</a>
 *
 */
public class ResourceRecord implements MessageParser {

	/**
	 * An owner name, i.e., the name of the node to which this
     * resource record pertains.
     * <br>
     * The domain name this RR pertains to.
	 */
	private String name;
	
	/**
	 * Two octets containing one of the RR TYPE codes.
	 * <pre>
	 * TYPE            value and meaning
	 *
	 * A               1 a host address
	 *
	 * NS              2 an authoritative name server
	 *
	 * MD              3 a mail destination (Obsolete - use MX)
	 *
	 * MF              4 a mail forwarder (Obsolete - use MX)
	 *
	 * CNAME           5 the canonical name for an alias
	 *
	 * SOA             6 marks the start of a zone of authority
	 *
	 * MB              7 a mailbox domain name (EXPERIMENTAL)
	 *
	 * MG              8 a mail group member (EXPERIMENTAL)
	 *
	 * MR              9 a mail rename domain name (EXPERIMENTAL)
	 *
	 * NULL            10 a null RR (EXPERIMENTAL)
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
	 * </pre>
	 */
	private short type;
	
	/**
	 * Two octets containing one of the RR CLASS codes.
	 * <pre>
	 * IN              1 the Internet
	 * 
	 * CS              2 the CSNET class (Obsolete - used only for examples in
 	 *                 some obsolete RFCs)
	 * 
	 * CH              3 the CHAOS class
	 * 
	 * HS              4 Hesiod [Dyer 87]
	 * </pre>
	 */
	private short _class;
	
	/**
	 * A 32 bit signed integer that specifies the time interval
     * that the resource record may be cached before the source
     * of the information should again be consulted.  Zero
     * values are interpreted to mean that the RR can only be
     * used for the transaction in progress, and should not be
     * cached.  For example, SOA records are always distributed
     * with a zero TTL to prohibit caching.  Zero values can
     * also be used for extremely volatile data.
	 */
	private int ttl;
	
	/**
	 * An unsigned 16 bit integer that specifies the length in
     * octets of the RDATA field.
	 */
	private int rdlength;
	
	/**
	 * A variable length string of octets that describes the
     * resource.  The format of this information varies
     * according to the TYPE and CLASS of the resource record.
	 */
	private RData rdata;

	/**
	 * The total length in bytes of this {@link ResourceRecord} in raw byte format.
	 */
	private int length;

	/**
	 * Constructs a DNS Resource Record from raw byte data.
	 * <br>
	 * Assumes the input {@code data} is correctly encoded with a {@link ResourceRecord} at index {@code start}.
	 * 
	 * @param data The raw byte data containing a properly encoded {@link ResourceRecord}
	 * @param start The byte index at which the encoded {@link ResourceRecord} starts at.
	 */
	public ResourceRecord(byte[] data, int start) {
		StringBuilder sb = new StringBuilder();
		// Parses through the {@code name} section of the {@link ResourceRecord}, returning the fully parsed out domain name and ending position.
		int pos = DNParser.parseDomainName(data, start, sb);	
		name = sb.toString();
		
		// Grabs the next two bytes as the {@link ResourceRecord} type.
		type = (short) (((data[pos] & 0xFF) << 8) | data[pos+1]);
		pos += 2;
		
		// Grabs the next two bytes as the {@link ResourceRecord} class.
		_class = (short) (((data[pos] & 0xFF) << 8) | data[pos+1]);
		pos += 2;
		
		// Grabs the next four bytes as the {@link ResourceRecord} time to live value.
		for(int i = 0; i < 4; i++) {
			ttl = ((ttl << 8) | (data[pos+i] & 0xFF));
		}
		pos += 4;
		
		// Grabs the next two bytes as the {@link ResourceRecord} data length.
		rdlength = ((data[pos] & 0xFF) << 8) | data[pos+1];
		pos += 2;
		
		// Parses the next {@code length} bytes as the {@link ResourceRecord} data.
		// If the type is unsupported, the data is ignored and {@code rdata} remains null.
		try {
			rdata = RDataFactory.parseRData(type, data, pos, rdlength);
		} catch(RDataNotSupportedException ex) {
			System.err.println(ex.getMessage());
		}
		
		// Store the total {@link ResourceRecord} length in number of bytes.
		length = (pos + rdlength - start);
	}

	/**
	 * Constructs a {@link ResourceRecord} given standard parameters.
	 * 
	 * @param name Domain name this {@link ResourceRecord} represents.
	 * @param type A two byte integer indicating the {@link ResourceRecord} type.
	 * @param _class A two byte integer indicating the {@link ResourceRecord} class.
	 * @param ttl A four byte integer indicating the time to live.
	 * @param rdata A {@link RData} object representing the data to be stored in this {@link ResourceRecord}.
	 */
	public ResourceRecord(String name, short type, short _class, int ttl, RData rdata) {
		this.name = name;
		this.type = type;
		this._class = _class;
		this.ttl = ttl;
		this.rdata = rdata;	// TODO: Validate {@link RData} object with provided {@code type}.
		this.rdlength = rdata.toByteArray().size();
	}

	/**
	 * Retrieve the length in bytes of this {@link ResourceRecord} in raw byte format.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Converts this {@link ResourceRecord} to its raw byte format for transmission.
	 * 
	 * @return The raw byte format of this {@link ResourceRecord}
	 */
	public ArrayList<Byte> toByteArray() {
		ArrayList<Byte> array = new ArrayList<>(getLength());
		// Convert the domain name to the correct byte format.
		array.addAll(DNParser.toByteArray(name));
		
		// Append the two bytes representing the type.
		array.add((byte) ((type & 0xFF00) >> 8));
		array.add((byte) (type & 0x00FF));
		
		// Append the two bytes representing the class.
		array.add((byte) ((_class & 0xFF00) >> 8));
		array.add((byte) (_class & 0x00FF));
		
		// Append the 4 bytes representing the time to live.
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << (4-i-1);
			byte b = (byte) ((ttl & mask) >> (4-i-1));
			array.add(b);
		}
		
		// Append the 4 bytes representing the {@link RData} length in bytes.
		for(int i = 0; i < 4; i++) {
			int mask = 0xFF << (4-i-1);
			byte b = (byte) ((rdlength & mask) >> (4-i-1));
			array.add(b);
		}
		
		// Convert the {@link RData} into raw byte format and append it.
		array.addAll(rdata.toByteArray());
		return array;
	}

	/**
	 * Converts this {@link ResourceRecord} into an easy to read string for debugging purposes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NAME: " + name + "\n");
		sb.append("TYPE: " + type + "\n");
		sb.append("CLASS: " + _class + "\n");
		sb.append("TTL: " + ttl + "\n");
		sb.append("RDLENGTH: " + rdlength + "\n");
		sb.append(rdata.toString());
		return sb.toString();
	}

	/**
	 * @return The domain name pertaining to this {@link ResourceRecord}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The type of {@link ResourceRecord} as described above.
	 */
	public short getType() {
		return type;
	}

	/**
	 * @return The {@link ResourceRecord} class as described above.
	 */
	public short get_class() {
		return _class;
	}

	/**
	 * @return The time in seconds before this {@link ResourceRecord} expires.
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @return The total length in bytes of the data attached to this {@link ResourceRecord}.
	 */
	public int getRdlength() {
		return rdlength;
	}

	/**
	 * @return The parsed out data attached to this {@link ResourceRecord}.
	 */
	public RData getRdata() {
		return rdata;
	}
}
