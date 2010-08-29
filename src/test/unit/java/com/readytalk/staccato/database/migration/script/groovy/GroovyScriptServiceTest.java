package com.readytalk.staccato.database.migration.script.groovy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationService;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParserImpl;
import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.database.migration.script.ScriptTemplate;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidator;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;
import com.readytalk.staccato.utils.Version;

/**
 * @author jhumphrey
 */
public class GroovyScriptServiceTest {

  @Test
  public void testToClass() throws MalformedURLException {

    ResourceLoader loader = EasyMock.createMock(ResourceLoader.class);

    GroovyScriptService service = new GroovyScriptService(loader, null, new MigrationAnnotationParserImpl());

    File file = new File("src/test/unit/groovy/TestScript_1_0.groovy");

    Class groovyClass = service.toClass(file.toURI().toURL());

    Assert.assertNotNull(groovyClass);
  }

  @Test
  public void testLoad() throws MalformedURLException {

    String expectedResourceOneFilename = "Script_20100816T123240_1_0.groovy";
    String expectedResourceTwoFilename = "Script_20100816T123230_base.groovy";
    String expectedResourceThreeFilename = "Script_20100916T123230_2_0.groovy";

    Resource resourceOne = new Resource();
    resourceOne.setFilename(expectedResourceOneFilename);
    resourceOne.setUrl(new File("src/test/unit/resources/migrations/1.0/" + expectedResourceOneFilename).toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename(expectedResourceTwoFilename);
    resourceTwo.setUrl(new File("src/test/unit/resources/migrations/" + expectedResourceTwoFilename).toURI().toURL());
    Resource resourceThree = new Resource();
    resourceThree.setFilename(expectedResourceThreeFilename);
    resourceThree.setUrl(new File("src/test/unit/resources/migrations/2.0/" + expectedResourceThreeFilename).toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);
    resources.add(resourceThree);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(MigrationService.DEFAULT_MIGRATION_DIR, "groovy")).andReturn(resources);
    EasyMock.replay(loader);

    // don't care about validation so create a nice mock
    ScriptValidator validator = EasyMock.createNiceMock(ScriptValidator.class);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    GroovyScriptService service = new GroovyScriptService(loader, validator, annotationParser);

    List<GroovyScript> actualScripts = service.load(MigrationService.DEFAULT_MIGRATION_DIR);

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

    String expectedResourceOneFilename = "TestScript_1_0.groovy";
    String expectedResourceTwoFilename = "TestScript_unique_date_violation.groovy";

    Resource resourceOne = new Resource();
    resourceOne.setFilename(expectedResourceOneFilename);
    resourceOne.setUrl(new File("src/test/unit/groovy/" + expectedResourceOneFilename).toURI().toURL());
    Resource resourceTwo = new Resource();
    resourceTwo.setFilename(expectedResourceTwoFilename);
    resourceTwo.setUrl(new File("src/test/unit/groovy/" + expectedResourceTwoFilename).toURI().toURL());

    Set<Resource> resources = new HashSet<Resource>();
    resources.add(resourceOne);
    resources.add(resourceTwo);

    ResourceLoader loader = EasyMock.createStrictMock(ResourceLoader.class);
    EasyMock.expect(loader.loadRecursively(MigrationService.DEFAULT_MIGRATION_DIR, "groovy")).andReturn(resources);
    EasyMock.replay(loader);

    // don't care about validation so create a nice mock
    ScriptValidator validator = EasyMock.createNiceMock(ScriptValidator.class);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    GroovyScriptService service = new GroovyScriptService(loader, validator, annotationParser);

    try {
      service.load(MigrationService.DEFAULT_MIGRATION_DIR);
      Assert.fail("should have thrown an exception due to the unique date violation");
    } catch (MigrationException e) {
      Assert.assertTrue(true, e.getMessage());
      // no-op
    }
  }

  @Test
  public void testGetScriptTemplate() throws IOException, URISyntaxException, NoSuchAlgorithmException {

    DateTime expectedDateTime = new DateTime();
    String expectedScriptDateStr = DateTimeFormat.forPattern(GroovyScriptService.TEMPLATE_DATE_FORMAT).print(new DateTime());

    String expectedClassname = GroovyScriptService.TEMPLATE_CLASSNAME_PREFIX + "_" + expectedScriptDateStr;
    String expectedUser = System.getenv("USER");
    String expectedDatabaseVersion = "1.0";

    ResourceLoader loader = EasyMock.createMock(ResourceLoader.class);
    ScriptValidator validator = EasyMock.createMock(ScriptValidator.class);
    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();
    GroovyScriptService service = new GroovyScriptService(loader, validator, annotationParser);
    ScriptTemplate template = service.getScriptTemplate(expectedDateTime, expectedUser, expectedDatabaseVersion);

    Assert.assertTrue(template.getContents().contains(expectedUser));
    Assert.assertTrue(template.getContents().contains(expectedDatabaseVersion));
    Assert.assertTrue(template.getContents().contains(expectedClassname));
    Assert.assertTrue(template.getContents().contains(expectedScriptDateStr));
  }

  /**
   * IMPORTANT NOTE:  Because the Migration.scriptVersion annotation attribute is used to validate
   * script execution compatibility (see GrooryScriptService.load()), it's critical that whenever
   * changes are made to the groovy script template that the scriptVersion is incremented.  Because
   * developers forget things, I have implemented the following unit test code to insure this happens...
   *
   * It validates by using a SHA1 hash to compare changes in content.
   *
   * How?  When this unit test executes, it gets the scriptVersion defined in the raw template
   * and looks it up in the groovy-script-template-version.properties file.  If the version doesn't exist
   * in the properties file, the unit test fails.  If it does exist, it then expects that the
   * property file hash value equals the actual hash of the raw contents of the script file.
   *
   * @throws java.io.IOException on exception
   */
  @Test
  public void testTemplateVersion() throws IOException {

    ResourceLoader loader = EasyMock.createMock(ResourceLoader.class);
    ScriptValidator validator = EasyMock.createMock(ScriptValidator.class);
    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    GroovyScriptService service = null;
    try {
      service = new GroovyScriptService(loader, validator, annotationParser);
    } catch (Exception e) {
      Assert.fail("The " + Migration.class.getName() + ".scriptVersion annotation attribute must resolve to a valid, strict Version and cannot be null or empty string.");
    }

    Version scriptTemplateVersion = service.getScriptTemplateVersion();

    ResourceBundle resourceBundle = ResourceBundle.getBundle("groovy-script-template-version");
    File scriptTemplate = new File("src/main/resources/GroovyScriptTemplate.groovy");

    Set<String> keySet = resourceBundle.keySet();
    if (keySet.size() == 0) {
      Assert.fail("groovy-script-template-version.properties must contain at least one key-value " +
        "pair where the key is the version of the script template file and the value is a SHA1 hash of the raw contents");
    }

    String expectedHash = null;
    try {
      expectedHash = resourceBundle.getString(scriptTemplateVersion.toString());
    } catch (MissingResourceException e) {
      Assert.fail("Unable to locate script version '" + scriptTemplateVersion.toString() + "' in properties file: groovy-script-template.properties", e);
    }

    if (expectedHash == null || expectedHash.equals("")) {
      Assert.fail("No SHA1 hash defined for script version key: " + scriptTemplateVersion);
    }

    String actualHash = DigestUtils.shaHex(scriptTemplate.toURI().toURL().openStream());

    Assert.assertEquals(actualHash, expectedHash, "The SHA1 hash defined in the properties file does not equal the actual hash of the contents of GroovyScriptTemplate");
  }
}
