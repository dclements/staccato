package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;

/**
 * Simple struct for modeling database meta-data
 *
 * @author jhumphrey
 */
public class DatabaseContext {

  private Connection connection;
  private URI jdbcUri;
  private String username;
  private String password;
  private String dbName;
  private DatabaseType databaseType;

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public URI getJdbcUri() {
    return jdbcUri;
  }

  public void setJdbcUri(URI jdbcUri) {
    this.jdbcUri = jdbcUri;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public DatabaseType getDatabaseType() {
    return databaseType;
  }

  public void setDatabaseType(DatabaseType databaseType) {
    this.databaseType = databaseType;
  }
}