
import com.ecovate.database.migration.annotation.DataUp
import com.ecovate.database.migration.annotation.PostUp
import com.ecovate.database.migration.annotation.PreUp
import com.ecovate.database.migration.annotation.SchemaUp
import com.ecovate.database.migration.annotation.Migration
import com.ecovate.database.migration.annotation.TestWorkflowStepOne
import com.ecovate.database.migration.annotation.TestWorkflowStepTwo
import com.ecovate.database.migration.MigrationRuntime

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