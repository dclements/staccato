package com.readytalk.staccato.utils;

import java.util.Set;

import com.google.inject.ImplementedBy;

/**
 * Interfaces for loading system resource
 *
 * @author jhumphrey
 */
@ImplementedBy(ResourceLoaderImpl.class)
public interface ResourceLoader {

  /**
   * Loads resources from the classloader by looking in the directory with file extension
   *
   * @param directory the directory to load from
   * @param fileExtension the file extension to load
   * @return a set of resource objects
   */
  public Set<Resource> loadRecursively(String directory, String fileExtension);
}
