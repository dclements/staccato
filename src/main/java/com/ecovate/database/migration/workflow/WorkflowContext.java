package com.ecovate.database.migration.workflow;

import com.ecovate.database.migration.MigrationRuntime;
import com.ecovate.database.migration.annotation.MigrationAnnotationParser;

/**
 * Context for a given workflow
 *
 * @author jhumphrey
 */
public class WorkflowContext {
  private MigrationAnnotationParser annotationParser;
  private MigrationRuntime migrationRumtime;

  public WorkflowContext(MigrationAnnotationParser annotationParser, MigrationRuntime migrationRuntime) {
    this.annotationParser = annotationParser;
    this.migrationRumtime = migrationRuntime;
  }

  public MigrationAnnotationParser getAnnotationParser() {
    return annotationParser;
  }

  public MigrationRuntime getMigrationRuntime() {
    return migrationRumtime;
  }
}
