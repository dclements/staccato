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
@Constraint(validatedBy = {VersionValidator.class})
@Documented
public @interface Version {

	boolean strictMode() default false;

	String message() default "Version must conform to format X.Y.Z";

	Class<?>[] groups() default {};

	Class<?>[] payload() default {};
}