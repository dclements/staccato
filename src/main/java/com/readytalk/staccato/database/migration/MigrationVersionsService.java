package com.readytalk.staccato.database.migration;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

/**
 * @author jhumphrey
 */
@ImplementedBy(MigrationVersionsServiceImpl.class)
public interface MigrationVersionsService {

  public static final String MIGRATION_VERSIONS_TABLE = "STACCATO_MIGRATIONS";

  /**
   * Creates the migration versions table
   *
   * @param context the database context
   */
  public void createVersionsTable(DatabaseContext context);

  /**
   * Returns true if the migration versions table exists
   *
   * @param context the database context
   * @return true if exists, false otherwise
   */
  public boolean versionTableExists(DatabaseContext context);

  /**
   * Logs the script to the migrations versions table
   *
   * @param datbaseContext the datbase context
   * @param script the script
   */
  void log(DatabaseContext datbaseContext, DynamicLanguageScript script);
}
