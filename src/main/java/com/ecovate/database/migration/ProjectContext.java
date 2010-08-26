package com.ecovate.database.migration;

import com.ecovate.utils.Version;

/**
 * @author jhumphrey
 */
public class ProjectContext {

  private String name;
  private String version;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
