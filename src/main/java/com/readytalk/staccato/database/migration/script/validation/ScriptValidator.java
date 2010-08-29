package com.readytalk.staccato.database.migration.script.validation;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.script.Script;
import com.readytalk.staccato.database.migration.script.validation.javax.ScriptValidatorImpl;

/**
 * Used for validating {@link Script}
 *
 * @author jhumphrey
 */
@ImplementedBy(ScriptValidatorImpl.class)
public interface ScriptValidator {

  /**
   * Validates a script
   *
   * @param script the {@link Script} to validate
   * @throws ScriptValidationException if there's an invalidation
   */
  public void validate(Script script) throws ScriptValidationException;
}
