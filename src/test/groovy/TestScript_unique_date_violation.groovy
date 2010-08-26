import com.ecovate.database.migration.annotation.DataUp
import com.ecovate.database.migration.annotation.PostUp
import com.ecovate.database.migration.annotation.PreUp
import com.ecovate.database.migration.annotation.SchemaUp
import com.ecovate.database.migration.annotation.Migration

/**
 * @author jhumphrey
 */
@Migration(scriptDate = "2010-08-17T07:00:00-06:00", databaseVersion = "1.0", description = "A test script")
class TestScript_unique_date_violation {

  @DataUp
  void dataUp() {

  }

  @PostUp
  void postUp() {

  }

  @SchemaUp
  void schemaUp() {

  }

  @PreUp
  void preUp() {

  }
}