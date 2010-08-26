package com.readytalk.staccato.database.migration;

import java.net.URI;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Contains the main for running this application.
 *
 * @author jhumphrey
 */
public class Main {

  public static Injector injector = Guice.createInjector(new MigrationModule());

  public static final OptionSet OPTION_SET = new OptionSet();

  public static void main(String... args) {

    if (args.length == 0 || args[0].equals("help")) {
      printHelp(OPTION_SET.options);
    } else {

      CommandLine line;
      try {
        line = parseArgs(args);
      } catch (ParseException e) {
        throw new MigrationException(e);
      }

      // extract options values from the command line
      String jdbcUri = line.getOptionValue(OPTION_SET.jdbcUrlOpt.getOpt());
      String dbName = line.getOptionValue(OPTION_SET.dbNameOpt.getOpt());
      String username = line.getOptionValue(OPTION_SET.usernameOpt.getOpt());
      String password = line.getOptionValue(OPTION_SET.passwordOpt.getOpt());
      String projectName = line.getOptionValue(OPTION_SET.projectNameOpt.getOpt());
      String projectVersion = line.getOptionValue(OPTION_SET.projectVersionOpt.getOpt());
      String migrationType = line.getOptionValue(OPTION_SET.migrationTypeOpt.getOpt());

      DatabaseService databaseService = injector.getInstance(DatabaseService.class);

      // initialize database context
      DatabaseContext dbContext = databaseService.buildContext(URI.create(jdbcUri), dbName, username, password);

      // initialize project context
      ProjectContext pContext = new ProjectContext();
      pContext.setName(projectName);
      pContext.setVersion(projectVersion);

      MigrationService migrationService = injector.getInstance(GroovyMigrationService.class);
      migrationService.run(dbContext, pContext, MigrationType.valueOf(migrationType));

    }
  }

  public static CommandLine parseArgs(String... args) throws ParseException {
    CommandLineParser parser = new BasicParser();
    return parser.parse(OPTION_SET.options, args);
  }

  private static void printHelp(Options options) {
    HelpFormatter help = new HelpFormatter();
    help.printHelp("java -jar database-manager.jar [options]", options);
  }

  /**
   * Struct for a set of Option objects.  These represent options given on the command line
   */
  private static class OptionSet {
    Option jdbcUrlOpt = new Option("jdbc", "jdbcUrl", true, "JDBC URL");
    Option dbNameOpt = new Option("dbn", "databaseName", true, "The database name");
    Option usernameOpt = new Option("dbu", "dbUsername", true, "The database username");
    Option passwordOpt = new Option("dbp", "dbPassword", true, "The database password");
    Option projectNameOpt = new Option("pn", "projectName", true, "The project name");
    Option projectVersionOpt = new Option("pv", "projectVersion", true, "The project version");
    Option migrationTypeOpt = new Option("m", "migrationType", true, "The migration type:\n" + MigrationType.description());

    Options options = new Options();

    private OptionSet() {
      jdbcUrlOpt.setRequired(true);
      options.addOption(jdbcUrlOpt);

      dbNameOpt.setRequired(true);
      options.addOption(dbNameOpt);

      usernameOpt.setRequired(true);
      options.addOption(usernameOpt);

      passwordOpt.setRequired(true);
      options.addOption(passwordOpt);

      projectNameOpt.setRequired(false);
      options.addOption(projectNameOpt);

      projectVersionOpt.setRequired(false);
      options.addOption(projectVersionOpt);

      migrationTypeOpt.setRequired(true);
      options.addOption(migrationTypeOpt);
    }
  }
}