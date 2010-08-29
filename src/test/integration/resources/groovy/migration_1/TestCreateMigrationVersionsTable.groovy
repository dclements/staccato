package groovy.migration_1

import com.readytalk.staccato.database.migration.MigrationRuntime
import com.readytalk.staccato.database.migration.annotation.Migration
import com.readytalk.staccato.database.migration.annotation.SchemaUp

/**
 * Represents a groovy migration script.
 *
 * Migration.scriptDate:
 * The migration script date is used to sort the
 * script for execution. This is a required field
 * and must be unique across the entire set of migration
 * scripts
 *
 * Migration.databaseVersion:
 * This attribute is required and is used for performing incremental
 * upwards or downwards migrations
 *
 * Migration.scriptVersion:
 * This field is used to perform API version compatibility checking prior to script execution.
 * If your annotated value doesn't equal the current template version bundled in
 * the jar, then the system will not execute the script and throw an exception.
 *
 * Migration.description:
 * Optional attribute that provides information about script execution.
 * This field is for informational purposes only but does
 * get outputted to the log file when defined.
 *
 * Migration.databaseType:
 * Optional field that informs the system which database type the script belongs to.
 * If undefined, the system will assume to queue the script for execution.
 *
 * @author jhumphrey
 */
@Migration(
scriptDate = "2010-08-28T18:46:33-06:00",
databaseVersion = "1.0.0-SNAPSHOT",
description = "Simple script for testing that the migration versions table gets created succesfully in both mysql and postgresql",
scriptVersion = "1.0.0")
class TestCreateMigrationVersionsTable {

  @SchemaUp
  schemaUp(MigrationRuntime runtime) {
  }
}