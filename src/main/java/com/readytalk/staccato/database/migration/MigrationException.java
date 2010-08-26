package com.readytalk.staccato.database.migration;

/**
 * @author jhumphrey
 */
public class MigrationException extends RuntimeException {
  public MigrationException() {
    super();
  }

  public MigrationException(Throwable cause) {
    super(cause);
  }

  public MigrationException(String message) {
    super(message);
  }

  public MigrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
