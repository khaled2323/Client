import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;

public class ServerHandler implements Callable<RSS> {
    RSS clientR;
    byte[] receive = new byte[65535];
    DatagramPacket DpReceive = null;

    private DatagramSocket ds;
    private int servingServerSocket;

    public ServerHandler(int serverSocket) {
        this.servingServerSocket = serverSocket;
    }

    public ServerHandler(DatagramSocket ds) {
        this.ds = ds;
    }

    public void setServingServerSocket(int servingServerSocket) {
        this.servingServerSocket = servingServerSocket;
    }

    public int getServingServerSocket() {
        return this.servingServerSocket;
    }

	/*
	 * @Override public void run() {
	 * 
	 * } // end of run
	 */
	@Override
	public RSS call() throws Exception {
		// TODO Auto-generated method stub
		try {
            while (true) {
                DpReceive = new DatagramPacket(receive, receive.length);

                ds.receive(DpReceive);

                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(receive));

                try {
                    clientR = (RSS) iStream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("Listening to Server: " + clientR.getServerSocket());

                //System.out.println("Status : " + clientR.getClientStatus());
                if (clientR == null) {
                    // nothing
                }

                else if (clientR.getClientStatus().equals("CHANGE-SERVER")) {
                    System.out.println("This server is no longer serving, you must talk to the other server\n with socket number is: " + clientR.getServerSocket());
                    // TESTING - must send this new server's socket # to client.java
                    setServingServerSocket(clientR.getServerSocket());
                    clientR.setServerSocket(clientR.getServerSocket());
                    servingServerSocket = clientR.getServerSocket();
                    
                    return clientR;
                }

                else if (clientR.getClientStatus().equals("REGISTERED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("\nEnter a Request in one of the above formats");
                    servingServerSocket = clientR.getServerSocket();
                 
                }

                else if (clientR.getClientStatus().equals("REGISTER-DENIED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Reason: " + clientR.getReason());
                    System.out.println("\nEnter a Request in one of the above formats");
             
                }

                else if (clientR.getClientStatus().equals("DE-REGISTERED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("Name: " + clientR.gettClienName());
                    System.out.println("\nEnter a Request in one of the above formats");
                }

                else if (clientR.getClientStatus().equals("DE-REGISTER-DENIED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Reason: " + clientR.getReason());
                    System.out.println("\nEnter a Request in one of the above formats");
                }

                else if (clientR.getClientStatus().equals("UPDATE-CONFIRMED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Name: " + clientR.gettClienName());
                    System.out.println("IP: " + clientR.getClientSimulationIp());
                    System.out.println("Socket: " + clientR.gettClientSocket());
                    System.out.println("\nEnter a Request in one of the above formats");
                 
                }
                else if (clientR.getClientStatus().equals("UPDATE-DENIED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Reason: " + clientR.getReason());
                    System.out.println("\nEnter a Request in one of the above formats");
                 
                }
                else if (clientR.getClientStatus().equals("SUBJECTS-UPDATED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Name: " + clientR.gettClienName());
                    System.out.println("Subject(s): " + clientR.getSubjects());
                    System.out.println("\nEnter a Request in one of the above formats");
                 
                }
                else if (clientR.getClientStatus().equals("SUBJECTS-REJECTED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Reason: " + clientR.getReason());
                    System.out.println("\nEnter a Request in one of the above formats");

                }
                else if (clientR.getClientStatus().equals("MESSAGE")) {                          
                	System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Name: " + clientR.gettClienName());
                    System.out.println("IP: " + clientR.getClientSimulationIp());
                    System.out.println("Socket: " + clientR.gettClientSocket());
                    System.out.println("MESSAGE RECEIVED, THE RELATED SUBJECT IS: "+ clientR.getsubject()+ ", THE MESSAGE is "+ clientR.getMessage());
                    System.out.println("\nEnter a Request in one of the above formats");
                 
                }
                else if (clientR.getClientStatus().equals("PUBLISH-DENIED")) {
                    System.out.println("Status: " + clientR.getClientStatus());
                    System.out.println("RQ#: " + clientR.getOrderNumber());
                    System.out.println("Reason: " + clientR.getReason());
                    System.out.println("\nEnter a Request in one of the above formats");
                }
              
                try {
                    iStream.close();
                   
                } catch (IOException e) {
                    e.printStackTrace();
                }
              
            } // end of while
        	
        } catch	(IOException e) {
            System.err.println("IO exception in Client Handler");
            System.err.println(e.getStackTrace());
        }
		 System.err.println("NOT ABLE TO REACH HERE");
		  return clientR;
	}

} // end of class ServerHandler