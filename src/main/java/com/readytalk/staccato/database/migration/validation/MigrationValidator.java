package com.readytalk.staccato.database.migration.validation;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.StaccatoOptions;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.script.Script;
import com.readytalk.staccato.database.migration.validation.javax.MigrationValidatorImpl;

/**
 * Used for validating {@link Script}
 *
 * @author jhumphrey
 */
@ImplementedBy(MigrationValidatorImpl.class)
public interface MigrationValidator {

  /**
   * Validates staccato options
   *
   * @param options staccato options
   */
  public void validate(StaccatoOptions options);

  /**
   * Validates a migration annotation
   *
   * @param migrationAnnotation a migration annotation
   * @param scriptFilename the script filename
   * @throws MigrationValidationException if there's an invalidation
   */
  public void validate(Migration migrationAnnotation, String scriptFilename) throws MigrationValidationException;
}
