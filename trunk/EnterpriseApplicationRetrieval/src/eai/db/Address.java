package eai.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Tim Church
 */
public class Address {
    private int addressId;
    private int customerId;
    private String street;
    private String city;
    private String zipCode;
    //private int countryId;
    private String countryName;

    public Address(int addressId, int customerId, String street, String city, String zipCode, String countryName) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        //this.countryId = countryId;
        this.countryName = countryName;
    }

    public static String getTableName() {
        return "address";
    }

    public static PreparedStatement getInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement("INSERT INTO " + getTableName() + "(customer_id, street, city, zip_code, country_id) VALUES (?,?,?,?,?)");
    }

    public static PreparedStatement getViewStatement(Connection con) throws SQLException {
        return con.prepareStatement("SELECT a.address_id, a.customer_id, a.street, a.city, a.zip_code, c.country_name FROM " + getTableName() + " as a, " + Country.getTableName() + " as c WHERE a.country_id = c.country_id");
    }

    public static PreparedStatement getUpdateStatement(Connection con) throws SQLException {
        return con.prepareStatement("UPDATE " + getTableName() + " SET customer_id = ?, street = ?, city = ?, zip_code = ?, country_id = ? WHERE address_id = ?");
    }

    public static PreparedStatement getDeleteStatement(Connection con) throws SQLException {
        return con.prepareStatement("DELETE FROM " + getTableName() + " WHERE address_id = ?");
    }

    @Override
    public String toString() {
        return this.addressId + " | " + this.customerId + " | " + this.street + " | " + this.city + " | " + this.zipCode + " | " + this.countryName;
    }
}
