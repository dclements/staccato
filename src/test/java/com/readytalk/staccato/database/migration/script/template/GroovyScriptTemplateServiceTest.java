package com.readytalk.staccato.database.migration.script.template;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParserImpl;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.readytalk.staccato.utils.Version;

/**
 * @author jhumphrey
 */
public class GroovyScriptTemplateServiceTest extends BaseTest {

  private GroovyScriptService scriptService;

  @Inject
  public void setScriptService(GroovyScriptService scriptService) {
    this.scriptService = scriptService;
  }

  @Test
  public void testLoadTemplate() throws IOException, URISyntaxException, NoSuchAlgorithmException {

    DateTime dateTime = new DateTime();

    String scriptDate = DateTimeFormat.forPattern(ScriptTemplateService.FILENAME_DATE_FORMAT).print(new DateTime());

    String expectedClassname = ScriptTemplateService.FILENAME_PREFIX + "_" + scriptDate;
    String user = System.getenv("USER");
    String projectVersion = "1.0";

    GroovyScriptTemplateService service = new GroovyScriptTemplateService(scriptService, new MigrationAnnotationParserImpl());
    ScriptTemplate template = service.loadTemplate(dateTime, user, projectVersion);

    Assert.assertTrue(template.getTemplateContents().contains(user));
    Assert.assertTrue(template.getTemplateContents().contains(projectVersion));
    Assert.assertTrue(template.getTemplateContents().contains(expectedClassname));
    Assert.assertTrue(template.getTemplateContents().contains(scriptDate));

    /**
     * IMPORTANT NOTE:  Because the Migration.scriptVersion annotation attribute is used to validate
     * script execution compatibility, it's critical that whenever changes are made to the
     * groovy script template that the scriptVersion is incremented.  Because developers forget things,
     * I have implemented the following unit test code to insure this happens...
     *
     *
     * It validates by using a SHA1 hash to compare changes in content.
     *
     * How?  When this unit test executes, it gets the scriptVersion defined in the raw template
     * and looks it up in the groovy-script-template-version.properties file.  If the version doesn't exist
     * in the properties file, the unit test fails.  If it does exist, it then expects that the
     * property file hash value equals the actual hash of the raw contents of the script file.
     *
     */
    String scriptTemplateVersion = template.getScriptVersion();

    if (scriptTemplateVersion == null || scriptTemplateVersion.equals("")) {
      Assert.fail("The " + Migration.class.getName() + ".scriptVersion annotation attribute cannot be null or empty string.");
    }

    try {
      new Version(scriptTemplateVersion, true);
    } catch (IllegalArgumentException e) {
      Assert.fail("script version is an invalid format: " + scriptTemplateVersion, e);
    }

    ResourceBundle resourceBundle = ResourceBundle.getBundle("groovy-script-template-version");
    File scriptTemplate = new File("src/main/resources/GroovyScriptTemplate.groovy");

    Set<String> keySet = resourceBundle.keySet();
    if (keySet.size() == 0) {
      Assert.fail("groovy-script-template-version.properties must contain at least one key-value " +
      "pair where the key is the version of the script template file and the value is a SHA1 hash of the raw contents");
    }

    String expectedHash = null;
    try {
      expectedHash = resourceBundle.getString(scriptTemplateVersion);
    } catch (MissingResourceException e) {
      Assert.fail("Unable to locate script version in properties file: " + scriptTemplateVersion, e);
    }

    if (expectedHash == null || expectedHash.equals("")) {
      Assert.fail("No SHA1 hash defined for script version key: " + scriptTemplateVersion);
    }

    String actualHash = DigestUtils.shaHex(scriptTemplate.toURI().toURL().openStream());

    Assert.assertEquals(actualHash, expectedHash, "The SHA1 hash defined in the properties file does not equal the actual hash of the contents of GroovyScriptTemplate");
  }
}
