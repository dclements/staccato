package com.readytalk.staccato.database.migration.validation.javax;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.readytalk.staccato.database.migration.MigrationType;


@RunWith(Theories.class)
public class MigrationTypeValidatorTest {

	private MigrationTypeValidator mtv;
	private final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
	private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

	@DataPoints
	public final static MigrationType [] types = MigrationType.values();

	@Before
	public void setUp() throws Exception {
		reset(context, builder);
		mtv = new MigrationTypeValidator();
		
		when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Theory
	public void testIsValid(MigrationType type) {
		assertTrue(mtv.isValid(type.name(), context));
	}
	
	@Test
	public void testIsValidNull() {
		assertFalse(mtv.isValid(null, context));
		
		verify(context).disableDefaultConstraintViolation();
		verify(builder, times(1)).addConstraintViolation();
	}
	
	@Test
	public void testIsValidInvalid() {
		assertFalse(mtv.isValid("blue", context));
		
		verify(context).disableDefaultConstraintViolation();
		verify(builder, times(1)).addConstraintViolation();
	}

}
