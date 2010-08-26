package com.readytalk.staccato.database.migration.script.validation.javax;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.readytalk.staccato.database.migration.script.Script;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidationException;
import com.readytalk.staccato.database.migration.script.validation.ScriptValidator;
import com.google.inject.Inject;

/**
 * An implementation using javax.validation (JSR 303)
 *
 * @author jhumphrey
 */
public class ScriptValidatorImpl implements ScriptValidator {

  Validator validator;

  @Inject
  public ScriptValidatorImpl(Validator validator) {
    this.validator = validator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Script script) throws ScriptValidationException {
    Set<ConstraintViolation<Script>> constraintViolations = validator.validate(script);
    if (constraintViolations.size() > 0) {
      throw new ScriptValidationException(script.getFilename() + " is not a valid script: " + constraintViolations);
    }
  }
}
