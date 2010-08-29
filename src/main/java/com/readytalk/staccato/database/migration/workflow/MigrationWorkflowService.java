package com.readytalk.staccato.database.migration.workflow;

import java.lang.annotation.Annotation;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.MigrationResult;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

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
   * @return the {@link com.readytalk.staccato.database.migration.MigrationResult}
   */
  MigrationResult executeWorkflow(DynamicLanguageScript script, Class<? extends Annotation>[] migrationWorkflow, MigrationRuntime migrationRuntime);
}
