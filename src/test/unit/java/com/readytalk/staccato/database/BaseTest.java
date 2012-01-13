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
import com.readytalk.staccato.database.migration.MigrationLoggingService;
import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.utils.SQLUtils;

/**
 * @author jhumphrey
 */
public class BaseTest {

  public final URI mysqlJdbcUri = URI.create("jdbc:mysql://localhost:3306/");
  public final URI postgresqlJdbcUri = URI.create("jdbc:postgresql://localhost:5432/");
  public final String rootDbName = "staccato_root";
  public final String dbName = "staccato";
  public final String dbUser = "staccato";
  public final String dbPwd = "staccato";
  public final String dbSuperUser = dbUser;
  public final String dbSuperUserPwd = dbPwd;

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
      "is created with grants for username '" + dbUser + "' with password '" + dbPwd + "'.  " +
      "Please refer to the src/test/database directory for sql scripts to " +
      "help with this setup";
  }

  public Connection makeConnection(URI jdbcUri) {
    DatabaseType dbType = DatabaseType.getTypeFromJDBCUri(jdbcUri);

    try {

      Class.forName(dbType.getDriver());

      String username = dbUser;
      String password = dbPwd;

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

      st.execute("drop table " + MigrationLoggingService.MIGRATION_VERSIONS_TABLE);

    } catch (Exception e) {
      // this should only throw if the table doesnt exist, which is OK
    }
  }

  protected List<GroovyScript> loadScriptsFromVersionsTable(Connection connection) throws SQLException {

    List<GroovyScript> scripts = new ArrayList<GroovyScript>();

    try {
      ResultSet rs;
      rs = SQLUtils.execute(connection, "select script_date, script_filename, script_hash from " + MigrationLoggingService.MIGRATION_VERSIONS_TABLE);
      while (rs.next()) {
        GroovyScript groovyScript = new GroovyScript();
        groovyScript.setScriptDate(new DateTime(rs.getTimestamp(1)));
        groovyScript.setFilename(rs.getString(2));
        groovyScript.setSha1Hash(rs.getString(3));
        scripts.add(groovyScript);
      }
    } catch (SQLException e) {
      // exceptions are ok here
    }

    return scripts;
  }

  @DataProvider(name = "jdbcProvider")
  public Object[][] jdbcProvider() {
    return new Object[][]{
      {postgresqlJdbcUri}
    };
  }

  @DataProvider(name = "fullyQualifiedJdbcProvider")
  public Object[][] fullyQualifedJdbcProvider() {
    return new Object[][]{
      {URI.create(postgresqlJdbcUri.toString() + dbName)}
    };
  }
}
