package com.readytalk.staccato.database.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;

import java.lang.annotation.IncompleteAnnotationException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.workflow.MigrationWorkflowService;

public class GroovyMigrationServiceTest {
	private final MigrationWorkflowService mfs = mock(MigrationWorkflowService.class);
	private final MigrationAnnotationParser map = mock(MigrationAnnotationParser.class);
	
	private final GroovyMigrationService service =  spy(new GroovyMigrationService(mfs, map));
	private final DatabaseContext context = mock(DatabaseContext.class);
	private final Migration migration = mock(Migration.class);
	private final MigrationRuntime runtime = mock(MigrationRuntime.class);
	
	@Before
	public void setUp() throws Exception {
		reset(mfs, map, context, migration, service);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunEmpty() {
		final List<GroovyScript> scripts = Arrays.asList(new GroovyScript [] {} );
		
		service.run(scripts, runtime);
		
		//Just making sure that nothing significant happens.
		verify(map, never()).getMigrationAnnotation(any());
	}
	
	//TODO: More tests needed for run() that aren't simply mocked code paths.

	@Test
	public void testIsValidDatabaseType() {
		when(migration.databaseType()).thenReturn(DatabaseType.HSQLDB);
		when(context.getDatabaseType()).thenReturn(DatabaseType.HSQLDB);
		
		assertTrue(service.isValidDatabaseType(migration, context));
	}
	
	@Test
	public void testIsValidDatabaseTypeFalse() {
		when(migration.databaseType()).thenReturn(DatabaseType.POSTGRESQL);
		when(context.getDatabaseType()).thenReturn(DatabaseType.HSQLDB);
		
		assertFalse(service.isValidDatabaseType(migration, context));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsValidDatabaseTypeError() {
		when(migration.databaseType()).thenThrow(IncompleteAnnotationException.class);
		when(context.getDatabaseType()).thenReturn(DatabaseType.HSQLDB);
		
		assertTrue(service.isValidDatabaseType(migration, context));
	}
	

}
