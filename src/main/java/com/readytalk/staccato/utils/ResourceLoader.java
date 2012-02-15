package com.readytalk.staccato.utils;

import java.io.File;
import java.net.URL;
import java.util.Set;

import com.google.inject.ImplementedBy;

/**
 * Interfaces for loading system resource.
 */
@ImplementedBy(ResourceLoaderImpl.class)
public interface ResourceLoader {
	
	Set<File> loadRecursively(String directory, String fileExtension);

	/**
	 * Loads resources from the classloader by looking in the directory with file extension
	 *
	 * @param directory the directory to load from
	 * @param fileExtension the file extension to load
	 * @param classLoader the classloader to load from
	 * @return a set of resource objects
	 */
	Set<Resource> loadRecursively(String directory, String fileExtension, ClassLoader classLoader);
	
	/**
	 * Finds the URI for a given resource within a given classloader.
	 * @param loader The classloader.
	 * @param name The name of the resource.
	 * @return A URI connecting to the named resource.
	 */
	URL retrieveURL(ClassLoader loader, String name);
}
