import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import javax.xml.ws.handler.Handler;

public class Server {

	private static ServerSocket SSocket;
	Handler updateConversationHandler;
	Thread serverThread = null;
	public static final int SERVERPORT = 4444;
	
	public void main(String[] args)
	{
		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
	}
	
	 class ServerThread implements Runnable {
		 
		 public void run() 
		 {
			 Socket socket = null;
			 try
			 {
				 	System.out.println("Attemptiong to start server...");
					SSocket = new ServerSocket();
					
					//Get local IP and Name
					System.out.println("Getting host IP and Name...");
					String serverName = InetAddress.getLocalHost().getHostName();
					
					String serverIP = InetAddress.getLocalHost().getHostAddress();
					
					//Display hostname and IP to user.
					System.out.println("HostName: " + serverName + "\nHostIP: " + serverIP);
					
					//Create a new serverSocket and bind it to specified host name and port.
					System.out.println("Creating socket and binding socket");
					SocketAddress endpoint = new InetSocketAddress(serverName, SERVERPORT);			
					SSocket.bind(endpoint);
					System.out.println("Socket successfully bound.");
					
					while (!Thread.currentThread().isInterrupted()) {
						                try {
											System.out.println("Waiting for a connection....");
											
						                	socket = SSocket.accept();
						                    CommunicationThread commThread = new CommunicationThread(socket);
						                    new Thread(commThread).start();
						                } catch (IOException e) {
						                    e.printStackTrace();
						                }
						            }
					System.out.println("Connection made.");
					
					SSocket.close();
				}
				catch(IOException e)
				{
					System.out.println("Could not connect on port 4444.");
					e.printStackTrace();
					System.exit(1);
				}
		 }
	 }
//-------------------------------------------------------------------------------//
	    class CommunicationThread implements Runnable {
	        private Socket clientSocket;
	        private BufferedReader input;
	        public CommunicationThread(Socket clientSocket) {
	            this.clientSocket = clientSocket;
	            try {
	                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        public void run() {
	            while (!Thread.currentThread().isInterrupted()) {
	                try {
	                    String read = input.readLine();
	                    updateConversationHandler.post(new updateUIThread(read));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	    
//-------------------------------------------------------------------------//	    
	    class updateUIThread implements Runnable {
	        private String msg;
	        public updateUIThread(String str) {
	            this.msg = str;
	        }
	        @Override
	        public void run() {
	            System.out.println("Client Says: "+ msg + "\n");
	        }
	    }
}
