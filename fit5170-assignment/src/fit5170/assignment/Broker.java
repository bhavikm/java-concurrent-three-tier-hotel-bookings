package fit5170.assignment;

import java.io.*;
import java.net.*;

/**
 * The Broker Class acts as the middleware between the server/database layer and the client
 * 
 * Broker listens on port 8000 for TCP socket connections from clients and connects to
 * Server layer through TCP socket on port 9000
 *
 * Communication between the Broker and other components is entirely through JSON to ensure
 * that the layer is well encapsulated and can serve as general API for different client or
 * server layers
 * 
 * @author Bhavik Maneck
 */
public class Broker {
    //Any port not in the range of so called 'well known' ports may be used (0 to 1023 are well known)
    public static int port = 8000;
   
    public static void main(String[] args) {
    	ServerSocket s = null;
            
        //Try open a server socket for TCP communication with a client
    	try {
    	    s = new ServerSocket(port);
    	} catch(IOException e) {
    	    System.out.println(e);
    	    System.exit(1);
    	}
            
        //Keep listening for client socket connections
    	while (true) {
    	    Socket incoming = null;
                
    	    try {
    		      incoming = s.accept();
    	    } catch(IOException e) {
    		      System.out.println(e);
    		      continue;
    	    }
                
            //Concurrent handling of client connections
            //Begin new thread for each client connection
            new BrokerSocketHandler(incoming).start();
            
            //Non-concurrent handling of client connections use the statements 
            //below
            /*
        	    try {
        		    incoming.setSoTimeout(100000);
        	    } catch(SocketException e) {
                    System.out.println(e);
        	    }

        	    try {
                      // Accepted socket connection with client, now read and write with client
        		      handleSocket(incoming);
        	    } catch(InterruptedIOException e) {
        		      System.out.println("Time expired " + e);
        	    } catch(IOException e) {
        		      System.out.println(e);
        	    }

        	    try {
        		      incoming.close();
        	    } catch(IOException e) {
        		      System.out.println(e);
        	    }
            */
    	}  
    }
    
    //This method is exactly the same as the run() method in BrokerSocketHandler class
    //Except it can only be run non-concurrently by Broker
    public static void handleSocket(Socket incoming) throws IOException {
                
        try {
                //Use DataInputStream and DataOutputStream in combination with 
                //BufferReader and PrintStream for working with JSON over TCP socket connection
        		DataInputStream is = new DataInputStream(incoming.getInputStream());
        		DataOutputStream os = new DataOutputStream(incoming.getOutputStream());
        		BufferedReader clientReader = new BufferedReader(new InputStreamReader(is));
                PrintStream clientWriter = new PrintStream(os);
              
                //Read JSON message from client
                JSONObject json = new JSONObject(clientReader.readLine());
                
                System.out.println(json.toString()); //for debug
                
                //Does the JSON message from the client need to talk with server
                boolean requestNeedsServer = true;
                
                //Now that we have a JSON message from client use header 
                //information in all message to figure out what to do
                //Header messages include a request_type and a corresponding 
                //request_name
                //request_type includes GET (for reads), POST (for write), VALIDATE (for validation of data)
                if(json.has("request_type")) {
                    String requestType = json.getString("request_type");

                    if (requestType.equals("VALIDATE"))
                    {
                        String requestName = json.getString("request_name");
                        
                        //if validating don't need server connection, broker handles this
                        requestNeedsServer = false; 
                        
                        if (requestName.equals("dates")) //validate dates
                        {
                            String error = Validation.validateDateInputs(json.getString("check_in"),json.getString("check_out"));
                            
                            //return response in JSON
                            JSONObject jsonResponse = new JSONObject();
                            jsonResponse.put("request_type", requestType);
                            jsonResponse.put("request_name", requestName);
                            jsonResponse.put("error", error);
                            clientWriter.println(jsonResponse);
                            
                        } else if (requestName.equals("client")) { //validate new client information for booking
                            boolean error = false;
                            
                            //Check all the client inputs
                            String creditCardError = Validation.validateCreditCard(json.getString("creditCard"));
                            String phoneError = Validation.validatePhoneNumber(json.getString("phone"));
                            String emailError = Validation.validateEmail(json.getString("email"));
                            String firstNameError = Validation.validateName(json.getString("first_name"));
                            String lastNameError = Validation.validateName(json.getString("last_name"));
                            
                            //Was there any errors
                            if (!creditCardError.equalsIgnoreCase("None") || 
                                !phoneError.equalsIgnoreCase("None") ||
                                !emailError.equalsIgnoreCase("None") ||
                                !firstNameError.equalsIgnoreCase("None") ||
                                !lastNameError.equalsIgnoreCase("None")) 
                            {
                                error = true;
                            }
                            
                            //return response in JSON to client
                            JSONObject jsonResponse = new JSONObject();
                            jsonResponse.put("request_type", requestType);
                            jsonResponse.put("request_name", requestName);
                            jsonResponse.put("error", error);
                            if (error)
                            {
                                jsonResponse.put("error_credit_card", creditCardError);
                                jsonResponse.put("error_phone", phoneError);
                                jsonResponse.put("error_email", emailError);
                                jsonResponse.put("error_first_name", firstNameError);
                                jsonResponse.put("error_last_name", lastNameError);
                            }
                            clientWriter.println(jsonResponse);
                        }
                        
                    } else { //GET or POST so we need to communicate with Server/Database
                        requestNeedsServer = true;
                    }
                }
                
                //FOR CONNECTION FROM BROKER TO SERVER
                if (requestNeedsServer) {
                    
                    try {
                        //Connect over socket for TCP communication with server
                        //Need localhost IP address and using pre-defined port 9000 (not in well known ports)
                        InetAddress address = InetAddress.getByName("localhost");
                        Socket server = new Socket(address, 9000);
                        
                        //Again setup connection over TCP for reading and writing JSON with machine-independent streams
                        DataInputStream server2broker = new DataInputStream(server.getInputStream());
                        DataOutputStream broker2server = new DataOutputStream(server.getOutputStream());
                        BufferedReader serverReader = new BufferedReader(new InputStreamReader(server2broker));
                        PrintStream serverWriter = new PrintStream(broker2server);
                        
                        //Pass the JSON message to server (GET/POST)
                        serverWriter.println(json);
                        serverWriter.flush();
                        
                        //Read Server response
                        JSONObject serverJsonResponse = new JSONObject(serverReader.readLine());
                        
                        //Pass Server response back to client
                        clientWriter.println(serverJsonResponse);

                        System.out.println(serverJsonResponse.toString()); //for debug

                        server.close();
                        server2broker.close();
                        broker2server.close();

                    } catch(Exception e) {
                        System.out.println(e);
                    }

                    is.close();
                    os.close();
                
                }
 
	} catch (IOException e) {
        System.out.println(e);		
	}
        
	   incoming.close();
    }
    
}
