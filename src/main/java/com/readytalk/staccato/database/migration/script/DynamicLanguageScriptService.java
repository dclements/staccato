package com.readytalk.staccato.database.migration.script;

import java.io.IOException;
import java.net.URL;

import org.joda.time.DateTime;

import com.readytalk.staccato.database.migration.script.groovy.GroovyScriptService;
import com.google.inject.ImplementedBy;
import com.readytalk.staccato.utils.Version;

/**
 * Interface for scripts that can be converted to classes
 *
 * @author jhumphrey
 */
@ImplementedBy(GroovyScriptService.class)
public interface DynamicLanguageScriptService<T extends DynamicLanguageScript> extends ScriptService<T> {

  /**
   * Parses the given url to a java class
   *
   * @param url the script url
   * @return the java class representation
   */
  Class<?> toClass(URL url);


  /**
   * Returns a string representation of the script template with the DATE,
   * USER, and DATABASE_VERSION tokens replaced with the supplied valus
   *
   * @param date the script date
   * @param user the user creating the script
   * @param databaseVersion the database version
   * @return the template
   * @throws java.io.IOException on error when loading script template from classpath
   */
  ScriptTemplate getScriptTemplate(DateTime date, String user, String databaseVersion) throws IOException;

  /**
   * Returns the script template version
   *
   * @return the script template version
   */
  Version getScriptTemplateVersion();

  /**
   * Returns the raw contents of the script template
   *
   * @return the raw contents
   */
  String getScriptTemplateRawContents();
}
