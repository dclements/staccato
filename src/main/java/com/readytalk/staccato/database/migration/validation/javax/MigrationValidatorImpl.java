package com.readytalk.staccato.database.migration.validation.javax;

import java.lang.annotation.IncompleteAnnotationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.readytalk.staccato.StaccatoOptions;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;
import com.readytalk.staccato.database.migration.validation.MigrationValidator;

/**
 * An implementation using javax.validation (JSR 303).
 */
public class MigrationValidatorImpl implements MigrationValidator {

	private final Validator validator;

	@Inject
	public MigrationValidatorImpl(final Validator _validator) {
		this.validator = _validator;
	}

	@Override
	public void validate(final StaccatoOptions options) {
		final List<MigrationValidationException.Violation> violations = processConstraintViolations(options);
		if (violations.size() > 0) {
			throw new MigrationValidationException("One or more Staccato options are invalid", violations);
		}
	}

	@Override
	public void validate(final Migration migrationAnnotation, final String scriptFilename)
			throws MigrationValidationException {

		final MigrationAnnotationStruct struct = new MigrationAnnotationStruct();

		final List<MigrationValidationException.Violation> violations =
			new ArrayList<MigrationValidationException.Violation>();

		try {
			struct.databaseVersion = migrationAnnotation.databaseVersion();
		} catch (IncompleteAnnotationException e) {
			violations.add(handleIncompleteAnnotationException("databaseVersion"));
		}

		try {
			struct.scriptDate = migrationAnnotation.scriptDate();
		} catch (IncompleteAnnotationException e) {
			violations.add(handleIncompleteAnnotationException("scriptDate"));
		}

		try {
			struct.scriptVersion = migrationAnnotation.scriptVersion();
		} catch (IncompleteAnnotationException e) {
			violations.add(handleIncompleteAnnotationException("scriptVersion"));
		}

		violations.addAll(processConstraintViolations(struct));

		if (violations.size() > 0) {
			throw new MigrationValidationException("@Migration annotation invalid for script: "
					+ scriptFilename, violations);
		}
	}

	/**
	 * Helper method for handling incomplete annotation exceptions
	 *
	 * @param propertyName the property name
	 * @return a violation
	 */
	private MigrationValidationException.Violation handleIncompleteAnnotationException(final String propertyName) {
		final MigrationValidationException.Violation violation = new MigrationValidationException.Violation();
		violation.message = propertyName + " is undefined but is a required field.";
		violation.propertyValue = "undefined";
		violation.propertyName = propertyName;
		return violation;
	}

	/**
	 * Helper method for converting javax constraint violations to a MigrationExceptionValidation.Violation
	 *
	 * @param objectToValidate the object to validate
	 * @return a list of violations
	 */
	private List<MigrationValidationException.Violation> processConstraintViolations(final Object objectToValidate) {

		final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(objectToValidate);

		final List<MigrationValidationException.Violation> violations =
			new ArrayList<MigrationValidationException.Violation>();

		for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
			MigrationValidationException.Violation violation = new MigrationValidationException.Violation();
			violation.message = constraintViolation.getMessage();
			violation.propertyValue = constraintViolation.getInvalidValue();
			violation.propertyName = String.valueOf(constraintViolation.getPropertyPath());
			violations.add(violation);
		}

		return violations;
	}

	/**
	 * This struct is used to validate the @Migration annotation.  JSR 303 is lame and
	 * can't validate annotations because annotation properties don't follow the JavaBean
	 * method naming conventions (e.g. scriptDate() -vs- getScriptDate())
	 */
	private static class MigrationAnnotationStruct {

		@SuppressWarnings("unused")
		@NotNull
		@DateFormat
		private String scriptDate;

		@SuppressWarnings("unused")
		@NotNull
		@Version(strictMode = Migration.scriptVersionStrictMode)
		private String scriptVersion;

		@SuppressWarnings("unused")
		@NotNull
		@Version(strictMode = Migration.databaseVersionStrictMode)
		private String databaseVersion;
	}
}
