
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
import java.security.Provider.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class client {
	
	private static int serverSocket;
	private static Future<RSS> serving = null;

	public static void main(String [] args) throws IOException, TimeoutException {
		ArrayList<ServerHandler> server = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(10); // max number of clients

		Random r = new Random();
		int socketN = 0;

		String Ip_Addr = null;
		DatagramSocket ds = new DatagramSocket(socketN);
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		boolean talkingToServer = false;
		String subject = null;
		String message = null;
		System.out.println("Enter Client Name"); 
		 System.out.println("Enter Client Socket Number");
		String name = null;
		int socketNum = 0;
		client.serverSocket = 3050;
		InetAddress ip = InetAddress.getLocalHost();
		ServerHandler serverHandler = new ServerHandler(client.serverSocket);
		int orderNumber = 0;
	    ArrayList<String> subjects = new ArrayList<String>();
		System.out.println("<REGISTER> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<DE-REGISTER> <RQ#> <Name>");
		System.out.println("<UPDATE> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<SUBJECTS> <Name> <number Of subjects>  <List of Subjects>"); // not done but super easy
		System.out.println("Enter a Request in one of the above formats");
		// testing generating random ip address and socket number
		Ip_Addr = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
		socketN = r.nextInt(100) + 1; // sample socket#
		while (true) {
			// TESTING - Trying to get new server's socket # from ServerHandler.java
			
			//serverSocket = serverHandler.getServingServerSocket();

			// TESTING: to see if client can get Server B's socket # from ServerHandler
			System.out.println("TEST - Server's socket #: " + client.serverSocket);

			//System.out.println("\nEnter a Request in one of the above formats");

			Scanner s = new Scanner(System.in);
			String req = s.nextLine();
			String [] input = req.split("\\s+");
			req = input[0]; // REQUEST
			
			
			if (req.equals("REGISTER") || req.equals("UPDATE")) {
			orderNumber++;
			name = input[1];

		
			}
			else if (req.equals("DE-REGISTER")) {
				orderNumber++; // ORDER NUMBER (int)
				name = input[1];
			}
			else if (req.equals("SUBJECTS")) {
				orderNumber++; // ORDER NUMBER (int)
				name = input[1];
				int subjNum = Integer.parseInt(input[2]);
				for (int i=0;i<subjNum;i++) {
					subjects.add(input[i+3]);
				}
			}else if (req.equals("PUBLISH")) {
				orderNumber++; // ORDER NUMBER (int)
				name = input[1];
				subject = input[2];
				System.out.println("Enter a message related to this "+ subject);
				message = s.nextLine();
			}
			
			
			if(socketNum != socketN) {
			 ds = new DatagramSocket(socketN);
			 socketNum = socketN;
			}

			RSS c = new RSS(name, socketN, orderNumber, ip, req);
			c.setClientSimulationIp(Ip_Addr);
			c.setServerSocket(client.serverSocket);
			c.setFrom("CLIENT");
			// check if subjects are asked for
			if(subjects != null) {
			c.setSubjects(subjects);
			}
			if(subject != null) {
				c.setSubject(subject);
				c.setMessage(message);
				}
			// Serialize to a byte array
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			try {
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(c);
			oo.close();

			byte[] serializedMessage = bStream.toByteArray();
			

			DatagramPacket dpSend = new DatagramPacket(serializedMessage, serializedMessage.length, ip, client.serverSocket);
			
				ds.send(dpSend);
			
			    } catch (IOException ex) {
			    	ex.printStackTrace();
			    }

			System.out.println(ds + "socket num " + socketN);

			 subjects = new ArrayList<String>();

			// running ServerHandler threads to listen to servers
			/*
			 * ServerHandler serverThread = new ServerHandler(ds); server.add(serverThread);
			 * pool.execute(serverThread);
			 */
			
		    // converting runnable task to callable
		    //Callable<Integer> callable = new ServerHandler(ds);
		    // submit method
		    //int timeout = 2;
			talkingToServer = true;
			serving = es.submit(new ServerHandler(ds));
			new Thread(() -> {
			    //Do whatever
			    try {
			    	//System.out.println("here- socket is " + c.getServerSocket());
			    	
			      RSS temp = serving.get(); 
			      
			    	System.out.println("NEW AND DONE Result- " + temp.getServerSocket());
			    	if(temp.getServerSocket() != 0) {
			    		client.serverSocket = temp.getServerSocket();
			    		
			    		
			    	}
			    	
			    } catch (InterruptedException | ExecutionException e) {
			      // TODO Auto-generated catch block
			      e.printStackTrace();
			    }
			}).start();
	         
	        

		    //es.shutdown();
		    talkingToServer = false;
			
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
