package org.roylance.yaorm.services.sqlite;

import org.jetbrains.annotations.NotNull;
import org.roylance.yaorm.services.IConnectionSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnectionSourceFactory
        implements IConnectionSourceFactory {

    private static final String SqliteJdbcDbTemplate = "jdbc:sqlite:%1$s";
    private final Connection commonConnection;
    private boolean isClosed;

    public SqliteConnectionSourceFactory(
            @NotNull String dbPath) throws SQLException {
        this.commonConnection = DriverManager.getConnection(
                String.format(SqliteJdbcDbTemplate, dbPath));
    }

    @Override
    public Connection getConnectionSource()
            throws SQLException {
        if (this.isClosed) {
            throw new SQLException("already closed...");
        }
        return this.commonConnection;
    }

    @Override
    public void close() throws Exception {
        this.commonConnection.close();
        this.isClosed = true;
    }
}