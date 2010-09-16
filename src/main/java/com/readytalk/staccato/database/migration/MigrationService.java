package com.readytalk.staccato.database.migration;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

/**
 * @author jhumphrey
 */
@ImplementedBy(GroovyMigrationService.class)
public interface MigrationService<T extends DynamicLanguageScript> {

  public static final String DEFAULT_MIGRATIONS_DIR = "migrations/";

  /**
   * Runs a migration
   *
   * @param migrationScripts the migration scripts to run
   * @param migrationRuntime the migration runtime
   */
  void run(List<T> migrationScripts, MigrationRuntime migrationRuntime);
}
