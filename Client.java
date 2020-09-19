/**
 * Iterative Socket Server
 * @version 1.1
 * @since   2019-10-11
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * A multi-threaded client capable of spawning numerous client sessions that connect to the server.
 * The server and client programs connect to the same network address and port
 * The client tansmits requests to the server on a specified network port	
 * @author Jose Daniel Oropeza 
 */
public class Client { 
	static Socket[] socketArray = new Socket[50]; // Support opening up to 50 connections
	static String ip, cmd; // value set by DisplayMenu() ; IP/hostname should be "localhost" when on local machine
	static int port, numClients; // value set by DisplayMenu()
	static long sum = 0; // Sum of all turn around times for all threads
	static double avg = 0.0; // Average ToT for all connections
	
	static int i = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Client client =new Client(); // instantiate the a Client class object
        client.displayMenu(); // Collects the desired IP Address, port, and command to be sent from the the client user
		client.startConnections(ip, port); //Starts a number of client connections   
		// Wait a specified amount of time so that all the threads can finish
		try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // At this point we have 
		avg = client.calculateAvgToT();
		System.out.print("\nTotal ToT for " + numClients + " connections: " +sum + " m/s \n");
		client.printAvgToT();		
	}
	
	/**
	   * Creates a new socket for each of x amount of clients and services that client's requests on a different thread
	   * by calling start() on the ClientGenerator class
	   * @param ip  IPAddress
	   * @param port  The port number           
	   */
	public void startConnections(String ip, int port) {
        for(int i=0;i<numClients;i++) {     	
            try {
            	Socket socket = new Socket(ip, port);       	
     			socketArray[i]  = socket;
     			int session = i + 1;  			
				new ClientGenerator(socketArray[i], ip, port, session, cmd).start();
			} 
            catch (UnknownHostException e) {
				e.printStackTrace();
			} 
            catch (IOException e) {
				e.printStackTrace();
			}
        }
        return;
    }

	/**
	 * Blueprint for the work each connection thread does
	 */
	private static class ClientGenerator extends Thread {
	        private Socket clientSocket;
		    private String ip;
		    private int port;
		    private int session;
	        private String cmd;
	        private PrintWriter out;
	        private BufferedReader in;
	 
	        public ClientGenerator(Socket clientSocket, String ip, int port, int session, String cmd) {  	
		        this.clientSocket = clientSocket;
		        this.ip = ip;
		        this.port = port;
		        this.session = session;
		        this.cmd = cmd;	        
	        }
	      
	    	// The time required for the client request to travel to the server, be processed by the server, and return to the client
	        public void run() {

		        try {
		        	System.out.println("New client thread is spawned and about to send a request to the Server: Session = " + session + "");
		        	
		            out = new PrintWriter(clientSocket.getOutputStream(), true); // the output stream of the client is connected to the input stream of the server 
		            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // The input stream of the client is connected to the output stream of the server 
		            
		            long start;  // Time client request is sent to the server 
		            long end; // Time request returns from the client. This is after the client request travels to the server and is processed by the server. 
		            long total; // Response time for 1 connection
		            
		            System.out.println(in.readLine()); // Prints the connection confirmation message sent by the server; shows connection acknowledgement
		        	
					out.println(cmd); // write the desired command string to the server socket
					start = System.currentTimeMillis();

					
					System.out.println("Client # " + session + ": \n" + in.readLine() + "\n"); // read server response
					end = System.currentTimeMillis();
					total = (end - start); // Turn-around Time (elapsed time) for the client request
					sum = sum + total; // add to sum of ToTs
					
					System.out.println( "----------------------------------------------------- \n" +
										"Response time for client " + session + ": " + total + " m/s \n" +
										"Time @start for Client # " + session + " was: " + start + "\n" +
										"Time @end for Client # " + session + " was: " + end + "\n" +
										"----------------------------------------------------- \n");					
		            System.out.println("Terminating Connection for client " + session +  " ");
		            in.close();
		            out.close();
		            clientSocket.close();	            
		        } 
		        
		        catch (IOException ex) {
		            System.out.println("Server exception: " + ex.getMessage());
		            ex.printStackTrace();
		        } //end catch  
	        }   
	  }
	
	/**
	 * Calculates Average Turn-around Time (Total Turn-around Time divided by the number client requests)
	 * @return Total turn around time
	 */
	public double calculateAvgToT() {
		return (sum / numClients);
	}
	
	/**
	 * Prints Average Turn-around Time 
	 */
	public void printAvgToT() {
		System.out.println("Average ToT for " + numClients + " connections: " + avg + " m/s \n");
		return;
	}
	
	

    /**
	 * Requests the network address and port to which to connect, the operation to request, 
	 * and how many client requests to generate (1, 5, 10, 15, 20  and 25).        
	 */
    public void displayMenu() {
    	Scanner userInput = new Scanner(System.in); 		// Allow user input
		
		// Request the network address and port to which to connect
        System.out.println("Enter a network address/hostname:");
		ip = userInput.next(); 				
		System.out.println("Enter a port number:");
		port = Integer.parseInt(userInput.next());
        
		// Select Request type - Menu
		System.out.println("Select type of client request by entering a number between 1 and 6:");
		System.out.println("Options:");
		System.out.println("1. Date and Time");
		System.out.println("2. Uptime");
		System.out.println("3. Memory Use");
		System.out.println("4. Netstat");
		System.out.println("5. Current Users");
		System.out.println("6. Running Processes");
		int requestType = Integer.parseInt(userInput.next());
		switch (requestType) {
	    case 1:
	      System.out.println("Option 1 (Date and Time) was selected");
	      cmd = "date";
	      break;
	    case 2:
	    	System.out.println("Option 2 (Uptime) was selected");
	    	cmd = "uptime";
	      break;
	    case 3:
	    	System.out.println("Option 3 (Memory Use) was selected");
	    	cmd = "free";
	      break;
	    case 4:
	    	System.out.println("Option 4 (Netstat) was selected");  
		      cmd = "netstat";
		      break;
	    case 5:
	    	System.out.println("Option 5 (Current Users) was selected");
	          cmd ="who";
		      break;
	    case 6:
	    	System.out.println("Option 6 (Running Processes) was selected");
	    	cmd = "ps";
		      break;
	    default:
	      System.out.println("Invalid selection");
	      break;
	    }
		
		// Select Number of Requests - Menu
		System.out.println("Enter desired number of client requests to generate (1, 5, 10, 15, 20 or 25)");
		numClients = Integer.parseInt(userInput.next());
		// Switch construct
	    switch (numClients) {
	    case 1:
	      System.out.println("Spawning 1 client request");
	      break;
	    case 5:
	    	System.out.println("Spawning 5 client requests");
	      break;
	    case 10:
	    	System.out.println("Spawning 10 client requests");
	      break;
	    case 15:
	    	System.out.println("Spawning 15 client requests");
		      break;
	    case 20:
	    	System.out.println("Spawning 20 client requests");
		      break;
	    case 25:
	    	System.out.println("Spawning 25 client requests");
		      break;
	    default:
	      System.out.println("Invalid selection");
	      break; // This break is not really necessary
	    }
	    userInput.close(); // conserve resources
	    return; //done
    } 
} 
