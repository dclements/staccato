package com.readytalk.staccato;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationException;

/**
 * @author jhumphrey
 */
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
  public void testParse() {
    CommandLineServiceImpl service = new CommandLineServiceImpl(parser, helpFormatter);

    String[] args = new String[]{"-j", "jdbcUri", "-dn", "dbName", "-du", "dbUser", "-dp", dbPwd,
      "-m", "UP", "mfd", "fromDate", "mtd", "toDate", "-ms", "test.groovy", "-md", "migrations/",
      "-dsu", "superuser", "-dsup", "superuserPwd", "-mfd", "fromVer", "-mtv", "toVer"};

    try {
      service.parse(args);
    } catch (MigrationException e) {
      Assert.fail("Should not have thrown", e);
    }

  }
}
