package com.ecovate.database.migration.script;

import java.net.URL;

import com.ecovate.database.migration.script.groovy.GroovyScriptService;
import com.google.inject.ImplementedBy;

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
}
