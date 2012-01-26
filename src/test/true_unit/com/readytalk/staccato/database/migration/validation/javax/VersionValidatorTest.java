package com.readytalk.staccato.database.migration.validation.javax;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class VersionValidatorTest {
	
	private final Version version = mock(Version.class);
	
	private VersionValidator validator;
	
	@DataPoints
	public static final String [] validVersions = new String [] {"1.0.0", "1.0", "1", "1.0.1-beta10", "1.1-alpha1"};

	@Before
	public void setUp() throws Exception {
		reset(version);
		validator = new VersionValidator();
		validator.initialize(version);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsValidNull() {
		assertTrue(validator.isValid(null, null));
	}
	
	@Test
	public void testIsValidEmpty() {
		assertTrue(validator.isValid("", null));
	}
	
	@Theory
	public void testIsValid100(final String ver) {
		assertTrue(validator.isValid(ver, null));
	}
	
	@Test
	public void testIsValidFail() {
		assertTrue(validator.isValid("abcd", null));
	}

}
