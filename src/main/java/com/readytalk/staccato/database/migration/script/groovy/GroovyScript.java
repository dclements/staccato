package com.readytalk.staccato.database.migration.script.groovy;

import java.net.URL;

import org.joda.time.DateTime;

import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.Version;

/**
 * Models a groovy script (groovy, etc)
 *
 * GroovyScript equality and comparability is determined by comparing the script date
 *
 * @author jhumphrey
 */
public class GroovyScript implements DynamicLanguageScript<GroovyScript> {

  private String filename;

  private Class<?> scriptClass;

  private Object scriptInstance;

  private URL url;

  private DateTime scriptDate;

  private Version scriptVersion;

  private Version databaseVersion;

  private String sha1Hash;

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
  public Version getScriptVersion() {
    return scriptVersion;
  }

  @Override
  public Version getDatabaseVersion() {
    return databaseVersion;
  }

  public void setDatabaseVersion(Version databaseVersion) {
    this.databaseVersion = databaseVersion;
  }

  @Override
  public String getSHA1Hash() {
    return sha1Hash;
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

  public void setScriptVersion(Version scriptVersion) {
    this.scriptVersion = scriptVersion;
  }

  public void setSha1Hash(String sha1Hash) {
    this.sha1Hash = sha1Hash;
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

  @Override
  public int hashCode() {
    return scriptDate.hashCode();
  }

  @Override
  public String toString() {
    return filename;
  }
}
