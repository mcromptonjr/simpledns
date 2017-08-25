SimpleDNS
=========
SimpleDNS is a pure Java library implementing the non-Experimental querying features of RFC 1034 and RFC 1035.  This library was created for the purpose of learning how DNS queries and responses are encoded and interpreted. While you are free to use this software in commercial applications, I would highly advise against doing so as this code has not gone through thorough testing, and is intended for educational use only.

### Example
An example DNS query can be found in the DNSExample.java source file.  For your convenience, the meat of the example can be found below:
```java
// These are the Level3 Primary and Secondary Public Domain Name Servers
DNS dns = new DNS("209.244.0.3", "209.244.0.4");

// Construct the message to be sent. Simple query header.
MessageHeader mh = new MessageHeader(1, false, 0, false, false, false, false, 0, 0, 0, 0, 0);
Message message = new Message(mh);

// Query the public dns for information about the 'google.com' authoritative name server.
MessageQuestion mq = new MessageQuestion("google.com", (short)2, (short)1);
message.addQuestion(mq);

// Convert the request to a byte array, send it, and capture the response.
byte[] request = message.toByteArray();
byte[] response = dns.makeRequest(request);

// Pretty print the response.
Message responseMessage = new Message(response, 0);
System.out.println(responseMessage);
```


