
import javax.xml.crypto.Data;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.Provider.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class client {
	
	private static int serverSocket = 3050;
	private static int otherServerSocket = 3051;
	private static Future<RSS> serving = null;
	private static boolean talkingToServer = false;
	private static String subject = null;
	private static String message = null;
	private static String name = null;
	private static int orderNumber = 0;
	private static int socketNum = 0;
	private static int socketN = 0;
	private static String Ip_Addr = null;
	private static InetAddress ip;
	private static ArrayList<String> subjects = new ArrayList<String>();
	private static DatagramSocket ds;
	private static ExecutorService es = Executors.newFixedThreadPool(10);

	static {
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			ds = new DatagramSocket(socketN);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public client() throws SocketException, UnknownHostException {
	}


	public static void main(String [] args) throws IOException, TimeoutException, InterruptedException {
		ArrayList<ServerHandler> server = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(10); // max number of clients

		firstRequest();

		ServerHandler serverHandler = new ServerHandler(client.serverSocket);

		// testing generating random ip address and socket number
		Random r = new Random();
		Ip_Addr = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
		socketN = r.nextInt(100) + 1; // sample socket#
		while (true) {
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

	private static void firstRequest() throws SocketException {
		System.out.println("<REGISTER> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<DE-REGISTER> <RQ#> <Name>");
		System.out.println("<UPDATE> <RQ#> <Name> <IP Address> <Socket#>");
		System.out.println("<SUBJECTS> <Name> <Number of Subjects>  <List of Subjects>");
		System.out.println("<PUBLISH> <RQ#> <Name> <Subject> <Text>");
		System.out.println("Enter a Request in one of the above formats");

		Random r = new Random();
		Ip_Addr = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
		socketN = r.nextInt(100) + 1; // sample socket#

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
			System.out.println("Enter a message related to "+ subject);
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
		c.setClientStatus("FIRST-REQUEST");
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
			DatagramPacket dpSend2 = new DatagramPacket(serializedMessage, serializedMessage.length, ip, client.otherServerSocket);

			ds.send(dpSend);
			ds.send(dpSend2);

			System.out.println("Sending to server A " + client.serverSocket);
			System.out.println("Sending to server B " + client.otherServerSocket);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println(ds + "socket num " + socketN);

		subjects = new ArrayList<String>();

		talkingToServer = true;
		serving = es.submit(new ServerHandler(ds));
		new Thread(() -> {
			//Do whatever
			try {
				RSS temp = serving.get();

				if(temp.getServerSocket() != 0) {
					client.serverSocket = temp.getServerSocket();
				}

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

} // end of class client
