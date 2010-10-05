package eai;

import eai.db.Customer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Church
 */
public class Middleware {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //final String dbIds[] = {"Z", "B"};
        final String databases[] = {"eai_zurich", "eai_bern"};
        HashMap<String, Connection> connections = new HashMap();

        try {
            //load the PostgreSQL Driver
            Class.forName("org.postgresql.Driver").newInstance();
            final String dbUsername = "postgres"; //TODO load from properties file
            final String dbPassword = "rainer03"; //TODO load from properties file

            //create one JDBC connection to each database
            Connection con;
            for(String db : databases) {
                //create a connection
                con = DriverManager.getConnection("jdbc:postgresql:" + db +  "?user=" + dbUsername + "&password=" + dbPassword);
                con.setAutoCommit(true);
                connections.put(db, con);
            }

            //TEST - print all customers from each db
            for(String db : databases) {
                String query = "SELECT * FROM customer";
                Statement stmt = connections.get(db).createStatement();
                System.out.println(db + ": " + query);

                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Customer customer = new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7));
                    System.out.println(customer);
                }
                System.out.println("");
                rs.close();
            }

        } catch (InstantiationException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
