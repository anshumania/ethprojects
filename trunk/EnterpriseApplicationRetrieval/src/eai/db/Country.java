package eai.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Tim Church
 */
public class Country {
    private int countryId;
    private String countryName;

    public Country(Integer id, String name) {
        this.countryId = id;
        this.countryName = name;
    }

    public String getTableName() {
        return "country";
    }

    public PreparedStatement getInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement("INSERT INTO " + this.getTableName() + " VALUES (?)");
    }

    @Override
    public String toString() {
        return this.countryId + " | " + this.countryName;
    }
}
