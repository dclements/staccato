package com.ecovate.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;

import com.ecovate.database.migration.MigrationException;

/**
 * @author jhumphrey
 */
public class ResourceLoaderImpl implements ResourceLoader {

  @Override
  public Set<Resource> loadRecursively(String directory, String fileExtension) {

    Set<Resource> resources = new HashSet<Resource>();

    Enumeration<URL> urlResources;

    try {
      urlResources = this.getClass().getClassLoader().getResources(directory);
      while (urlResources.hasMoreElements()) {
        URL resourceUrl = urlResources.nextElement();
        String protocol = resourceUrl.getProtocol();

        if (protocol.equals("file")) {
          resources.addAll(readFromFileDir(resourceUrl, directory, fileExtension));
        } else if (protocol.equals("jar")) {
          resources.addAll(readFromJarDir(resourceUrl, directory, fileExtension));
        }
      }
    } catch (IOException e) {
      throw new ResourceLoaderException(e);
    }

    return resources;
  }

  // helper method for loading file resources

  Collection<? extends Resource> readFromFileDir(URL resourceUrl, String directory, String fileExtension) {

    Set<Resource> resources = new HashSet<Resource>();

    try {
      File dir = new File(resourceUrl.toURI());

      @SuppressWarnings("unchecked")
      Collection<File> files = (Collection<File>) FileUtils.listFiles(dir, new String[]{fileExtension}, true);

      for (File file : files) {
        Resource resource = new Resource();
        resource.setFilename(file.getName());
        resource.setUrl(file.toURI().toURL());
        resource.setType(ResourceType.FILE);
        resources.add(resource);
      }

    } catch (URISyntaxException e) {
      throw new ResourceLoaderException(e);
    } catch (MalformedURLException e) {
      throw new ResourceLoaderException(e);
    } catch (IllegalArgumentException e) {
      throw new ResourceLoaderException(e);
    }

    return resources;
  }

  // helper method for loading jar resources

  Collection<? extends Resource> readFromJarDir(URL resourceUrl, String directory, String fileExtension) throws IOException {

    if (!new JarEntry(resourceUrl.toExternalForm()).isDirectory()) {
      throw new ResourceLoaderException("jar resource [" + resourceUrl.toExternalForm() + "] is not a directory");
    }

    Set<Resource> resources = new HashSet<Resource>();

    String jarLocation = resourceUrl.toExternalForm().replace("jar:file:", "").replace("!/" + directory, "");
    File file = new File(jarLocation);
    if (!file.exists()) {
      throw new MigrationException("jar resource [" + jarLocation + "] does not exist");
    }

    JarFile jarFile = new JarFile(file);
    Enumeration<JarEntry> jarEntries = jarFile.entries();

    while (jarEntries.hasMoreElements()) {
      JarEntry jarEntry = jarEntries.nextElement();
      String jarEntryName = jarEntry.getName();
      if (jarEntryName.startsWith(directory) && !jarEntry.isDirectory()) {
        String[] jarEntryTokens = jarEntryName.split("/");
        String filename = jarEntryTokens[jarEntryTokens.length - 1];
        if (filename.endsWith(fileExtension)) {
          URL entryUrl = new URL(resourceUrl.toExternalForm().replace(directory, "") + jarEntryName);
          Resource resource = new Resource();
          resource.setFilename(filename);
          resource.setUrl(entryUrl);
          resource.setType(ResourceType.JAR);
          resources.add(resource);
        }
      }
    }

    return resources;
  }
}
