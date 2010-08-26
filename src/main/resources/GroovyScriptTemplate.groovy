package migrations

import com.ecovate.database.migration.annotation.DataUp
import com.ecovate.database.migration.annotation.Migration
import com.ecovate.database.migration.annotation.PostUp
import com.ecovate.database.migration.annotation.PreUp
import com.ecovate.database.migration.annotation.SchemaUp
import com.ecovate.database.migration.MigrationRuntime

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
 * The migration projectVersion is for informational purposes
 * only and is used for associating the migration to a contextual
 * version of the database.  This is a required field
 *
 * Migration.description:
 * Non-required field that explains what the script is doing.
 * This field is for informational purposes only but does
 * get outputted to the log file if defined.
 *
 * @author USER
 * @version 1.0
 */
@Migration(scriptDate = "DATE", databaseVersion = "DATABASE_VERSION")
class GroovyScriptTemplate {

  @PreUp
  preUp() {
  }

  @SchemaUp
  schemaUp(MigrationRuntime runtime) {
  }

  @DataUp
  dataUp() {
  }

  @PostUp
  postUp() {
  }
}