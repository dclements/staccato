package com.ecovate.database.migration.script.validation;

/**
 * Thrown on script invalidation
 *
 * @author jhumphrey
 */
public class ScriptValidationException extends RuntimeException {
  public ScriptValidationException() {
    super();
  }

  public ScriptValidationException(Throwable cause) {
    super(cause);
  }

  public ScriptValidationException(String message) {
    super(message);
  }

  public ScriptValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
