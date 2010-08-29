package com.readytalk.staccato.database.migration.workflow;

import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

/**
 * Context for a given workflow
 *
 * @author jhumphrey
 */
public class WorkflowContext {
  private MigrationAnnotationParser annotationParser;
  private MigrationRuntime migrationRuntime;

  public WorkflowContext(MigrationAnnotationParser annotationParser, MigrationRuntime migrationRuntime) {
    this.annotationParser = annotationParser;
    this.migrationRuntime = migrationRuntime;
  }

  public MigrationAnnotationParser getAnnotationParser() {
    return annotationParser;
  }

  public MigrationRuntime getMigrationRuntime() {
    return migrationRuntime;
  }
}
