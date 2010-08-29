package com.readytalk.staccato;

import java.sql.Connection;
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
import com.readytalk.staccato.database.migration.MigrationVersionsServiceImpl;
import com.readytalk.staccato.database.migration.ProjectContext;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;
import com.readytalk.staccato.utils.SQLUtils;

/**
 * @author jhumphrey
 */
public class GroovyMigration1IntegrationTest extends BaseTest {

  public static final String migrationDir = "groovy/migration_1";

  @Inject
  public GroovyMigrationService migrationService;

  @Inject
  public DatabaseService dbService;

  @Inject
  public GroovyScriptService groovyScriptService;

  @Inject
  public SQLScriptService sqlScriptService;

  @Test
  public void testMigrationVersionsTableIsCreated() {
    DatabaseContext dbCtx = dbService.buildContext(postgresqlJdbcUri, dbName, dbUsername, dbPassword);

    ProjectContext pCtx = new ProjectContext();
    pCtx.setName("foo");
    pCtx.setVersion("1.0");

    List<SQLScript> sqlScripts = sqlScriptService.load(migrationDir);

    MigrationRuntime migrationRuntime = new MigrationRuntimeImpl(dbCtx, pCtx, sqlScripts, MigrationType.SCHEMA_UP);

    List<GroovyScript> migrationScripts = groovyScriptService.load(migrationDir);
    migrationService.run(migrationScripts, migrationRuntime);

    Connection connection = makePostgresqlConnection();

    try {
      SQLUtils.execute(connection, "select * from " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);
    } catch (SQLException e) {
      Assert.fail("should not have thrown", e);
    }

    // delete the migrations table
    try {
      SQLUtils.execute(makePostgresqlConnection(), "drop table " + MigrationVersionsService.MIGRATION_VERSIONS_TABLE);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}