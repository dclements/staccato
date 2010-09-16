package com.readytalk.staccato.database;

import java.net.URI;

import com.readytalk.staccato.database.migration.MigrationException;

/**
 * Represents a database type
 *
 * @author jhumphrey
 */
public enum DatabaseType {
  MYSQL("mysql", "com.mysql.jdbc.Driver", "mysql"),
  POSTGRESQL("postgresql", "org.postgresql.Driver", "postgres");

  private String type;
  private String driver;
  private String root;

  DatabaseType(String type, String driver, String root) {
    this.type = type;
    this.driver = driver;
    this.root = root;
  }

  /**
   * Returns the jdbc driver
   *
   * @return the jdbc driver
   */
  public String getDriver() {
    return driver;
  }

  /**
   * Returns the database type
   *
   * @return the database type
   */
  public String getType() {
    return type;
  }

  public String getRoot() {
    return root;
  }

  /**
   * Convenience method for returning a type from a JDBC URI
   *
   * @param uri JDBC URI
   * @return a database type
   */
  public static DatabaseType getTypeFromJDBCUri(URI uri) {
    try {
      return DatabaseType.valueOf(uri.getSchemeSpecificPart().split(":")[0].toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new MigrationException("Staccato currently does not provide support for the following jdbc uri: " + uri);
    }
  }

}