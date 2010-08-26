package com.ecovate.database;

import java.net.URI;

/**
 * Represents a database type
 *
 * @author jhumphrey
 */
public enum DatabaseType {
  MYSQL("mysql", "com.mysql.jdbc.Driver"),
  POSTGRESQL("postgresql", "org.postgresql.Driver");

  private String type;
  private String driver;

  DatabaseType(String type, String driver) {
    this.type = type;
    this.driver = driver;
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

  /**
   * Convenience method for returning a type from a JDBC URI
   *
   * @param uri JDBC URI
   * @return a database type
   */
  public static DatabaseType getTypeFromJDBCUri(URI uri) {
    return DatabaseType.valueOf(uri.getSchemeSpecificPart().split(":")[0].toUpperCase());
  }

}