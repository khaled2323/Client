
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
import java.util.Scanner;
import java.io.ObjectOutputStream;

public class Main {
	
	public static void main(String [] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
		int socketN=1;
		String Ip_Addr = null;
		DatagramSocket ds = new DatagramSocket(socketN);
		/*
		 * System.out.println("Enter Client Name"); String UserName = sc.nextLine();
		 * System.out.println("Enter Client Socket Number"); socketN = sc.nextInt();
		 */
		System.out.println("Enter Client Name"); 
		 System.out.println("Enter Client Socket Number");
		String UserName = null;
		int socketNum = 0;
		//DatagramSocket ds = new DatagramSocket(3);
		InetAddress ip = InetAddress.getLocalHost();
		
		int orderNumber = 0;
	
		 // DatagramSocket ds = new DatagramSocket(socketN);
		System.out.println("(REGISTER) (RQ#) (NAME) (IP address) (SOCKET#)");
		System.out.println("(DE-REGISTER) (RQ#) (NAME)");
		System.out.println("(UPDATE) (RQ#) (NAME) (IP address) (SOCKET#)");
		System.out.println("(subjects) (RQ#) (NAME) (list of subjects)"); //not done but super easy
		while(true) {
			
			System.out.println("Enter a Request in one of the above formats");  // will add a list later when we r finalizing
			
			Scanner s = new Scanner(System.in);
			String req = s.nextLine();
			String [] inpu = req.split("\\s+");
			req = inpu[0]; // REQUEST
			if(req.equals("REGISTER")||req.equals("UPDATE")) {
			orderNumber++;
			UserName = inpu[1];
			Ip_Addr = "192.168.3.3"; // Sample IP
			socketN = 5; // sample socket#
			
			}else if(req.equals("DE-REGISTER")) {
				orderNumber++; // ORDER NUMBER (int)
				UserName = inpu[1];
			}
			if(socketNum != socketN) {
			 ds = new DatagramSocket(socketN);
			 socketNum = socketN;
			}
			RSS c = new RSS(UserName,socketN,orderNumber,ip, req);
			c.setClientSimulationIp(Ip_Addr);
			
			// Serialize to a byte array
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			try    
			{      
			
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(c);
			oo.close();

			byte[] serializedMessage = bStream.toByteArray();
			
			
			DatagramPacket dpSend = new DatagramPacket(serializedMessage, serializedMessage.length,ip,3050);
			//DatagramPacket dpSend2 = new DatagramPacket(serializedMessage, serializedMessage.length,ip,3051);
				ds.send(dpSend);
				//ds.send(dpSend2);
				
			
			    }
			    catch (IOException ex)
			    {
			    	ex.printStackTrace();
			    }
			
			byte[] receive = new byte[65535];
			DatagramPacket DpReceive = null;
			DpReceive = new DatagramPacket(receive, receive.length);
			System.out.println(ds+ "socket num "+ socketN);
			ds.receive(DpReceive);
			RSS clientR = null;
			ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(receive));
			try {
				clientR = (RSS) iStream.readObject();
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iStream.close();
			
			//System.out.println("Status : " + clientR.getClientStatus());
			if(clientR.getClientStatus()== null) {
				// nothing
				}
			else if(clientR.getClientStatus().equals("REGISTERED")) {
			System.out.println("Status : " + clientR.getClientStatus());
			System.out.println("RQ# : " + clientR.getOrderNumber());
			}
			else if(clientR.getClientStatus().equals("REGISTER-DENIED")) {
				System.out.println("Status : " + clientR.getClientStatus());
				System.out.println("RQ# : " + clientR.getOrderNumber());
				System.out.println("Reason : " + clientR.getReason());
				clientR.setClientStatus("REGISTERED");
				}
			else if(clientR.getClientStatus().equals("DE-REGISTER")) {
				System.out.println("Status : " + clientR.getClientStatus());
				System.out.println("Name: " + clientR.gettClienName());
				}
			else if(clientR.getClientStatus().equals("UPDATE-CONFIRMED")) {
				String out = clientR.getClientStatus()+ " " +clientR.getOrderNumber()+ " "+clientR.gettClienName()+ " " + clientR.getClientSimulationIp()+ " "+ clientR.gettClientSocket();
				System.out.println(out);
			}
			
			if(req.equals("bye")) {
				break;
			}
		}
	}

	private static void UpdateDatagram(int socketN) {
		// TODO Auto-generated method stub
		try {
			DatagramSocket ds = new DatagramSocket(socketN);
			System.out.println("Socket num reinicialized now is " + socketN);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
