package com.readytalk.staccato.database;

public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID=1L;
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
