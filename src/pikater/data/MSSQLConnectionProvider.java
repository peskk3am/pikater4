package pikater.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: Kuba
 * Date: 9.11.13
 * Time: 19:55
 */
public class MSSQLConnectionProvider implements ConnectionProvider {
    private final String connectionUrl;

    public MSSQLConnectionProvider(String connectionUrl)
    {
        this.connectionUrl = connectionUrl;
    }

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(connectionUrl);
    }

    @Override
    public String getConnectionInfo() {
        return connectionUrl;
    }
}
