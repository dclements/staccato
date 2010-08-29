
import com.readytalk.staccato.database.migration.annotation.DataUp
import com.readytalk.staccato.database.migration.annotation.PostUp
import com.readytalk.staccato.database.migration.annotation.SchemaUp

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