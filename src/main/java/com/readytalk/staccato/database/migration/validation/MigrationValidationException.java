package com.readytalk.staccato.database.migration.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown on script invalidation
 */
public class MigrationValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final List<Violation> violations = new ArrayList<Violation>();

	public MigrationValidationException(final String message, final List<Violation> violations) {
		super(message);
		this.violations.addAll(violations);
	}

	public MigrationValidationException(final String s) {
		super(s);
	}

	public MigrationValidationException(final String s, final Throwable throwable) {
		super(s, throwable);
	}

	public MigrationValidationException(final Throwable throwable) {
		super(throwable);
	}

	public static class Violation {
		public String message;
		public String propertyName;
		public Object propertyValue;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(super.getMessage())).append("\n");
		for (Violation violation : violations) {
			builder.append("Property: ").append(violation.propertyName).append("\n");
			builder.append("Value: ").append(violation.propertyValue).append("\n");
			builder.append("Reason: ").append(violation.message).append("\n");
		}
		return builder.toString();
	}

	/**
	 * Returns a list of violations
	 *
	 * @return the violation list
	 */
	public List<Violation> getViolations() {
		return violations;
	}
}