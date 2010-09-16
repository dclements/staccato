package com.readytalk.staccato.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationService;

/**
 * @author jhumphrey
 */
public class ResourceLoaderImplTest {

  @Test
  public void testLoadFromInvalidFileDir() throws MalformedURLException, FileNotFoundException, URISyntaxException {

    File file = new File("src/test/foo");
    URL url = file.toURI().toURL();

    ResourceLoaderImpl resourceLoader = new ResourceLoaderImpl();

    try {
      resourceLoader.readFromFileDir(url, "groovy");
      Assert.fail("should have thrown an exception since the directory [" + file.getAbsolutePath() + "] is invalid");
    } catch (ResourceLoaderException e) {
      // no-op
    }
  }

  @Test
  public void testLoadFromInvalidJarDir() throws IOException, URISyntaxException {

    ResourceLoaderImpl loader = new ResourceLoaderImpl();

    File file = new File("target/migration.jar");
    String invalidJarPath = "jar:file:" + file.getAbsolutePath() + "!/foo/";
    URL url = new URL(invalidJarPath);

    try {
      loader.readFromJarDir(url, MigrationService.DEFAULT_MIGRATIONS_DIR, "groovy");
      Assert.fail("should have thrown an exception since the directory [" + invalidJarPath + "] is invalid");
    } catch (MigrationException e) {
      // no-op
    }
  }

  @Test
  public void testLoadFromValidJarDir() throws IOException, URISyntaxException {

    ResourceLoaderImpl loader = new ResourceLoaderImpl();

    File file = new File("target/migration.jar");

    String validJarPath = "jar:file:" + file.getAbsolutePath() + "!/" + MigrationService.DEFAULT_MIGRATIONS_DIR;
    URL url = new URL(validJarPath);

    Collection<? extends Resource> actualScripts;
    try {
      actualScripts = loader.readFromJarDir(url, MigrationService.DEFAULT_MIGRATIONS_DIR, "groovy");
      Assert.assertEquals(2, actualScripts.size());
    } catch (MigrationException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test()
  public void testLoadGroovyScriptsFromClassLoader() {

    ResourceLoaderImpl loader = new ResourceLoaderImpl();

    Set<Resource> actualScripts = loader.loadRecursively(MigrationService.DEFAULT_MIGRATIONS_DIR, "groovy", this.getClass().getClassLoader());

    // should be 3 groovy scripts, only those scripts in the test/resources/migrations dir are loaded
    Assert.assertEquals(3, actualScripts.size());
  }

  @Test()
  public void testLoadSQLScriptsFromClassLoader() {

    ResourceLoaderImpl loader = new ResourceLoaderImpl();

    Set<Resource> actualScripts = loader.loadRecursively(MigrationService.DEFAULT_MIGRATIONS_DIR, "sql", this.getClass().getClassLoader());

    // should be 4 sql scripts.  Only those scripts that are in the test/resources/migrations directory are loaded
    Assert.assertEquals(3, actualScripts.size());
  }
}
