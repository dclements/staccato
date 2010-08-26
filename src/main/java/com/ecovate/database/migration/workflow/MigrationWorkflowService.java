package com.ecovate.database.migration.workflow;

import java.lang.annotation.Annotation;

import com.ecovate.database.migration.MigrationResult;
import com.ecovate.database.migration.MigrationRuntime;
import com.ecovate.database.migration.script.DynamicLanguageScript;
import com.google.inject.ImplementedBy;

/**
 * Interface for executing migrations
 *
 * @author jhumphrey
 */
@ImplementedBy(MigrationWorkflowServiceImpl.class)
public interface MigrationWorkflowService {

  /**
   * Executes a migration workflow
   *
   * @param script a dynamic language script
   * @param migrationWorkflow the migration type
   * @param migrationRuntime the migration runtime
   * @return the {@link com.ecovate.database.migration.MigrationResult}
   */
  MigrationResult executeWorkflow(DynamicLanguageScript script, Class<? extends Annotation>[] migrationWorkflow, MigrationRuntime migrationRuntime);
}
