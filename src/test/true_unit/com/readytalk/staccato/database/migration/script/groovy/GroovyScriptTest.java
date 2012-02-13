package com.readytalk.staccato.database.migration.script.groovy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.readytalk.staccato.database.DatabaseType;

public class GroovyScriptTest {
	private GroovyScript script1;
	private GroovyScript script2;
	private final DateTimeFormatter parser = DateTimeFormat.forPattern("DDD-YYYYY HH:mm:ss");
	
	@Before
	public void setUp() throws Exception {
		script1 = new GroovyScript();
		script2 = new GroovyScript();
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testCompareToDifferent() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.POSTGRESQL);
		
		assertEquals(1, script1.compareTo(script2));
	}
	
	@Test
	public void testCompareToDates() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("001-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		//FIXME: Is this really correct ordering?
		assertTrue(script1.compareTo(script2) > 0);
	}
	
	@Test
	public void testCompareToDatesReversed() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("001-2012 13:00:00"));
		
		assertTrue(script1.compareTo(script2) < 0);
	}
	
	@Test
	public void testCompareToDatesSame() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertEquals(0, script1.compareTo(script2));
	}

	
	@Test
	public void testEqualsIdentity() {
		assertTrue(script1.equals(script1));
	}
	
	@Test
	public void testEqualsObject() {
		assertFalse(script1.equals(new Object()));
	}
	
	@Test
	public void testEqualsNull() {
		assertFalse(script1.equals(null));
	}
	
	@Test
	public void testEqualsTrue() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertTrue(script1.equals(script2));
	}
	
	@Test
	public void testEqualsTrueDateOnly() {		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertTrue(script1.equals(script2));
	}
	
	@Test
	public void testEqualsFalseType() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.POSTGRESQL);
		
		assertFalse(script1.equals(script2));
	}
	
	@Test
	public void testEqualsFalseDate() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 14:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertFalse(script1.equals(script2));
	}
	
	@Test
	public void testHashCodeEquals() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.HSQLDB);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertEquals(script1.hashCode(), script2.hashCode());
	}
	
	@Test
	public void testHashCodeDifferent() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.POSTGRESQL);
		
		script1.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		script2.setScriptDate(parser.parseDateTime("002-2012 13:00:00"));
		
		assertNotSame(script1.hashCode(), script2.hashCode());
	}
	
	@Test
	public void testHashCodeIncomplete() {
		script1.setDatabaseType(DatabaseType.HSQLDB);
		script2.setDatabaseType(DatabaseType.POSTGRESQL);
		
		assertNotSame(script1.hashCode(), script2.hashCode());
	}
}
