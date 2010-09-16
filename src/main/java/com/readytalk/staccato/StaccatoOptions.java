package com.readytalk.staccato;

import javax.validation.constraints.NotNull;

import com.readytalk.staccato.database.migration.MigrationService;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.javax.DateFormat;
import com.readytalk.staccato.database.migration.validation.javax.Version;

/**
 * @author jhumphrey
 */
public class StaccatoOptions {

  @NotNull
  public String jdbcUrl;

  @NotNull
  public String dbName;

  @NotNull
  public String dbUser;

  @NotNull
  public String dbPwd;

  @NotNull
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

  @Version(strictMode = Migration.databaseVersionStrictMode)
  public String migrateFromVer;

  @Version(strictMode = Migration.databaseVersionStrictMode)
  public String migrateToVer;

  public static enum Arg {
    JDBC_URL("j", "The JDBC URL.  This url should not contain the database name. Please provide the database name via the 'dn' option.", true),
    DB_NAME("dn", "The database name", true),
    DB_USER("du", "The database user", true),
    DB_PWD("dp", "The database password", true),
    MIGRATION_TYPE("m", "The migration type:\n" + MigrationType.description(), true),
    MIGRATE_FROM_DATE("mfd", "The date to migrate from.  Must be defined using the ISO-8601 format:  " +
      "If not specified, and the migrateScript option is undefined, then Staccato will run the migration starting from the script with the earliest date", false),
    MIGRATE_TO_DATE("mtd", "The date to migrate to.  Must be defined using ISO-8601 format.  This option is only interpreted " +
      "if the migrationFromDate is specified. If the migrateFromDate is specified and this field is not specified, then the system will migrate to the current date/time", false),
    MIGRATE_SCRIPT("ms", "Runs a single migration script only.  Option must be equal to the name of the script (e.g. ScriptFoo.groovy) " +
      "and the script must be available in the classpath.  If this option is specified, then any values defined for migrateFromDate and migrateToDate will be ignored.", false),
    MIGRATIONS_DIR("md", "The directory where Staccato will search for migration scripts.  " +
      "This directory must be in the classpath.  If not defined, the default is: " + MigrationService.DEFAULT_MIGRATIONS_DIR, false),
    ROOT_DB("drn", "The root database name.  Defaults to user 'postgres' or 'mysql' if not specified", false),
    DB_SUPERUSER("dsu", "The superuser to use when creating a new database.  Defaults to user 'postgres' or 'mysql' if not specified", false),
    DB_SUPERUSER_PWD("dsup", "The superuser password to use when creating a new database.", false),
    MIGRATE_FROM_VER("mfv", "The version to migrate from", false),
    MIGRATE_TO_VER("mtv", "The version to migrate to", false);

    String opt;

    String desc;

    boolean required;

    Arg(String opt, String desc, boolean required) {
      this.opt = opt;
      this.desc = desc;
      this.required = required;
    }

    public String getOpt() {
      return opt;
    }

    public String getDesc() {
      return desc;
    }

    public boolean isRequired() {
      return required;
    }
  }
}
