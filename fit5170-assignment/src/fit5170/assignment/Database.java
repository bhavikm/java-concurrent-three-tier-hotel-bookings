package fit5170.assignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides JDBC connection to MySQL database
 * Abstracts away MySQL specific queries and provides
 * API in terms of information actions such as getHotels(),
 * getAvailability(date1,date2), addBooking() etc.
 * 
 * Prepared statements are used wherever possible to add a layer
 * of security and prevent against SQL injection attacks
 * 
 * Provides JSON responses where appropriate to maintain encapsulation
 * 
 * @author Bhavik Maneck
 */
public class Database {
    
    private Connection dbConnection;
    private final String jdbcMysqlUrl = "jdbc:mysql://localhost:8889/fit5170?zeroDateTimeBehavior=convertToNull";
    private final String username = "root";
    private final String password = "root";
    
    public Database()
    {
        dbConnection = null;
        
        //Connect to database with jdbc driver
        try {
            dbConnection = DriverManager.getConnection(jdbcMysqlUrl, username, password);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    //Add a new client entity for a new booking, carried out before adding the booking information
    //Returns 0 if inset was unsuccesful or clientID of succesfully inserted client entity
    public int addBookingClient(String firstName, String lastName, String email, int phone, String creditCardNo)
    {
        PreparedStatement pst = null;
        int insertID = 0;
        
        try {
            pst = dbConnection.prepareStatement("INSERT INTO Client(firstName,lastName,email,phone,creditCardNo) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, email);
            pst.setInt(4, phone);
            pst.setString(5, creditCardNo);
            int numero = pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            
            if (rs.next()){
                insertID = rs.getInt(1);
            }
            
            return insertID;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            return insertID;
            
        }
    }
    
    //Returns the primary-key roomTypeID for a given roomTypeName
    private int getRoomTypeID(String roomName)
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        int roomTypeID = 0;
        
        try {
   
            pst = dbConnection.prepareStatement("SELECT * FROM Rooms "
                                                + "WHERE roomTypeName = ? ");
            pst.setString(1, roomName);
            ResultSet rs = pst.executeQuery();
           
            
            while(rs.next()) {
                roomTypeID = rs.getInt("roomTypeID");
            }
            
            return roomTypeID;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return roomTypeID;
        
        }
    }
    
    //Returns the unique primary-key hotelID for a particular city and hotel chain
    private int getHotelID(String cityName, String hotelName)
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        int hotelID = 0;
        
        try {
   
            pst = dbConnection.prepareStatement("SELECT * FROM Hotel, HotelChain, City "
                                                + "WHERE hotelChainName = ? "
                                                + "AND cityName = ? "
                                                + "AND Hotel.hotelChainID = HotelChain.hotelChainID "
                                                + "AND Hotel.cityID = City.cityID");
            pst.setString(1, hotelName);
            pst.setString(2, cityName);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                hotelID = rs.getInt("hotelID");
            }
            
            return hotelID;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return hotelID;
        
        }
    }
    
    
    //Add a new hotel booking, availability is assumed to have already been confirmed before calling this 
    //Returns 0 if the booking failed to insert, otherwise returns the bookingID of the new booking
    public int addNewBooking(int clientID, String city, String roomName, int roomsBooked, String hotelName, String checkInString, String checkOutString)
    {
        int roomTypeID = getRoomTypeID(roomName);
        
        int hotelID = getHotelID(city,hotelName);
        
        //The dates need to be in java.sql.Timestamp format to be inserted
        //as a MySQL timestamp. 
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date checkInDate = new Date();
        Date checkOutDate = new Date();
        try {
            checkInDate = formatter.parse(checkInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            checkOutDate = formatter.parse(checkOutString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Timestamp sqlCheckInDate = new java.sql.Timestamp(checkInDate.getTime());
        java.sql.Timestamp sqlCheckOutDate = new java.sql.Timestamp(checkOutDate.getTime());
        
        PreparedStatement pst = null;
        
        int bookingID = 0;
        
        try {
   
            pst = dbConnection.prepareStatement("INSERT INTO Booking(roomTypeID,clientID,checkInDate,checkOutDate,roomsBooked,hotelID) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, roomTypeID);
            pst.setInt(2, clientID);
            pst.setTimestamp(3, sqlCheckInDate);
            pst.setTimestamp(4, sqlCheckOutDate);
            pst.setInt(5, roomsBooked);
            pst.setInt(6, hotelID);
            pst.executeUpdate();
            
            //Get the last insert ID
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()){
                bookingID = rs.getInt(1);
            }
            
            return bookingID;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            return bookingID;
            
        } finally {
        
            try {
                if (dbConnection != null) {
                    dbConnection.close();
                }
                return bookingID;
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
                return bookingID;
            }
        }
    }
    
    //Returns city names as a JSONObject that have at least one hotel operating
    //List of cities in return object will need to be parsed as a JSONArray
    public JSONObject getCityNames()
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        
        try {
   
            pst = dbConnection.prepareStatement("SELECT * FROM City WHERE cityID IN (SELECT cityID FROM Hotel)");
            ResultSet rs = pst.executeQuery();
            
            List  l1 = new LinkedList();
            
            String result = "";
            while(rs.next()) {
                Map m = new HashMap();
                
                m.put("city_id",rs.getString("cityID"));
                m.put("city_name",rs.getString("cityName"));
                
                l1.add(m);
            }
            
            String requestType = "GET response";
            String requestName = "city_names";

            obj.put("request_type", requestType);
            obj.put("request_name", requestName);
            obj.put("results",l1);
            
            
            return obj;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return obj;
        
        } finally {
        
            try {
                if (dbConnection != null) {
                    dbConnection.close();
                }
                return obj;
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
                return obj;
            }
            
            
        }
    }
    
    //Returns hotel chains in a city as a JSONObject
    //List of hotels in return object will need to be parsed as a JSONArray
    public JSONObject getHotels(String city_name)
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        
        try {
   
            pst = dbConnection.prepareStatement("SELECT * FROM Hotel, HotelChain, City "
                                                + "WHERE cityName = ? "
                                                + "AND Hotel.cityID = City.cityID "
                                                + "AND HotelChain.hotelChainID = Hotel.hotelChainID");
            pst.setString(1, city_name);
            ResultSet rs = pst.executeQuery();
            
            List  l1 = new LinkedList();
            
            while(rs.next()) {
                Map m = new HashMap();
                
                m.put("hotel_id",rs.getString("hotelID"));
                m.put("hotel_name",rs.getString("hotelChainName"));
                
                l1.add(m);
            }
            
            String requestType = "GET response";
            String requestName = "hotels";

            obj.put("request_type", requestType);
            obj.put("request_name", requestName);
            obj.put("results",l1);
            
            
            return obj;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return obj;
        
        } finally {
        
            try {
                if (dbConnection != null) {
                    dbConnection.close();
                }
                return obj;
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
                return obj;
            }
        }
    }
    
    //Returns room types for given hotel in a city as a JSONObject
    //List of rooms in return object will need to be parsed as a JSONArray
    public JSONObject getRooms(String city_name, String hotel)
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        
        try {
   
            pst = dbConnection.prepareStatement("SELECT * FROM Rooms, HotelRooms, Hotel, HotelChain, City "
                                                + "WHERE cityName = ? "
                                                + "AND hotelChainName = ? "
                                                + "AND Hotel.cityID = City.cityID "
                                                + "AND HotelChain.hotelChainID = Hotel.hotelChainID "
                                                + "AND HotelRooms.hotelID = Hotel.hotelID "
                                                + "AND HotelRooms.roomTypeID = Rooms.roomTypeID");
            pst.setString(1, city_name);
            pst.setString(2, hotel);
            ResultSet rs = pst.executeQuery();
            
            List l1 = new LinkedList();
            
            while(rs.next()) {
                Map m = new HashMap();
                
                m.put("room_type",rs.getString("roomTypeName"));
                m.put("room_rate",rs.getString("roomRate"));
                m.put("capacity",rs.getString("numberOfRooms"));
                
                l1.add(m);
            }
            
            String requestType = "GET response";
            String requestName = "rooms";

            obj.put("request_type", requestType);
            obj.put("request_name", requestName);
            obj.put("results",l1);
            
            
            return obj;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return obj;
        
        } finally {
        
            try {
                if (dbConnection != null) {
                    dbConnection.close();
                }
                return obj;
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
                return obj;
            }
        }
    }
    
