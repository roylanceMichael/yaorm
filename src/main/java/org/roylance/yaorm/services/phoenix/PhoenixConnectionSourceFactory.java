package org.roylance.yaorm.services.phoenix;

import org.roylance.yaorm.services.IConnectionSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by mikeroylance on 10/26/15.
 */
public class PhoenixConnectionSourceFactory
    implements IConnectionSourceFactory {

    private static final String JDBCDriverName = "org.apache.phoenix.jdbc.PhoenixDriver";
    private static final String JDBCUrl = "jdbc:phoenix:%1$s:/hbase-unsecure";

    private boolean isClosed;
    private final Connection commonConnection;

    public PhoenixConnectionSourceFactory(final String host) throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDriverName);
        final String jdbcUrl = String.format(JDBCUrl, host);
        this.commonConnection = DriverManager.getConnection(jdbcUrl);
    }

    @Override
    public Connection getConnectionSource() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("already closed...");
        }
        return this.commonConnection;
    }

    @Override
    public void close() throws Exception {
        if (!this.isClosed) {
            this.commonConnection.close();
        }
        this.isClosed = true;
    }
}