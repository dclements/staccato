package com.readytalk.staccato.database.migration.validation.javax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint for validating DateRange objects.
 */

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {DateFormatValidator.class})
@Documented
public @interface DateFormat {

	static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";

	String message() default "date must conform to the ISO8601 format: {dateFormat}";

	String dateFormat() default ISO_DATE_FORMAT;

	Class<?>[] groups() default {};

	Class<?>[] payload() default {};
}