    //Check availability for check in and check out date for a particular hotel, city and room type
    //Assumes dates are validated and don't conflict (checkout is before checkin etc.)
    //Returns JSONObject of number of rooms already booked out of all available for given information
    public JSONObject getAvailability(String city_name, String hotel, String room, String checkIn, String checkOut)
    {
        PreparedStatement pst = null;
        JSONObject obj = new JSONObject();
        
        //Need to setup dates as java.sql.Timestamp to be compatable with 
        //Mysql timestamp types
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date checkInDate = new Date();
        Date checkOutDate = new Date();
        try {
            checkInDate = formatter.parse(checkIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            checkOutDate = formatter.parse(checkOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Timestamp sqlCheckInDate = new java.sql.Timestamp(checkInDate.getTime());
        java.sql.Timestamp sqlCheckOutDate = new java.sql.Timestamp(checkOutDate.getTime());
        
        try {
            pst = dbConnection.prepareStatement("SELECT sum(roomsBooked) roomsAlreadyBooked FROM Rooms, HotelRooms, Hotel, HotelChain, City, Booking "
                                                + "WHERE Hotel.cityID = City.cityID "
                                                + "AND HotelChain.hotelChainID = Hotel.hotelChainID "
                                                + "AND HotelRooms.hotelID = Hotel.hotelID "
                                                + "AND HotelRooms.roomTypeID = Rooms.roomTypeID "
                                                + "AND Booking.roomTypeID = HotelRooms.roomTypeID "
                                                + "AND cityName = ? "
                                                + "AND hotelChainName = ? "
                                                + "AND roomTypeName = ? "
                                                + "AND DATE(checkInDate) <= DATE(?) "
                                                + "AND DATE(?) < DATE(checkOutDate)");
            
            pst.setString(1, city_name);
            pst.setString(2, hotel);
            pst.setString(3, room);
            pst.setTimestamp(4, sqlCheckOutDate);
            pst.setTimestamp(5, sqlCheckInDate);
            ResultSet rs = pst.executeQuery();
            
            String requestType = "GET response";
            String requestName = "availability";

            obj.put("request_type", requestType);
            obj.put("request_name", requestName);
            
            List  l1 = new LinkedList();
            
            while(rs.next()) {
                Map m = new HashMap();
                
                if (rs.getString("roomsAlreadyBooked") == null)
                {
                    m.put("rooms_booked","0");
                } else {
                    m.put("rooms_booked",rs.getString("roomsAlreadyBooked"));
                }
                
                System.out.println(rs.getString("roomsAlreadyBooked"));
                l1.add(m);
            }
            
            obj.put("results",l1);
            
            return obj;
            
        } catch (SQLException ex) {
            
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            
            return obj;
        
        } finally {
        
            try {
                if (dbConnection != null) {
                    dbConnection.close();
                }
                return obj;
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
                return obj;
            }
        }
    }
}
