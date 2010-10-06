package eai.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Tim Church
 */
public class Country {// implements DAOInterface {
    private int countryId;
    private String countryName;

    public Country(Integer id, String name) {
        this.countryId = id;
        this.countryName = name;
    }

    public static String getTableName() {
        return "country";
    }

    public static PreparedStatement getInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement("INSERT INTO " + getTableName() + "(country_name) VALUES (?)");
    }

    public static PreparedStatement getViewStatement(Connection con) throws SQLException {
        return con.prepareStatement("SELECT * FROM " + getTableName() + " WHERE country_name = ?");
    }

    @Override
    public String toString() {
        return this.countryId + " | " + this.countryName;
    }
}
