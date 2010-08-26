package com.readytalk.staccato.database.migration.script.groovy;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParserImpl;
import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidator;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;

/**
 * @author jhumphrey
 */
public class GroovyScriptServiceTest {

  @Test
  public void testToClass() throws MalformedURLException {

    ResourceLoader loader = EasyMock.createMock(ResourceLoader.class);

    GroovyScriptService service = new GroovyScriptService(loader, null, null);

    File file = new File("src/test/groovy/TestScript_1_0.groovy");

    Class groovyClass = service.toClass(file.toURI().toURL());

    Assert.assertNotNull(groovyClass);
  }

  @Test
  public void testLoad() throws MalformedURLException {

    String expectedResourceOneFilename =    "Script_20100816T123240_1_0.groovy";
    String expectedResourceTwoFilename =    "Script_20100816T123230_base.groovy";
    String expectedResourceThreeFilename =  "Script_20100916T123230_2_0.groovy";

    Resource resourceOne = new Resource();
    resourceOne.setFilename(expectedResourceOneFilename);
    resourceOne.setUrl(new File("src/test/resources/migrations/1.0/" + expectedResourceOneFilename).toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename(expectedResourceTwoFilename);
    resourceTwo.setUrl(new File("src/test/resources/migrations/" + expectedResourceTwoFilename).toURI().toURL());
    Resource resourceThree = new Resource();
    resourceThree.setFilename(expectedResourceThreeFilename);
    resourceThree.setUrl(new File("src/test/resources/migrations/2.0/" + expectedResourceThreeFilename).toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);
    resources.add(resourceThree);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(ScriptService.MIGRATION_DIR, "groovy")).andReturn(resources);
    EasyMock.replay(loader);

    // don't care about validation so create a nice mock
    ScriptValidator validator = EasyMock.createNiceMock(ScriptValidator.class);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    GroovyScriptService service = new GroovyScriptService(loader, validator, annotationParser);

    List<GroovyScript> actualScripts = service.load();

    Assert.assertEquals(actualScripts.size(), 3);

    // test the order

    GroovyScript actualScriptOne = actualScripts.get(0);
    GroovyScript actualScriptTwo = actualScripts.get(1);
    GroovyScript actualScriptThree = actualScripts.get(2);

    Assert.assertNotNull(actualScriptOne);
    Assert.assertNotNull(actualScriptTwo);
    Assert.assertNotNull(actualScriptThree);

    // test ordering, should be by date aascending
    Assert.assertEquals(actualScriptOne.getFilename(), expectedResourceTwoFilename);
    Assert.assertEquals(actualScriptTwo.getFilename(), expectedResourceOneFilename);
    Assert.assertEquals(actualScriptThree.getFilename(), expectedResourceThreeFilename);
  }


  @Test
  public void testFailedLoadWithUniqueDateViolation() throws MalformedURLException {


    String expectedResourceOneFilename =    "TestScript_1_0.groovy";
    String expectedResourceTwoFilename =    "TestScript_unique_date_violation.groovy";

    Resource resourceOne = new Resource();
    resourceOne.setFilename(expectedResourceOneFilename);
    resourceOne.setUrl(new File("src/test/groovy/" + expectedResourceOneFilename).toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename(expectedResourceTwoFilename);
    resourceTwo.setUrl(new File("src/test/groovy/" + expectedResourceTwoFilename).toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(ScriptService.MIGRATION_DIR, "groovy")).andReturn(resources);
    EasyMock.replay(loader);

    // don't care about validation so create a nice mock
    ScriptValidator validator = EasyMock.createNiceMock(ScriptValidator.class);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    GroovyScriptService service = new GroovyScriptService(loader, validator, annotationParser);

    try {
      service.load();
      Assert.fail("should have thrown an exception due to the unique date violation");
    } catch (MigrationException e) {
      Assert.assertTrue(true, e.getMessage());
      // no-op
    }
  }
}
