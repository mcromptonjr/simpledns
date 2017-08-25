package com.zorben.dns.rrdef.rdata;

/**
 * A static factory class for generating {@link RData} objects given the appropriate {@code type}.
 * 
 * @author Mac Crompton
 *
 */
public class RDataFactory {
	
	/**
	 * Generates an {@link RData} object given the appropriate {@code type} and assuming the data at the specified index is encoded properly.
	 * <br>
	 * The following are supported {@link RData} types:<br>
	 * 1  - A - Internet Address<br>
	 * 2  - NS - Name Server<br>
	 * 5  - CNAME - Canonical Name<br>
	 * 6  - SOA - Start of Authority<br>
	 * 11 - WKS - Well Known Service<br>
	 * 12 - PTR - Domain Pointer<br>
	 * 13 - HINFO - Host Information<br>
	 * 15 - MX - Mail Exchange<br>
	 * 16 - TXT - Text Data<br>
	 * <br>
	 * Types listed in RFC 1035 as <strong>(EXPERIMENTAL)</strong> or <strong>(Obsolete)</strong> are unsupported.
	 * 
	 * @param type The {@link RData} type as described above.
	 * @param data The raw byte data to form the {@link RData} object from.
	 * @param start The byte index to start reading from.
	 * @param length The length in bytes of the {@link RData} object.
	 * @return If the type is supported, returns an {@link RData} object representing the contents of the encoded {@link RData} object in {@code data} at {@code start}.
	 * @throws RDataNotSupportedException An exception is thrown if the provided type is unsupported.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc1035#section-3.2.2">RFC 1035 Section 3.2.2 - Type Values</a>
	 */
	public static RData parseRData(short type, byte[] data, int start, int length) throws RDataNotSupportedException {
		RData rdata = null;
		
		switch(type) {
		
		case 1:
			rdata = new A(data, start, length);
			break;
		case 2:
			rdata = new Ns(data, start, length);
			break;
		case 3:
			throw new RDataNotSupportedException("MD");
		case 4:
			throw new RDataNotSupportedException("MF");
		case 5:
			rdata = new CName(data, start, length);
			break;
		case 6:
			rdata = new Soa(data, start, length);
			break;
		case 7:
			throw new RDataNotSupportedException("MB");
		case 8:
			throw new RDataNotSupportedException("MG");
		case 9:
			throw new RDataNotSupportedException("MR");
		case 10:
			throw new RDataNotSupportedException("NULL");
		case 11:
			rdata = new Wks(data, start, length);
			break;
		case 12:
			rdata = new Ptr(data, start, length);
			break;
		case 13:
			rdata = new HInfo(data, start, length);
			break;
		case 14:
			throw new RDataNotSupportedException("MINFO");
		case 15:
			rdata = new Mx(data, start, length);
			break;
		case 16:
			rdata = new Txt(data, start, length);
			break;
		default:
			throw new RDataNotSupportedException(type+"");
		}
		return rdata;
	}
}
