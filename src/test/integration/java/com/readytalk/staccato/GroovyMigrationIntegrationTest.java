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
import com.readytalk.staccato.database.migration.MigrationLoggingService;
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
  public MigrationLoggingService loggingService;

  @Test(dataProvider = "jdbcProvider")
  public void testMigrationVersionLoggingWithPostgres(URI baseJdbcUri) throws SQLException {
    DatabaseContext dbCtx = dbService.getDatabaseContextBuilder().setContext(baseJdbcUri.toString(), dbName, dbUser, dbPwd, dbSuperUser, dbSuperUserPwd, rootDbName).build();
    dbCtx.setConnection(dbService.connect(dbCtx.getFullyQualifiedJdbcUri(), dbCtx.getUsername(), dbCtx.getPassword(), dbCtx.getDatabaseType()));

    List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir, this.getClass().getClassLoader());

    MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, sqlScripts, MigrationType.SCHEMA_UP, true);

    List<GroovyScript> migrationScripts = groovyScriptService.load(migrationDir, this.getClass().getClassLoader());
    migrationService.run(migrationScripts, migrationRuntime);

    Assert.assertTrue(loggingService.versionTableExists(dbCtx));

    // make sure script execution was logged
    ResultSet rs = SQLUtils.execute(dbCtx.getConnection(), "select * from " + MigrationLoggingService.MIGRATION_VERSIONS_TABLE);

    Assert.assertNotNull(rs);

    SQLUtils.execute(dbCtx.getConnection(), "drop table " + MigrationLoggingService.MIGRATION_VERSIONS_TABLE);
  }
}