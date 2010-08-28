package com.readytalk.staccato.database.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.Version;

import static com.readytalk.staccato.database.migration.MigrationVersionsService.MIGRATION_VERSIONS_TABLE;

/**
 * @author jhumphrey
 */
public class MigrationVersionsServiceImplTest extends BaseTest {

  @Test
  public void testAPI() {

    MigrationVersionsServiceImpl service = new MigrationVersionsServiceImpl();

    DatabaseContext context = new DatabaseContext();
    context.setConnection(makeConnection());
    context.setDatabaseType(DatabaseType.POSTGRESQL);

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
    DateTime expectedScriptDate = new DateTime();
    Version expectedVersion = new Version("1.0.0", true);
    String expectedFilename = "foo.groovy";
    String expectedHash = "hasheesh";
    DynamicLanguageScript script = EasyMock.createMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getScriptDate()).andReturn(expectedScriptDate);
    EasyMock.expect(script.getScriptVersion()).andReturn(expectedVersion);
    EasyMock.expect(script.getFilename()).andReturn(expectedFilename);
    EasyMock.expect(script.getSHA1Hash()).andReturn(expectedHash);
    EasyMock.replay(script);

    service.log(context, script);

    // now delete it
    ResultSet rs;

    // test that it got inserted
    Statement st = null;
    try {
      st = context.getConnection().createStatement();
      rs = st.executeQuery("select script_date, script_filename, script_hash, script_version from " + MIGRATION_VERSIONS_TABLE);

      while (rs.next()) {
        Assert.assertEquals(new DateTime(rs.getTimestamp(1)), expectedScriptDate);
        Assert.assertEquals(rs.getString(2), expectedFilename);
        Assert.assertEquals(rs.getString(3), expectedHash);
        Assert.assertEquals(new Version(rs.getString(4)), expectedVersion);
      }

    } catch (SQLException e) {
      Assert.fail("queries failed, test failed", e);
    }

    try {
      st = context.getConnection().createStatement();

      st.execute("drop table " + MIGRATION_VERSIONS_TABLE);

      Assert.assertFalse(service.versionTableExists(context));

    } catch (Exception e) {
      Assert.fail("failed to delete the " + MIGRATION_VERSIONS_TABLE + " table.", e);
    }
  }
}
