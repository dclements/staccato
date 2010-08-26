package com.ecovate.database;

import java.net.URI;

import com.google.inject.ImplementedBy;

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
}
