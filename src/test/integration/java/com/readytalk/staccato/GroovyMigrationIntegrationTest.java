package com.readytalk.staccato;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.migration.GroovyMigrationService;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.MigrationRuntimeImpl;
import com.readytalk.staccato.database.migration.MigrationType;
import com.readytalk.staccato.database.migration.MigrationVersionsService;
import com.readytalk.staccato.database.migration.ProjectContext;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;
import com.readytalk.staccato.utils.SQLUtils;

/**
 * @author jhumphrey
 */
public class GroovyMigrationIntegrationTest extends BaseTest {

  public static final String migrationDir = "groovy/migration";

  @Inject
  public GroovyMigrationService migrationService;

  @Inject
  public DatabaseService dbService;

  @Inject
  public GroovyScriptService groovyScriptService;

  @Inject
  public SQLScriptService sqlScriptService;

  @Inject
  public MigrationVersionsService versionsService;

  @Test(dataProvider = "jdbcProvider")
  public void testMigrationVersionLoggingWithPostgres(URI baseJdbcUri) throws SQLException {
    DatabaseContext dbCtx = dbService.getDatabaseContextBuilder().setBaseJdbcContext(baseJdbcUri.toString(), dbName, dbUsername, dbPassword).build();
    dbCtx.setConnection(dbService.connect(dbCtx.getFullyQualifiedJdbcUri(), dbCtx.getUsername(), dbCtx.getPassword(), dbCtx.getDatabaseType()));

    ProjectContext pCtx = new ProjectContext();
    pCtx.setName("foo");
    pCtx.setVersion("1.0");

    List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir);

    MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, pCtx, sqlScripts, MigrationType.SCHEMA_UP);

    List<GroovyScript> migrationScripts = groovyScriptService.load(migrationDir);
    migrationService.run(migrationScripts, migrationRuntime);

    Assert.assertTrue(versionsService.versionTableExists(dbCtx));

    // make sure script execution was logged
    ResultSet rs = SQLUtils.execute(dbCtx.getConnection(), "select * from " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);

    Assert.assertNotNull(rs);

    SQLUtils.execute(dbCtx.getConnection(), "drop table " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);
  }
}