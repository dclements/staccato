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

  public static final String MIGRATION_DIR = "migrations/";

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
}
