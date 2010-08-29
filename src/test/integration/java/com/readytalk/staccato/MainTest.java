package com.readytalk.staccato;

import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;

/**
 * @author jhumphrey
 */
public class MainTest extends BaseTest {

  @Test
  public void testHelpWithArg() {
    try {
      Main.main("help");
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void testHelpWithOutArg() {
    try {
      Main.main();
    } catch (Exception e) {
      Assert.fail();
    }
  }

  /**
   * Tests that only Script_1 gets executed
   * @throws java.sql.SQLException on exception
   */
  @Test
  public void testRunScript1Postgresql() throws SQLException {
    MigrationType migrationType = MigrationType.DATA_UP;
    String migrationDir = "groovy/main_1";
    String scriptDate = "2010-08-29T15:38:01.454-06:00"; // this is defined in the Migration.scriptDate annotation descriptor
    String scriptName = "Script_1.groovy";

    try {
      Main.main("-jdbc", postgresqlJdbcUri.toString(), "-dbn", dbName, "-dbu", dbUsername, "-dbp", dbPassword,
        "-m", migrationType.name(), "-md", migrationDir, "-ms", scriptName);

      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable();

      Assert.assertEquals(loggedScripts.size(), 1);
      Assert.assertEquals(loggedScripts.get(0).getScriptDate(), new DateTime(scriptDate));

    } catch (MigrationException e) {
      Assert.fail(e.getMessage(), e);
    } finally {
      deleteVersionsTable(makePostgresqlConnection());
    }
  }

  /**
   * Tests that only Script_2 gets executed
   *
   * @throws java.sql.SQLException on exception
   */
  @Test
  public void testRunScript2Postgresql() throws SQLException {
    MigrationType migrationType = MigrationType.DATA_UP;
    String migrationDir = "groovy/main_1";
    String scriptDate = "2010-08-29T15:39:40.261-06:00"; // this is defined in the Migration.scriptDate annotation descriptor
    String scriptName = "Script_2.groovy";

    try {
      Main.main("-jdbc", postgresqlJdbcUri.toString(), "-dbn", dbName, "-dbu", dbUsername, "-dbp", dbPassword,
        "-m", migrationType.name(), "-md", migrationDir, "-ms", scriptName);

      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable();

      Assert.assertEquals(loggedScripts.size(), 1);
      Assert.assertEquals(loggedScripts.get(0).getScriptDate(), new DateTime(scriptDate));

    } catch (MigrationException e) {
      Assert.fail(e.getMessage(), e);
    } finally {
      deleteVersionsTable(makePostgresqlConnection());
    }
  }
}
