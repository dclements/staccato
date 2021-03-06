package com.readytalk.staccato;


import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

@RunWith(Theories.class)
public class CommandLineServiceTest extends GuiceTest {
	private CommandLineService service;
	
	private final CommandLineParser parser = spy(getInjector().getInstance(CommandLineParser.class));
	private final HelpFormatter hf = spy(getInjector().getInstance(HelpFormatter.class));
	
	private final Injector injector = createOverride(new AbstractModule() {

		@Override
		protected void configure() {
			bind(CommandLineParser.class).toInstance(parser);
			bind(HelpFormatter.class).toInstance(hf);
		}
		
	});
	
	@Before
	public void setUp() throws Exception {
		reset(parser, hf);
		
		service = injector.getInstance(CommandLineService.class);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@DataPoints
	public static String [] [] helpTheories() {
		return new String [] [] {
				{},
				{"help"},
				{"--help"},
				{"-h"}
		};
	}
	
	
	@Theory
	public void testHelpParse(String [] test) {
		assertNull(service.parse(test));
		verify(hf, times(1)).printHelp(anyString(), any(Options.class));
	}
	
	@Test
	public void testAllRequiredParse() throws Exception {
		String [] args = new String [] {
				"--jdbc", "jdbc:postgres:localhost",
				"--dbName", "staccato1",
				"--dbUser", "staccato2",
				"--dbPwd", "staccato3",
				"--migration", "migration",
				"--logging"
		};
		final StaccatoOptions so = service.parse(args);
		
		verify(parser).parse(any(Options.class), eq(args));
		assertTrue(so.enableLogging);
		assertEquals("staccato3", so.dbPwd);
	}
	
	@Test
	public void testAllRequiredShortParse() throws Exception {
		String [] args = new String [] {
				"-j", "jdbc:postgres:localhost",
				"-n", "staccato1",
				"-u", "staccato2",
				"-p", "staccato3",
				"-m", "migration",
				"-l"
		};
		final StaccatoOptions so = service.parse(args);
		
		verify(parser).parse(any(Options.class), eq(args));
		assertTrue(so.enableLogging);
		assertEquals("staccato3", so.dbPwd);
	}
	
	@Test
	public void testAllShortParse() throws Exception {
		String[] args = new String[]{"-j", "jdbcUri", "-n", "dbName", "-u", "dbUser", "-p", "staccato3",
				"-m", "UP", "fd", "fromDate", "td", "toDate", "-s", "test.groovy", "-d", "migrations/",
				"rn", "rootDbName", "-su", "superuser", "-sup", "superuserPwd", "-fv", "fromVer", "-tv", "toVer", "-l"};
		final StaccatoOptions so = service.parse(args);
		
		verify(parser).parse(any(Options.class), eq(args));
		assertTrue(so.enableLogging);
		assertEquals("staccato3", so.dbPwd);
	}
	
	@Test
	public void testAllLongParse() throws Exception {
		String[] args = new String[]{"-jdbc", "jdbcUri", "-dbName", "dbName", "-dbUser", "dbUser", "-dbPwd", "staccato3",
				"-migration", "UP", "fromDate", "fromDate", "toDate", "toDate", "-script", "test.groovy", "-directory", "migrations/",
				"rootDbName", "rootDbName", "-dbSuperUser", "superuser", "-dbSuperPwd", "superuserPwd", "-fromVersion", "fromVer", "-tv", "toVersion",
				"-logging"};

		final StaccatoOptions so = service.parse(args);
		
		verify(parser).parse(any(Options.class), eq(args));
		assertTrue(so.enableLogging);
		assertEquals("staccato3", so.dbPwd);
	}
	
	
}
