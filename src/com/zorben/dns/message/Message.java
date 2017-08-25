package com.zorben.dns.message;

import java.util.ArrayList;

import com.zorben.dns.rrdef.ResourceRecord;

/**
 * Used for parsing and storing DNS Message data.
 * <br>
 * Data in this class is parsed according to RFC 1035 Section 4.
 * 
 * @author Mac Crompton
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4">RFC 1035 Section 4</a>
 *
 */
public class Message {
	
	
	/**
	 * The {@link MessageHeader} section is always present.  The header includes fields that
	 * specify which of the remaining sections are present, and also specify
	 * whether the message is a query or a response, a standard query or some
	 * other opcode, etc.
	 */
	protected MessageHeader header;
	
	/**
	 * The {@link MessageQuestion} section contains fields that describe a
	 * question to a name server.  These fields are a query type (QTYPE), a
	 * query class (QCLASS), and a query domain name (QNAME).  
	 */
	protected ArrayList<MessageQuestion> questions;
	
	/**
	 * The answer section contains {@link ResourceRecord}s that answer the
	 * question.
	 */
	protected ArrayList<ResourceRecord> answers;
	
	/**
	 * The authority section contains {@link ResourceRecord}s that point toward an
	 * authoritative name server.
	 */
	protected ArrayList<ResourceRecord> authorities;
	
	/**
	 * the additional records section contains {@link ResourceRecord}s
	 * which relate to the query, but are not strictly answers for the
	 * question.
	 */
	protected ArrayList<ResourceRecord> additional;
	
	/**
	 * Constructs a {@link Message} with a random id and default {@link MessageHeader} parameters.
	 */
	public Message() {
		// TODO: This is a pretty poor way of generating proper IDs. Come up with a new scheme.
		this(new MessageHeader((int) (Math.random()*Short.MAX_VALUE), false, 0, false, false, true, false, 0, 0, 0, 0, 0));
	}
	
	/**
	 * Constructs a {@link Message} with the provided {@link MessageHeader}.
	 * @param header The {@link MessageHeader} containing information about this {@link Message}. Describing the type of operations expected from this {@link Message}.
	 */
	public Message(MessageHeader header) {
		this.header = header;
		questions = new ArrayList<MessageQuestion>();
		answers = new ArrayList<ResourceRecord>();
		authorities = new ArrayList<ResourceRecord>();
		additional = new ArrayList<ResourceRecord>();
	}

	/**
	 * Constructs a {@link Message} from raw byte data and a starting byte offset.
	 * This constructor assumes a properly formatted DNS {@link Message} at the specified start offset.
	 * 
	 * @param data A byte array containing the DNS {@link Message} to be parsed.
	 * @param start An integer specifying the byte at which the {@link Message} starts in the {@code data} array.
	 * @see <a href="https://tools.ietf.org/html/rfc1035#page-25">RFC 1035 Section 4.1 - Message Format</a>
	 */
	public Message(byte[] data, int start) {
		int pos = start;	// Set the position to the start byte
		header = new MessageHeader(data, pos);	// Parse the {@link MessageHeader}
		pos += header.getLength();		// Move the position index up by the length of the {@link MessageHeader}
		
		// Parse the set of {@link MessageQuestion}s
		questions = new ArrayList<MessageQuestion>();
		for(int i = 0; i < header.getNumQuestions(); i++) {
			MessageQuestion question = new MessageQuestion(data, pos);
			pos += question.getLength();
			questions.add(question);
		}
		
		// Parse the set of Answer {@link ResourceRecord}s
		answers = new ArrayList<ResourceRecord>();
		for(int i = 0; i < header.getNumAnswers(); i++) {
			ResourceRecord rr = new ResourceRecord(data, pos);
			pos += rr.getLength();
			answers.add(rr);
		}
		
		// Parse the set of Authority {@link ResourceRecord}s
		authorities = new ArrayList<ResourceRecord>();
		for(int i = 0; i < header.getNumAuthorities(); i++) {
			ResourceRecord rr = new ResourceRecord(data, pos);
			pos += rr.getLength();
			authorities.add(rr);
		}
		
		// Parse the set of Additional {@link ResourceRecord}s
		additional = new ArrayList<ResourceRecord>();
		for(int i = 0; i < header.getNumAdditional(); i++) {
			ResourceRecord rr = new ResourceRecord(data, pos);
			pos += rr.getLength();
			additional.add(rr);
		}
	}
	
	/**
	 * Adds a {@link MessageQuestion} to this {@link Message} to be queried.
	 * @param question The {@link MessageQuestion} to be added to this {@link Message}.
	 */
	public void addQuestion(MessageQuestion question) {
		questions.add(question);
		header.addQuestion();
	}
	
	/**
	 * A method for converting this {@link Message} into a properly formatted byte array for sending over the network.
	 * @return The properlty formatted byte array.
	 */
	public byte[] toByteArray() {
		// TODO: Now that I look at this, there is no need for an ArrayList<Byte>. 
		// Change over to just use a byte[] from the start using the MH, MQ, and RR lengths.
		
		
		// Convert the {@link MessageHeader} to a byte array
		ArrayList<Byte> array = new ArrayList<Byte>();
		array.addAll(header.toByteArray());
		
		// Convert the {@link MessageQuestion} to a byte array
		for(MessageQuestion q : questions) {
			array.addAll(q.toByteArray());
		}
		
		// Convert all relevant {@link ResourceRecord}s to a byte array
		for(ResourceRecord rr : answers) {
			array.addAll(rr.toByteArray());
		}
		for(ResourceRecord rr : authorities) {
			array.addAll(rr.toByteArray());
		}
		for(ResourceRecord rr : additional) {
			array.addAll(rr.toByteArray());
		}
		
		// Place all java {@link Byte}s into a primitive byte array.
		byte[] ret = new byte[array.size()];
		for(int i = 0; i < array.size(); i++)
			ret[i] = array.get(i);
		return ret;
	}

	/**
	 * Returns a string representing this {@link Message}. 
	 * The string is in an easy to read format for debugging purposes.
	 */
	public String toString() {
		// TODO: This could be in a prettier format...
		
		StringBuilder sb = new StringBuilder();
		sb.append(header.toString());
		sb.append("\n");
		for(MessageQuestion q : questions) {
			sb.append(q.toString());
			sb.append("\n");
		}
		for(ResourceRecord rr : answers) {
			sb.append(rr.toString());
			sb.append("\n\n");
		}
		for(ResourceRecord rr : authorities) {
			sb.append(rr.toString());
			sb.append("\n");
		}
		for(ResourceRecord rr : additional) {
			sb.append(rr.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * @return The {@link MessageHeader} defining this {@link Message}.
	 */
	public MessageHeader getHeader() {
		return header;
	}

	/**
	 * @return The list of {@link MessageQuestion}s being asked by this {@link Message}.
	 */
	public ArrayList<MessageQuestion> getQuestions() {
		return questions;
	}

	/**
	 * @return The list of {@link ResourceRecord}s answering the queries asked by this {@link Message}.
	 */
	public ArrayList<ResourceRecord> getAnswers() {
		return answers;
	}

	/**
	 * @return The list of {@link ResourceRecord}s representing authority servers related to queries asked by this {@link Message}.
	 */
	public ArrayList<ResourceRecord> getAuthorities() {
		return authorities;
	}

	/**
	 * @return The list of additional {@link ResourceRecord}s attached to the queries asked by this {@link Message}.
	 */
	public ArrayList<ResourceRecord> getAdditional() {
		return additional;
	}
}
