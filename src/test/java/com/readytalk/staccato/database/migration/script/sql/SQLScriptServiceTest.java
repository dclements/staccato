package com.readytalk.staccato.database.migration.script.sql;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;

/**
 * @author jhumphrey
 */
public class SQLScriptServiceTest {

  @Test
  public void testSuccessfulLoad() throws MalformedURLException {

    Resource resourceOne = new Resource();
    resourceOne.setFilename("base.sql");
    resourceOne.setUrl(new File("src/test/resources/migrations/base.sql").toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename("1.0.sql");
    resourceTwo.setUrl(new File("src/test/resources/migrations/1.0/1.0.groovy").toURI().toURL());
    Resource resourceThree = new Resource();
    resourceThree.setFilename("2.0.sql");
    resourceThree.setUrl(new File("src/test/resources/migrations/2.0/2.0.sql").toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);
    resources.add(resourceThree);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(ScriptService.MIGRATION_DIR, "sql")).andReturn(resources);
    EasyMock.replay(loader);

    SQLScriptService service = new SQLScriptService(loader);

    List<SQLScript> actualScripts = service.load();

    Assert.assertNotNull(actualScripts);

    Assert.assertEquals(actualScripts.size(), 3);

    int assertCount = 0;
    for (SQLScript actualScript : actualScripts) {
      if (actualScript.getFilename().equals("base.sql") ||
        actualScript.getFilename().equals("1.0.sql") ||
        actualScript.getFilename().equals("2.0.sql")) {
        assertCount++;
      }
    }

    Assert.assertEquals(assertCount, 3);
  }

  @Test
  public void testUniqueNameViolationLoad() throws MalformedURLException {

    Resource resourceOne = new Resource();
    resourceOne.setFilename("base.sql");
    resourceOne.setUrl(new File("src/test/resources/migrations/base.sql").toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename("base.sql");
    resourceTwo.setUrl(new File("src/test/resources/migrations/1.0/1.0.sql").toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(ScriptService.MIGRATION_DIR, "sql")).andReturn(resources);
    EasyMock.replay(loader);

    SQLScriptService service = new SQLScriptService(loader);

    try {
      service.load();
      Assert.fail("should fail because the filenames are the same");
    } catch (Exception e) {
      // no-op, success
    }
  }
}
