package eai;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Church
 */
public class Middleware {

    private static final Map<String, String> databases =
            Collections.unmodifiableMap(new HashMap<String, String>() {

        {
            put("Z", "eai_zurich");
            put("B", "eai_bern");
        }
    });
    HashMap<String, Connection> connections;
    ServerSocket serverSocket = null;

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
            System.exit(-1);
        } catch (InstantiationException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        //create one JDBC connection to each database
        for (Entry<String, String> dbEntry : databases.entrySet()) {
            try {
                //create a connection
                Connection con = DriverManager.getConnection("jdbc:postgresql:" + dbEntry.getValue() + "?user=" + dbUsername + "&password=" + dbPassword);
                con.setAutoCommit(true);
                connections.put(dbEntry.getKey(), con);
            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Unable to connect to database: " + dbEntry.getValue());
            }
        }
    }

    public void runServer() throws IOException {
        //start server socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }
        System.out.println("Middleware server started");

        boolean listening = true;
        while (listening) {
            new MiddlewareThread(serverSocket.accept(), this.connections).start();
        }

        serverSocket.close();
        this.closeJDBCConnections();
    }

    //Clean up method - closes all JDBC connections
    public void closeJDBCConnections() {
        for (Connection con : this.connections.values()) {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Middleware mw = new Middleware();
        mw.runServer();
    }
}
