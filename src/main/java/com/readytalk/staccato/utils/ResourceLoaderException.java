package com.readytalk.staccato.utils;

/**
 * Thrown if there are errors loading resources.
 */
public class ResourceLoaderException extends RuntimeException {
	private static final long serialVersionUID=1L;
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
