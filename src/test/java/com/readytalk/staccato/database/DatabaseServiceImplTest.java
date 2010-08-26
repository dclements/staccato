package com.readytalk.staccato.database;

import java.net.URI;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class DatabaseServiceImplTest {

  @Test(enabled = false)
  public void testInitContextWithMysql() {

    DatabaseService service = new DatabaseServiceImpl();

    String dbName = "staccato";
    URI jdbcUri = URI.create("jdbc:mysql://localhost:3306/" + dbName);
    String username = "staccato";
    String password = "staccato";

    try {
      DatabaseContext context = service.buildContext(jdbcUri, dbName, username, password);
      Assert.assertEquals(jdbcUri, context.getJdbcUri());
      Assert.assertEquals(username, context.getUsername());
      Assert.assertEquals(password, context.getPassword());
    } catch (DatabaseException e) {
      Assert.fail("The JDBC url [" + jdbcUri + "] is not reachable." +
        " This test requires that MySQL be " +
        "installed on the system and that a database called 'staccato' " +
        "is created with grants for username '" + username + "' with password '" + password + "'.  " +
        "Please refer to the src/test/database directory for sql migration to " +
        "help with this setup");
    }
  }

  @Test
  public void testInitContextWithPostgres() {

    DatabaseService service = new DatabaseServiceImpl();

    String dbName = "staccato";
    URI jdbcUri = URI.create("jdbc:postgresql://localhost:5432/" + dbName);
    String username = "staccato";
    String password = "staccato";

    try {
      DatabaseContext context = service.buildContext(jdbcUri, dbName, username, password);
      Assert.assertEquals(jdbcUri, context.getJdbcUri());
      Assert.assertEquals(username, context.getUsername());
      Assert.assertEquals(password, context.getPassword());
      service.connect(context);
    } catch (DatabaseException e) {
      Assert.fail("The JDBC url [" + jdbcUri + "] is not reachable." +
        " This test requires that PostgreSQL be " +
        "installed on the system and that a database called '" + dbName + "' " +
        "is created with grants for username '" + username + "' with password '" + password + "'.  " +
        "Please refer to the src/test/database directory for sql migration to " +
        "help with this setup");
    }
  }
}
