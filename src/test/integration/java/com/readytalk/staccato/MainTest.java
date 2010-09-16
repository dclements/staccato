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

  /*
 [ Options: [ short {
 mtv=[ option: mtv  :: The version to migrate to ],
 du=[ option: du  :: The database user ],
 dsup=[ option: dsup  :: The superuser password to use when creating a new database. ],
 mfd=[ option: mfd  :: The date to migrate from.  Must be defined using the ISO-8601 format:  If not specified, and the migrateScript option is undefined, then Staccato will run the migration starting from the script with the earliest date ],
 ms=[ option: ms  :: Runs a single migration script only.  Option must be equal to the name of the script (e.g. ScriptFoo.groovy) and the script must be available in the classpath.  If this option is specified, then any values defined for migrateFromDate and migrateToDate will be ignored. ],
 drn=[ option: drn  :: The root database name.  Defaults to user 'postgres' or 'mysql' if not specified ],
 dsu=[ option: dsu  :: The superuser to use when creating a new database.  Defaults to user 'postgres' or 'mysql' if not specified ],
 m=[ option: m  :: The migration type:
     SCHEMA_UP - Executes migration workflow: SchemaUp
     SCHEMA_DATA_UP - Executes migration workflow: SchemaUp DataUp
     DATA_UP - Executes migration workflow: DataUp
     PRE_UP - Executes migration workflow: PreUp
     POST_UP - Executes migration workflow: PostUp
     UP - Executes migration workflow: PreUp SchemaUp DataUp PostUp
     CREATE - Executes migration workflow: Create],
 j=[ option: j  :: The JDBC URL.  This url should not contain the database name. Please provide the database name via the 'dn' option. ],
 mtd=[ option: mtd  :: The date to migrate to.  Must be defined using ISO-8601 format.  This option is only interpreted if the migrationFromDate is specified. If the migrateFromDate is specified and this field is not specified, then the system will migrate to the current date/time ],
 mfv=[ option: mfv  :: The version to migrate from ],
 md=[ option: md  :: The directory where Staccato will search for migration scripts.  This directory must be in the classpath.  If not defined, the default is: migrations/ ],
 dn=[ option: dn  :: The database name ],
 dp=[ option: dp  :: The database password ]} ] [ long {} ]
  */

  @Test
  public void testRequiredCheck() {

    try {
      String[] args = new String[]{"-dn", dbName, "-m", MigrationType.DATA_UP.name()};
      Main.main(args);
      Assert.fail("Should have failed");
    } catch (MigrationException e) {

    }
  }

  @Test
  public void testSuccessful() {

    try {
      String[] args = new String[]{"-j", postgresqlJdbcUri.toString(), "-dn", dbName, "-du", dbUser, "-dp", dbPwd, "-m", MigrationType.DATA_UP.name()};
      Main.main(args);
    } catch (MigrationException e) {
      Assert.fail("Should not have failed", e);
    }

  }

  @Test
  public void testCreateUnSuccessful() {

    try {
      String[] args = new String[]{"-j", postgresqlJdbcUri.toString(), "-dn", dbName, "-du", dbUser, "-dp", dbPwd, "-m", MigrationType.CREATE.name()};
      Main.main(args);
      Assert.fail("Should fail because no superuser password is supplied");
    } catch (MigrationException e) {

    }
  }

  public void testClassLoading() throws MalformedURLException {
    File file = new File("target/migration.jar");
    URL url = file.toURI().toURL();
    ClassLoader classLoader = new URLClassLoader(new URL[]{url});

    
  }
}
