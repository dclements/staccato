package com.readytalk.staccato.database.migration.validation.javax;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.readytalk.staccato.database.migration.validation.javax.DateFormat;
import com.readytalk.staccato.database.migration.validation.javax.DateFormatValidator;

public class JUDateFormatValidatorTest {

	private DateFormatValidator dfv;
	
	@Before
	public void setUp() throws Exception {
		DateFormat df = mock(DateFormat.class);
		
		when(df.dateFormat()).thenReturn("DDD-YYYY");
		
		dfv = new DateFormatValidator();
		dfv.initialize(df);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsValidNull() {
		assertTrue(dfv.isValid(null, null));
	}
	
	@Test
	public void testIsValidEmpty() {
		assertTrue(dfv.isValid("", null));
	}
	
	@Test
	public void testIsValidTrue() {
		assertTrue(dfv.isValid("100-2012", null));
	}
	
	@Test
	public void testIsValidFalse() {
		assertFalse(dfv.isValid("12/12/12", null));
	}

}
