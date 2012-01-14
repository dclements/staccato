package com.readytalk.staccato;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

public class Staccato {

	private final Logger logger = Logger.getLogger(Staccato.class);

	private final DynamicLanguageScriptService<GroovyScript> groovyScriptService;
	private final ScriptService<SQLScript> sqlScriptService;
	private final MigrationValidator validator;
	private final DatabaseService databaseService;
	private final MigrationService<GroovyScript> migrationService;

	@Inject
	public Staccato(DatabaseService databaseService, DynamicLanguageScriptService<GroovyScript> groovyScriptService,
			MigrationService<GroovyScript> migrationService, ScriptService<SQLScript> sqlScriptService, MigrationValidator validator) {
		this.databaseService = databaseService;
		this.groovyScriptService = groovyScriptService;
		this.migrationService = migrationService;
		this.sqlScriptService = sqlScriptService;
		this.validator = validator;
	}

	/**
	 * Executes Staccato via the {@link com.readytalk.staccato.StaccatoOptions} provided
	 *
	 * @param options
	 */
	public void execute(StaccatoOptions options) {

		// set the migration dir to the default if not defined by user.  This has to
		// be done prior to validating the staccato option set since it's a required field
		if (StringUtils.isEmpty(options.migrationsDir)) {
			options.migrationsDir = MigrationService.DEFAULT_MIGRATIONS_DIR;
		}

		// validate options
		validator.validate(options);

		// get the migration type from the migrationType string option
		MigrationType migrationType = MigrationType.valueOf(options.migrationType);

		// set the classloader.  If they specified a path to the migration jar, then load that jar into a classloader
		ClassLoader cl = this.getClass().getClassLoader();
		if (!StringUtils.isEmpty(options.migrationJarPath)) {
			File file = new File(options.migrationJarPath);
			if (!file.exists()) {
				throw new MigrationException("Migration jar path [" + options.migrationJarPath + "] does not exist");
			}
			try {
				cl = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
			} catch (MalformedURLException e) {
				throw new MigrationException(e);
			}
		}

		// load all groovy scripts
		List<GroovyScript> allScripts = groovyScriptService.load(options.migrationsDir, cl);

		// stores the scripts to run
		List<GroovyScript> scriptsToRun = determineScriptsToRun(allScripts, options);

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
		} else if (options.dbSuperUserPwd == null) {
			throw new MigrationException("Database superuser password is required when executing a " + MigrationType.CREATE);
		}

		// load sql scripts
		List<SQLScript> sqlScripts = sqlScriptService.load(options.migrationsDir, cl);

		// initialize the runtime.
		// This runtime object eventually gets passed to the scripts themselves as a method argument
		MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, sqlScripts, migrationType, options.enableLogging);

		try {
			migrationService.run(scriptsToRun, migrationRuntime);
		} finally {
			// make sure we disconnect no matter what
			databaseService.disconnect(dbCtx);
		}
	}

	private List<GroovyScript> determineScriptsToRun(List<GroovyScript> allScripts, StaccatoOptions options) {
		List<GroovyScript> scriptsToRun = new ArrayList<GroovyScript>();

		/**
		 * The following if-block processes based on the following criteria:
		 *
		 * 1.  if the migrateScript option is defined, then then this option takes precedence
		 *      and a migration is run on just that script
		 *
		 * 2.  If the from/to dates are defined, then a migration is executed on the date range
		 * 3.  Finally, if the from/to version is defined, a migration is executed on the version range
		 *
		 */
		if (!StringUtils.isEmpty(options.migrateScript)) {

			logger.info("Running a migration for script: " + options.migrateScript);

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

			if (fromDate != null && toDate == null) {
				logger.info("Running a migration for all scripts with script dates equal to or after: " + options.migrateFromDate);
			} else if (fromDate == null && toDate != null) {
				logger.info("Running a migration for all scripts with script dates equal to or before: " + options.migrateToDate);
			} else {
				logger.info("Running a migration for all scripts in the date range: " + options.migrateFromDate + " - " + options.migrateToDate);
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

			if (fromVer != null && toVer == null) {
				logger.info("Running a migration for all scripts with database versions equal to or greater than: " + options.migrateFromVer);
			} else if (fromVer == null && toVer != null) {
				logger.info("Running a migration for all scripts with database versions equal to or less than : " + options.migrateToVer);
			} else {
				logger.info("Running a migration for all scripts in the version range: " + options.migrateFromVer + " - " + options.migrateToVer);
			}

			scriptsToRun = groovyScriptService.filterByDatabaseVersion(allScripts, fromVer, toVer);
		} else {
			logger.info("Running a migration on all scripts");
			scriptsToRun.addAll(allScripts);
		}

		return scriptsToRun;
	}
}
