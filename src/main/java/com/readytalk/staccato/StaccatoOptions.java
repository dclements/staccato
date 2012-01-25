package com.readytalk.staccato;

import javax.validation.constraints.NotNull;

import com.readytalk.staccato.database.migration.MigrationService;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.javax.DateFormat;
import com.readytalk.staccato.database.migration.validation.javax.MigrationTypeConstraint;
import com.readytalk.staccato.database.migration.validation.javax.Version;

public class StaccatoOptions {

	@NotNull
	public String jdbcUrl;

	@NotNull
	public String dbName;

	@NotNull
	public String dbUser;

	@NotNull
	public String dbPwd;

	@MigrationTypeConstraint
	public String migrationType;

	@DateFormat
	public String migrateFromDate;

	@DateFormat
	public String migrateToDate;

	public String migrateScript;

	@NotNull
	public String migrationsDir;

	public String dbSuperUser;

	public String dbSuperUserPwd;

	public String rootDb;

	public String migrationJarPath;

	@Version(strictMode = Migration.databaseVersionStrictMode)
	public String migrateFromVer;

	@Version(strictMode = Migration.databaseVersionStrictMode)
	public String migrateToVer;

	public boolean enableLogging = true;

	public static enum Arg {
		JDBC_URL("j", "jdbc", "The JDBC URL.  This url should not contain the database name. Please provide the database name via the 'n' option.", true),
		DB_NAME("n", "dbName", "The name of the database migrations are being run on", true),
		DB_USER("u", "dbUser", "The database user used when connecting to the database", true),
		DB_PWD("p", "dbPwd", "The database password used when connecting to the database", true),
		MIGRATION_TYPE("m", "migration", "The migration type:\n" + MigrationType.description(), true),
		MIGRATE_FROM_DATE("fd", "fromDate", "The date to migrate from.  Must be defined using the ISO-8601 format:  " +
			"If not specified, and the migrateScript option is undefined, then Staccato will run the migration starting from the script with the earliest date", false),
		MIGRATE_TO_DATE("td", "toDate", "The date to migrate to.  Must be defined using ISO-8601 format.  This option is only interpreted " +
			"if the migrationFromDate is specified. If the migrateFromDate is specified and this field is not specified, then the system will migrate to the current date/time", false),
		MIGRATE_SCRIPT("s", "script", "The script to run the migration on.  Must be equal to the name of the script (e.g. ScriptFoo.groovy) " +
			"and the script must be available in the classpath.  If this option is specified, then any values defined for fromDate and toDate will be ignored.", false),
		MIGRATIONS_DIR("d", "directory", "The directory where Staccato will search for migration scripts.  " +
			"This directory must be in the classpath.  If not defined, the default is: " + MigrationService.DEFAULT_MIGRATIONS_DIR, false),
		ROOT_DB("rn", "rootDbName", "The root database name.  Defaults to user 'postgres' or 'mysql' if not specified", false),
		DB_SUPERUSER("su", "dbSuperUser", "The superuser to use when creating a new database.  Defaults to user 'postgres' or 'mysql' if not specified", false),
		DB_SUPERUSER_PWD("sup", "dbSuperPwd", "The superuser password to use when creating a new database.", false),
		MIGRATE_FROM_VER("fv", "fromVersion", "The version to migrate from", false),
		MIGRATE_TO_VER("tv", "toVersion", "The version to migrate to", false),
		MIGRATION_JAR_PATH("mj", "migrationJar", "The path to the migration jar", false),
		LOGGING("l", "logging", "Toggles the migration logging system, which logs script " +
			"execution to the 'staccato_migrations' table in the database.  Set to 'false' if you don't " +
			"want Staccato to create a 'staccato_migrations' table in your database.  Defaults to 'true' if anything other than 'false' is specified.", false);

		private String opt;
		private String longOpt;
		private String desc;
		private boolean required;

		Arg(final String _opt, final String _longOpt, final String _desc, final boolean _required) {
			this.opt = _opt;
			this.longOpt = _longOpt;
			this.desc = _desc;
			this.required = _required;
		}

		public String getOpt() {
			return opt;
		}

		public String getDesc() {
			return desc;
		}

		public String getLongOpt() {
			return longOpt;
		}

		public boolean isRequired() {
			return required;
		}
	}
}
