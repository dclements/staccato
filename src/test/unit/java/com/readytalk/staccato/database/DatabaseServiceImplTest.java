package com.readytalk.staccato.database;

import java.net.URI;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DatabaseServiceImplTest extends BaseTest {

	@Test(dataProvider = "jdbcProvider")
	public void testInitContextWithMysql(URI baseJdbcUri) {

		DatabaseService service = new DatabaseServiceImpl();
		DatabaseContextBuilder dbCtxBuilder = service.getDatabaseContextBuilder();
		dbCtxBuilder.setContext(baseJdbcUri.toString(), dbName, dbUser, dbPwd, dbSuperUser, dbSuperUserPwd, rootDbName);

		DatabaseContext context = dbCtxBuilder.build();
		Assert.assertEquals(context.getFullyQualifiedJdbcUri(), URI.create(baseJdbcUri.toString() + dbName));
		Assert.assertEquals(context.getUsername(), dbUser);
		Assert.assertEquals(context.getPassword(), dbPwd);
		Assert.assertEquals(context.getDbName(), dbName);
		Assert.assertEquals(context.getRootDbName(), rootDbName);
		Assert.assertEquals(context.getSuperUserPwd(), dbSuperUserPwd);
		Assert.assertEquals(context.getSuperUser(), dbSuperUser);

		try {
			service.connect(context.getFullyQualifiedJdbcUri(), context.getUsername(), context.getPassword(), context.getDatabaseType());
			service.disconnect(context);
		} catch (DatabaseException e) {
			Assert.fail(getDatabaseErrorMessage(mysqlJdbcUri));
		}
	}

	//  @Test()
	//  public void testStartAndEndTransactionWithPostgresql() {
	//    DatabaseServiceImpl service = new DatabaseServiceImpl();
	//
	//    try {
	//      DatabaseContext context = service.initialize(postgresqlJdbcUri, dbName, dbUsername, dbPassword);
	//      service.connect(context);
	//
	//      String expectedFilename = "foo";
	//      Script script = EasyMock.createNiceMock(Script.class);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.replay(script);
	//      service.startTransaction(context, script);
	//
	//      String expectedSavepointName = service.buildSavepointName(script);
	//
	//      Map<String, Savepoint> savepoints = context.getTxnSavepoints();
	//      Assert.assertEquals(savepoints.size(), 1);
	//      Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
	//      Assert.assertNotNull(savepoint);
	//
	//      try {
	//        Assert.assertEquals(savepoint.getSavepointName(), expectedSavepointName);
	//      } catch (SQLException e) {
	//        Assert.fail("savepoint should exist", e);
	//      }
	//
	//      service.endTransaction(context, script);
	//
	//      Assert.assertEquals(savepoints.size(), 0);
	//
	//    } catch (DatabaseException e) {
	//      Assert.fail(getDatabaseErrorMessage(postgresqlJdbcUri));
	//    }
	//  }
	//
	//  @Test()
	//  public void testStartAndRollbackTransactionWithPostgresql() {
	//    DatabaseServiceImpl service = new DatabaseServiceImpl();
	//
	//    try {
	//      DatabaseContext context = service.initialize(postgresqlJdbcUri, dbName, dbUsername, dbPassword);
	//      service.connect(context);
	//
	//      String expectedFilename = "foo";
	//      Script script = EasyMock.createNiceMock(Script.class);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
	//      EasyMock.replay(script);
	//      service.startTransaction(context, script);
	//
	//      String expectedSavepointName = service.buildSavepointName(script);
	//
	//      Map<String, Savepoint> savepoints = context.getTxnSavepoints();
	//      Assert.assertEquals(savepoints.size(), 1);
	//      Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
	//      Assert.assertNotNull(savepoint);
	//
	//      try {
	//        service.rollback(context, script);
	//      } catch (Exception e) {
	//        Assert.fail("rollback should not have failed", e);
	//      }
	//
	//      try {
	//        Assert.assertEquals(savepoint.getSavepointName(), expectedSavepointName);
	//      } catch (SQLException e) {
	//        Assert.fail("savepoint should exist", e);
	//      }
	//
	//    } catch (DatabaseException e) {
	//      Assert.fail(getDatabaseErrorMessage(postgresqlJdbcUri));
	//    }
	//  }
}
