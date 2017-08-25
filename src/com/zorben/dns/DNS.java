package com.zorben.dns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A convenience class for communicating with a known domain name server.
 * 
 * @author Mac Crompton
 *
 */
public class DNS {
	
	/**
	 * The set of domain name servers to attempt to communicate with.
	 */
	public ArrayList<String> nameServers;
	
	/**
	 * Be sure to communicate on port 53!
	 */
	public final int DNS_PORT = 53;
	
	/**
	 * Create this object using a set of statically set name servers.
	 * @param ns The array of known name servers.
	 */
	public DNS(String... ns) {
		nameServers = new ArrayList<String>(Arrays.asList(ns));
	}
	
	/**
	 * Create this object using a config file containing the set of known name servers.
	 * 
	 * @param nameServerINI The path to the config file. File must contain name servers on separate lines.
	 * @throws FileNotFoundException If we can't find the file, we can't construct this object with it...
	 */
	public DNS(String nameServerINI) throws FileNotFoundException {
		nameServers = new ArrayList<String>();
		Scanner input = new Scanner(new File(nameServerINI));
		while(input.hasNextLine()) {
			String line = input.nextLine();
			if(line != null && !line.equals("")) {
				nameServers.add(line);
			}
		}
		input.close();
	}
	
	/**
	 * Given properly a properly encoded DNS request, attempts to send the request over UDP to each of the known domain name servers until a response is received.
	 * @param request The properly encoded DNS request.
	 * @return Returns the response as a raw byte array to the provided DNS request, or null if the servers do not respond.
	 */
	public byte[] makeRequest(byte[] request) {
		for(String dns : nameServers) {
			try {
				DatagramSocket socket = new DatagramSocket();
				socket.setSoTimeout(200);
				// Note: I am fully aware that I am potentially performing name resolution to then manually perform name resolution...
				//      see - {@code InetAddress.getByName(dns)}
				DatagramPacket packet = new DatagramPacket(request, request.length, InetAddress.getByName(dns), DNS_PORT);
				socket.send(packet);
				byte[] buffer = new byte[(Short.MAX_VALUE+1)*2-1];
				DatagramPacket rec = new DatagramPacket(buffer, buffer.length);
				socket.receive(rec);
				socket.close();
				
				return rec.getData();
			} catch(SocketTimeoutException e) {
				System.err.println("DNS Server: " + dns + " timed out.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
