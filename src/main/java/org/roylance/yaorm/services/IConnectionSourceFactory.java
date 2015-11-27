package org.roylance.yaorm.services;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionSourceFactory
        extends AutoCloseable {
    Connection getConnectionSource() throws SQLException;
    String getGeneratedKeysColumnName();
}
