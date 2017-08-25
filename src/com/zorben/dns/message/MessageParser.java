package com.zorben.dns.message;

import java.util.ArrayList;

/**
 * An interface creating a framework for how DNS data should behave.
 * 
 * @author Mac Crompton
 *
 */
public interface MessageParser {
	
	int getLength();
	
	ArrayList<Byte> toByteArray();
}
