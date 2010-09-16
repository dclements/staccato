package com.readytalk.staccato.database.migration.script.groovy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScriptService;
import com.readytalk.staccato.database.migration.script.ScriptTemplate;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;
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

  public static final Logger logger = Logger.getLogger(GroovyScriptService.class);

  private String scriptTemplateRawContents;
  private Version scriptTemplateVersion;

  private ResourceLoader loader;
  private MigrationValidator validator;
  private MigrationAnnotationParser annotationParser;

  @Inject
  public GroovyScriptService(ResourceLoader loader, MigrationValidator validator, MigrationAnnotationParser annotationParser) {
    this.loader = loader;
    this.validator = validator;
    this.annotationParser = annotationParser;

    loadScriptTemplate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<GroovyScript> load(String migrationDir, ClassLoader classLoader) {

    logger.debug("Loading groovy scripts from migration directory: " + migrationDir);

    Set<Resource> resources = loader.loadRecursively(migrationDir, getScriptFileExtension(), classLoader);

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

        // validate the Migration annotation
        validator.validate(migrationAnnotation, resource.getFilename());

        GroovyScript script = new GroovyScript();
        script.setUrl(resource.getUrl());
        script.setFilename(resource.getFilename());
        script.setScriptClass(scriptClass);
        script.setScriptInstance(scriptInstance);
        script.setScriptDate(new DateTime(migrationAnnotation.scriptDate()));
        script.setScriptVersion(new Version(migrationAnnotation.scriptVersion(), Migration.scriptVersionStrictMode));
        script.setDatabaseVersion(new Version(migrationAnnotation.scriptVersion(), Migration.scriptVersionStrictMode));

        // validate that the script version is equal to the version in the script template
        if (!script.getScriptVersion().equals(scriptTemplateVersion)) {
          throw new MigrationException("Cannot load script: " + script.getFilename() + ". Script version '" + migrationAnnotation.scriptVersion() +
            "' is incompatible with the current script template version '" + scriptTemplateVersion + "'.  Please update your groovy script to " +
            "the latest version of the template");
        }

        // set the sha1 hash
        try {
          script.setSha1Hash(DigestUtils.shaHex(resource.getUrl().openStream()));
        } catch (IOException e) {
          // no-op, this will never throw
        }

        // scripts are uniquely IDed by the script date so check to
        // see if this script is already in the set
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
        logger.debug("Added groovy script to migration: " + script.getFilename());
      } else {
        logger.warn("Unable to load script [" + resource.getFilename() + "].  Not marked for migration with the annotation: " + Migration.class.getName());
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

    logger.trace("Converting groovy script [" + url.toExternalForm() + "] to a java class");

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

        logger.debug("Loaded groovy script template version " + scriptTemplateVersion.toString() + ": " + url.toExternalForm());

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

    String scriptDate = DateTimeFormat.forPattern(TEMPLATE_SCRIPT_DATE_FORMAT).print(date);
    String classnameDate = DateTimeFormat.forPattern(TEMPLATE_CLASSNAME_DATE_FORMAT).print(date);
    String classname = TEMPLATE_CLASSNAME_PREFIX + "_" + classnameDate;

    String contents = scriptTemplateRawContents.toString().replace("USER", user).replace(TEMPLATE_NAME, classname).
      replace("DATABASE_VERSION", databaseVersion).replace("DATE", scriptDate).trim();

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

  @Override
  public List<GroovyScript> filterByDate(List<GroovyScript> scriptsToFilter, DateTime fromDate, DateTime toDate) {

    List<GroovyScript> filteredScripts = new ArrayList<GroovyScript>();

    // if both null, then add all
    if (fromDate == null && toDate == null) {
      filteredScripts.addAll(scriptsToFilter);
    } else {
      for (GroovyScript scriptToFilter : scriptsToFilter) {
        DateTime loadedScriptDate = scriptToFilter.getScriptDate();

        // if both from and to dates are defined
        if (fromDate != null && toDate != null &&
            (loadedScriptDate.isEqual(fromDate) || loadedScriptDate.isAfter(fromDate)) &&
            (loadedScriptDate.isEqual(toDate) || loadedScriptDate.isBefore(toDate))
          ) {
          filteredScripts.add(scriptToFilter);
        }
        // if just from date is defined
        else if (fromDate != null && toDate == null && (loadedScriptDate.isEqual(fromDate) || loadedScriptDate.isAfter(fromDate))) {
          filteredScripts.add(scriptToFilter);
        }
        // if just to date is defined
        else if (toDate != null && fromDate == null && (loadedScriptDate.isEqual(toDate) || loadedScriptDate.isBefore(toDate))) {
          filteredScripts.add(scriptToFilter);
        }
      }
    }

    return filteredScripts;
  }

  public List<GroovyScript> filterByDatabaseVersion(List<GroovyScript> allScripts, Version fromVer, Version toVer) {
    List<GroovyScript> filteredScripts = new ArrayList<GroovyScript>();

    // if both null, then add all
    if (fromVer == null && toVer == null) {
      filteredScripts.addAll(allScripts);
    } else {
      for (GroovyScript scriptToFilter : allScripts) {
        Version scriptToFilterVersion = scriptToFilter.getDatabaseVersion();

        // if both from and to dates are defined
        if (fromVer != null && toVer != null &&
            (scriptToFilterVersion.equals(fromVer) || scriptToFilterVersion.compareTo(fromVer) > 0) &&
            (scriptToFilterVersion.equals(toVer) || scriptToFilterVersion.compareTo(toVer) < 0)
          ) {
          filteredScripts.add(scriptToFilter);
        }
        // if just from ver is defined
        else if (fromVer != null && toVer == null && (scriptToFilterVersion.equals(fromVer) || scriptToFilterVersion.compareTo(fromVer) > 0)) {
          filteredScripts.add(scriptToFilter);
        }
        // if just to date is defined
        else if (toVer != null && fromVer == null && (scriptToFilterVersion.equals(toVer) || scriptToFilterVersion.compareTo(toVer) < 0)) {
          filteredScripts.add(scriptToFilter);
        }
      }
    }

    return filteredScripts;
  }
}
