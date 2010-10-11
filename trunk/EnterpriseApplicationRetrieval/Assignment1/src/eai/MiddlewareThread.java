package eai;

import eai.db.Address;
import eai.db.Country;
import eai.db.Customer;
import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiddlewareThread extends Thread {

    private Socket socket = null;
    private HashMap<String, Connection> connections;

    public MiddlewareThread(Socket socket, HashMap<String, Connection> connections) {
        super("MiddlewareThread");
        this.socket = socket;
        this.connections = connections;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            System.out.println("Started Middleware Thread");

            String request;
            ArrayList<String> response = new ArrayList<String>();
            while ((request = in.readLine()) != null) {
                response = executeRequest(request);
                out.println(response.size());
                for (String responseLine : response) {
                    out.println(responseLine);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        System.arraycopy(params, 3, functionParams, 0, params.length - 3);

        try {
            if (operation.equals("C")) {
                //'C' == CREATE (i.e. INSERT)
                int numCreatedRows = doCreateQuery(location, table, functionParams);
                response.add(Integer.toString(numCreatedRows) + " row(s) created");
            } else if (operation.equals("V")) {
                //'V' == VIEW (i.e. SELECT)
                //return doViewQuery(location, table);
                return doViewQuery(table); //TEMP - only support Customers table for view.  params[2] == is actual location value
            } else if (operation.equals("U")) {
                //'U' == UPDATE
                int numUpdatedRows = doUpdateQuery(location, table, functionParams);
                response.add(Integer.toString(numUpdatedRows) + " row(s) updated");
            } else if (operation.equals("D")) {
                //'D' == DELETE
                int numDeletedRows = doDeleteQuery(location, table, Integer.valueOf(functionParams[0]).intValue());
                response.add(Integer.toString(numDeletedRows) + " row(s) deleted");
            }/* TODO - else throw error */
        } catch (SQLException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            String errors[] = ex.getMessage().split("[\n]");
            response.addAll(Arrays.asList(errors));
        }

        return response;
    }

    private int doCreateQuery(String location, String table, String[] paramList) throws SQLException {
        PreparedStatement statement = null;
        if (table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            statement = Customer.getInsertStatement(this.connections.get(location));
            statement.setString(1, paramList[0]);
            statement.setString(2, paramList[1]);
            statement.setString(3, paramList[2]);
            statement.setString(4, paramList[3]);
            statement.setString(5, paramList[4]);
        } else if (table.equalsIgnoreCase("A")) {
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
        if (location.equalsIgnoreCase("A")) {
            statements = new PreparedStatement[2];
        } else {
            statements = new PreparedStatement[1];
        }

        //if(table.equalsIgnoreCase("C")) {
        //'C' == CUSTOMER TABLE
        if (location.equalsIgnoreCase("A")) {
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

        final String databases[] = {"ZURICH", "BERN"};
        int count = 0;
        for (PreparedStatement statement : statements) {
            response.add("FROM " + databases[count] + ":");
            ResultSet rs = statement.executeQuery();
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
            count++;
        }

        return response;
    }

    private int doUpdateQuery(String location, String table, String[] paramList) throws SQLException {
        PreparedStatement statement = null;
        if (table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            statement = Customer.getUpdateStatement(this.connections.get(location));
            statement.setString(1, paramList[1]);
            statement.setString(2, paramList[2]);
            statement.setString(3, paramList[3]);
            statement.setString(4, paramList[4]);
            statement.setString(5, paramList[5]);
            statement.setInt(6, Integer.valueOf(paramList[0]).intValue());
        } else if (table.equalsIgnoreCase("A")) {
            //'A' == ADDRESS TABLE
            //First need to check Country already exists in Country table
            int countryId = findCountryId(location, paramList[4]);

            statement = Address.getUpdateStatement(this.connections.get(location));
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
        if (table.equalsIgnoreCase("C")) {
            //'C' == CUSTOMER TABLE
            statement = Customer.getDeleteStatement(this.connections.get(location));
            statement.setInt(1, id);
        } else if (table.equalsIgnoreCase("A")) {
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
        if (rs.next()) {
            countryId = rs.getInt("country_id");
        } else {
            //this is a new country, insert it
            statement = Country.getInsertStatement(this.connections.get(location));
            statement.setString(1, countryName);
            int numRowsInserted = statement.executeUpdate();
            if (numRowsInserted == 1) {
                statement = Country.getViewStatement(this.connections.get(location));
                statement.setString(1, countryName);
                rs = statement.executeQuery();
                rs.next();
                countryId = rs.getInt("country_id");
            }
        }
        return countryId;
    }
}
