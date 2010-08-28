package com.readytalk.staccato.database.migration.script;

import java.util.List;

import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;
import com.google.inject.ImplementedBy;

/**
 * Interface for interacting with Script objects
 *
 * @author jhumphrey
 */
@ImplementedBy(SQLScriptService.class)
public interface ScriptService<T extends Script> {

  public static final String DEFAULT_MIGRATION_DIR = "migrations/";

  /**
   * Loads scripts into a list.  The expectation is that index 0 of the list
   * is the first script to load when running a migration
   *
   * @return a set of {@link com.readytalk.staccato.database.migration.script.groovy.GroovyScript} objects
   */
  List<T> load();

  /**
   * Returns the filename extension of the script
   *
   * @return the script filename extension
   */
  String getScriptFileExtension();

  /**
   * Used to set the directory where all migration scripts are stored.  By default,
   * this directory is set to 'migrations/'
   *
   * @param migrationDir the migrations directory
   */
  void setMigrationDir(String migrationDir);

  /**
   * The migrations directory.  This is the directory where all migration scripts are stored.
   * By default, this directory is set to 'migrations/'
   *
   * @return the migrations directory
   */
  String getMigrationDir();
}
