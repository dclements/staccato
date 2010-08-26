
import com.readytalk.staccato.database.migration.annotation.DataUp
import com.readytalk.staccato.database.migration.annotation.PostUp
import com.readytalk.staccato.database.migration.annotation.PreUp
import com.readytalk.staccato.database.migration.annotation.SchemaUp
import com.readytalk.staccato.database.migration.annotation.Migration
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepOne
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepTwo
import com.readytalk.staccato.database.migration.MigrationRuntime

/**
 * @author jhumphrey
 */
@Migration
class TestScript {


  void testExecuteSQL(MigrationRuntime runtime) {

  }

  @TestWorkflowStepOne("foo")
  String testMethodOne(MigrationRuntime runtime) {

    return "bar";
  }

  @TestWorkflowStepTwo
  testMethodTwo() {

  }

  @DataUp
  dataUp() {
  }

  @PostUp
  postUp() {
  }

  @SchemaUp
  schemaUp() {
  }

  @PreUp
  preUp() {
  }
}