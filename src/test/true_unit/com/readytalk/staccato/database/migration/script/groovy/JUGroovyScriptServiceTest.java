package com.readytalk.staccato.database.migration.script.groovy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.ScriptTemplate;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;
import com.readytalk.staccato.utils.ResourceLoader;
import com.readytalk.staccato.utils.Version;

public class JUGroovyScriptServiceTest {
	private final ResourceLoader loader = mock(ResourceLoader.class);
	private final MigrationValidator validator = mock(MigrationValidator.class);
	private final MigrationAnnotationParser parser = mock(MigrationAnnotationParser.class);
	
	private GroovyScriptService service;
	
	private final GroovyScript script1 = mock(GroovyScript.class);
	private final GroovyScript script2 = mock(GroovyScript.class);
	private final GroovyScript script3 = mock(GroovyScript.class);
	
	private final List<GroovyScript> scripts = Arrays.asList(new GroovyScript [] {script1, script2, script3});
	
	private final DateTimeFormatter dtparser = DateTimeFormat.forPattern("DDD-YYYY");
	
	public JUGroovyScriptServiceTest() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		reset(loader, validator, parser, script1, script2, script3);
		
		service=spy(new GroovyScriptService(loader, validator, parser));
		/*
		 * Currently this method is extremely difficult to unit test.
		 * It reads from the disk, converts things to URLs, accesses
		 * the classpath, etc. So just bypassing it altogether until
		 * it can be refactored. 
		 */
		doNothing().when(service).loadScriptTemplate();
		
		when(script1.getDatabaseVersion()).thenReturn(new Version("1.0"));
		when(script2.getDatabaseVersion()).thenReturn(new Version("2.0"));
		when(script3.getDatabaseVersion()).thenReturn(new Version("3.0"));
		
		when(script1.getScriptDate()).thenReturn(dtparser.parseDateTime("100-2012"));
		when(script2.getScriptDate()).thenReturn(dtparser.parseDateTime("200-2012"));
		when(script3.getScriptDate()).thenReturn(dtparser.parseDateTime("300-2012"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testLoad() {
		//TODO: Implement a test here.
		fail("Not yet implemented");
	}

	@Test
	public void testGetScriptFileExtension() {
		assertEquals("groovy", service.getScriptFileExtension());
	}
	
	@Ignore
	@Test
	public void testToClass() {
		//TODO: Implement a test here.
		fail("Not yet implemented");
	}

	@Test
	public void testGetScriptTemplate() throws Exception {
		when(service.getScriptTemplateRawContents()).thenReturn("USER, GroovyScriptTemplate, DATABASE_VERSION, DATE");
		
		ScriptTemplate st = service.getScriptTemplate(dtparser.parseDateTime("100-2012"), "readytalk", "1.0");
		
		String results = st.getContents().substring(0, 50);
		assertEquals("readytalk, Script_20120409T000000, 1.0, 2012-04-09", results);
	}
	
	@Test
	public void testFilterByDateNulls() {
		assertEquals(scripts, service.filterByDate(scripts, null, null));
	}
	
	@Test
	public void testFilterByDateTo() {
		final List<GroovyScript> results = service.filterByDate(scripts, null, dtparser.parseDateTime("150-2012"));
		assertEquals(scripts.subList(0, 1), results);
	}
	
	@Test
	public void testFilterByDateFrom() {
		final List<GroovyScript> results = service.filterByDate(scripts, dtparser.parseDateTime("150-2012"), null);
		assertEquals(scripts.subList(1, 3), results);
	}
	
	@Test
	public void testFilterByDateToAndFrom() {
		final List<GroovyScript> results = service.filterByDate(scripts, dtparser.parseDateTime("150-2012"), dtparser.parseDateTime("250-2012"));
		assertEquals(scripts.subList(1, 2), results);
	}

	@Test
	public void testFilterByDatabaseVersionNulls() {
		assertEquals(scripts, service.filterByDatabaseVersion(scripts, null, null));
	}
	
	@Test
	public void testFilterByDatabaseVersionTo() {
		final List<GroovyScript> results = service.filterByDatabaseVersion(scripts, null, new Version("1.5"));
		assertEquals(scripts.subList(0, 1), results);
	}
	
	@Test
	public void testFilterByDatabaseVersionFrom() {
		final List<GroovyScript> results = service.filterByDatabaseVersion(scripts, new Version("1.5"), null);
		assertEquals(scripts.subList(1, 3), results);
	}
	
	@Test
	public void testFilterByDatabaseVersionToAndFrom() {
		final List<GroovyScript> results = service.filterByDatabaseVersion(scripts, new Version("1.5"), new Version("2.5"));
		assertEquals(scripts.subList(1, 2), results);
	}

}
