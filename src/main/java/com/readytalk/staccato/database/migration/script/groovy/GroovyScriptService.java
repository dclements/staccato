package com.readytalk.staccato.database.migration.script.groovy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.IncompleteAnnotationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScriptService;
import com.readytalk.staccato.database.migration.script.ScriptTemplate;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidationException;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidator;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;
import com.readytalk.staccato.utils.Version;
import groovy.lang.GroovyClassLoader;

/**
 * Groovy script service implementation
 *
 * @author jhumphrey
 */
@Singleton
public class GroovyScriptService implements DynamicLanguageScriptService<GroovyScript> {

  Logger logger = Logger.getLogger(this.getClass().getName());

  public static final String TEMPLATE_DATE_FORMAT = "yyyyMMdd'T'HHmmss";
  public static final String TEMPLATE_CLASSNAME_PREFIX = "Script";
  public static final String TEMPLATE_NAME = "GroovyScriptTemplate";

  private String scriptTemplateRawContents;
  private Version scriptTemplateVersion;

  private ResourceLoader loader;
  private ScriptValidator validator;
  private MigrationAnnotationParser annotationParser;

  @Inject
  public GroovyScriptService(ResourceLoader loader, ScriptValidator validator, MigrationAnnotationParser annotationParser) {
    this.loader = loader;
    this.validator = validator;
    this.annotationParser = annotationParser;

    loadScriptTemplate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<GroovyScript> load(String migrationDir) {
    Set<Resource> resources = loader.loadRecursively(migrationDir, getScriptFileExtension());

    SortedSet<GroovyScript> scripts = new TreeSet<GroovyScript>();

    for (Resource resource : resources) {

      Class<?> scriptClass = this.toClass(resource.getUrl());

      if (annotationParser.isMigrationScript(scriptClass)) {

        Object scriptInstance;
        try {
          scriptInstance = scriptClass.newInstance();
        } catch (InstantiationException e) {
          throw new MigrationException("Unable to instantiate script: " + scriptClass.getName(), e);
        } catch (IllegalAccessException e) {
          throw new MigrationException("Unable to access script: " + scriptClass.getName(), e);
        }

        Migration migrationAnnotation = annotationParser.getMigrationAnnotation(scriptInstance);

        GroovyScript script = new GroovyScript();
        script.setUrl(resource.getUrl());
        script.setFilename(resource.getFilename());
        script.setScriptClass(scriptClass);
        script.setScriptInstance(scriptInstance);

        // set the sha1 hash
        try {
          script.setSha1Hash(DigestUtils.shaHex(resource.getUrl().openStream()));
        } catch (IOException e) {
          throw new MigrationException("Error while opening stream for url: " + resource.getUrl().toExternalForm(), e);
        }

        // set the script date
        try {
          String scriptDateStr = migrationAnnotation.scriptDate();
          DateTime scriptDate = new DateTime(scriptDateStr);
          script.setScriptDate(scriptDate);
        } catch (IllegalArgumentException e) {
          throw new MigrationException("Script date is an invalid format for script: " + scriptClass.getName());
        } catch (IncompleteAnnotationException e) {
          throw new MigrationException(Migration.class.getName() + " script date is required for script: " + script.getFilename(), e);
        }

        // set the script version
        try {

          String scriptVersionStr = migrationAnnotation.scriptVersion();
          Version scriptVersion = new Version(scriptVersionStr, true);

          // first check to make sure that the script version is compatible with the
          // current script template version
          if (!scriptVersion.equals(scriptTemplateVersion)) {
            throw new MigrationException("Cannot load script: " + script.getFilename() + ". Script version '" + scriptVersionStr +
              "' is incompatible with the current script template version '" + scriptTemplateVersion + "'.  Please update your groovy script to " +
              "the latest version of the template");
          }

          script.setScriptVersion(scriptVersion);
        } catch (IllegalArgumentException e) {
          throw new MigrationException(Migration.class.getName() + " script version is an invalid format for script: " + script.getFilename(), e);
        } catch (IncompleteAnnotationException e) {
          throw new MigrationException(Migration.class.getName() + " script version is required for script: " + script.getFilename(), e);
        }

        // now validate all fields
        try {
          validator.validate(script);
        } catch (ScriptValidationException e) {
          throw new MigrationException("Unable to validate script: " + script.getFilename(), e);
        }

        // if the script isn't already in the set then add it
        if (scripts.contains(script)) {
          GroovyScript firstScript = null;
          for (GroovyScript aScript : scripts) {
            if (aScript.equals(script)) {
              firstScript = aScript;
            }
          }
          throw new MigrationException("Unique script violation.  Groovy script [" + script.getUrl() + "] violates" +
            " unique date constraint.  Script [" + firstScript.getUrl().toExternalForm() + "] already contains the same date");
        }

        scripts.add(script);
      } else {
        logger.warning("Unable to load script [" + resource.getFilename() + "].  Not marked for migration with the annotation: " + Migration.class.getName());
      }
    }

    List<GroovyScript> reverseSortedOrder = new ArrayList<GroovyScript>(scripts);
    Collections.reverse(reverseSortedOrder);

    return reverseSortedOrder;
  }

  @Override
  public String getScriptFileExtension() {
    return "groovy";
  }

  /**
   * Used for converting a groovy script to a java class file
   *
   * @param url the script url
   * @return a java class
   */
  public Class<?> toClass(URL url) {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    try {
      StringBuilder sb = new StringBuilder();
      String line;

      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        while ((line = reader.readLine()) != null) {
          sb.append(line).append("\n");
        }
      } catch (Exception e) {
        throw new MigrationException("unable to parse groovy script: " + url.toExternalForm(), e);
      } finally {
        url.openStream().close();
      }

      return gcl.parseClass(sb.toString());

    } catch (Exception e) {
      e.printStackTrace();
      throw new MigrationException("unable to parse groovy script: " + url.toExternalForm(), e);
    }
  }

