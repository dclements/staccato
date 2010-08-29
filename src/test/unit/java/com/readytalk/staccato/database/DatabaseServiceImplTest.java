package com.readytalk.staccato.database;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.script.Script;

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

  @Test()
  public void testStartAndEndTransactionWithPostgresql() {
    DatabaseServiceImpl service = new DatabaseServiceImpl();

    try {
      DatabaseContext context = service.buildContext(postgresqlJdbcUri, dbName, dbUsername, dbPassword);
      service.connect(context);

      String expectedFilename = "foo";
      Script script = EasyMock.createNiceMock(Script.class);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.replay(script);
      service.startTransaction(context, script);

      String expectedSavepointName = service.buildSavepointName(script);

      Map<String, Savepoint> savepoints = context.getTxnSavepoints();
      Assert.assertEquals(savepoints.size(), 1);
      Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
      Assert.assertNotNull(savepoint);

      try {
        Assert.assertEquals(savepoint.getSavepointName(), expectedSavepointName);
      } catch (SQLException e) {
        Assert.fail("savepoint should exist", e);
      }

      service.endTransaction(context, script);

      Assert.assertEquals(savepoints.size(), 0);

    } catch (DatabaseException e) {
      Assert.fail(getDatabaseErrorMessage(postgresqlJdbcUri));
    }
  }

  @Test()
  public void testStartAndRollbackTransactionWithPostgresql() {
    DatabaseServiceImpl service = new DatabaseServiceImpl();

    try {
      DatabaseContext context = service.buildContext(postgresqlJdbcUri, dbName, dbUsername, dbPassword);
      service.connect(context);

      String expectedFilename = "foo";
      Script script = EasyMock.createNiceMock(Script.class);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
      EasyMock.replay(script);
      service.startTransaction(context, script);

      String expectedSavepointName = service.buildSavepointName(script);

      Map<String, Savepoint> savepoints = context.getTxnSavepoints();
      Assert.assertEquals(savepoints.size(), 1);
      Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
      Assert.assertNotNull(savepoint);

      try {
        service.rollback(context, script);
      } catch (Exception e) {
        Assert.fail("rollback should not have failed", e);
      }

      try {
        Assert.assertEquals(savepoint.getSavepointName(), expectedSavepointName);
      } catch (SQLException e) {
        Assert.fail("savepoint should exist", e);
      }

    } catch (DatabaseException e) {
      Assert.fail(getDatabaseErrorMessage(postgresqlJdbcUri));
    }
  }
}
