package com.readytalk.staccato;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;

/**
 * @author jhumphrey
 */
public class StaccatoTest extends BaseTest {

  Staccato staccato;

  @Inject
  public void setStaccato(Staccato staccato) {
    this.staccato = staccato;
  }

  @Test
  public void testNoOptions() {
    try {
      StaccatoOptions options = new StaccatoOptions();
      staccato.execute(options);
      Assert.fail("should have thrown an exception because required options aren't set");
    } catch (Exception e) {
      // no-op, test successful
    }
  }

  @Test
  public void testInvalidMigrationType() {
    try {
      StaccatoOptions options = new StaccatoOptions();
      options.jdbcUrl = mysqlJdbcUri.toString();
      options.dbName = dbName;
      options.dbUser = dbUser;
      options.dbPwd = dbPwd;
      staccato.execute(options);
    } catch (MigrationValidationException e) {
      e.printStackTrace();
      // no-op
    }
  }

  @Test
//  @DataProvider(name = "jdbcUrlProvider")
public void testVersionMigrations() {
    StaccatoOptions options = new StaccatoOptions();
    options.jdbcUrl = mysqlJdbcUri.toString();
    options.dbName = dbName;
    options.dbUser = dbUser;
    options.dbPwd = dbPwd;
    options.dbSuperUser = "root";
    options.dbSuperUserPwd = "Pazwurd1";
    options.migrationType = MigrationType.CREATE.name();

    File file = new File("/Users/jhumphrey/dev/inversoft/internal/inversoft.com/target/inversoft-db-migrations.jar");

    options.migrationJarPath = file.getAbsolutePath();
    staccato.execute(options);
  }

//
//  /**
//   * Tests that Main works when using the --ms (migrateScript) option.  This option
//   * runs a migration against a single script only
//   *
//   * @param scriptDate the script date
//   * @param scriptName the script name
//   * @param jdbcUri the jdbc uri
//   * @throws java.sql.SQLException on exception
//   */
//  @Test(dataProvider = "migrateScriptTestOptions")
//  public void testRunWithMigrateScriptOption(String scriptDate, String scriptName, URI jdbcUri) throws SQLException {
//    MigrationType migrationType = MigrationType.DATA_UP;
//    String migrationDir = "groovy/main";
//
//    try {
//      Main.main("-j", jdbcUri.toString(), "-dn", dbName, "-du", dbUser, "-dp", dbPwd,
//        "-m", migrationType.name(), "-md", migrationDir, "-ms", scriptName);
//
//      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable(makeConnection(jdbcUri));
//
//      Assert.assertEquals(loggedScripts.size(), 1);
//      Assert.assertEquals(loggedScripts.get(0).getScriptDate(), new DateTime(scriptDate));
//
//    } catch (MigrationException e) {
//      Assert.fail(e.getMessage(), e);
//    } finally {
//      deleteVersionsTable(makeConnection(jdbcUri));
//    }
//  }
//
//  /**
//   * Tests that Main works when using the --ms (migrateScript) option.  This option
//   * runs a migration against a single script only
//   *
//   * @param jdbcUri the jdbc uri
//   * @throws java.sql.SQLException on exception
//   */
//  @Test(dataProvider = "jdbcProvider")
//  public void testRunWithDateRangeOptionsVariations(URI jdbcUri) throws SQLException {
//    MigrationType migrationType = MigrationType.DATA_UP;
//    String migrationDir = "groovy/main";
//
//    // all should run in this test since
//    try {
//      Main.main("-jdbc", jdbcUri.toString(), "-dbn", dbName, "-dbu", dbUser, "-dbp", dbPwd,
//        "-m", migrationType.name(), "-md", migrationDir, "-mfd", "2000-01-01");
//
//      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable(makeConnection(jdbcUri));
//
//      Assert.assertEquals(loggedScripts.size(), 10);
//
//    } catch (MigrationException e) {
//      Assert.fail(e.getMessage(), e);
//    } finally {
//      deleteVersionsTable(makeConnection(jdbcUri));
//    }
//
//    try {
//      Main.main("-jdbc", jdbcUri.toString(), "-dbn", dbName, "-dbu", dbUser, "-dbp", dbPwd,
//        "-m", migrationType.name(), "-md", migrationDir, "-mfd", "2005-01-01T00:00:00-06:00");
//
//      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable(makeConnection(jdbcUri));
//
//      Assert.assertEquals(loggedScripts.size(), 6);
//
//    } catch (MigrationException e) {
//      Assert.fail(e.getMessage(), e);
//    } finally {
//      deleteVersionsTable(makeConnection(jdbcUri));
//    }
//
//    try {
//      Main.main("-jdbc", jdbcUri.toString(), "-dbn", dbName, "-dbu", dbUser, "-dbp", dbPwd,
//        "-m", migrationType.name(), "-md", migrationDir, "-mfd", "2011-01-01");
//
//      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable(makeConnection(jdbcUri));
//
//      Assert.assertEquals(loggedScripts.size(), 0);
//
//    } catch (MigrationException e) {
//      Assert.fail(e.getMessage(), e);
//    } finally {
//      deleteVersionsTable(makeConnection(jdbcUri));
//    }
//  }

  @DataProvider(name = "migrateScriptTestOptions")
  public Object[][] migrateScriptTestOptions() {
    return new Object[][]{
      {"2001-01-01T00:00:00-06:00", "Script_1.groovy", postgresqlJdbcUri},
      {"2002-01-01T00:00:00-06:00", "Script_2.groovy", postgresqlJdbcUri},
      {"2003-01-01T00:00:00-06:00", "Script_1.groovy", mysqlJdbcUri},
      {"2004-01-01T00:00:00-06:00", "Script_2.groovy", mysqlJdbcUri}
    };
  }
}
