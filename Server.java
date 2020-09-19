/**
 * Iterative Socket Server
 * @version 1.0
 * @since   2019-10-11
 */
import java.io.*;
import java.net.*;
/**
 * An iterative (single-threaded) server that accepts requests from clients
 * The server and client programs connect to the same network address and port
 * The (single-threaded) server handles one client request at a time (serially)
 * @author Jose Daniel Oropeza 
 */
public class Server {
	private ServerSocket serverSocket; //
	private static Socket clientSocket; 
	private PrintWriter out;
	private BufferedReader in;
	static private int port;
	static private String ip;
	/**
	 * Main method
	 * @param args
	 * @throws UnknownHostException 
	 * */
	public static void main(String[] args) throws UnknownHostException {
		ip = "";
		int initialPort = 3500;
        Server server=new Server(); 
        server.start(initialPort); // Start listening for client requests
        try {
			clientSocket.close();
			server.stopConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // terminate connection with client
    }


	/**
	 * Blueprint for the work each server client handler thread does. The client handler keeps listening for requests inside a while loop.
	 */
	private static class ClientHandler extends Thread {
	        private Socket clientSocket;
	        private PrintWriter out;
	        private BufferedReader in;
	 
	        public ClientHandler(Socket clientSocket) {  	
		        this.clientSocket = clientSocket;
		               
	        }
	      
	    	// The time required for the client request to travel to the server, be processed by the server, and return to the client
	        public void run() {
	        	try {          
	    			out = new PrintWriter(clientSocket.getOutputStream(), true); // for sending data to the client
	    	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading data from the client
	    	        
	    	        out.println("Server Says: New client Thread Connected! Requests for this thread serviced through clientSocket address: " + clientSocket.getRemoteSocketAddress());
					out.flush();
					
	    	        String cmd = in.readLine(); // read desired command to be ran on the server side
	    	        String output ="";
	    	        // Perform the requested operation and collect the resulting output
	    	        if ("date".equals(cmd)) {
	    	        	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");     
	                }
	                else if ("uptime".equals(cmd)) {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	                else if ("free".equals(cmd)) {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	                else if ("netstat".equals(cmd)) {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	                else if ("who".equals(cmd)) {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	                else if ("ps".equals(cmd)) {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	                else {
	                	output = RunCommand(cmd);
	                    out.println("The " + cmd + " command was exceuted with a output of : " + output + "\n ");
	                }
	    	        
	    	        in.close();
	    	        out.close();
	    	        clientSocket.close(); // terminate connection with client
				} catch (IOException e) {
					e.printStackTrace();
				} 
	        }   
	  }
	
	/*
	 * Starts the Server at the specified port, to service requests concurrently this method listens for connections and services them using the clientHandler.
	 * @param int port The port number the server services requests at.
	 */
    public void start(int port) {   
		try { //Try to create a server Socket
			serverSocket = new ServerSocket(port); //start listening
			System.out.println("Server is listening for connection requests from clients at port 3500");
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
        while (true) {   	       
        	try {
				new ClientHandler(serverSocket.accept()).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	     	  
		}
    }
    
    /*
     * Runs the specified Linux command and returns the output
     * @param String cmd The command requested by the client
     */
    public static String RunCommand(String cmd) {
		   String s = null;		
		   StringBuilder sb = new StringBuilder();
   
	    try {
	        
	        Process p = Runtime.getRuntime().exec(cmd);       
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));     
	        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	        String line = " ";
	        // read the output from the command      
	        while ((s = stdInput.readLine()) != null) {
	            sb.append(s);	            
	            System.out.println(sb.toString());            
	        }	        
	    }
	    catch (IOException e) {
	        System.out.println("Command Error!");   
	        return null;
	    }	    
	    catch (IllegalArgumentException e)
	    {
	    	System.out.println("Exception! Empty Character Detected!");
	    	return "Error! Empty Character Detected!";
	    }    
	  return sb.toString();
	}
 
    public void stopConnection() {
        try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // stop the server
    }
}
