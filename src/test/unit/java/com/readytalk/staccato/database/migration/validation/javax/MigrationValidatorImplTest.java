package com.readytalk.staccato.database.migration.validation.javax;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.List;

import javax.validation.Validator;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;

public class MigrationValidatorImplTest extends BaseTest {

	Validator validator;

	@Inject
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Test
	public void testInvalidMigrationAnnotation() throws MalformedURLException {

		{
			Migration migration = mock(Migration.class);
			when(migration.scriptDate()).thenReturn("foo");
			when(migration.scriptVersion()).thenReturn("bar");
			when(migration.databaseVersion()).thenReturn("baz");

			MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

			try {
				migrationValidator.validate(migration, "test.groovy");
				Assert.fail("should have thrown exception");
			} catch (MigrationValidationException e) {
				// validate violations
				List<MigrationValidationException.Violation> violations = e.getViolations();
				Assert.assertEquals(violations.size(), 3);
			}
		}

		{
			Migration migration = mock(Migration.class);
			when(migration.scriptDate()).thenReturn(null);
			when(migration.scriptVersion()).thenReturn(null);
			when(migration.databaseVersion()).thenReturn(null);

			MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

			try {
				migrationValidator.validate(migration, "test.groovy");
				Assert.fail("should have thrown exception");
			} catch (MigrationValidationException e) {
				// validate violations
				List<MigrationValidationException.Violation> violations = e.getViolations();
				Assert.assertEquals(violations.size(), 3);
			}
		}
	}

	@Test
	public void testValidMigrationAnnotation() throws MalformedURLException {

		{
			Migration migration = mock(Migration.class);
			when(migration.scriptDate()).thenReturn("2010-01-01T08:00:00-06:00");
			when(migration.scriptVersion()).thenReturn("1.0");
			when(migration.databaseVersion()).thenReturn("1.0");

			MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

			try {
				migrationValidator.validate(migration, "test.groovy");
			} catch (MigrationValidationException e) {
				Assert.fail("should not have thrown exception");
			}
		}

		{
			Migration migration = mock(Migration.class);
			when(migration.scriptDate()).thenReturn("2010-01-01T08:00:00-06:00");
			when(migration.scriptVersion()).thenReturn("1.0-SNAPSHOT");
			when(migration.databaseVersion()).thenReturn("1.0");

			MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

			try {
				migrationValidator.validate(migration, "test.groovy");
			} catch (MigrationValidationException e) {
				Assert.fail("should not have thrown exception");
			}
		}
	}
}
