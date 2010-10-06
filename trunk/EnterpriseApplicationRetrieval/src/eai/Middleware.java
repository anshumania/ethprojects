package eai;

import eai.db.Address;
import eai.db.Country;
import eai.db.Customer;
import eai.db.DAOInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Church
 */
public class Middleware {
    //final String dbIds[] = {"Z", "B"};
    //final String databases[] = {"eai_zurich", "eai_bern"};
    private static final Map<String, String> databases =
        Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("Z", "eai_zurich");
            put("B", "eai_bern");
        }});

    HashMap<String, Connection> connections;
    Boolean connectionsClosed = false;  //checks whether close() method has been called yet.
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    PrintWriter socketOut = null;
    BufferedReader socketIn = null;


    public Middleware() {
        initMiddleware();
    }

    /**
     * Initializes the middleware: 
     *  - sets up all JDBC connections
     */
    private void initMiddleware() {
        connections = new HashMap();
        final String dbUsername = "postgres"; //TODO load from properties file
        final String dbPassword = "rainer03"; //TODO load from properties file

        //load the PostgreSQL Driver
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Unable to connect to PostgreSQL driver");
        } catch (InstantiationException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Unable to connect to PostgreSQL driver");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Unable to connect to PostgreSQL driver");
        }

        //create one JDBC connection to each database
        for(Entry<String,String> dbEntry : databases.entrySet()) {
            try {
                //create a connection
                Connection con = DriverManager.getConnection("jdbc:postgresql:" + dbEntry.getValue() +  "?user=" + dbUsername + "&password=" + dbPassword);
                con.setAutoCommit(true);
                connections.put(dbEntry.getKey(), con);
            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Unable to connect to database: " + dbEntry.getValue());
            }
        }
    }

    public void runServer() {
        //start server socket
        try {
            this.serverSocket = new ServerSocket(4444);
            this.clientSocket = this.serverSocket.accept();
            this.socketOut = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.socketIn = new BufferedReader( new InputStreamReader( this.clientSocket.getInputStream() ));

            String request;
            ArrayList<String> response = new ArrayList<String>();
            while ((request = this.socketIn.readLine()) != null) {
                //this.socketOut.println(request); //echo request for now
                response = executeRequest(request);
                this.socketOut.println(response.size());
                for(String responseLine : response) {
                    this.socketOut.println(responseLine);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Clean up method that should always be called after using a Middleware
    public void closeConnections() {
        //close all JDBC connections
        for(Connection con : this.connections.values()) {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //close sockets
        try {
            this.socketIn.close();
            this.socketOut.close();
            this.clientSocket.close();
            this.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.connectionsClosed = true;
    }

    /**
     * Tries to make sure that all db connections are closed during cleanup.
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if(!this.connectionsClosed) {
            //log warning and close all connections.
            Logger.getLogger(Middleware.class.getName()).log(Level.WARNING, "closeConnections was never called.  Closing connections now in cleanup.");
            this.closeConnections();
        }
        super.finalize();
    }

    private ArrayList<String> executeRequest(String request) {
        ArrayList<String> response = new ArrayList<String>();
        
        //parse request
        String params[] = request.split("[|]");

        //TODO - validate input request
        String location = params[0]; //'Z' or 'B' (or 'A')
        String operation = params[1];//'C', 'V', 'U', or 'D'
        String table = params[2];    //'C' or 'A'

        //trim off the paramaters we have already used
        String functionParams[] = new String[params.length - 3];
        System.arraycopy(params,3,functionParams,0,params.length - 3);

        try {
            if(operation.equals("C")) {
                //'C' == CREATE (i.e. INSERT)
                int numCreatedRows = doCreateQuery(location, table, functionParams);
                response.add(Integer.toString(numCreatedRows) + " row(s) created");
            } else if(operation.equals("V")) {
                //'V' == VIEW (i.e. SELECT)
                //return doViewQuery(location, table);
                return doViewQuery(table); //TEMP - only support Customers table for view.  params[2] == is actual location value
            } else if(operation.equals("U")) {
                //'U' == UPDATE
                int numUpdatedRows = doUpdateQuery(location, table, functionParams);
                response.add(Integer.toString(numUpdatedRows) + " row(s) updated");
            } else if(operation.equals("D")) {
                //'D' == DELETE
                int numDeletedRows = doDeleteQuery(location, table, Integer.valueOf(functionParams[0]).intValue());
                response.add(Integer.toString(numDeletedRows) + " row(s) deleted");
            }/* TODO - else throw error */
        } catch(SQLException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            response.add("AN ERROR HAS OCCURRED");
        }

        return response;
    }

    private int doCreateQuery(String location, String table, String[] paramList) throws SQLException {
        PreparedStatement statement = null;
        if(table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            statement = Customer.getInsertStatement(this.connections.get(location));
            statement.setString(1, paramList[0]);
            statement.setString(2, paramList[1]);
            statement.setString(3, paramList[2]);
            statement.setString(4, paramList[3]);
            statement.setString(5, paramList[4]);
        } else if(table.equalsIgnoreCase("A")) {
            //'A' == ADDRESS TABLE
            //First need to check Country already exists in Country table
            int countryId = findCountryId(location, paramList[4]);
            statement = Address.getInsertStatement(this.connections.get(location));
            statement.setInt(1, Integer.valueOf(paramList[0]).intValue());
            statement.setString(2, paramList[1]);
            statement.setString(3, paramList[2]);
            statement.setString(4, paramList[3]);
            statement.setInt(5, countryId);
        }

        return statement.executeUpdate();
    }

    
    private ArrayList<String> doViewQuery(String location) throws SQLException {
        ArrayList<String> response = new ArrayList<String>();
        PreparedStatement statements[] = null;
        if(location.equalsIgnoreCase("A")) {
            statements = new PreparedStatement[2];
        } else {
            statements = new PreparedStatement[1];
        }

        //if(table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            if(location.equalsIgnoreCase("A")) {
                //'A' == ALL (i.e. BOTH ZURICH AND BERN)
                statements[0] = Customer.getViewStatement(this.connections.get("Z"));
                statements[1] = Customer.getViewStatement(this.connections.get("B"));
            } else {
                statements[0] = Customer.getViewStatement(this.connections.get(location));
            }
        /*} else if(table.equalsIgnoreCase("A")) {
            //'A' == ADDRESS TABLE
            if(location.equalsIgnoreCase("A")) {
                //'A' == ALL (i.e. BOTH ZURICH AND BERN)
                statements[0] = Address.getViewStatement(this.connections.get('Z'));
                statements[1] = Address.getViewStatement(this.connections.get('B'));
            } else {
                statements[0] = Address.getViewStatement(this.connections.get(location));
            }
        }*/

        for(PreparedStatement statement : statements) {
            ResultSet rs =  statement.executeQuery();
            while (rs.next()) {
                //if(table.equalsIgnoreCase("C")) {
                    Customer customer = new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7));
                    response.add(customer.toString());
                /*} else if(table.equalsIgnoreCase("A")) {
                    //'A' == ADDRESS TABLE
                    Address address = new Address(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
                    response += address.toString();
                }*/
            }
            rs.close();
        }

        return response;
    }

    private int doUpdateQuery(String location, String table, String[] paramList) throws SQLException {
        PreparedStatement statement = null;
        if(table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            statement = Customer.getInsertStatement(this.connections.get(location));
            statement.setString(1, paramList[1]);
            statement.setString(2, paramList[2]);
            statement.setString(3, paramList[3]);
            statement.setString(4, paramList[4]);
            statement.setString(5, paramList[0]);
        } else if(table.equalsIgnoreCase("A")) {
            //'A' == ADDRESS TABLE
            //First need to check Country already exists in Country table
            int countryId = findCountryId(location, paramList[4]);

            statement = Address.getInsertStatement(this.connections.get(location));
            statement.setInt(1, Integer.valueOf(paramList[1]).intValue());
            statement.setString(2, paramList[2]);
            statement.setString(3, paramList[3]);
            statement.setInt(4, countryId);
            statement.setString(5, paramList[0]);
        }

        return statement.executeUpdate();
    }

    private int doDeleteQuery(String location, String table, int id) throws SQLException {
        PreparedStatement statement = null;
        if(table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
             statement = Customer.getDeleteStatement(this.connections.get(location));
             statement.setInt(1, id);
        } else if(table.equalsIgnoreCase("A")) {
            //'A' == ADDRESS TABLE
            statement = Address.getDeleteStatement(this.connections.get(location));
            statement.setInt(1, id);
        }
        return statement.executeUpdate();
    }

    public int findCountryId(String location, String countryName) throws SQLException {
        int countryId = 0;
        PreparedStatement statement = Country.getViewStatement(this.connections.get(location));
        statement.setString(1, countryName);
        ResultSet rs = statement.executeQuery();
        if(rs.next()) {
            countryId = rs.getInt("country_id");
        } else {
            //this is a new country, insert it
            statement = Country.getInsertStatement(this.connections.get(location));
            statement.setString(1,countryName);
            int numRowsInserted = statement.executeUpdate();
            if(numRowsInserted == 1) {
                statement = Country.getViewStatement(this.connections.get(location));
                statement.setString(1, countryName);
                rs = statement.executeQuery();
                rs.next();
                countryId = rs.getInt("country_id");
            }
        }
        return countryId;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Middleware mw = new Middleware();
        try {
            mw.runServer();
        } catch(Exception ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            mw.closeConnections();
        }
    }
}
