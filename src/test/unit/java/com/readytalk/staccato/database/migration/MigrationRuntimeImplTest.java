package com.readytalk.staccato.database.migration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.DatabaseServiceImpl;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;

/**
 * @author jhumphrey
 */
public class MigrationRuntimeImplTest {

  @Test
  public void testExecuteSQL() throws SQLException {

//    DatabaseContext dbContext = makeDbContext();
//
//    ProjectContext pContext = new ProjectContext();
//
//    List<SQLScript> sqlScripts = new ArrayList<SQLScript>();
//
//    MigrationRuntime runtime = new MigrationRuntimeImpl(dbContext, pContext, sqlScripts);
//
//    ResultSet rs = runtime.executeSQL("select * from foo");
//
//    while (rs.next()) {
//      Assert.assertEquals(rs.getInt(1), 1);
//      Assert.assertEquals(rs.getString(2), "baz");
//    }

  }

  @Test
  public void testExecuteSQLFile() throws SQLException, MalformedURLException {

//    DatabaseContext dbContext = makeDbContext();
//
//    ProjectContext pContext = new ProjectContext();
//
//    File testSQL = new File("src/test/sql/test.sql");
//
//    SQLScript sqlScript = new SQLScript();
//    sqlScript.setFilename(testSQL.getName());
//    sqlScript.setUrl(testSQL.toURI().toURL());
//
//    List<SQLScript> sqlScripts = new ArrayList<SQLScript>();
//    sqlScripts.add(sqlScript);
//
//    MigrationRuntime runtime = new MigrationRuntimeImpl(dbContext, pContext, sqlScripts);
//
//    ResultSet rs = runtime.executeSQLFile("test.sql");
//
//    while (rs.next()) {
//      Assert.assertEquals(rs.getInt(1), 1);
//      Assert.assertEquals(rs.getString(2), "baz");
//    }
  }
}
