package com.readytalk.staccato.database;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.script.Script;
import com.readytalk.staccato.utils.SQLUtils;

public class DatabaseServiceImplTest extends BaseTest {

	@Test(dataProvider = "jdbcProvider")
	public void testInitContext(URI baseJdbcUri) {

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
			Assert.fail(getDatabaseErrorMessage(baseJdbcUri));
		}
	}

	@Test(dataProvider="jdbcProvider")
	public void testStartAndEndTransaction(URI baseJdbcUri) {
		DatabaseContext context = null;
		try {
			DatabaseService service = new DatabaseServiceImpl();
			DatabaseContextBuilder dbCtxBuilder = service.getDatabaseContextBuilder();
			dbCtxBuilder.setContext(baseJdbcUri.toString(), dbName, dbUser, dbPwd, dbSuperUser, dbSuperUserPwd, rootDbName);

			context = dbCtxBuilder.build();

			Connection conn = service.connect(context.getFullyQualifiedJdbcUri(), context.getUsername(), context.getPassword(), context.getDatabaseType());
			context.setConnection(conn);

			String expectedFilename = "foo";
			Script<?> script = mock(Script.class);
			when(script.getFilename()).thenReturn(expectedFilename);
			service.startTransaction(context, script);

			String expectedSavepointName = "transaction_savepoint_" + script.getFilename();

			Map<String, Savepoint> savepoints = context.getTxnSavepoints();
			Assert.assertEquals(savepoints.size(), 1);
			Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
			Assert.assertNotNull(savepoint);

			try {
				Assert.assertEquals(savepoint.getSavepointName(), expectedSavepointName);
			} catch (SQLException e) {
				Assert.fail("Savepoint should exist.", e);
			}

			service.endTransaction(context, script);

			Assert.assertEquals(savepoints.size(), 0);

			service.disconnect(context);

		} catch (DatabaseException e) {
			Assert.fail(getDatabaseErrorMessage(baseJdbcUri));
		} finally {
			if(context != null) {
				DbUtils.closeQuietly(context.getConnection());
			}
		}
	}

	@Test(dataProvider="jdbcProvider")
	public void testStartAndRollbackTransaction(URI baseJdbcUri) throws Exception {
		
		DatabaseContext context = null;
		try {
			DatabaseService service = new DatabaseServiceImpl();
			DatabaseContextBuilder dbCtxBuilder = service.getDatabaseContextBuilder();
			dbCtxBuilder.setContext(baseJdbcUri.toString(), dbName, dbUser, dbPwd, dbSuperUser, dbSuperUserPwd, rootDbName);

			context = dbCtxBuilder.build();
			
			service.connect(context);

			String expectedFilename = "foo";
			Script<?> script = mock(Script.class);
			when(script.getFilename()).thenReturn(expectedFilename);
			service.startTransaction(context, script);

			String expectedSavepointName = "transaction_savepoint_" + script.getFilename();
			
			Map<String, Savepoint> savepoints = context.getTxnSavepoints();
			Assert.assertEquals(savepoints.size(), 1);
			Savepoint savepoint = context.getTxnSavepoints().get(expectedSavepointName);
			Assert.assertNotNull(savepoint);
			
			
			try {
				service.rollback(context, script);
			} catch (Exception e) {
				Assert.fail("rollback should not have failed", e);
			}

		} catch (DatabaseException e) {
			Assert.fail(getDatabaseErrorMessage(baseJdbcUri));
		} finally {
			if(context != null) {
				DbUtils.closeQuietly(context.getConnection());
			}
		}
	}
	
	@Test(dataProvider="jdbcProvider")
	public void testRollbackStatementTransaction(URI baseJdbcUri) throws Exception {
		/*TODO: Needs cleanup, moving a lot of the boilerplate in all of the methods in this
		 *     class into before and after methods. */
		final Script<?> script = mock(Script.class);
		when(script.getFilename()).thenReturn("test");
		DatabaseService service = new DatabaseServiceImpl();
		DatabaseContextBuilder dbCtxBuilder = service.getDatabaseContextBuilder();
		dbCtxBuilder.setContext(baseJdbcUri.toString(), dbName, dbUser, dbPwd, dbSuperUser, dbSuperUserPwd, rootDbName);

		DatabaseContext context = dbCtxBuilder.build();
		
		final Connection c = service.connect(context);
		
		SQLUtils.execute(c, "CREATE TEMPORARY TABLE FooBar (id int, c varchar(50))");
		
		try {
			ResultSet rs;
			service.startTransaction(context, script);
		
			SQLUtils.execute(context.getConnection(), "INSERT INTO FooBar (id, c) VALUES (1, 'test')");
			
			rs = SQLUtils.execute(context.getConnection(), "SELECT COUNT(*) FROM FooBar");
			try {
				rs.next();
				Assert.assertEquals(rs.getInt(1), 1);
			} finally {
				rs.close();
			}
			
			service.rollback(context, script);
			
			rs = SQLUtils.execute(context.getConnection(), "SELECT COUNT(*) FROM FooBar");
			try {
				rs.next();
				Assert.assertEquals(rs.getInt(1), 0);
			} finally {
				rs.close();
			}
		} finally {
			DbUtils.closeQuietly(c);
			
			final Connection c2 = service.connect(context);
			try {
				SQLUtils.execute(c2, "DROP TABLE FooBar");
			} catch(Exception ex) {
				//If it doesn't exist, that's okay.
			} finally {
				DbUtils.closeQuietly(c2);
			}
		}
	}
}
