import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;

public class clientReciever implements Callable<RSS> {
    RSS clientR;
    byte[] receive = new byte[65535];
    DatagramPacket DpReceive = null;

    private DatagramSocket ds;
    private int servingServerSocket;

    public clientReciever(int serverSocket) {
        this.servingServerSocket = serverSocket;
    }

    public clientReciever(DatagramSocket ds) {
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

                //System.out.println("Status : " + clientR.getClientStatus());
                if (clientR == null) {
                    // nothing
                	
                }

                else if (clientR.getClientStatus().equals("CHANGE-SERVER")) {
                    System.out.println("This server is no longer serving, you must talk to the other server. Its socket number is: " + clientR.getServerSocket());
                    // TESTING - must send this new server's socket # to client.java
                    setServingServerSocket(clientR.getServerSocket());
                    clientR.setServerSocket(clientR.getServerSocket());
                    return clientR;
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
		return clientR;
	}

} // end of class ServerHandler