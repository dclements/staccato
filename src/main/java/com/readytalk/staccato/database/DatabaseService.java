package com.readytalk.staccato.database;

import java.net.URI;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.utils.VersionRange;

/**
 * @author jhumphrey
 */
@ImplementedBy(DatabaseServiceImpl.class)
public interface DatabaseService {

  /**
   * Initializes context for a database
   *
   * @param jdbcUri the jdbc URI
   * @param dbName the database name
   * @param username the db username
   * @param password the db password
   * @return database context
   */
  DatabaseContext buildContext(URI jdbcUri, String dbName, String username, String password);

  /**
   * Connects to the database using the context specified
   *
   * @param context the database context
   */
  void connect(DatabaseContext context);

  /**
   * Disconnects from the database using the context specified
   *
   * @param context the database context
   */
  void disconnect(DatabaseContext context);

  /**
   * Starts a database transaction
   *
   * @param context the database context
   */
  void startTransaction(DatabaseContext context);

  /**
   * Ends the transaction
   *
   * @param context the database context
   */
  void endTransaction(DatabaseContext context);

  /**
   * Rollsback any queries in the connection
   *
   * @param context the database context
   */
  void rollback(DatabaseContext context);
}
