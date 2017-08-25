/**
 * 
 */
package com.zorben.dns.example;

import com.zorben.dns.DNS;
import com.zorben.dns.message.Message;
import com.zorben.dns.message.MessageHeader;
import com.zorben.dns.message.MessageQuestion;

/**
 * An example class showing how to make a simple request to a public domain name server.
 * 
 * @author Mac Crompton
 *
 */
public class DNSExample {

	/**
	 * An example DNS query of the Google authoritative name server.
	 * <br>
	 * <br>
	 * Query type 2, class 1.
	 */
	public static void main(String[] args) {
		// These are the Level3 Primary and Secondary Public Domain Name Servers
		DNS dns = new DNS("209.244.0.3", "209.244.0.4");
		
		// Construct the message to be sent. Simple query header.
		MessageHeader mh = new MessageHeader(1, false, 0, false, false, false, false, 0, 0, 0, 0, 0);	// Can also just use the default {@link Message} constructor.
		Message message = new Message(mh);
		
		// Query the public dns for information about the 'google.com' authoritative name server.
		MessageQuestion mq = new MessageQuestion("google.com", (short)2, (short)1);	// Authoritative Name Server
		message.addQuestion(mq);
		
		// Convert the request to a byte array, send it, and capture the response.
		byte[] request = message.toByteArray();
		byte[] response = dns.makeRequest(request);
		
		// Pretty print the response.
		Message responseMessage = new Message(response, 0);
		System.out.println(responseMessage);
	}

}
