import com.ecovate.database.migration.annotation.PostUp
import com.ecovate.database.migration.annotation.DataUp
import com.ecovate.database.migration.MigrationRuntime
import com.ecovate.database.migration.annotation.SchemaUp
import com.ecovate.database.migration.annotation.PreUp
import com.ecovate.database.migration.annotation.Migration

@Migration(scriptDate = "2010-08-16T12:32:40.000-06:00", databaseVersion = "1.0")
class Script_20100816T123240_1_0 {

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