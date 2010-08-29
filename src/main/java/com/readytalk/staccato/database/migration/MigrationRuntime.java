package com.readytalk.staccato.database.migration;

import java.sql.ResultSet;
import java.util.List;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;

/**
 * @author jhumphrey
 */
public interface MigrationRuntime {

  /**
   * Returns the {@link com.readytalk.staccato.database.DatabaseContext}
   *
   * @return the database context
   */
  DatabaseContext getDatabaseContext();

  /**
   * Returns the {@link ProjectContext}
   *
   * @return the project contedt
   */
  ProjectContext getProjectContext();

  /**
   * Returns the migration type for this runtime
   *
   * @return the migration type
   */
  MigrationType getMigrationType();

  /**
   * The list of sql scripts for the migration runtime
   *
   * @return list of sql scripts
   */
  List<SQLScript> sqlScripts();

  /**
   * Executes SQL contained in a string:
   *
   * e.g.
   *
   * String sql = "select * from foo";
   * migrationRuntime.executeSQL(sql);
   *
   * @param sql the sql string (e.g. select * from foo)
   * @return the result set
   */
  ResultSet executeSQL(String sql);

  /**
   * Executes sql located in a file.  The filename is specified via the filename param
   *
   * @param filename the name of the file to execute sql from
   * @return the result set
   */
  ResultSet executeSQLFile(String filename);
}
