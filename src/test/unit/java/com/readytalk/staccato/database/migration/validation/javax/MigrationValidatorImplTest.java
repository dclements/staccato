package com.readytalk.staccato.database.migration.validation.javax;

import java.net.MalformedURLException;
import java.util.List;
import javax.validation.Validator;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;

/**
 * @author jhumphrey
 */
public class MigrationValidatorImplTest extends BaseTest {

  Validator validator;

  @Inject
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  @Test
  public void testInvalidMigrationAnnotation() throws MalformedURLException {

    {
      Migration migration = EasyMock.createMock(Migration.class);
      EasyMock.expect(migration.scriptDate()).andReturn("foo");
      EasyMock.expect(migration.scriptVersion()).andReturn("bar");
      EasyMock.expect(migration.databaseVersion()).andReturn("baz");
      EasyMock.replay(migration);

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
      Migration migration = EasyMock.createMock(Migration.class);
      EasyMock.expect(migration.scriptDate()).andReturn(null);
      EasyMock.expect(migration.scriptVersion()).andReturn(null);
      EasyMock.expect(migration.databaseVersion()).andReturn(null);
      EasyMock.replay(migration);

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
      Migration migration = EasyMock.createMock(Migration.class);
      EasyMock.expect(migration.scriptDate()).andReturn("2010-01-01T08:00:00-06:00");
      EasyMock.expect(migration.scriptVersion()).andReturn("1.0");
      EasyMock.expect(migration.databaseVersion()).andReturn("1.0");
      EasyMock.replay(migration);

      MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

      try {
        migrationValidator.validate(migration, "test.groovy");
      } catch (MigrationValidationException e) {
        Assert.fail("should not have thrown exception");
      }
    }

    {
      Migration migration = EasyMock.createMock(Migration.class);
      EasyMock.expect(migration.scriptDate()).andReturn("2010-01-01T08:00:00-06:00");
      EasyMock.expect(migration.scriptVersion()).andReturn("1.0-SNAPSHOT");
      EasyMock.expect(migration.databaseVersion()).andReturn("1.0");
      EasyMock.replay(migration);

      MigrationValidator migrationValidator = new MigrationValidatorImpl(validator);

      try {
        migrationValidator.validate(migration, "test.groovy");
      } catch (MigrationValidationException e) {
        Assert.fail("should not have thrown exception");
      }
    }
  }
}
