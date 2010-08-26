package com.ecovate.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author jhumphrey
 */
public class DatabaseServiceImpl implements DatabaseService {

  Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public DatabaseContext buildContext(URI jdbcUri, String dbName, String username, String password) {

    DatabaseContext context = new DatabaseContext();
    context.setUsername(username);
    context.setPassword(password);
    context.setJdbcUri(jdbcUri);
    context.setDbName(dbName);

    return context;
  }

  @Override
  public void connect(DatabaseContext context) {

    URI jdbcUri = context.getJdbcUri();

    try {
      DatabaseType databaseType = DatabaseType.getTypeFromJDBCUri(jdbcUri);

      Class.forName(databaseType.getDriver());

      logger.info("Connecting to database: " + jdbcUri.toString());

      String username = context.getUsername();
      String password = context.getPassword();

      Connection connection = DriverManager.getConnection(jdbcUri.toString(), username, password);

      context.setConnection(connection);

    } catch (ClassNotFoundException e) {
      throw new DatabaseException("SQL driver not found", e);
    } catch (SQLException e) {
      throw new DatabaseException("SQL exception occurred when establishing connection to database: " + jdbcUri, e);
    }
  }
}
