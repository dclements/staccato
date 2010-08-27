package com.readytalk.staccato.database.migration.script;

import org.joda.time.DateTime;

import com.readytalk.staccato.utils.Version;

/**
 * Models dynamic language scripts (e.g. groovy, etc).
 *
 * T is the type that is used for comparison purposes
 *
 * @author jhumphrey
 */
public interface DynamicLanguageScript<T> extends Script<T> {

  /**
   * Returns the date and time of the script
   *
   * @return the script date time
   */
  DateTime getScriptDate();

  /**
   * Returns the script class
   *
   * @return the script class
   */
  Class<?> getScriptClass();

  /**
   * The script instance
   *
   * @return the script instance
   */
  Object getScriptInstance();

  /**
   * Returns the script version
   *
   * @return the script version
   */
  Version getScriptVersion();
}
