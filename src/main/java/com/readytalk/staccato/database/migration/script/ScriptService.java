package com.readytalk.staccato.database.migration.script;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;

/**
 * Interface for interacting with Script objects
 *
 * @author jhumphrey
 */
@ImplementedBy(SQLScriptService.class)
public interface ScriptService<T extends Script> {

  /**
   * Loads scripts into a list.  The expectation is that index 0 of the list
   * is the first script to load when running a migration
   *
   * @param migrationDir the migration directory to load the script from
   * @param classLoader the class loader
   * @return a set of {@link com.readytalk.staccato.database.migration.script.groovy.GroovyScript} objects
   */
  List<T> load(String migrationDir, ClassLoader classLoader);

  /**
   * Returns the filename extension of the script
   *
   * @return the script filename extension
   */
  String getScriptFileExtension();
}
