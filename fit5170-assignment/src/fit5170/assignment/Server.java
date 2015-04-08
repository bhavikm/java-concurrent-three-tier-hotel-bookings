package fit5170.assignment;

import java.io.*;
import java.net.*;

/**
 * The Server class sits in front of a Database and communicates with a Broker or
 * other middleware components to serve and save data
 * 
 * Server listens on port 9000 for TCP socket connections from clients
 * 
 * All the Server's API are through JSON request and responses to allow different 
 * clients to easily connect to the Server for data operations
 * 
 * @author Bhavik Maneck
 */
public class Server {
    //Any port not in the range of so called 'well known' ports may be used (0 to 1023 are well known)
    public static int port = 9000;
     
    public static void main(String[] args) {
	ServerSocket s = null;
        
	try {
	    s = new ServerSocket(port);
	} catch(IOException e) {
	    System.out.println(e);
	    System.exit(1);
	}
        
	while (true) {
	    Socket incoming = null;
            
	    try {
		  incoming = s.accept();
	    } catch(IOException e) {
		  System.out.println(e);
		  continue;
	    }

	    try {
		  incoming.setSoTimeout(100000); // 10 seconds
	    } catch(SocketException e) {
		  System.out.println(e);
	    }

	    try {
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
	}  
    }
    
    public static void handleSocket(Socket incoming) throws IOException {
	    DataInputStream is;
        DataOutputStream os;
        
        try {
            is = new DataInputStream(incoming.getInputStream());
            os = new DataOutputStream(incoming.getOutputStream());
            PrintStream pw = new PrintStream(os);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            
            JSONObject json = new JSONObject(in.readLine());
            System.out.println(json.toString());

            Database db = new Database();
            JSONObject jsonResponse = new JSONObject();
            String requestType = "response";
            String requestName = "received request";
            jsonResponse.put("request_type", requestType);
            jsonResponse.put("request_name", requestName);
            
            if(json.has("request_type")) {
                String request = json.getString("request_name");
                
                if (request.equals("city_names"))
                {
                    jsonResponse = db.getCityNames();
                } else if(request.equals("hotels")) {
                    jsonResponse = db.getHotels(json.getString("city_name"));
                } else if(request.equals("rooms")) {
                    jsonResponse = db.getRooms(json.getString("city_name"),json.getString("hotel"));
                } else if(request.equals("availability")) {
                    jsonResponse = db.getAvailability(json.getString("city_name"),json.getString("hotel"),json.getString("room"),json.getString("check_in"),json.getString("check_out"));
                } else if(request.equals("booking")) {
                    int clientID = db.addBookingClient(json.getString("first_name"),json.getString("last_name"),json.getString("email"),Integer.parseInt(json.getString("phone")),json.getString("creditCard"));
                    int bookingID = db.addNewBooking(clientID,json.getString("city"),json.getString("room"),Integer.parseInt(json.getString("rooms")),json.getString("hotel"),json.getString("check_in"),json.getString("check_out"));
                    if (bookingID > 0)
                    {
                        jsonResponse.put("results","success");
                    } else {
                        jsonResponse.put("results","fail");
                    }   
                } else {
                    System.out.println("Request name not found on server!\n");
                }
                
            }
            
            System.out.println(jsonResponse.toString()); //for debug
            
            pw.println(jsonResponse);
            
            is.close();
            os.close();
 
    	} catch (IOException e) {
                System.out.println(e);			
    	} catch (JSONException e) {
                System.out.println(e);
    	}
        
	   incoming.close();
    }
}
