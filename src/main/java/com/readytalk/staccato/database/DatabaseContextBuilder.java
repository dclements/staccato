package com.readytalk.staccato.database;

import java.net.URI;

/**
 * @author jhumphrey
 */
public class DatabaseContextBuilder {

  private DatabaseContext ctx = new DatabaseContext();

  /**
   * Returns the database context
   *
   * @return {@link com.readytalk.staccato.database.DatabaseContext}
   */
  public DatabaseContext build() {
    return ctx;
  }

  /**
   * Sets the base jdbc context
   *
   * @param rootDbName the database name
   * @param rootUsername the database username
   * @param rootPassword the database password
   * @return this builder
   */

  public DatabaseContextBuilder setRootJdbcContext(String rootDbName, String rootUsername, String rootPassword) {

    ctx.setRootDbName(rootDbName);
    ctx.setRootPassword(rootPassword);
    ctx.setRootUsername(rootUsername);

    return this;
  }

  /**
   * Sets the base jdbc context
   *
   * @param baseJdbcUri the base jdbc uri
   * @param dbName the database name
   * @param username the database username
   * @param password the database password
   * @return this builder
   */

  public DatabaseContextBuilder setBaseJdbcContext(String baseJdbcUri, String dbName, String username, String password) {

    String fullyQualifiedJdbcUriStr = baseJdbcUri.toString();

    if (fullyQualifiedJdbcUriStr.endsWith("/")) {
      fullyQualifiedJdbcUriStr += dbName;
    } else {
      fullyQualifiedJdbcUriStr += "/" + dbName;
    }

    URI fullyQualifiedJdbcUri = URI.create(fullyQualifiedJdbcUriStr);

    ctx.setBaseJdbcUri(URI.create(baseJdbcUri));
    ctx.setFullyQualifiedJdbcUri(fullyQualifiedJdbcUri);
    ctx.setDbName(dbName);
    ctx.setUsername(username);
    ctx.setPassword(password);

    DatabaseType databaseType = DatabaseType.getTypeFromJDBCUri(fullyQualifiedJdbcUri);
    ctx.setDatabaseType(databaseType);

    return this;
  }
}
