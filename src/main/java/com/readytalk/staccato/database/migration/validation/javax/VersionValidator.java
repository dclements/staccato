package com.readytalk.staccato.database.migration.validation.javax;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates that a date meets the date format criteria specified by the DateFormat constraint.
 */
public class VersionValidator implements ConstraintValidator<Version, String> {

	private Version version;

	public void initialize(Version constraint) {
		version = constraint;
	}

	/**
	 * Validates that the version string meets the criteria specified by {@link com.readytalk.staccato.utils.Version}
	 *
	 * @param version the version to validate
	 * @param context constraint context
	 * @return true if valid, false otherwise
	 */
	public boolean isValid(final String version, final ConstraintValidatorContext context) {

		if (StringUtils.isEmpty(version)) {
			return true;
		}
		
		// if exception throws, then it's malformed so return invalid
		try {
			new com.readytalk.staccato.utils.Version(version, this.version.strictMode());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
