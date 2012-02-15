package com.readytalk.staccato.utils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({FileUtils.class})
public class ResourceLoaderTest {
	
	@Rule
	public static final PowerMockRule powermock = new PowerMockRule();
	
	private File f;
	
	private ResourceLoaderImpl loader;

	@Before
	public void setUp() throws Exception {
		f  = mock(File.class);
		loader = spy(new ResourceLoaderImpl());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testLoadRecursivelyArbitrary() throws Exception {
		File f= File.createTempFile("tmp", ".groovy");
		f.deleteOnExit();
		File d = f.getParentFile();
		
		final Set<File> sf = loader.loadRecursively(d.getAbsolutePath(), "groovy");
		
		assertTrue(sf.contains(f));
	}
	
	@Test
	public void testLoadRecursivelyNotPresent() throws Exception {
		File f= File.createTempFile("tmp", ".groovy2");
		f.deleteOnExit();
		File d = f.getParentFile();
		
		final Set<File> sf = loader.loadRecursively(d.getAbsolutePath(), "sql");
		
		assertEquals(0, sf.size());
	}

	@Test
	public void testLoadRecursively() throws Exception {
		ClassLoader cl = mock(ClassLoader.class);
		Resource r1 = mock(Resource.class);
		Resource r2 = mock(Resource.class);
		
		URL url1 = new URL("file://tmp");
		URL url2 = new URL("jar", "file", "tmp");
		Collection<? extends Resource> ret1 = Arrays.asList(new Resource [] {r1});
		Collection<? extends Resource> ret2 = Arrays.asList(new Resource [] {r2});
		Vector<URL> urlvec = new Vector<URL>(Arrays.asList(new URL [] {url1, url2}));
		String dir = "test";
		String fext = "jar";
		
		when(cl.getResources(eq(dir))).thenReturn(urlvec.elements());
		
		doReturn(ret1).when(loader).readFromFileDir(any(URL.class), anyString());
		doReturn(ret2).when(loader).readFromJarDir(any(URL.class), anyString(), anyString());
		
		Set<Resource> retval = loader.loadRecursively(dir, fext, cl);
		
		verify(loader, times(1)).readFromFileDir(eq(url1), eq(fext));
		verify(loader, times(1)).readFromJarDir(eq(url2), eq(dir), eq(fext));
		
		assertEquals(2, retval.size());
	}
	
	@Test(expected=ResourceLoaderException.class)
	public void testLoadRecursivelyException() throws Exception {
		ClassLoader cl = mock(ClassLoader.class);
		
		doThrow(IOException.class).when(cl).getResources(anyString());

		loader.loadRecursively("test", "jar", cl);
	}

	@Test
	public void testReadFromFileDir() throws Exception {
		File tmp = File.createTempFile("test", ".tmp");
		URL url = tmp.toURI().toURL();
		tmp.deleteOnExit();
		
		when(f.toURI()).thenReturn(tmp.toURI());
		when(f.getName()).thenReturn("name");
		
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.when(FileUtils
				.listFiles(any(File.class), any(String[].class), eq(true)))
				.thenReturn(Arrays.asList(new File [] {f}));
		final List<? extends Resource> retval = new LinkedList<Resource>(loader.readFromFileDir(url, "groovy"));
		
		assertEquals(1, retval.size());
		assertEquals(url, retval.get(0).getUrl());
	}

	@Test
	public void testRetrieveURI() throws Exception {
		final ClassLoader l = mock(ClassLoader.class);
		final URL url = new URL("file://tmp");
		final String str = "name";
		
		when(l.getResource(str)).thenReturn(url);
		
		assertEquals(url, loader.retrieveURL(l, str));
	}

}
