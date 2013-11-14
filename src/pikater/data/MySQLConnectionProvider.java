package pikater.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: Kuba
 * Date: 9.11.13
 * Time: 19:54
 */
public class MySQLConnectionProvider implements ConnectionProvider {
    private final String url;
    private final String userName;
    private final String password;

    public MySQLConnectionProvider(String url, String userName, String password)
    {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, userName, password);
    }

    @Override
    public String getConnectionInfo() {
        return url;
    }
}