  private void loadScriptTemplate() {
    try {
      URL url = this.getClass().getClassLoader().getResource(TEMPLATE_NAME + "." + getScriptFileExtension());

      InputStream inputStream = url.openStream();

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }

      scriptTemplateRawContents = sb.toString();

      // extract the version
      Class<?> templateClass = toClass(url);
      try {
        Object scriptInstance = templateClass.newInstance();

        Migration migrationAnnotation = annotationParser.getMigrationAnnotation(scriptInstance);
        scriptTemplateVersion = new Version(migrationAnnotation.scriptVersion(), true);
      } catch (InstantiationException e) {
        throw new MigrationException("Unable to instantiate groovy script template", e);
      } catch (IllegalAccessException e) {
        throw new MigrationException("Error while accessing groovy script template class", e);
      }
    } catch (IOException e) {
      throw new MigrationException("Unable to load script template: " + TEMPLATE_NAME, e);
    }
  }

  @Override
  public ScriptTemplate getScriptTemplate(DateTime date, String user, String databaseVersion) throws IOException {

    String scriptDate = DateTimeFormat.forPattern(TEMPLATE_DATE_FORMAT).print(date);
    String classname = TEMPLATE_CLASSNAME_PREFIX + "_" + scriptDate;

    String contents = scriptTemplateRawContents.toString().replace("USER", user).replace(TEMPLATE_NAME, classname).
      replace("DATABASE_VERSION", databaseVersion).replace("DATE", date.toString()).trim();

    ScriptTemplate scriptTemplate = new ScriptTemplate();
    scriptTemplate.setVersion(scriptTemplateVersion);
    scriptTemplate.setClassname(classname);
    scriptTemplate.setContents(contents);

    return scriptTemplate;
  }

  @Override
  public Version getScriptTemplateVersion() {
    return scriptTemplateVersion;
  }

  @Override
  public String getScriptTemplateRawContents() {
    return scriptTemplateRawContents;
  }
}
