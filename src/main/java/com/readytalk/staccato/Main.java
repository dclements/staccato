package com.readytalk.staccato;

import java.net.URI;
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
      String migrateFromDateOpt = line.getOptionValue(OPTION_SET.migrateFromDateOpt.getOpt());
      String migrateToDateOpt = line.getOptionValue(OPTION_SET.migrateToDateOpt.getOpt());
      String migrateScript = line.getOptionValue(OPTION_SET.migrateScriptOpt.getOpt());
      String migrationDir = line.getOptionValue(OPTION_SET.migrationsDirOpt.getOpt());

      // set the migration dir
      if (StringUtils.isEmpty(migrationDir)) {
        migrationDir = MigrationService.DEFAULT_MIGRATION_DIR;
      }

      // check the migrate to and from dates
      DateTime migrateFromDate = null;
      if (migrateFromDateOpt != null && !migrateFromDateOpt.equals("")) {
        try {
          migrateFromDate = new DateTime(migrateFromDateOpt);
        } catch (Exception e) {
          throw new MigrationException(OPTION_SET.migrateFromDateOpt.getArgName() + " value must be in ISO-8601 format: " + OPTION_SET.migrateFromDateOpt.getOpt());
        }
      }

      DateTime migrateToDate = null;
      if (migrateToDateOpt != null && !migrateToDateOpt.equals("")) {
        try {
          migrateToDate = new DateTime(migrateToDateOpt);
        } catch (Exception e) {
          throw new MigrationException(OPTION_SET.migrateToDateOpt.getArgName() + " value must be in ISO-8601 format: " + OPTION_SET.migrateToDateOpt.getOpt());
        }
      }

      // load groovy scripts
      GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class);
      List<GroovyScript> loadedScripts = scriptService.load(migrationDir);

      // stores the scripts to run
      List<GroovyScript> scriptsToRun = new ArrayList<GroovyScript>();

      // the migrateScript option takes precedence so filter on it first
      if (!StringUtils.isEmpty(migrateScript)) {
        for (GroovyScript script : loadedScripts) {
          if (script.getFilename().equals(migrateScript)) {
            scriptsToRun.add(script);
            break;
          }
        }

        if (scriptsToRun.size() == 0) {
          throw new MigrationException("The script to migrate was not found: " + migrateScript);
        }

      } else {

        // if the migrate script option isn't defined, then filter on dates
        if (migrateFromDate == null && migrateToDate == null) {
          scriptsToRun.addAll(loadedScripts);
        } else {
          for (GroovyScript loadedScript : loadedScripts) {
            DateTime loadedScriptDate = loadedScript.getScriptDate();
            boolean include = false;
            if (migrateFromDate != null && loadedScriptDate.isAfter(migrateFromDate)) {
              include = true;
            }

            if (migrateToDate != null && loadedScriptDate.isBefore(migrateFromDate)) {
              include = true;
            }

            if (include) {
              scriptsToRun.add(loadedScript);
            }
          }
        }
      }

      // initialize database context
      DatabaseService databaseService = injector.getInstance(DatabaseService.class);
      DatabaseContext dbCtx;
      try {
        dbCtx = databaseService.initialize(URI.create(jdbcUri), dbName, username, password);
      } catch (DatabaseException e) {
        //TODO:  Design for the workflow when the database does not yet exist
        //
        // Currently, the system won't work unless the database has been created prior to migration execution
        //
        // How to design to this?
        //
        // Possible ideas:
        //  1. create a new workflow called CREATE that is similar to UP but has one additional step called at the beginning called @Create.
        //     The idea here is that there would be a script method annotated with @Create that would contain the logic necessary to create the database from scratch.
        //
        //      cons:  Since only one @Create would be necessary, this annotation doesn't really follow the same pattern
        //             as all the other workflow step annotations.  In other words, there wouldn't be ONE-TO-MANY of these throughout the script set.
        //             Kinda hacky...  Maybe add an attribute called 'unique'?  This doesn't really solve the problem but makes it contextually better..maybe?
        //
        //             ex:
        //
        //             @Create(unique=true)
        //             void createDatabase()
        //
        throw new MigrationException("Unable to establish a connection to the database for jdbc uri:" +
          jdbcUri + ", username: " + username + ", password: " + password + ".  Please make sure that " +
          "the database exists and that that the user permissions are set appropriately.", e);
      }

      // initialize project context
      ProjectContext pCtx = new ProjectContext();
      pCtx.setName(projectName);
      pCtx.setVersion(projectVersion);

      // load sql scripts
      SQLScriptService sqlScriptService = injector.getInstance(SQLScriptService.class);
      List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir);

      // initialize the runtime
      MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, pCtx, sqlScripts, MigrationType.valueOf(migrationType));

      MigrationService<GroovyScript> migrationService = injector.getInstance(GroovyMigrationService.class);

      try {
        migrationService.run(scriptsToRun, migrationRuntime);
      } finally {
        // make sure we disconnect no matter what
        databaseService.disconnect(dbCtx);
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

    public Option jdbcUrlOpt = new Option("jdbc", "jdbcUrl", true, "JDBC URL");
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
    }
  }
}