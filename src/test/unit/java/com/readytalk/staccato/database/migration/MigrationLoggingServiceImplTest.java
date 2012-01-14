package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.PreUp;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;

import static com.readytalk.staccato.database.migration.MigrationLoggingService.MIGRATION_VERSIONS_TABLE;

/**
 * @author jhumphrey
 */
public class MigrationLoggingServiceImplTest extends BaseTest {

  @Test(dataProvider = "fullyQualifiedJdbcProvider")
  public void testAPI(URI jdbcUri) {

    MigrationLoggingServiceImpl service = new MigrationLoggingServiceImpl();

    DatabaseContext context = new DatabaseContext();
    context.setConnection(makeConnection(jdbcUri));
    context.setDatabaseType(DatabaseType.getTypeFromJDBCUri(jdbcUri));

    if (service.versionTableExists(context)) {
      try {
        Statement st = context.getConnection().createStatement();
        st.execute("drop table " + MIGRATION_VERSIONS_TABLE);
      } catch (Exception e) {
        Assert.fail("failed to delete the " + MIGRATION_VERSIONS_TABLE + " table.", e);
      }
    }

    // first test that the migration versions table isn't there
    Assert.assertFalse(service.versionTableExists(context));

    // now create it
    service.createVersionsTable(context);

    // now assert it's there
    Assert.assertTrue(service.versionTableExists(context));

    // insert a row to it:
    String expectedScriptDateStr = DateTimeFormat.forPattern(GroovyScriptService.TEMPLATE_SCRIPT_DATE_FORMAT).print(new DateTime());
    DateTime expectedScriptDate = new DateTime(expectedScriptDateStr);
    String expectedFilename = "foo.groovy";
    String expectedHash = "hasheesh";
    String expectedDatabaseVersion = "1.0";
    Class<? extends Annotation> expectedWorkflowStep = PreUp.class;

    DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
    
    when(script.getScriptDate()).thenReturn(expectedScriptDate);
    when(script.getFilename()).thenReturn(expectedFilename);
    when(script.getSHA1Hash()).thenReturn(expectedHash);

    Migration migrationAnnotation = mock(Migration.class);
    when(migrationAnnotation.databaseVersion()).thenReturn(expectedDatabaseVersion);

    // test with preup
    service.log(context, script, expectedWorkflowStep, migrationAnnotation);

    // now delete it
    ResultSet rs;

    // test that it got inserted
    Statement st;
    try {
      st = context.getConnection().createStatement();
      rs = st.executeQuery("select script_date, script_filename, script_hash, workflow_step, database_version from " + MIGRATION_VERSIONS_TABLE);

      while (rs.next()) {
        Assert.assertEquals(new DateTime(rs.getTimestamp(1)), expectedScriptDate);
        Assert.assertEquals(rs.getString(2), expectedFilename);
        Assert.assertEquals(rs.getString(3), expectedHash);
        Assert.assertEquals(rs.getString(4), expectedWorkflowStep.getSimpleName());
        Assert.assertEquals(rs.getString(5), expectedDatabaseVersion);
      }

    } catch (SQLException e) {
      Assert.fail("queries failed, test failed", e);
    }

    deleteVersionsTable(context.getConnection());
  }
}
