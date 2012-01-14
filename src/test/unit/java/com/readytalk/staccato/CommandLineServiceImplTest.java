package com.readytalk.staccato;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationException;


public class CommandLineServiceImplTest extends BaseTest {

	CommandLineParser parser;

	HelpFormatter helpFormatter;

	@Inject
	public void setHelpFormatter(HelpFormatter helpFormatter) {
		this.helpFormatter = helpFormatter;
	}

	@Inject
	public void setParser(CommandLineParser parser) {
		this.parser = parser;
	}

	@Test
	public void testHelpWithArg() {
		CommandLineServiceImpl service = new CommandLineServiceImpl(parser, helpFormatter);
		String[] args = new String[]{"help"};
		try {
			service.parse(args);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testHelpWithOutArg() {
		CommandLineServiceImpl service = new CommandLineServiceImpl(parser, helpFormatter);
		String[] args = new String[]{};
		try {
			service.parse(args);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testParseValidShortOpts() {
		CommandLineServiceImpl service = new CommandLineServiceImpl(parser, helpFormatter);

		String[] args = new String[]{"-j", "jdbcUri", "-n", "dbName", "-u", "dbUser", "-p", dbPwd,
				"-m", "UP", "fd", "fromDate", "td", "toDate", "-s", "test.groovy", "-d", "migrations/",
				"rn", "rootDbName", "-su", "superuser", "-sup", "superuserPwd", "-fv", "fromVer", "-tv", "toVer", "-l", "true"};

		try {
			StaccatoOptions options = service.parse(args);

			Assert.assertTrue(options.enableLogging);

		} catch (MigrationException e) {
			Assert.fail("Should not have thrown", e);
		}
	}

	@Test
	public void testParseLongOpts() {
		CommandLineServiceImpl service = new CommandLineServiceImpl(parser, helpFormatter);

		String[] args = new String[]{"-jdbc", "jdbcUri", "-dbName", "dbName", "-dbUser", "dbUser", "-dbPwd", dbPwd,
				"-migration", "UP", "fromDate", "fromDate", "toDate", "toDate", "-script", "test.groovy", "-directory", "migrations/",
				"rootDbName", "rootDbName", "-dbSuperUser", "superuser", "-dbSuperPwd", "superuserPwd", "-fromVersion", "fromVer", "-tv", "toVersion"};

		try {
			service.parse(args);
		} catch (MigrationException e) {
			Assert.fail("Should not have thrown", e);
		}
	}
}
