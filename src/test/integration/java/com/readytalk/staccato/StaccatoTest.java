package com.readytalk.staccato;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;

/**
 * @author jhumphrey
 */
public class StaccatoTest extends BaseTest {
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
