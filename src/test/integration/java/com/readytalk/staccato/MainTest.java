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
   * Tests that Main works when using the --ms (migrateScript) option.  This option
   * runs a migration against a single script only
   *
   * @param scriptDate the script date
   * @param scriptName the script name
   * @param jdbcUri the jdbc uri
   * @throws java.sql.SQLException on exception
   */
  @Test(dataProvider = "loadMainOptions")
  public void testRunWithMigrateScriptOption(String scriptDate, String scriptName, URI jdbcUri) throws SQLException {
    MigrationType migrationType = MigrationType.DATA_UP;
    String migrationDir = "groovy/main_1";

    try {
      Main.main("-jdbc", jdbcUri.toString(), "-dbn", dbName, "-dbu", dbUsername, "-dbp", dbPassword,
        "-m", migrationType.name(), "-md", migrationDir, "-ms", scriptName);

      List<GroovyScript> loggedScripts = loadScriptsFromVersionsTable(makeConnection(jdbcUri));

      Assert.assertEquals(loggedScripts.size(), 1);
      Assert.assertEquals(loggedScripts.get(0).getScriptDate(), new DateTime(scriptDate));

    } catch (MigrationException e) {
      Assert.fail(e.getMessage(), e);
    } finally {
      deleteVersionsTable(makeConnection(jdbcUri));
    }
  }

  @DataProvider(name = "loadMainOptions")
  public Object[][] loadMainOptions() {
    return new Object[][]{
      {"2000-01-01T00:00:00-06:00", "Script_1.groovy", postgresqlJdbcUri},
      {"2001-01-01T00:00:00-06:00", "Script_2.groovy", postgresqlJdbcUri},
      {"2001-01-01T00:00:00-06:00", "Script_1.groovy", mysqlJdbcUri},
      {"2001-01-01T00:00:00-06:00", "Script_2.groovy", mysqlJdbcUri},
    };
  }
}
