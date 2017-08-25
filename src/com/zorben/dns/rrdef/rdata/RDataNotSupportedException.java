/**
 * 
 */
package com.zorben.dns.rrdef.rdata;

/**
 * An exception for indicating that an {@link RData} that is trying to be constructed is unsupported.
 * 
 * @author Mac Crompton
 *
 */
public class RDataNotSupportedException extends Exception {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8448415128826431365L;

	/**
	 * @param type A text string indicating the unsupported {@link RData} type.
	 */
	public RDataNotSupportedException(String type) {
		super(type + " is not a supported type.");
		
	}
}
