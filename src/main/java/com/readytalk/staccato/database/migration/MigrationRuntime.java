package com.readytalk.staccato.database.migration;

import java.sql.ResultSet;

import com.readytalk.staccato.database.DatabaseContext;

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
