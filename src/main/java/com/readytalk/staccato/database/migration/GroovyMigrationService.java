package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.readytalk.staccato.database.migration.script.sql.SQLScriptService;
import com.readytalk.staccato.database.migration.workflow.MigrationWorkflowService;

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

    logger.info("Running " + databaseContext.getDatabaseType() + " migration: " + migrationType.name() + ", " + workflowOutput);

    // load scripts.  These all have been validated and ordered by the script date
    List<GroovyScript> scripts = scriptService.load();

    // establish the connection to the database
    try {
      databaseService.connect(databaseContext);
    } catch (DatabaseException e) {
      logger.severe(e.getMessage());
      throw new MigrationException("Unable to establish a connection to the database for jdbc uri:" +
        databaseContext.getJdbcUri() + ", username: " + databaseContext.getUsername() + ", password: " +
        databaseContext.getPassword() + ".  Please make sure that the database exists and that that the " +
        "user permissions are set appropriately.", e);
    }

    // create the migration runtime.  This can be passed to a migration workflow method as a method arg
    MigrationRuntimeImpl migrationRuntime = new MigrationRuntimeImpl(databaseContext, projectContext, sqlScriptService.load());

    // iterate through the groovy scripts for invocation
    for (GroovyScript script : scripts) {
      logger.info("Executing script: " + script.getFilename());

      Migration migrationAnnotation = annotationParser.getMigrationAnnotation(script.getScriptInstance());

      // print the description to the logs if it's not null
      String description = getDescription(migrationAnnotation);
      if (getDescription(migrationAnnotation) != null) {
        logger.info("Description: " + description);
      }

      // validate database type
      if (!isValidDatabaseType(script, migrationAnnotation, databaseContext)) {
        continue;
      }

      migrationWorkflowService.executeWorkflow(script, migrationType.getWorkflowSteps(), migrationRuntime);
    }

    databaseService.disconnect(databaseContext);
  }

  /**
   * Helper method to get the description from the migration annotation
   *
   * @param migrationAnnotation the migration annotation
   * @return the description
   */
  String getDescription(Migration migrationAnnotation) {
    try {
      return migrationAnnotation.description();
    } catch (IncompleteAnnotationException e) {
      return null;
    }
  }

  /**
   * Helper method to validate that the script we're executing belongs to this database migration.
   * If the Migration annotation database type is not equal to the
   * {@link com.readytalk.staccato.database.DatabaseContext} database type then do not execute
   * this script
   *
   * @param script the script
   * @param migrationAnnotation the migration annotation
   * @param databaseContext the database context
   * @return true if valid, false otherwise
   */
  boolean isValidDatabaseType(GroovyScript script, Migration migrationAnnotation, DatabaseContext databaseContext) {
    try {
      DatabaseType scriptDatabaseType = migrationAnnotation.databaseType();
      if (scriptDatabaseType != null && scriptDatabaseType != databaseContext.getDatabaseType()) {
        logger.fine("Excluding " + scriptDatabaseType + " script from execution: " + script.getFilename());
        return false;
      }
      return true;
    } catch (Exception e) {
      return true;
    }
  }
}
