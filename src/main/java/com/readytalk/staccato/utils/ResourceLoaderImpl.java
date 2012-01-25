package com.readytalk.staccato.utils;

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
import org.apache.log4j.Logger;

import com.readytalk.staccato.database.migration.MigrationException;

public class ResourceLoaderImpl implements ResourceLoader {

	private static final Logger logger = Logger.getLogger(ResourceLoaderImpl.class);
	
	@Override
	public Set<Resource> loadRecursively(final String directory, final String fileExtension,
			final ClassLoader classLoader) {

		logger.debug("Searching directory [" + directory + "] for resources with file extension [" 
				+ fileExtension + "]");

		final Set<Resource> resources = new HashSet<Resource>();

		final Enumeration<URL> urlResources;

		try {
			urlResources = classLoader.getResources(directory);
			while (urlResources.hasMoreElements()) {
				URL resourceUrl = urlResources.nextElement();
				String protocol = resourceUrl.getProtocol();

				if ("file".equals(protocol)) {
					resources.addAll(readFromFileDir(resourceUrl, fileExtension));
				} else if ("jar".equals(protocol)) {
					resources.addAll(readFromJarDir(resourceUrl, directory, fileExtension));
				}
			}
		} catch (IOException e) {
			throw new ResourceLoaderException(e);
		}

		logger.debug("Found " + resources.size() + " resources in directory: " + directory);

		if (resources.size() > 0) {
			logger.debug("\t" + resources);
		}

		return resources;
	}

	// helper method for loading file resources

	public Collection<? extends Resource> readFromFileDir(final URL resourceUrl, final String fileExtension) {

		final Set<Resource> resources = new HashSet<Resource>();

		try {
			final File dir = new File(resourceUrl.toURI());

			final Collection<File> files = FileUtils.listFiles(dir, new String[]{fileExtension}, true);

			for (File file : files) {
				Resource resource = new Resource();
				resource.setFilename(file.getName());
				resource.setUrl(file.toURI().toURL());
				resource.setType(ResourceType.FILE);
				resources.add(resource);
				logger.trace("Found " + ResourceType.FILE + " resource: " + file.getAbsolutePath());
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

	public Collection<? extends Resource> readFromJarDir(final URL resourceUrl, final String directory, 
			final String fileExtension) throws IOException {

		if (!new JarEntry(resourceUrl.toExternalForm()).isDirectory()) {
			throw new ResourceLoaderException("jar resource [" + resourceUrl.toExternalForm() + "] is not a directory");
		}

		final Set<Resource> resources = new HashSet<Resource>();

		final String jarLocation = resourceUrl.toExternalForm().replace("jar:file:", "").replace("!/" + directory, "");
		final File file = new File(jarLocation);
		if (!file.exists()) {
			throw new MigrationException("jar resource [" + jarLocation + "] does not exist");
		}

		final JarFile jarFile = new JarFile(file);
		final Enumeration<JarEntry> jarEntries = jarFile.entries();

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
					logger.trace("Found " + ResourceType.JAR + " resource: " + file.getAbsolutePath());
				}
			}
		}

		return resources;
	}

	@Override
	public URL retrieveURL(final ClassLoader loader, final String name) {
		return loader.getResource(name);
	}
	
	
}
