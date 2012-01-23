package com.readytalk.staccato.database.migration.script.sql;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JUSQLScriptTest {
	
	private SQLScript script1; 
	private SQLScript script2;
	
	@Before
	public void setUp() throws Exception {
		script1 = new SQLScript();
		script2 = new SQLScript();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testHashCodeSame() {
		assertEquals(script1.hashCode(), script2.hashCode());
	}
	
	@Test
	public void testHashCodeDifferent() {
		script1.setFilename("foo");
		assertNotSame(script1.hashCode(), script2.hashCode());
	}

	@Test
	public void testCompareToEquals() {
		assertEquals(0, script1.compareTo(script2));
	}
	
	@Test
	public void testCompareToNotEquals() {
		script1.setFilename("foo");
		assertNotSame(0, script1.compareTo(script2));
	}
	

	@Test
	public void testEqualsNull() {
		assertFalse(script1.equals(null));
	}
	
	@Test
	public void testEqualsInstance() {
		assertTrue(script1.equals(script1));
	}
	
	@Test
	public void testEqualsFilenameTrue() {
		script1.setFilename("foo");
		script2.setFilename("foo");
		assertTrue(script1.equals(script2));
	}
	
	@Test
	public void testEqualsFilenameFalse() {
		script1.setFilename("foo");
		script2.setFilename("bar");
		assertFalse(script1.equals(script2));
	}
	
	@Test
	public void testEqualsFilenameNull() {
		assertTrue(script1.equals(script2));
	}

}
