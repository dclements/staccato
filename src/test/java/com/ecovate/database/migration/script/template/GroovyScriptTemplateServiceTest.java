package com.ecovate.database.migration.script.template;

import java.io.IOException;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class GroovyScriptTemplateServiceTest {

  @Test
  public void testLoadTemplate() throws IOException, URISyntaxException {

    DateTime dateTime = new DateTime();

    String scriptDate = DateTimeFormat.forPattern(ScriptTemplateService.FILENAME_DATE_FORMAT).print(new DateTime());

    String expectedClassname = ScriptTemplateService.FILENAME_PREFIX + "_" + scriptDate;
    String user = System.getenv("USER");
    String projectVersion = "1.0";

    GroovyScriptTemplateService service = new GroovyScriptTemplateService();
    ScriptTemplate template = service.loadTemplate(dateTime, user, projectVersion);

    System.out.println("\n" + template);

    Assert.assertTrue(template.getTemplateContents().contains(user));
    Assert.assertTrue(template.getTemplateContents().contains(projectVersion));
    Assert.assertTrue(template.getTemplateContents().contains(expectedClassname));
    Assert.assertTrue(template.getTemplateContents().contains(scriptDate));
  }
}
