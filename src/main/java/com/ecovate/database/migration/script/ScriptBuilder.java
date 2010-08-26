package com.ecovate.database.migration.script;

import java.net.URL;

import com.ecovate.database.migration.MigrationException;

/**
 * @author jhumphrey
 */
public interface ScriptBuilder<T extends Script> {

  /**
   * Sets the filename
   *
   * @param filename the script filename
   * @return this builder
   */
  public ScriptBuilder<T> setFilename(String filename);

  /**
   * Sets the url
   *
   * @param url the script url
   * @return this builder
   */
  public ScriptBuilder<T> setUrl(URL url);

  /**
   * Returns an instantiated script

   * @return a {@link com.ecovate.database.migration.script.Script}
   * @throws com.ecovate.database.migration.MigrationException if there are errors during building
   */
  public T build() throws MigrationException;
}
