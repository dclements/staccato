package com.readytalk.staccato.database.migration.validation.javax;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.readytalk.staccato.database.migration.MigrationType;

public class MigrationTypeValidator implements ConstraintValidator<MigrationTypeConstraint, String> {

	private MigrationTypeConstraint constraint;

	@Override
	public void initialize(final MigrationTypeConstraint constraintAnnotation) {
		this.constraint = constraintAnnotation;

	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		try {
			MigrationType.valueOf(value);
			return true;
		} catch (Exception e) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid migrationType: "
					+ String.valueOf(value) + ".  The list of valid migration types are:\n"
					+ MigrationType.description()).addConstraintViolation();
			return false;
		}
	}
}
