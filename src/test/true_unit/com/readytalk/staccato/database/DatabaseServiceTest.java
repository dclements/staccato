package com.readytalk.staccato.database;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.readytalk.staccato.database.migration.script.Script;

@RunWith(Enclosed.class)
public class DatabaseServiceTest {

	@RunWith(PowerMockRunner.class)
	@PrepareForTest({DriverManagerWrapper.class, DbUtils.class})
	public static class DriverTests {
		private final DatabaseService service = new DatabaseServiceImpl();
		private final Connection conn = mock(Connection.class);

		@Before
		public void setUp() throws Exception {
			PowerMockito.mockStatic(DriverManagerWrapper.class);
			PowerMockito.mockStatic(DbUtils.class);
			PowerMockito.when(DbUtils.loadDriver(anyString())).thenReturn(true);
			PowerMockito.when(DriverManagerWrapper.getConnection(anyString(), anyString(), anyString())).thenReturn(conn);
		}

		@Test
		public void testConnect() throws Exception {

			service.connect(URI.create("jdbc:postgres:localhost"), "staccato", "staccato_pass", DatabaseType.POSTGRESQL);

			PowerMockito.verifyStatic(times(1));
			DbUtils.loadDriver(eq("org.postgresql.Driver"));
			PowerMockito.verifyStatic();
			DriverManagerWrapper.getConnection(eq("jdbc:postgres:localhost"), eq("staccato"), eq("staccato_pass"));
		}
	}

	public static class ConnectionTests {
		private final Connection conn = mock(Connection.class);
		private final DatabaseContext ctx = mock(DatabaseContext.class);
		private final DatabaseService service = new DatabaseServiceImpl();
		private final Script<?> script = mock(Script.class);
		@SuppressWarnings("unchecked")
		private final Map<String, Savepoint> savepoints = (Map<String, Savepoint>)mock(Map.class);

		@Before
		public void setUp() throws Exception {
			reset(conn, ctx, script, savepoints);
			when(ctx.getConnection()).thenReturn(conn);
			when(script.getFilename()).thenReturn("testname");
			when(ctx.getTxnSavepoints()).thenReturn(savepoints);
		}

		@Test
		public void testDisconnect() throws Exception {
			when(conn.isValid(any(Integer.class))).thenReturn(true);

			service.disconnect(ctx);

			verify(conn).close();
		}


		@Test
		public void testDisconnectRetry() throws Exception {
			when(conn.isValid(any(Integer.class))).thenReturn(true);
			when(conn.isClosed()).thenReturn(false);
			doThrow(SQLException.class).when(conn).close();

			service.disconnect(ctx);

			verify(conn, times(2)).close();
		}

		@Test
		public void testStartTransaction() throws Exception {
			service.startTransaction(ctx, script);

			verify(conn).setAutoCommit(eq(false));
			verify(conn, never()).setAutoCommit(eq(true));
			verify(savepoints).put(eq("transaction_savepoint_testname"), any(Savepoint.class));
		}

		@Test
		public void testEndTransaction() throws Exception {
			Savepoint svp = mock(Savepoint.class);
			when(conn.getAutoCommit()).thenReturn(false);
			when(savepoints.get("transaction_savepoint_testname")).thenReturn(svp);

			service.endTransaction(ctx, script);

			verify(conn).setAutoCommit(eq(true));
			verify(conn, never()).setAutoCommit(eq(false));
			verify(conn, never()).close();
			verify(conn, times(1)).releaseSavepoint(eq(svp));
			verify(savepoints).remove(eq("transaction_savepoint_testname"));
			
			
		}

		@Test
		public void testRollback() throws Exception {
			Savepoint svp = mock(Savepoint.class);
			
			when(conn.getAutoCommit()).thenReturn(false);
			when(savepoints.get("transaction_savepoint_testname")).thenReturn(svp);
			
			service.rollback(ctx, script);
			
			verify(conn, times(1)).rollback(eq(svp));
			verify(conn, never()).close();
			verify(savepoints).remove(eq("transaction_savepoint_testname"));
		}
	}
}
