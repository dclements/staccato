package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.easymock.EasyMock;
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
import com.readytalk.staccato.utils.Version;

import static com.readytalk.staccato.database.migration.MigrationVersionsService.MIGRATION_VERSIONS_TABLE;

/**
 * @author jhumphrey
 */
public class MigrationVersionsServiceImplTest extends BaseTest {

  @Test(dataProvider = "fullyQualifiedJdbcProvider")
  public void testAPI(URI jdbcUri) {

    MigrationVersionsServiceImpl service = new MigrationVersionsServiceImpl();

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

    DynamicLanguageScript script = EasyMock.createMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getScriptDate()).andReturn(expectedScriptDate);
    EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
    EasyMock.expect(script.getSHA1Hash()).andReturn(expectedHash);
    EasyMock.replay(script);

    Migration migrationAnnotation = EasyMock.createStrictMock(Migration.class);
    EasyMock.expect(migrationAnnotation.databaseVersion()).andReturn(expectedDatabaseVersion);
    EasyMock.replay(migrationAnnotation);

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
