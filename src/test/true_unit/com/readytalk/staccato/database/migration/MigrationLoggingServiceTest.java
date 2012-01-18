package com.readytalk.staccato.database.migration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.PreUp;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.ResourceLoader;
import com.readytalk.staccato.utils.SQLUtils;

@RunWith(Theories.class)
@PrepareForTest({SQLUtils.class})
public class MigrationLoggingServiceTest {
	private MigrationLoggingService mls;
	
	private DatabaseContext context;
	private Connection conn;
	private ResourceLoader resourceLoader;
	
	@Rule
	public final PowerMockRule powermock = new PowerMockRule();
	
	@DataPoints
	public static final DatabaseType [] typeArray = DatabaseType.values();
	
	
	@Before
	public void setUp() throws Exception {
		context = mock(DatabaseContext.class);
		conn = mock(Connection.class);
		resourceLoader = mock(ResourceLoader.class);
		mls = spy(new MigrationLoggingServiceImpl(resourceLoader));
		
		when(context.getConnection()).thenReturn(conn);
		when(conn.isClosed()).thenReturn(false);
		
		
		PowerMockito.mockStatic(SQLUtils.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Theory
	public void testCreateVersionsTable(DatabaseType type) throws Exception {
		URL url = new URL("file:/var/");
		doReturn(false).when(mls).versionTableExists(eq(context));
		when(context.getDatabaseType()).thenReturn(type);
		when(resourceLoader.retrieveURI(any(ClassLoader.class), anyString())).thenReturn(url);
		
		mls.createVersionsTable(context);
		
		PowerMockito.verifyStatic();
		SQLUtils.executeSQLFile(eq(conn), eq(url));
		verify(resourceLoader).retrieveURI(any(ClassLoader.class), Matchers.contains(type.getType()));
	}
	
	@Theory
	public void testCreateVersionsTableNoResource(DatabaseType type) throws Exception {
		doReturn(false).when(mls).versionTableExists(eq(context));
		when(context.getDatabaseType()).thenReturn(type);
		when(resourceLoader.retrieveURI(any(ClassLoader.class), anyString())).thenReturn(null);
		
		try {
			mls.createVersionsTable(context);
			fail("Succeeded when resource did not exist.");
		} catch(final DatabaseException de) {
			verify(resourceLoader).retrieveURI(any(ClassLoader.class), Matchers.contains(type.getType()));
			PowerMockito.verifyZeroInteractions(SQLUtils.class);
		}
	}
	
	@Test
	public void testCreateVersionsTableExtant() throws Exception {
		doReturn(true).when(mls).versionTableExists(eq(context));

		mls.createVersionsTable(context);

		PowerMockito.verifyZeroInteractions(SQLUtils.class);
		verify(resourceLoader, never()).retrieveURI(any(ClassLoader.class), anyString());
	}

	@Test
	public void testVersionTableExists() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		PowerMockito.when(SQLUtils.execute(eq(conn), anyString())).thenReturn(rs);
		
		assertTrue(mls.versionTableExists(context));
		verify(rs).close();
	}

	@Test
	public void testLog() throws Exception {
		final Migration annotation = mock(Migration.class);
		final DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
		when(script.getFilename()).thenReturn("test");
		when(script.getScriptDate()).thenReturn(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss").parseDateTime("2012-12-12 12:12:12"));
		when(context.getDatabaseType()).thenReturn(DatabaseType.HSQLDB);
		doNothing().when(mls).createVersionsTable(any(DatabaseContext.class));
		
		mls.log(context, script, PreUp.class, annotation);
		PowerMockito.verifyStatic();
		SQLUtils.execute(eq(conn), argThat(new BaseMatcher<String>() {
			public boolean matches(Object obj) {
				return "INSERT INTO STACCATO_MIGRATIONS (database_version, script_date, script_hash, script_filename, workflow_step) values('null', '2012-12-12 12:12:12', 'null', 'test', 'PreUp')".equals(obj);
			}
			
			public void describeTo(Description desc) {
				desc.appendText("SQL Verifier.");
			}
		}));
		
	}
}
