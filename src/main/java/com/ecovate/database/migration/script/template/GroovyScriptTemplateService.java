package com.ecovate.database.migration.script.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Loads a groovy script template from the classloader
 *
 * @author jhumphrey
 */
public class GroovyScriptTemplateService implements ScriptTemplateService {

  public static final String TEMPLATE_NAME = "GroovyScriptTemplate.groovy";

  @Override
  public ScriptTemplate loadTemplate(DateTime date, String user, String projectVersion) throws IOException {
    URL url = this.getClass().getClassLoader().getResource(TEMPLATE_NAME);

    InputStream inputStream = url.openStream();

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }

    String scriptDate = DateTimeFormat.forPattern(FILENAME_DATE_FORMAT).print(new DateTime());
    String classname = FILENAME_PREFIX + "_" + scriptDate;

    ScriptTemplate template = new ScriptTemplate();
    template.setRawTemplate(url);
    template.setFilename(classname + ".groovy");
    template.setClassname(classname);

    String contents = sb.toString().replace("USER", user).replace("GroovyScriptTemplate", classname).
      replace("DATABASE_VERSION", projectVersion).replace("DATE", new DateTime(date).toString()).trim();

    template.setTemplateContents(contents);

    return template;
  }
}
