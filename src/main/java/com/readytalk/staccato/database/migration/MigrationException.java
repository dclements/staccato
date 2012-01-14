package com.readytalk.staccato.database.migration;

public class MigrationException extends RuntimeException {
	private static final long serialVersionUID=1L;
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
