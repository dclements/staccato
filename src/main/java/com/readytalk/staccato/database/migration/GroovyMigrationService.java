package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.logging.Level;
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
  private MigrationVersionsService migrationVersionsService;

  @Inject
  public GroovyMigrationService(GroovyScriptService scriptService, MigrationWorkflowService migrationWorkflowService,
    SQLScriptService sqlScriptService, DatabaseService databaseService, MigrationAnnotationParser annotationParser,
    MigrationVersionsService migrationVersionsService) {
    this.scriptService = scriptService;
    this.migrationWorkflowService = migrationWorkflowService;
    this.sqlScriptService = sqlScriptService;
    this.databaseService = databaseService;
    this.annotationParser = annotationParser;
    this.migrationVersionsService = migrationVersionsService;
  }

  @Override
  public void run(DatabaseContext databaseContext, ProjectContext projectContext, MigrationType migrationType) {

    try {
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
        //TODO:  Design for the workflow when the database does not yet exist
        //
        // Currently, the system won't work unless the database has been created prior to migration execution
        //
        // How to design to this?
        //
        // Possible ideas:
        //  1. create a new workflow called CREATE that is similar to UP but has one additional step called at the beginning called @Create.
        //     The idea here is that there would be a script method annotated with @Create that would contain the logic necessary to create the database from scratch.
        //
        //      cons:  Since only one @Create would be necessary, this annotation doesn't really follow the same pattern
        //             as all the other workflow step annotations.  In other words, there wouldn't be ONE-TO-MANY of these throughout the script set.
        //             Kinda hacky...  Maybe add an attribute called 'unique'?  This doesn't really solve the problem but makes it contextually better..maybe?
        //
        //             ex:
        //
        //             @Create(unique=true)
        //             void createDatabase()
        //
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

        try {
          // make sure that we put all script workflow execution in a transaction
          databaseService.startTransaction(databaseContext);

          migrationWorkflowService.executeWorkflow(script, migrationType.getWorkflowSteps(), migrationRuntime);

          // if execute is successful, log to the migration versions table
          migrationVersionsService.log(databaseContext, script);

          // end the transaction.  This commits all sql queries
          databaseService.endTransaction(databaseContext);
        } catch (Exception e) {
          logger.log(Level.SEVERE, "An unexpected error occurred during migration execution of script: " + script.getFilename() + ".  Rolling back migration", e);

          try {
            databaseService.rollback(databaseContext);
          } catch (DatabaseException de) {
            logger.log(Level.SEVERE, "Unable to rollback transaction", de.getCause());
          }

        }
      }
    } finally {
      databaseService.disconnect(databaseContext);
    }
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
