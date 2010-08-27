package com.readytalk.staccato.database.migration.script.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.inject.Inject;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;

/**
 * Loads a groovy script template from the classloader
 *
 * @author jhumphrey
 */
public class GroovyScriptTemplateService implements ScriptTemplateService {

  public static final String TEMPLATE_NAME = "GroovyScriptTemplate.groovy";

  private GroovyScriptService scriptService;
  private MigrationAnnotationParser annotationParser;

  @Inject
  public GroovyScriptTemplateService(GroovyScriptService scriptService, MigrationAnnotationParser annotationParser) {
    this.scriptService = scriptService;
    this.annotationParser = annotationParser;
  }

  @Override
  public ScriptTemplate loadTemplate(DateTime date, String user, String databaseVersion) throws IOException {
    URL url = this.getClass().getClassLoader().getResource(TEMPLATE_NAME);

    InputStream inputStream = url.openStream();

    String contentHash = DigestUtils.shaHex(url.openStream());

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
    template.setRawContentHash(contentHash);

    String contents = sb.toString().replace("USER", user).replace("GroovyScriptTemplate", classname).
      replace("DATABASE_VERSION", databaseVersion).replace("DATE", new DateTime(date).toString()).trim();

    template.setTemplateContents(contents);

    // extract the version
    Class<?> templateClass = scriptService.toClass(url);
    try {
      Object scriptInstance = templateClass.newInstance();

      Migration migrationAnnotation = annotationParser.getMigrationAnnotation(scriptInstance);
      template.setScriptVersion(migrationAnnotation.scriptVersion());
    } catch (InstantiationException e) {
      throw new MigrationException("Unable to instantiate groovy script template", e);
    } catch (IllegalAccessException e) {
      throw new MigrationException("Error while accessing groovy script template class", e);
    }

    return template;
  }
}
