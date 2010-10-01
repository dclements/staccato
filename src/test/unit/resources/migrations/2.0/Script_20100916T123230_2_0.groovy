import com.readytalk.staccato.database.migration.MigrationRuntime
import com.readytalk.staccato.database.migration.annotation.DataUp
import com.readytalk.staccato.database.migration.annotation.Migration
import com.readytalk.staccato.database.migration.annotation.PostUp
import com.readytalk.staccato.database.migration.annotation.PreUp
import com.readytalk.staccato.database.migration.annotation.SchemaUp

@Migration(
scriptDate = "2010-09-16T12:32:30-06:00",
databaseVersion = "2.0",
scriptVersion = "1.0.0")
class Script_20100916T123230_2_0 {

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