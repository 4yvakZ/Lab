package PostgresSQL;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class PostgresConnector {

    private final String databaseURL;
    private final String user;
    private final String password;

    public PostgresConnector() {
        databaseURL = "jdbc:postgresql://localhost:5432/Lab";
        user = "postgres";
        password = "postgres";
    }

    public PostgresConnector(String databaseURL, String user, String password){
        this.databaseURL = databaseURL;
        this.user = user;
        this.password = password;
    }

    public Connection getSQLConnection() {

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return null;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(databaseURL, user, password);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return null;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        return connection;
    }
}