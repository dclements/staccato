package com.readytalk.staccato.database.migration;

import java.net.URI;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;

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

      String migrationDir = MigrationService.DEFAULT_MIGRATION_DIR;

      // initialize database context      
      DatabaseService databaseService = injector.getInstance(DatabaseService.class);
      DatabaseContext dbCtx = databaseService.buildContext(URI.create(jdbcUri), dbName, username, password);

      // initialize project context
      ProjectContext pCtx = new ProjectContext();
      pCtx.setName(projectName);
      pCtx.setVersion(projectVersion);

      // initialize the runtime
      SQLScriptService sqlScriptService = injector.getInstance(SQLScriptService.class);
      List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir);
      MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, pCtx, sqlScripts, MigrationType.valueOf(migrationType));

      // load the groovy scripts and run the migration
      GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class);
      List<GroovyScript> scripts = scriptService.load(migrationDir);
      MigrationService<GroovyScript> migrationService = injector.getInstance(GroovyMigrationService.class);
      migrationService.run(scripts, migrationRuntime);

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