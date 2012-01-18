package com.readytalk.staccato.database.migration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.utils.SQLUtils;

@PrepareForTest({SQLUtils.class})
public class MigrationRuntimeTest {
	private DatabaseContext context;
	private SQLScript scr;
	private List<SQLScript> scripts;
	private final MigrationType type = MigrationType.SCHEMA_UP;
	private MigrationRuntime runtime;;
	
	private Connection conn;
	
	@Rule
	public final PowerMockRule powermock = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		context = mock(DatabaseContext.class);
		scr = mock(SQLScript.class);
		scripts = Arrays.asList(new SQLScript [] {scr});
		conn = mock(Connection.class);
		
		runtime = new MigrationRuntimeImpl(context, scripts, type, true);
		
		when(context.getConnection()).thenReturn(conn);
		
		PowerMockito.mockStatic(SQLUtils.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDatabaseContext() {
		assertEquals(context, runtime.getDatabaseContext());
	}

	@Test
	public void testGetMigrationType() {
		assertEquals(type, runtime.getMigrationType());
	}

	@Test
	public void testIsLoggingEnabled() {
		assertEquals(true, runtime.isLoggingEnabled());
	}

	@Test
	public void testSqlScripts() throws Exception {
		assertEquals(scripts, runtime.sqlScripts());
	}

	@Test
	public void testExecuteSQL() throws Exception {
		runtime.executeSQL("test");
		
		PowerMockito.verifyStatic();
		SQLUtils.execute(conn, "test");
	}

	@Test
	public void testExecuteSQLFile() throws Exception {
		URL url = new URL("file://tmp");
		when(scr.getFilename()).thenReturn("test.sql");
		when(scr.getUrl()).thenReturn(url);
		
		runtime.executeSQLFile("test.sql");
		
		PowerMockito.verifyStatic();
		SQLUtils.executeSQLFile(eq(conn), eq(url));
	}
	
	@Test(expected=MigrationException.class)
	public void testExecuteSQLFileNotThere() throws Exception {
		URL url = new URL("file://tmp");
		when(scr.getFilename()).thenReturn("test.sql");
		when(scr.getUrl()).thenReturn(url);
		
		runtime.executeSQLFile("test2.sql");
	}
}
