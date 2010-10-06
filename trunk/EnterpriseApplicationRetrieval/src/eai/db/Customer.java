package eai.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author Tim Church
 */
public class Customer {
    private int customerId;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private Date dateAdded;

    public Customer(int customerId, String username, String password, String firstname, String lastname, String email) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public Customer(int customerId, String username, String password, String firstname, String lastname, String email, Date dateAdded) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.dateAdded = dateAdded;
    }
    
    public static String getTableName() {
        return "customer";
    }

    public static PreparedStatement getInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement("INSERT INTO " + getTableName() + "(username, password, firstname, lastname, email, date_added) VALUES (?,?,?,?,?,NOW())");
    }

    public static PreparedStatement getViewStatement(Connection con) throws SQLException {
        return con.prepareStatement("SELECT * FROM " + getTableName());
    }

    public static PreparedStatement getUpdateStatement(Connection con) throws SQLException {
        return con.prepareStatement("UPDATE " + getTableName() + " SET username = ?, password = ?, firstname = ?, lastname = ?, email = ? WHERE customer_id = ?");
    }

    public static PreparedStatement getDeleteStatement(Connection con) throws SQLException {
        return con.prepareStatement("DELETE FROM " + getTableName() + " WHERE customer_id = ?");
    }

    @Override
    public String toString() {
        return this.customerId + " | " + this.username + " | " + this.password + " | " + this.firstname + " | " + this.lastname + " | " + this.email + " | " + this.dateAdded;
    }
}
