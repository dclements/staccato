package com.ecovate.database;

/**
 * @author jhumphrey
 */
public class DatabaseException extends RuntimeException {
  public DatabaseException() {
    super();
  }

  public DatabaseException(String message) {
    super(message);
  }

  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatabaseException(Throwable cause) {
    super(cause);
  }
}
