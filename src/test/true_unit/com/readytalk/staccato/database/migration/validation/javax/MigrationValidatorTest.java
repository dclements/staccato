package com.readytalk.staccato.database.migration.validation.javax;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.IncompleteAnnotationException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.readytalk.staccato.StaccatoOptions;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;

public class MigrationValidatorTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final Validator validator = mock(Validator.class);
	private final StaccatoOptions opts = mock(StaccatoOptions.class);
	private final Migration annotation = mock(Migration.class);
	
	private MigrationValidator mvi;
	@Before
	public void setUp() throws Exception {
		reset(validator, opts, annotation);
		mvi = new MigrationValidatorImpl(validator);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidateStaccatoOptions() {
		when(validator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
		
		mvi.validate(opts);
		
		verify(validator, times(1)).validate(opts);
	}
	
	@Test
	public void testValidateStaccatoOptionsViolation() {
		thrown.expect(MigrationValidationException.class);
		thrown.expectMessage("invalid");
		
		@SuppressWarnings("unchecked")
		ConstraintViolation<Object> violation = (ConstraintViolation<Object>)mock(ConstraintViolation.class);
		Set<ConstraintViolation<Object>> s = new HashSet<ConstraintViolation<Object>>();
		s.add(violation);
		
		when(violation.getMessage()).thenReturn("test");
		when(violation.getInvalidValue()).thenReturn("q");
		
		when(validator.validate(any())).thenReturn(s);
		mvi.validate(opts);
	}
	
	@Test
	public void testValidateMigrationString() {
		when(validator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
		
		mvi.validate(annotation, "test");
		
		verify(validator).validate(any());
	}
	
	@Test
	public void testValidateMigrationStringDatabase() {
		thrown.expect(MigrationValidationException.class);
		when(validator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
		doThrow(IncompleteAnnotationException.class).when(annotation).databaseVersion();
		
		mvi.validate(annotation, "test");
	}
	
	@Test
	public void testValidateMigrationStringScriptDate() {
		thrown.expect(MigrationValidationException.class);
		when(validator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
		doThrow(IncompleteAnnotationException.class).when(annotation).scriptDate();
		
		mvi.validate(annotation, "test");
	}
	
	@Test
	public void testValidateMigrationStringScriptVersion() {
		thrown.expect(MigrationValidationException.class);
		when(validator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
		doThrow(IncompleteAnnotationException.class).when(annotation).scriptVersion();
		
		mvi.validate(annotation, "test");
	}
}
