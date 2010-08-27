package com.readytalk.staccato.database.migration.script.template;

import java.io.IOException;

import org.joda.time.DateTime;

/**
 * @author jhumphrey
 */
public interface ScriptTemplateService {

  public static final String FILENAME_DATE_FORMAT = "yyyyMMdd'T'HHmmss";
  public static final String FILENAME_PREFIX = "Script";

  /**
   * Returns a string representation of the template that
   * has all the token replacement done.
   *
   * @param date the script date
   * @param user the user creating the script
   * @param databaseVersion the database version
   * @return the template
   * @throws java.io.IOException on error when loading script template from classpath
   */
  public ScriptTemplate loadTemplate(DateTime date, String user, String databaseVersion) throws IOException;
}
