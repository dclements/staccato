package com.readytalk.staccato.utils;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;


@PrepareForTest({IOUtils.class})
public class JUSQLUtilsTest {
	@Rule
	public static final PowerMockRule powermock = new PowerMockRule();
	
	private Connection conn;
	private Statement statement;
	private ResultSet rs;
	
	@Before
	public void setUp() throws Exception {
		conn = mock(Connection.class);
		statement = mock(Statement.class);
		rs = mock(ResultSet.class);
		
		when(statement.getResultSet()).thenReturn(rs);
		when(conn.createStatement()).thenReturn(statement);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteNoReturn() throws Exception {
		String sql = "sql";
		when(statement.execute(anyString())).thenReturn(false);
		
		assertSame(null, SQLUtils.execute(conn, sql));
		
		verify(statement, times(1)).execute(eq(sql));
	}
	
	@Test
	public void testExecuteReturn() throws Exception {
		String sql = "sql";
		when(statement.execute(anyString())).thenReturn(true);
		
		assertSame(rs, SQLUtils.execute(conn, sql));
		
		verify(statement, times(1)).execute(eq(sql));
	}

	@Test
	public void testExecuteSQLFileNoReturn() throws Exception {
		String sql = "sql";
		File f = File.createTempFile("test", ".tmp");
		f.deleteOnExit();
		
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.toString(any(InputStream.class))).thenReturn(sql);
		
		when(statement.execute(anyString())).thenReturn(false);
		
		assertSame(null, SQLUtils.executeSQLFile(conn, f.toURI().toURL()));
		verify(statement, times(1)).execute(eq(sql));
	}
	
	@Test
	public void testExecuteSQLFileReturn() throws Exception {
		String sql = "sql";
		File f = File.createTempFile("test", ".tmp");
		f.deleteOnExit();
		
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.toString(any(InputStream.class))).thenReturn(sql);
		
		when(statement.execute(anyString())).thenReturn(true);
		
		assertSame(rs, SQLUtils.executeSQLFile(conn, f.toURI().toURL()));
		verify(statement, times(1)).execute(eq(sql));
		verifyZeroInteractions(rs);
		
		PowerMockito.verifyStatic();
		IOUtils.closeQuietly(any(InputStream.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SQLException.class)
	public void testExecuteSQLFileBadRead() throws Exception {
		File f = File.createTempFile("test", ".tmp");
		f.deleteOnExit();
		
		PowerMockito.mockStatic(IOUtils.class);
		when(IOUtils.toString(any(InputStream.class))).thenThrow(IOException.class);
		
		SQLUtils.executeSQLFile(conn, f.toURI().toURL());
	}

}
