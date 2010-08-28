package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.migration.guice.MigrationModule;

/**
 * @author jhumphrey
 */
public class BaseTest {

  public final String dbName = "staccato";
  public final URI mysqlJdbcUri = URI.create("jdbc:mysql://localhost:3306/" + dbName);
  public final URI postgresqlJdbcUri = URI.create("jdbc:postgresql://localhost:5432/" + dbName);
  public final String dbUsername = "staccato";
  public final String dbPassword = "staccato";

  protected Injector injector;

  @BeforeClass()
  public void initGuice() {
    injector = Guice.createInjector(new MigrationModule());
    injector.injectMembers(this);
  }

  public String getDatabaseErrorMessage(URI jdbcUri) {
    return "The JDBC url [" + jdbcUri + "] is not reachable." +
      " This test requires that the database type be " +
      "installed on the system and that a database called '" + dbName + "' " +
      "is created with grants for username '" + dbUsername + "' with password '" + dbPassword + "'.  " +
      "Please refer to the src/test/database directory for sql scripts to " +
      "help with this setup";
  }

  public Connection makeConnection() {
    URI jdbcUri = this.postgresqlJdbcUri;

    try {

      Class.forName("org.postgresql.Driver");

      String username = dbUsername;
      String password = dbPassword;

      return DriverManager.getConnection(jdbcUri.toString(), username, password);

    } catch (ClassNotFoundException e) {
      Assert.fail(getDatabaseErrorMessage(jdbcUri));
    } catch (SQLException e) {
      Assert.fail(getDatabaseErrorMessage(jdbcUri));
    }

    return null;
  }
}
