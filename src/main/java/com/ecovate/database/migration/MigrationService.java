package com.ecovate.database.migration;

import com.ecovate.database.DatabaseContext;
import com.google.inject.ImplementedBy;

/**
 * @author jhumphrey
 */
@ImplementedBy(GroovyMigrationService.class)
public interface MigrationService {

  /**
   * Runs a migration
   *
   * @param databaseContext the database context to run the migration under
   * @param projectContext the project context
   * @param migrationType the migration type
   */
  void run(DatabaseContext databaseContext, ProjectContext projectContext, MigrationType migrationType);
}
