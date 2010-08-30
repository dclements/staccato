package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.migration.MigrationVersionsService;
import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.utils.SQLUtils;
import com.readytalk.staccato.utils.Version;

/**
 * @author jhumphrey
 */
public class BaseTest {

  public final String dbName = "staccato";
  public final URI mysqlJdbcUri = URI.create("jdbc:mysql://localhost:3306/" + dbName);
  public final URI postgresqlJdbcUri = URI.create("jdbc:postgresql://localhost:5432/" + dbName);
  public final String dbUsername = "staccato";
  public final String dbPassword = "staccato";

  protected Injector injector;

  @BeforeClass()
  public void initGuice() {
    injector = Guice.createInjector(new MigrationModule());
    injector.injectMembers(this);
  }

  public String getDatabaseErrorMessage(URI jdbcUri) {
    return "The JDBC url [" + jdbcUri + "] is not reachable." +
      " This test requires that the database type be " +
      "installed on the system and that a database called '" + dbName + "' " +
      "is created with grants for username '" + dbUsername + "' with password '" + dbPassword + "'.  " +
      "Please refer to the src/test/database directory for sql scripts to " +
      "help with this setup";
  }

  @DataProvider(name = "jdbcProvider")
  public Object[][] jdbcProvider() {
    return new Object[][]{
      {postgresqlJdbcUri},
      {mysqlJdbcUri}
    };
  }

  public Connection makeConnection(URI jdbcUri) {
    DatabaseType dbType = DatabaseType.getTypeFromJDBCUri(jdbcUri);

    try {

      Class.forName(dbType.getDriver());

      String username = dbUsername;
      String password = dbPassword;

      return DriverManager.getConnection(jdbcUri.toString(), username, password);

    } catch (ClassNotFoundException e) {
      Assert.fail(getDatabaseErrorMessage(jdbcUri));
    } catch (SQLException e) {
      Assert.fail(getDatabaseErrorMessage(jdbcUri));
    }

    return null;
  }

  protected void deleteVersionsTable(Connection connection) {
    try {
      Statement st = connection.createStatement();

      st.execute("drop table " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);

    } catch (Exception e) {
      // this should only throw if the table doesnt exist, which is OK
    }
  }

  protected List<GroovyScript> loadScriptsFromVersionsTable(Connection connection) throws SQLException {
    ResultSet rs = SQLUtils.execute(connection, "select script_date, script_filename, script_version, script_hash from " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);
    List<GroovyScript> scripts = new ArrayList<GroovyScript>();
    while (rs.next()) {
      GroovyScript groovyScript = new GroovyScript();
      groovyScript.setScriptDate(new DateTime(rs.getTimestamp(1)));
      groovyScript.setFilename(rs.getString(2));
      groovyScript.setScriptVersion(new Version(rs.getString(3), true));
      groovyScript.setSha1Hash(rs.getString(4));
      scripts.add(groovyScript);
    }

    return scripts;
  }
}
