package com.readytalk.staccato;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationType;

/**
 * @author jhumphrey
 */
public class MainTest extends BaseTest {

  @Test
  public void testRequiredCheck() {

    try {
      String[] args = new String[]{"-n", dbName, "-m", MigrationType.DATA_UP.name()};
      Main.main(args);
      Assert.fail("Should have failed");
    } catch (MigrationException e) {

    }
  }

  @Test
  public void testSuccessful() {

    try {
      String[] args = new String[]{"-j", postgresqlJdbcUri.toString(), "-n", dbName, "-u", dbUser, "-p", dbPwd, "-m", MigrationType.DATA_UP.name()};
      Main.main(args);
    } catch (MigrationException e) {
      Assert.fail("Should not have failed", e);
    }

  }

  @Test
  public void testCreateUnSuccessful() {

    try {
      String[] args = new String[]{"-j", postgresqlJdbcUri.toString(), "-n", dbName, "-u", dbUser, "-p", dbPwd, "-m", MigrationType.CREATE.name()};
      Main.main(args);
      Assert.fail("Should fail because no superuser password is supplied");
    } catch (MigrationException e) {

    }
  }
}
