package com.readytalk.staccato.utils;

/**
 * Thrown if there are errors loading resources.
 */
public class ResourceLoaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ResourceLoaderException() {
		super();
	}

	public ResourceLoaderException(final Throwable cause) {
		super(cause);
	}

	public ResourceLoaderException(final String message) {
		super(message);
	}

	public ResourceLoaderException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
