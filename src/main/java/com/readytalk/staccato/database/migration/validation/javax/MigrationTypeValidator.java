package com.readytalk.staccato.database.migration.validation.javax;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.readytalk.staccato.database.migration.MigrationType;

public class MigrationTypeValidator implements ConstraintValidator<MigrationTypeConstraint, String> {

	MigrationTypeConstraint constraint;

	@Override
	public void initialize(MigrationTypeConstraint constraintAnnotation) {
		this.constraint = constraintAnnotation;

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		try {
			MigrationType.valueOf(value);
			return true;
		} catch (Exception e) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid migrationType: " + value + ".  The list of valid migration types are:\n" + MigrationType.description()).addConstraintViolation();
			return false;
		}
	}
}
