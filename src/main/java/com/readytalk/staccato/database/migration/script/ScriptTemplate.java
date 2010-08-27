package com.readytalk.staccato.database.migration.script;

import com.readytalk.staccato.utils.Version;

/**
 * Models a script template
 *
 * @author jhumphrey
 */
public class ScriptTemplate {

  private String classname;
  private String contents;
  private Version version;

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public Version getVersion() {
    return version;
  }

  public void setVersion(Version version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return contents;
  }
}
