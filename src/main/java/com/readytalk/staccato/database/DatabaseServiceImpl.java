package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.logging.Logger;

import com.readytalk.staccato.database.migration.script.Script;

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

    DatabaseType databaseType = DatabaseType.getTypeFromJDBCUri(jdbcUri);
    context.setDatabaseType(databaseType);

    return context;
  }

  @Override
  public void connect(DatabaseContext context) {

    URI jdbcUri = context.getJdbcUri();

    try {

      Class.forName(context.getDatabaseType().getDriver());

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

  @Override
  public void disconnect(DatabaseContext context) {

    logger.info("Disconnecting database connection: " + context.getJdbcUri().toString());

    Connection connection = context.getConnection();
    try {
      if (connection != null && connection.isValid(30)) {
        connection.close();
      }
    } catch (SQLException e1) {
      try {
        if (!connection.isClosed()) {
          connection.close();
        }
      } catch (SQLException e2) {
        logger.severe("Unable to close database connection to: " + context.getJdbcUri());
      }
    }
  }

  @Override
  public void startTransaction(DatabaseContext context, Script script) {
    try {
      Connection connection = context.getConnection();
      connection.setAutoCommit(false);

      String savepointName = buildSavepointName(script);

      Savepoint savepoint = connection.setSavepoint(savepointName);
      context.getTxnSavepoints().put(savepointName, savepoint);
      logger.finest("started transaction with savepoint: " + savepointName);
    } catch (SQLException e) {
      throw new DatabaseException("Unable to start transaction", e);
    }
  }

  @Override
  public void endTransaction(DatabaseContext context, Script script) {
    try {
      Connection connection = context.getConnection();
      if (!connection.getAutoCommit()) {
        connection.commit();
        connection.setAutoCommit(true);

        String savepointName = buildSavepointName(script);

        try {
          connection.releaseSavepoint(context.getTxnSavepoints().get(savepointName));
        } catch (SQLException e) {
          logger.warning("Savepoint not found when ending transaction: " + savepointName);
        } finally {
          context.getTxnSavepoints().remove(savepointName);
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Unable to end the transaction", e);
    }
  }

  /**
   * Helper method for building a transaction savepoint name
   *
   * @param script the script
   * @return the savepoint name
   */
  String buildSavepointName(Script script) {
    return "transaction_savepoint_" + script.getFilename();
  }

  @Override
  public void rollback(DatabaseContext context, Script script) {
    try {

      String savepointName = buildSavepointName(script);

      Connection connection = context.getConnection();
      if (!connection.getAutoCommit()) {
        logger.finest("rolling back transaction to savepoint: " + savepointName);
        connection.rollback(context.getTxnSavepoints().get(savepointName));
        context.getTxnSavepoints().remove(savepointName);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Unable to rollback database", e);
    }
  }
}
