package com.readytalk.staccato.database.migration.annotation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.MigrationException;

public class MigrationAnnotationParserTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	@Migration(
			databaseType=DatabaseType.HSQLDB,
			scriptVersion="1.2.3",
			scriptDate="2012-01-01T00:00:00-06:00",
			description="Sample",
			databaseVersion="3.2.1")
	private static class SampleMigration {
		@SuppressWarnings("unused")
		public void dataUp() {
			
		}
		
	}
	
	private MigrationAnnotationParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new MigrationAnnotationParserImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMigrationAnnotation() {
		final Migration migration = parser.getMigrationAnnotation(new SampleMigration(){});
		
		assertEquals(DatabaseType.HSQLDB, migration.databaseType());
	}
	
	@Test
	public void testGetMigrationAnnotationNone() {
		final Migration migration = parser.getMigrationAnnotation(new Object());
		
		assertNull(migration);
	}

	@Test
	public void testIsMigrationScriptTrue() {
		assertTrue(parser.isMigrationScript(SampleMigration.class));
	}
	
	@Test
	public void testIsMigrationScriptFalse() {
		assertFalse(parser.isMigrationScript(Object.class));
	}

	@Test
	public void testGetMethodAnnotation() {
		Annotation ann = parser.getMethodAnnotation(new SampleMigration() {
			@DataUp
			public void dataUp() {
				
			}
		}, DataUp.class);
		
		assertTrue(ann instanceof DataUp);
	}

	@Test
	public void testGetAnnotatedMethod() throws Exception {
		@SuppressWarnings("unchecked")
		final List<String> sb = mock(List.class);
		
		SampleMigration sm = new SampleMigration() {
			@DataUp
			public void dataUp() {
				sb.add("test");
			}
		};
		
		final Method method = parser.getAnnotatedMethod(sm, DataUp.class);
		
		System.out.println(Arrays.toString(sm.getClass().getMethods()));
		
		method.invoke(sm);
		
		verify(sb, times(1)).add("test");
	}
	
	@Test
	public void testGetAnnotatedMethodDuplicate() throws Exception {
		thrown.expect(MigrationException.class);
		
		SampleMigration sm = new SampleMigration() {
			@DataUp
			public void dataUp() {
			}
			
			@SuppressWarnings("unused")
			@DataUp
			public void badDataUp() {
			}
		};
		
		parser.getAnnotatedMethod(sm, DataUp.class);
	}

	@Test
	public void testContainsWorkflowStepsTrue() {
		@SuppressWarnings("unchecked")
		final boolean ann = parser.containsWorkflowSteps(new SampleMigration() {
			
			@SuppressWarnings("unused")
			@PreUp
			public void preUp() {
				
			}
			
			@DataUp
			public void dataUp() {
				
			}
		}, new Class [] {PreUp.class, DataUp.class});
		
		assertTrue(ann);
	}
	
	@Test
	public void testContainsWorkflowStepsNoOverlap() {
		@SuppressWarnings("unchecked")
		final boolean ann = parser.containsWorkflowSteps(new SampleMigration() {
			
			@SuppressWarnings("unused")
			@PreUp
			public void preUp() {
				
			}
			
			@DataUp
			public void dataUp() {
				
			}
		}, new Class [] {PostUp.class, SchemaUp.class});
		
		assertFalse(ann);
	}
	
	@Test
	public void testContainsWorkflowStepsHalf() {
		@SuppressWarnings("unchecked")
		final boolean ann = parser.containsWorkflowSteps(new SampleMigration() {
			
			@SuppressWarnings("unused")
			@PreUp
			public void preUp() {
				
			}
			
			@DataUp
			public void dataUp() {
				
			}
		}, new Class [] {PostUp.class, PreUp.class});
		
		assertTrue(ann);
	}

}
