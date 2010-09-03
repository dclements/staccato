package com.readytalk.staccato.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;

/**
 * @author jhumphrey
 */
public class SQLUtilsTest extends BaseTest {

  @Test(dataProvider = "fullyQualifiedJdbcProvider")
  public void testExecute(URI jdbcUri) throws SQLException {

    ResultSet rs = SQLUtils.execute(makeConnection(jdbcUri), "select * from foo");

    while (rs.next()) {
      Assert.assertEquals(rs.getInt(1), 1);
      Assert.assertEquals(rs.getString(2), "baz");
    }
  }

  @Test(dataProvider = "fullyQualifiedJdbcProvider")
  public void testExecuteFile(URI jdbcUri) throws MalformedURLException, SQLException {
    File file = new File("src/test/unit/resources/test.sql");
    ResultSet rs = SQLUtils.executeSQLFile(makeConnection(jdbcUri), file.toURI().toURL());

    while (rs.next()) {
      Assert.assertEquals(rs.getInt(1), 1);
      Assert.assertEquals(rs.getString(2), "baz");
    }
  }
}
