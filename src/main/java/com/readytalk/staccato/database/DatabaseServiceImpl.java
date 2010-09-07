package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.log4j.Logger;

import com.readytalk.staccato.database.migration.script.Script;

/**
 * @author jhumphrey
 */
public class DatabaseServiceImpl implements DatabaseService {

  public static final Logger logger = Logger.getLogger(DatabaseServiceImpl.class);

  @Override
  public DatabaseContextBuilder getDatabaseContextBuilder() {
    return new DatabaseContextBuilder();
  }

  @Override
  public Connection connect(URI jdbcUri, String username, String password, DatabaseType databaseType) {

    Connection connection;

    try {

      Class.forName(databaseType.getDriver());

      logger.debug("Connecting to database: " + jdbcUri.toString());

      connection = DriverManager.getConnection(jdbcUri.toString(), username, password);

    } catch (ClassNotFoundException e) {
      throw new DatabaseException("SQL driver not found", e);
    } catch (SQLException e) {
      throw new DatabaseException("SQL exception occurred when establishing connection to database: " + jdbcUri, e);
    }

    return connection;
  }

  @Override
  public void disconnect(DatabaseContext context) {

    logger.info("Disconnecting database connection: " + context.getFullyQualifiedJdbcUri().toString());

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
        logger.warn("Unable to close database connection to: " + context.getFullyQualifiedJdbcUri());
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
      logger.debug("started transaction with savepoint: " + savepointName);
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
          logger.debug("Savepoint not found when ending transaction: " + savepointName);
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
        logger.debug("rolling back transaction to savepoint: " + savepointName);
        connection.rollback(context.getTxnSavepoints().get(savepointName));
        context.getTxnSavepoints().remove(savepointName);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Unable to rollback database", e);
    }
  }
}
