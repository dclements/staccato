package com.readytalk.staccato.database.migration.script.groovy;

import java.net.URL;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

/**
 * Models a groovy script (groovy, etc)
 *
 * GroovyScript equality and comparability is determined by comparing the script date
 *
 * @author jhumphrey
 */
public class GroovyScript implements DynamicLanguageScript<GroovyScript> {

  @NotNull
  private String filename;

  @NotNull
  private Class<?> scriptClass;

  @NotNull
  private Object scriptInstance;

  @NotNull
  private URL url;

  @NotNull
  private DateTime scriptDate;

  @Override
  public String getFilename() {
    return filename;
  }

  @Override
  public URL getUrl() {
    return url;
  }

  @Override
  public DateTime getScriptDate() {
    return scriptDate;
  }

  @Override
  public Class<?> getScriptClass() {
    return scriptClass;
  }

  @Override
  public Object getScriptInstance() {
    return scriptInstance;
  }

  @Override
  public int compareTo(GroovyScript groovyScript) {
    return groovyScript.getScriptDate().compareTo(this.getScriptDate());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GroovyScript that = (GroovyScript) o;

    if (!scriptDate.equals(that.scriptDate)) return false;

    return true;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void setScriptClass(Class<?> scriptClass) {
    this.scriptClass = scriptClass;
  }

  public void setScriptDate(DateTime scriptDate) {
    this.scriptDate = scriptDate;
  }

  public void setScriptInstance(Object scriptInstance) {
    this.scriptInstance = scriptInstance;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  @Override
  public int hashCode() {
    return scriptDate.hashCode();
  }

  @Override
  public String toString() {
    return filename;
  }
}
