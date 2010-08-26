
import com.ecovate.database.migration.annotation.DataUp
import com.ecovate.database.migration.annotation.PostUp
import com.ecovate.database.migration.annotation.SchemaUp

/**
 * @author jhumphrey
 */
class BadTestScript {

  @DataUp
  dataUp() {
  }

  // bad script because there are 2 DataUp annotations
  @DataUp
  badDataUp() {
  }

  @PostUp
  postup() {
  }

  @SchemaUp
  schemaUp() {
  }
}