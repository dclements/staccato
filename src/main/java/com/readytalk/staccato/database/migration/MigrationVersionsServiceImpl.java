package com.readytalk.staccato.database.migration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.SQLUtils;

/**
 * @author jhumphrey
 */
public class MigrationVersionsServiceImpl implements MigrationVersionsService {

  /**
   * {@inheritDoc}
   */
  @Override
  public void createVersionsTable(DatabaseContext context) {

    if (!versionTableExists(context)) {

      URL url = null;
      String sqlFile = null;

      switch (context.getDatabaseType()) {
        case MYSQL:
          sqlFile = "mysql-staccato-migrations.sql";
          url = this.getClass().getClassLoader().getResource(sqlFile);
          break;
        case POSTGRESQL:
          sqlFile = "postgresql-staccato-migrations.sql";
          url = this.getClass().getClassLoader().getResource(sqlFile);
          break;
      }

      if (url == null) {
        throw new DatabaseException("Unable to create the " + MIGRATION_VERSIONS_TABLE + ".  Cannot locate the sql file: " + sqlFile);
      }

      try {
        SQLUtils.executeSQLFile(context.getConnection(), url);
      } catch (SQLException e) {
        throw new DatabaseException("Unable to execute mysql script: " + url.toExternalForm(), e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean versionTableExists(DatabaseContext context) {

    try {
      if (context.getConnection().isClosed()) {
        throw new DatabaseException("Connection is closed to: " + context.getJdbcUri());
      }
    } catch (SQLException e) {
      throw new DatabaseException("Unable to determine connection status for: " + context.getJdbcUri(), e);
    }

    Connection conn = context.getConnection();
    try {
      ResultSet rs = SQLUtils.execute(conn, "select * from " + MIGRATION_VERSIONS_TABLE);
      rs.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public void log(DatabaseContext databaseContext, DynamicLanguageScript script) {
    // create the migration versions table
    createVersionsTable(databaseContext);

    try {
      String filename = script.getFilename();
      String date = script.getScriptDate().toString();
      String hash = script.getSHA1Hash();
      String version = script.getScriptVersion().toString();

      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("INSERT INTO ").append(MIGRATION_VERSIONS_TABLE).append(" ");
      sqlBuilder.append("(script_date, script_version, script_hash, script_filename)").append(" ");
      sqlBuilder.append("  values('").append(date).append("', '").append(version).append("', '").append(hash).append("', '").append(filename).append("')");
      SQLUtils.execute(databaseContext.getConnection(), sqlBuilder.toString());
    } catch (SQLException e) {
      throw new MigrationException("Unable to execute query", e);
    }

  }
}
