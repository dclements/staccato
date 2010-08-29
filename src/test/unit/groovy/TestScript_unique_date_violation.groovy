import com.readytalk.staccato.database.migration.annotation.DataUp
import com.readytalk.staccato.database.migration.annotation.PostUp
import com.readytalk.staccato.database.migration.annotation.PreUp
import com.readytalk.staccato.database.migration.annotation.SchemaUp
import com.readytalk.staccato.database.migration.annotation.Migration

/**
 * @author jhumphrey
 */
@Migration(
scriptDate = "2010-08-17T07:00:00-06:00",
databaseVersion = "1.0",
description = "A test script",
scriptVersion = "1.0.0")
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