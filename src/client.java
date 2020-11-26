
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class client {
	
	public static void main(String [] args) throws IOException {
		ArrayList<ServerHandler> server = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(10); // max number of clients

		Random r = new Random();
		int socketN = 0;

		String Ip_Addr = null;
		DatagramSocket ds = new DatagramSocket(socketN);

		System.out.println("Enter Client Name"); 
		 System.out.println("Enter Client Socket Number");
		String name = null;
		int socketNum = 0;
		int serverSocket = 3050;
		InetAddress ip = InetAddress.getLocalHost();
		
		int orderNumber = 0;
	
		System.out.println("<REGISTER> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<DE-REGISTER> <RQ#> <Name>");
		System.out.println("<UPDATE> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<SUBJECTS? <RQ#> <Name> <List of Subjects>"); // not done but super easy
		System.out.println("Enter a Request in one of the above formats");

		while (true) {
			// TESTING - Trying to get new server's socket # from ServerHandler.java
			ServerHandler serverHandler = new ServerHandler(serverSocket);
			serverSocket = serverHandler.getServingServerSocket();

			// TESTING: to see if client can get Server B's socket # from ServerHandler
			System.out.println("TEST - Server's socket #: " + serverSocket);

			//System.out.println("\nEnter a Request in one of the above formats");

			Scanner s = new Scanner(System.in);
			String req = s.nextLine();
			String [] input = req.split("\\s+");
			req = input[0]; // REQUEST

			if (req.equals("REGISTER") || req.equals("UPDATE")) {
			orderNumber++;
			name = input[1];

			// testing generating random ip address and socket number
			Ip_Addr = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
			socketN = r.nextInt(100) + 1; // sample socket#
			}
			else if (req.equals("DE-REGISTER")) {
				orderNumber++; // ORDER NUMBER (int)
				name = input[1];
			}

			if(socketNum != socketN) {
			 ds = new DatagramSocket(socketN);
			 socketNum = socketN;
			}

			RSS c = new RSS(name, socketN, orderNumber, ip, req);
			c.setClientSimulationIp(Ip_Addr);

			// Serialize to a byte array
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			try {
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(c);
			oo.close();

			byte[] serializedMessage = bStream.toByteArray();
			

			DatagramPacket dpSend = new DatagramPacket(serializedMessage, serializedMessage.length, ip, serverSocket);
				ds.send(dpSend);
			    } catch (IOException ex) {
			    	ex.printStackTrace();
			    }

			System.out.println(ds + "socket num " + socketN);

			// running ServerHandler threads to listen to servers
			ServerHandler serverThread = new ServerHandler(ds);
			server.add(serverThread);
			pool.execute(serverThread);
		} // end of while loop
	} // end of main


	private static void UpdateDatagram(int socketN) {
		// TODO Auto-generated method stub
		try {
			DatagramSocket ds = new DatagramSocket(socketN);
			System.out.println("Socket num reinitialized is now " + socketN);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end of UpdateDiagram

} // end of class client
