package groovy.main

import com.readytalk.staccato.database.migration.annotation.DataUp
import com.readytalk.staccato.database.migration.annotation.Migration

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
 */
@Migration(
scriptDate = "2004-01-01T00:00:00-06:00",
databaseVersion = "1.0.0-SNAPSHOT",
scriptVersion = "1.0.0")
class Script_4 {

  @DataUp
  dataUp() {
  }
}