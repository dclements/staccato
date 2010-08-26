package com.ecovate.utils;

/**
 * Thrown if there are errors loading resources
 *
 * @author jhumphrey
 */
public class ResourceLoaderException extends RuntimeException {
  public ResourceLoaderException() {
    super();
  }

  public ResourceLoaderException(Throwable cause) {
    super(cause);
  }

  public ResourceLoaderException(String message) {
    super(message);
  }

  public ResourceLoaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
