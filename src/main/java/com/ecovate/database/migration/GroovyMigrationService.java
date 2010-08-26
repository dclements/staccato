package com.ecovate.database.migration;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Logger;

import com.ecovate.database.DatabaseContext;
import com.ecovate.database.DatabaseException;
import com.ecovate.database.DatabaseService;
import com.ecovate.database.migration.annotation.Migration;
import com.ecovate.database.migration.annotation.MigrationAnnotationParser;
import com.ecovate.database.migration.script.groovy.GroovyScript;
import com.ecovate.database.migration.script.groovy.GroovyScriptService;
import com.ecovate.database.migration.script.sql.SQLScriptService;
import com.ecovate.database.migration.workflow.MigrationWorkflowService;
import com.google.inject.Inject;

/**
 * @author jhumphrey
 */
public class GroovyMigrationService implements MigrationService {

  Logger logger = Logger.getLogger(this.getClass().getName());

  private GroovyScriptService scriptService;
  private MigrationWorkflowService migrationWorkflowService;
  private SQLScriptService sqlScriptService;
  private DatabaseService databaseService;
  private MigrationAnnotationParser annotationParser;

  @Inject
  public GroovyMigrationService(GroovyScriptService scriptService, MigrationWorkflowService migrationWorkflowService,
    SQLScriptService sqlScriptService, DatabaseService databaseService, MigrationAnnotationParser annotationParser) {
    this.scriptService = scriptService;
    this.migrationWorkflowService = migrationWorkflowService;
    this.sqlScriptService = sqlScriptService;
    this.databaseService = databaseService;
    this.annotationParser = annotationParser;
  }

  @Override
  public void run(DatabaseContext databaseContext, ProjectContext projectContext, MigrationType migrationType) {

    String workflowOutput = "workflow: ";
    for (Class<? extends Annotation> aClass : migrationType.getWorkflowSteps()) {
      workflowOutput += aClass.getSimpleName() + " ";
    }

    logger.info("Running migration: " + migrationType.name() + ", " + workflowOutput);

    // load scripts.  These all have been validated and ordered by the script date
    List<GroovyScript> scripts = scriptService.load();

    // establish the connection to the database
    try {
      databaseService.connect(databaseContext);
    } catch (DatabaseException e) {
      logger.severe(e.getMessage());
      throw new MigrationException(e);
    }

    // create the migration runtime.  This can be passed to a migration workflow method as a method arg
    MigrationRuntimeImpl migrationRuntime = new MigrationRuntimeImpl(databaseContext, projectContext, sqlScriptService.load());

    // iterate through the groovy scripts for invocation
    for (GroovyScript script : scripts) {
      logger.info("Executing script: " + script.getFilename());

      // print description if it's there
      Migration migrationAnnotation = annotationParser.getMigrationAnnotation(script.getScriptInstance());

      try {
        String description = migrationAnnotation.description();
        if (description != null && !description.equals("")) {
          logger.info("Description: " + description);
        }
      } catch (Exception e) {
        // no worries here, the Migration.description annotation is not required
        // so exceptions are ok
      }

      migrationWorkflowService.executeWorkflow(script, migrationType.getWorkflowSteps(), migrationRuntime);
    }
  }
}
