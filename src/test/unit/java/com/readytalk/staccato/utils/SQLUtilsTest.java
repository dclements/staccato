package com.readytalk.staccato.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;

/**
 * @author jhumphrey
 */
public class SQLUtilsTest extends BaseTest {

  @Test
  public void testExecute() throws SQLException {

    ResultSet rs = SQLUtils.execute(makePostgresqlConnection(), "select * from foo");

    while (rs.next()) {
      Assert.assertEquals(rs.getInt(1), 1);
      Assert.assertEquals(rs.getString(2), "baz");
    }
  }

  @Test
  public void testExecuteFile() throws MalformedURLException, SQLException {
    File file = new File("src/test/unit/resources/test.sql");
    ResultSet rs = SQLUtils.executeSQLFile(makePostgresqlConnection(), file.toURI().toURL());

    while (rs.next()) {
      Assert.assertEquals(rs.getInt(1), 1);
      Assert.assertEquals(rs.getString(2), "baz");
    }
  }
}
