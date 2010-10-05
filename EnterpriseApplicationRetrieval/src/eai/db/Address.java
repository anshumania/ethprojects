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
    private int countryId;

    public Address(int addressId, int customerId, String street, String city, String zipCode, int countryId) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.countryId = countryId;
    }

    public String getTableName() {
        return "address";
    }

    public PreparedStatement getInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement("INSERT INTO " + this.getTableName() + " VALUES (?,?,?,?,?)");
    }

    @Override
    public String toString() {
        return this.addressId + " | " + this.customerId + " | " + this.street + " | " + this.city + " | " + this.zipCode + " | " + this.countryId;
    }
}
