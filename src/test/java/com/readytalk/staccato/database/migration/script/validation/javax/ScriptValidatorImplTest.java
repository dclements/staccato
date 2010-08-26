package com.readytalk.staccato.database.migration.script.validation.javax;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.script.Script;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidationException;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidator;

/**
 * @author jhumphrey
 */
public class ScriptValidatorImplTest {

  @Test
  public void testSuccessfulValidation() throws MalformedURLException {

    Script script = EasyMock.createMock(Script.class);

    Set<ConstraintViolation<Script>> constraintViolations = new HashSet<ConstraintViolation<Script>>();

    Validator validator = EasyMock.createStrictMock(Validator.class);
    EasyMock.expect(validator.validate(script)).andReturn(constraintViolations);
    EasyMock.replay(validator);

    ScriptValidator scriptValidator = new ScriptValidatorImpl(validator);

    try {
      scriptValidator.validate(script);
    } catch (ScriptValidationException e) {
      Assert.fail("should not have thrown a script validation exception", e);
    }
  }

  @Test
  public void testUnsuccessfulValidation() throws MalformedURLException {

    Script script = EasyMock.createMock(Script.class);

    @SuppressWarnings(value = "unchecked")
    ConstraintViolation<Script> constraintViolation = EasyMock.createMock(ConstraintViolation.class);
    Set<ConstraintViolation<Script>> constraintViolations = new HashSet<ConstraintViolation<Script>>();
    constraintViolations.add(constraintViolation);

    Validator validator = EasyMock.createStrictMock(Validator.class);
    EasyMock.expect(validator.validate(script)).andReturn(constraintViolations);
    EasyMock.replay(validator);

    ScriptValidator scriptValidator = new ScriptValidatorImpl(validator);

    try {
      scriptValidator.validate(script);
      Assert.fail("should have thrown a script validation exception");
    } catch (ScriptValidationException e) {
      // no-op, test successful
    }
  }
}
