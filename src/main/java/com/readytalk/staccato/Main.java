package com.readytalk.staccato;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseContextBuilder;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.migration.GroovyMigrationService;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.MigrationRuntimeImpl;
import com.readytalk.staccato.database.migration.MigrationService;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.ProjectContext;
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

      CommandLine cli;
      try {
        cli = parseArgs(args);
      } catch (ParseException e) {
        throw new MigrationException(e);
      }

      // extract options values from the command line
      String baseJdbcUriStr = cli.getOptionValue(OPTION_SET.jdbcUrlOpt.getOpt());
      String dbName = cli.getOptionValue(OPTION_SET.dbNameOpt.getOpt());
      String username = cli.getOptionValue(OPTION_SET.usernameOpt.getOpt());
      String password = cli.getOptionValue(OPTION_SET.passwordOpt.getOpt());
      String projectName = cli.getOptionValue(OPTION_SET.projectNameOpt.getOpt());
      String projectVersion = cli.getOptionValue(OPTION_SET.projectVersionOpt.getOpt());
      String migrationTypeOpt = cli.getOptionValue(OPTION_SET.migrationTypeOpt.getOpt());
      String migrateFromDateOpt = cli.getOptionValue(OPTION_SET.migrateFromDateOpt.getOpt());
      String migrateToDateOpt = cli.getOptionValue(OPTION_SET.migrateToDateOpt.getOpt());
      String migrateScript = cli.getOptionValue(OPTION_SET.migrateScriptOpt.getOpt());
      String migrationDir = cli.getOptionValue(OPTION_SET.migrationsDirOpt.getOpt());
      String rootDbName = cli.getOptionValue(OPTION_SET.rootDbNameOpt.getOpt());
      String rootDbUsername = cli.getOptionValue(OPTION_SET.rootDbUsernameDirOpt.getOpt());
      String rootDbPassword = cli.getOptionValue(OPTION_SET.rootDbPasswordOpt.getOpt());

      // set the migration dir
      if (StringUtils.isEmpty(migrationDir)) {
        migrationDir = MigrationService.DEFAULT_MIGRATION_DIR;
      }

      // load all groovy scripts
      GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class);
      List<GroovyScript> allScripts = scriptService.load(migrationDir);

      // stores the scripts to run
      List<GroovyScript> scriptsToRun = new ArrayList<GroovyScript>();

      // the migrateScript option takes precedence so filter on it first
      if (!StringUtils.isEmpty(migrateScript)) {
        for (GroovyScript script : allScripts) {
          if (script.getFilename().equals(migrateScript)) {
            scriptsToRun.add(script);
            break;
          }
        }

        if (scriptsToRun.size() == 0) {
          throw new MigrationException("The migration script was not found: " + migrateScript);
        }

      } else {

        // make sure dates are valid (if provided)
        validateDateOptions(migrateFromDateOpt, migrateToDateOpt);

        DateTime migrateFromDate = null;
        if (!StringUtils.isEmpty(migrateFromDateOpt)) {
          migrateFromDate = new DateTime(migrateFromDateOpt);
        }

        DateTime migrateToDate = null;
        if (!StringUtils.isEmpty(migrateToDateOpt)) {
          migrateToDate = new DateTime(migrateToDateOpt);
        }

        // if the migrate script option isn't defined, then filter on dates
        if (migrateFromDate == null && migrateToDate == null) {
          scriptsToRun.addAll(allScripts);
        } else {
          for (GroovyScript loadedScript : allScripts) {
            DateTime loadedScriptDate = loadedScript.getScriptDate();
            boolean include = false;
            if (migrateFromDate != null && (loadedScriptDate.isEqual(migrateFromDate) || loadedScriptDate.isAfter(migrateFromDate))) {
              include = true;
            }

            if (migrateToDate != null && (loadedScriptDate.isEqual(migrateFromDate) || loadedScriptDate.isBefore(migrateFromDate))) {
              include = true;
            }

            if (include) {
              scriptsToRun.add(loadedScript);
            }
          }
        }
      }

      // set the migration type
      MigrationType migrationType;
      try {
        migrationType = MigrationType.valueOf(migrationTypeOpt);
      } catch (IllegalArgumentException e) {
        throw new MigrationException("Invalid migrationType: " + migrationTypeOpt + ".  The list of valid migration types are:\n" + MigrationType.description());
      }

      // set the database context
      DatabaseService databaseService = injector.getInstance(DatabaseService.class);
      DatabaseContextBuilder dbCtxBuilder = databaseService.getDatabaseContextBuilder();
      dbCtxBuilder.setBaseJdbcContext(baseJdbcUriStr, dbName, username, password).build();
      dbCtxBuilder.setRootJdbcContext(rootDbName, rootDbUsername, rootDbPassword);

      DatabaseContext dbCtx = dbCtxBuilder.build();

      // if a CREATE migraiton is not being execute, then initialize the database
      if (!migrationType.equals(MigrationType.CREATE)) {
        try {
          dbCtx.setConnection(databaseService.connect(dbCtx.getFullyQualifiedJdbcUri(), dbCtx.getUsername(),
            dbCtx.getPassword(), dbCtx.getDatabaseType()));
        } catch (DatabaseException e) {
          throw new MigrationException("Unable to establish a connection to the database for jdbc uri:" +
            baseJdbcUriStr + ", username: " + username + ", password: " + password + ".  Please make sure that " +
            "the database exists and that that the user permissions are set appropriately.", e);
        }
      }

      // initialize project context
      ProjectContext pCtx = new ProjectContext();
      pCtx.setName(projectName);
      pCtx.setVersion(projectVersion);

      // load sql scripts
      SQLScriptService sqlScriptService = injector.getInstance(SQLScriptService.class);
      List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir);

      // initialize the runtime
      MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, pCtx, sqlScripts, migrationType);

      MigrationService<GroovyScript> migrationService = injector.getInstance(GroovyMigrationService.class);

      try {
        migrationService.run(scriptsToRun, migrationRuntime);
      } finally {
        // make sure we disconnect no matter what
        databaseService.disconnect(dbCtx);
      }

    }
  }

  /**
   * Helper method to validate date options
   *
   * @param migrateFromDateOpt the migrate from date
   * @param migrateToDateOpt the migrate to date
   */
  private static void validateDateOptions(String migrateFromDateOpt, String migrateToDateOpt) {
    if (!StringUtils.isEmpty(migrateFromDateOpt)) {
      try {
        new DateTime(migrateFromDateOpt);
      } catch (Exception e) {
        throw new MigrationException(OPTION_SET.migrateFromDateOpt.getArgName() + " value must be in ISO-8601 format: " + OPTION_SET.migrateFromDateOpt.getOpt());
      }
    }

    if (!StringUtils.isEmpty(migrateToDateOpt)) {
      try {
        new DateTime(migrateToDateOpt);
      } catch (Exception e) {
        throw new MigrationException(OPTION_SET.migrateToDateOpt.getArgName() + " value must be in ISO-8601 format: " + OPTION_SET.migrateToDateOpt.getOpt());
      }
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

    Options options = new Options();

    public Option jdbcUrlOpt = new Option("jdbc", "jdbcUrl", true, "The JDBC URL.  This url should not contain the database name. Please provide the database name via the 'databaseName' option.");
    public Option dbNameOpt = new Option("dbn", "databaseName", true, "The database name");
    public Option usernameOpt = new Option("dbu", "dbUsername", true, "The database username");
    public Option passwordOpt = new Option("dbp", "dbPassword", true, "The database password");
    public Option projectNameOpt = new Option("pn", "projectName", true, "The project name");
    public Option projectVersionOpt = new Option("pv", "projectVersion", true, "The project version");
    public Option migrationTypeOpt = new Option("m", "migrationType", true, "The migration type:\n" + MigrationType.description());
    public Option migrateFromDateOpt = new Option("mfd", "migrateFromDate", true, "The date to migrate from.  Must be defined using ISO-8601 format.  If not specified, " +
      "and the migrateScript option is undefined, then Staccato will run the migration starting from the script with the earliest date");
    public Option migrateToDateOpt = new Option("mtd", "migrateToDate", true, "The date to migrate to.  Must be defined using ISO-8601 format.  This option is only interpreted " +
      "if the migrationFromDate is specified. If the migrateFromDate is specified and this field is not specified, then the system will migrate to the current date/time");
    public Option migrateScriptOpt = new Option("ms", "migrateScript", true, "Runs a single script only.  Option must be equal to the name of the script (e.g. ScriptFoo.groovy) " +
      "and the script must be available in the classpath.  If this option is specified, then any values defined for migrateFromDate and migrateToDate will be ignored.");
    public Option migrationsDirOpt = new Option("md", "migrationsDir", true, "The directory where Staccato will search for migration scripts.  " +
      "This directory must be in the classpath.  If not defined, the default is: " + MigrationService.DEFAULT_MIGRATION_DIR);
    public Option rootDbNameOpt = new Option("rdbn", "rootDbName", true, "The name of the root database to use when creating a new database");
    public Option rootDbUsernameDirOpt = new Option("rdbu", "rootDbUsername", true, "The root database username to use when creating a new database");
    public Option rootDbPasswordOpt = new Option("rdbp", "rootDbPassword", true, "The root database password to use when creating a new database");
    ;

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

      migrateFromDateOpt.setRequired(false);
      options.addOption(migrateFromDateOpt);

      migrateToDateOpt.setRequired(false);
      options.addOption(migrateToDateOpt);

      migrateScriptOpt.setRequired(false);
      options.addOption(migrateScriptOpt);

      migrationsDirOpt.setRequired(false);
      options.addOption(migrationsDirOpt);

      rootDbNameOpt.setRequired(false);
      options.addOption(rootDbNameOpt);

      rootDbUsernameDirOpt.setRequired(false);
      options.addOption(rootDbUsernameDirOpt);

      rootDbPasswordOpt.setRequired(false);
      options.addOption(rootDbPasswordOpt);
    }
  }
}