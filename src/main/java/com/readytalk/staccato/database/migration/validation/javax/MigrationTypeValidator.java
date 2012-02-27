package com.readytalk.staccato.database.migration.validation.javax;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.readytalk.staccato.database.migration.MigrationType;

public class MigrationTypeValidator implements ConstraintValidator<MigrationTypeConstraint, String> {

	@Override
	public void initialize(final MigrationTypeConstraint constraintAnnotation) {
		//Nothing to do with the annotation, so we won't store it.
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if(StringUtils.isEmpty(value)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Value cannot be null for MigrationType.").addConstraintViolation();
			return false;
		}
		
		try {
			MigrationType.valueOf(value);
			return true;
		} catch(final IllegalArgumentException e) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid migrationType: "
					+ String.valueOf(value) + ".  The list of valid migration types are:\n"
					+ MigrationType.description()).addConstraintViolation();
			return false;
		}
	}
}
