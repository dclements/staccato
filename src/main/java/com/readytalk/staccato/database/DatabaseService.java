package com.readytalk.staccato.database;

import java.net.URI;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.script.Script;

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
  DatabaseContext initialize(URI jdbcUri, String dbName, String username, String password);

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
   * @param script the script creating a transaction for
   */
  void startTransaction(DatabaseContext context, Script script);

  /**
   * Ends the transaction
   *
   * @param context the database connection
   * @param script the script ending the transaction for
   */
  void endTransaction(DatabaseContext context, Script script);

  /**
   * Rollsback any queries in the connection
   *
   * @param context the database connection
   * @param script the script rolling back
   */
  void rollback(DatabaseContext context, Script script);
}
