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
 * An implementation using javax.validation (JSR 303)
 *
 * @author jhumphrey
 */
public class MigrationValidatorImpl implements MigrationValidator {

  Validator validator;

  @Inject
  public MigrationValidatorImpl(Validator validator) {
    this.validator = validator;
  }

  @Override
  public void validate(StaccatoOptions options) {
    this.processConstraintViolations(options);
  }

  @Override
  public void validate(Migration migrationAnnotation, String scriptFilename) throws MigrationValidationException {

    MigrationAnnotationStruct struct = new MigrationAnnotationStruct();

    List<MigrationValidationException.Violation> violations = new ArrayList<MigrationValidationException.Violation>();

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
      throw new MigrationValidationException("@Migration annotation invalid for script: " + scriptFilename, violations);
    }
  }

  /**
   * Helper method for handling incomplete annotation exceptions
   *
   * @param propertyName the property name
   * @return a violation
   */
  private MigrationValidationException.Violation handleIncompleteAnnotationException(String propertyName) {
    MigrationValidationException.Violation violation = new MigrationValidationException.Violation();
    violation.message = propertyName + " is undefined but is a required field.";
    violation.propertyValue = "undefined";
    violation.propertyName = propertyName;
    return violation;
  }

  /**
   * Helper method for converting javax constraint violations to a MigrationExceptionValidation.Violation
   *
   * @param objectToValidate the object to validate
   * @return a list of {@link com.readytalk.staccato.database.migration.validation.MigrationValidationException.Violation}
   */
  private List<MigrationValidationException.Violation> processConstraintViolations(Object objectToValidate) {

    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(objectToValidate);

    List<MigrationValidationException.Violation> violations = new ArrayList<MigrationValidationException.Violation>();

    for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
      MigrationValidationException.Violation violation = new MigrationValidationException.Violation();
      violation.message = constraintViolation.getMessage();
      violation.propertyValue = constraintViolation.getInvalidValue();
      violation.propertyName = constraintViolation.getPropertyPath().toString();
      violations.add(violation);
    }

    return violations;
  }

  /**
   * This struct is used to validate the @Migration annotation.  JSR 303 is lame and
   * can't validate annotations because annotation properties don't follow the JavaBean
   * method naming conventions (e.g. scriptDate() -vs- getScriptDate())
   */
  class MigrationAnnotationStruct {

    @NotNull
    @DateFormat
    String scriptDate;

    @NotNull
    @Version(strictMode = Migration.scriptVersionStrictMode)
    String scriptVersion;

    @NotNull
    @Version(strictMode = Migration.databaseVersionStrictMode)
    String databaseVersion;
  }
}
