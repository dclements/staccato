package com.readytalk.staccato.database.migration.validation.javax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

import com.readytalk.staccato.database.migration.MigrationType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint for validating DateRange objects
 *
 * @author jhumphrey
 */

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {MigrationTypeValidator.class})
@Documented
public @interface MigrationTypeConstraint {

  String message() default "Invalid migrationType";

  Class[] groups() default {};

  Class[] payload() default {};
}