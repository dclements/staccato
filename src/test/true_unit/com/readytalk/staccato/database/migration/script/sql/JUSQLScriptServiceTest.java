package com.readytalk.staccato.database.migration.script.sql;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;

public class JUSQLScriptServiceTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	private final ResourceLoader loader = mock(ResourceLoader.class);
	private final Resource resource1 = mock(Resource.class);
	private final Resource resource2 = mock(Resource.class);
	private final Set<Resource> resources = new LinkedHashSet<Resource>(Arrays.asList(new Resource [] {resource1, resource2})); 
	
	private SQLScript ss1;
	private SQLScript ss2;
	
	private SQLScriptService service;
	private List<SQLScript> testList;
	@Before
	public void setUp() throws Exception {
		reset(loader, resource1, resource2);
		service = new SQLScriptService(loader);
		
		when(loader.loadRecursively(anyString(), anyString(), any(ClassLoader.class))).thenReturn(resources);
		
		ss1 = new SQLScript();
		ss2 = new SQLScript();
		
		testList = Arrays.asList(new SQLScript [] {ss1, ss2});
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoad() throws Exception {
		
		when(resource1.getFilename()).thenReturn("tmp");
		when(resource2.getFilename()).thenReturn("tmp2");
		
		ss1.setFilename("tmp");
		ss2.setFilename("tmp2");
		
		assertEquals(testList, service.load("/", null));
	}
	
	@Test
	public void testLoadNonUnique() throws Exception {
		thrown.expect(MigrationException.class);
		thrown.expectMessage("Unique script violation.");
		final String filename = "tmp";
		final URL url = new URL("file://tmp");
		when(resource1.getFilename()).thenReturn(filename);
		when(resource1.getUrl()).thenReturn(url);
		when(resource2.getFilename()).thenReturn(filename);
		when(resource2.getUrl()).thenReturn(url);
		
		service.load("/", null);
	}

	@Test
	public void testGetScriptFileExtension() {
		assertEquals("sql", service.getScriptFileExtension());
	}

}
