package com.readytalk.staccato;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseContextBuilder;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.MigrationRuntimeImpl;
import com.readytalk.staccato.database.migration.MigrationService;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScriptService;
import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;
import com.readytalk.staccato.utils.Version;

/**
 * @author jhumphrey
 */
public class Staccato {

  DynamicLanguageScriptService<GroovyScript> groovyScriptService;
  ScriptService<SQLScript> sqlScriptService;
  MigrationValidator validator;
  DatabaseService databaseService;
  MigrationService<GroovyScript> migrationService;

  @Inject
  public Staccato(DatabaseService databaseService, DynamicLanguageScriptService<GroovyScript> groovyScriptService,
    MigrationService<GroovyScript> migrationService, ScriptService<SQLScript> sqlScriptService, MigrationValidator validator) {
    this.databaseService = databaseService;
    this.groovyScriptService = groovyScriptService;
    this.migrationService = migrationService;
    this.sqlScriptService = sqlScriptService;
    this.validator = validator;
  }

  public void execute(StaccatoOptions options) {

    // validate the options
    validator.validate(options);

    // validate the migration type.  Not easy to do it via jsr 303 so doing it here
    MigrationType migrationType;
    try {
      migrationType = MigrationType.valueOf(options.migrationType);
    } catch (IllegalArgumentException e) {
      throw new MigrationException("Invalid migrationType: " + options.migrationType + ".  The list of valid migration types are:\n" + MigrationType.description());
    }

    // set the migration dir
    if (StringUtils.isEmpty(options.migrationsDir)) {
      options.migrationsDir = MigrationService.DEFAULT_MIGRATIONS_DIR;
    }

    // load all groovy scripts
    List<GroovyScript> allScripts = groovyScriptService.load(options.migrationsDir, this.getClass().getClassLoader());

    // stores the scripts to run
    List<GroovyScript> scriptsToRun = new ArrayList<GroovyScript>();

    // process the migrateScript, dates and versions to filter the script list
    if (!StringUtils.isEmpty(options.migrateScript)) {
      for (GroovyScript script : allScripts) {
        if (script.getFilename().equals(options.migrateScript)) {
          scriptsToRun.add(script);
          break;
        }
      }

      if (scriptsToRun.size() == 0) {
        throw new MigrationException("The migration script was not found: " + options.migrateScript);
      }

    } else if (!StringUtils.isEmpty(options.migrateFromDate) || !StringUtils.isEmpty(options.migrateToDate)) {

      DateTime fromDate = null;
      if (!StringUtils.isEmpty(options.migrateFromDate)) {
        fromDate = new DateTime(fromDate);
      }

      DateTime toDate = null;
      if (!StringUtils.isEmpty(options.migrateToDate)) {
        toDate = new DateTime(toDate);
      }

      scriptsToRun = groovyScriptService.filterByDate(allScripts, fromDate, toDate);
    } else if (!StringUtils.isEmpty(options.migrateFromVer) || !StringUtils.isEmpty(options.migrateToVer)) {

      Version fromVer = null;
      if (!StringUtils.isEmpty(options.migrateFromVer)) {
        fromVer = new Version(options.migrateFromVer, Migration.databaseVersionStrictMode);
      }

      Version toVer = null;
      if (!StringUtils.isEmpty(options.migrateToVer)) {
        toVer = new Version(options.migrateToVer, Migration.databaseVersionStrictMode);
      }

      groovyScriptService.filterByDatabaseVersion(allScripts, fromVer, toVer);
    } else {
      scriptsToRun.addAll(allScripts);
    }

    // set the database context
    DatabaseContextBuilder dbCtxBuilder = databaseService.getDatabaseContextBuilder();
    DatabaseContext dbCtx = dbCtxBuilder.setContext(options.jdbcUrl, options.dbName, options.dbUser, options.dbPwd,
      options.dbSuperUser, options.dbSuperUserPwd, options.rootDb).build();

    // if a CREATE migraiton is not being executed, then initialize the database
    if (!migrationType.equals(MigrationType.CREATE)) {
      try {
        dbCtx.setConnection(databaseService.connect(dbCtx.getFullyQualifiedJdbcUri(), dbCtx.getUsername(), dbCtx.getPassword(), dbCtx.getDatabaseType()));
      } catch (DatabaseException e) {
        throw new MigrationException("Unable to establish a connection to the database for jdbc uri:" +
          options.jdbcUrl + ", user: " + options.dbUser + ", pwd: " + options.dbPwd + ".  Please make sure that " +
          "the database exists and that that the user permissions are set appropriately.", e);
      }
    } else if (StringUtils.isEmpty(options.dbSuperUserPwd)) {
        throw new MigrationException("Database superuser password is required when executing a " + MigrationType.CREATE);
    }

    // load sql scripts
    List<SQLScript> sqlScripts = sqlScriptService.load(options.migrationsDir, this.getClass().getClassLoader());

    // initialize the runtime
    MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, sqlScripts, migrationType);

    try {
      migrationService.run(scriptsToRun, migrationRuntime);
    } finally {
      // make sure we disconnect no matter what
      databaseService.disconnect(dbCtx);
    }
  }
}
