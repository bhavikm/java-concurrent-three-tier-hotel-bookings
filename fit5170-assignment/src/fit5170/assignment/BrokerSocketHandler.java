package fit5170.assignment;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Threaded version of socket handler for Broker
 * 
 * Communicates with client connection and communicates to Server,
 * all through JSON messages
 * 
 * @author Bhavik Maneck
 */
public class BrokerSocketHandler extends Thread {
    Socket incoming;
    
    public BrokerSocketHandler(Socket incomingConnection) {
        incoming = incomingConnection;
    }
    
    public void run() {
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
	        
        try {
            incoming.close();
        } catch (IOException ex) {
            Logger.getLogger(BrokerSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
