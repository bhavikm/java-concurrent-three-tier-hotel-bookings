# java-concurrent-three-tier-hotel-bookings
A basic implementation of a three-tier Java system with JSON communication over TCP  

The Server class sits in front of a Database layer. 

The Database layer is abstracted and in this example uses JDBC to connect to a MySQL database.  

A Broker communicates between the Server and multiple clients concurrently via Threading.   

Example Clients are provided in this project with Java's Swing.  

Each layer communicates via JSON messages.  

Made with Netbeans 8.  

# To use:
  1. Import the project into Netbeans 8
  2. Setup MySQL database with JDBC driver
    1. Navigate to 'Services' menu on left side of Netbeans and find the 'Databases' item
    2. Exapand and find MySQL server icon, right click and 'Create Database...'
    3. Create a datbase called 'fit5170' and tick the box 'Grant Full Access To:' *@localhost
    4. Right click on the created database under the MySQL icon and click 'Connect...'
    5. Right click on the corresponding JDBC driver and select 'Execute Command...'
    6. Copy paste the contents of fit5170-mysql-setup.sql 
    7. Modify Database.java file in project with appropiate jdbcMysqlUrl, username and password
  3. Run 'server', 'broker' then 'client' from the green arrow in Netbeans (in that order) 
