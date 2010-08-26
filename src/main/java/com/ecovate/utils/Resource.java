package com.ecovate.utils;

import java.net.URL;

/**
 * Represents a system resource
 *
 * @author jhumphrey
 */
public class Resource {

  private String filename;

  private URL url;

  private ResourceType type;

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Resource)) return false;

    Resource resource = (Resource) o;

    if (!filename.equals(resource.filename)) return false;
    if (!url.equals(resource.url)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = filename.hashCode();
    result = 31 * result + url.hashCode();
    return result;
  }
}
