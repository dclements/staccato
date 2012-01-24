package com.readytalk.staccato.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourceTest {
	
	private Resource r1;
	private Resource r2;
	
	@Before
	public void setUp() throws Exception {
		r1 = new Resource();
		r2 = new Resource();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCodeEquals() {
		assertEquals(r1.hashCode(), r2.hashCode());
	}
	
	@Test
	public void testHashCodeNotEqual() {
		r1.setFilename("test");
		assertNotSame(r1.hashCode(), r2.hashCode());
	}

	@Test
	public void testEqualsIdentity() {
		assertTrue(r1.equals(r1));
	}
	
	@Test
	public void testEqualsNull() {
		assertFalse(r1.equals(null));
	}
	
	@Test
	public void testEqualsSame() {
		assertTrue(r1.equals(r2));
	}
	
	@Test
	public void testEqualsDifferent() {
		r1.setFilename("test");
		assertFalse(r1.equals(r2));
	}

}
