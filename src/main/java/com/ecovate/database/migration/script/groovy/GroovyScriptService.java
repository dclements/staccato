package com.ecovate.database.migration.script.groovy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.ecovate.database.migration.MigrationException;
import com.ecovate.database.migration.annotation.Migration;
import com.ecovate.database.migration.annotation.MigrationAnnotationParser;
import com.ecovate.database.migration.script.DynamicLanguageScriptService;
import com.ecovate.database.migration.script.validation.ScriptValidationException;
import com.ecovate.database.migration.script.validation.ScriptValidator;
import com.ecovate.utils.Resource;
import com.ecovate.utils.ResourceLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import groovy.lang.GroovyClassLoader;

/**
 * Groovy script service implementation
 *
 * @author jhumphrey
 */
@Singleton
public class GroovyScriptService implements DynamicLanguageScriptService<GroovyScript> {

  Logger logger = Logger.getLogger(this.getClass().getName());

  private ResourceLoader loader;
  private ScriptValidator validator;
  private MigrationAnnotationParser annotationParser;

  @Inject
  public GroovyScriptService(ResourceLoader loader, ScriptValidator validator, MigrationAnnotationParser annotationParser) {
    this.loader = loader;
    this.validator = validator;
    this.annotationParser = annotationParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<GroovyScript> load() {
    Set<Resource> resources = loader.loadRecursively(MIGRATION_DIR, getScriptFileExtension());

    SortedSet<GroovyScript> scripts = new TreeSet<GroovyScript>();

    for (Resource resource : resources) {

      Class<?> scriptClass = this.toClass(resource.getUrl());

      if (annotationParser.isMigrationScript(scriptClass)) {

        try {
          Object scriptInstance = scriptClass.newInstance();

          Migration migrationAnnotation = annotationParser.getMigrationAnnotation(scriptInstance);
          String scriptDateStr = migrationAnnotation.scriptDate();
          try {
            DateTime scriptDate = new DateTime(scriptDateStr);

            GroovyScript script = new GroovyScript();
            script.setUrl(resource.getUrl());
            script.setFilename(resource.getFilename());
            script.setScriptClass(scriptClass);
            script.setScriptDate(scriptDate);
            script.setScriptInstance(scriptInstance);

            try {
              validator.validate(script);
            } catch (ScriptValidationException e) {
              throw new MigrationException("Unable to validate script: " + script.getFilename(), e);
            }

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
          } catch (IllegalArgumentException e) {
            throw new MigrationException("Script date [" + scriptDateStr + "] is an invalid format for script: " + scriptClass.getName());
          }

        } catch (InstantiationException e) {
          throw new MigrationException("Unable to instantiate script: " + scriptClass.getName(), e);
        } catch (IllegalAccessException e) {
          throw new MigrationException("Unable to access script: " + scriptClass.getName(), e);
        }
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
}
