package com.readytalk.staccato.database;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class DatabaseServiceImplTest extends BaseTest {

  @Test()
  public void testInitContextWithMysql() {

    DatabaseService service = new DatabaseServiceImpl();

    try {
      DatabaseContext context = service.buildContext(mysqlJdbcUri, dbName, dbUsername, dbPassword);
      Assert.assertEquals(context.getJdbcUri(), mysqlJdbcUri);
      Assert.assertEquals(context.getUsername(), dbUsername);
      Assert.assertEquals(context.getPassword(), dbPassword);
      Assert.assertEquals(context.getDbName(), dbName);
      service.connect(context);
      service.disconnect(context);
    } catch (DatabaseException e) {
      Assert.fail(getDatabaseErrorMessage(mysqlJdbcUri));
    }
  }

  @Test
  public void testInitContextWithPostgres() {

    DatabaseService service = new DatabaseServiceImpl();

    try {
      DatabaseContext context = service.buildContext(postgresqlJdbcUri, dbName, dbUsername, dbPassword);
      Assert.assertEquals(context.getJdbcUri(), postgresqlJdbcUri);
      Assert.assertEquals(context.getUsername(), dbUsername);
      Assert.assertEquals(context.getPassword(), dbPassword);
      Assert.assertEquals(context.getDbName(), dbName);
      service.connect(context);
      service.disconnect(context);
    } catch (DatabaseException e) {
      Assert.fail(getDatabaseErrorMessage(postgresqlJdbcUri));
    }
  }
}
