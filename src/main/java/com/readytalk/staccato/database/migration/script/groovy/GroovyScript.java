package com.readytalk.staccato.database.migration.script.groovy;

import java.net.URL;

import org.joda.time.DateTime;

import com.readytalk.staccato.database.DatabaseType;
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

  private DatabaseType databaseType;

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

  @Override
  public DatabaseType getDatabaseType() {
    return databaseType;
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

  public void setDatabaseType(DatabaseType databaseType) {
    this.databaseType = databaseType;
  }

  @Override
  public int compareTo(GroovyScript groovyScript) {
    if (groovyScript.databaseType != null && this.databaseType != null && !groovyScript.databaseType.equals(this.databaseType)) {
      return 1;
    } else {
      return groovyScript.getScriptDate().compareTo(this.getScriptDate());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GroovyScript)) return false;

    GroovyScript that = (GroovyScript) o;

    if (databaseType == that.databaseType && scriptDate.equals(that.scriptDate)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = scriptDate.hashCode();
    result = 31 * result + databaseType.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return filename;
  }
}
