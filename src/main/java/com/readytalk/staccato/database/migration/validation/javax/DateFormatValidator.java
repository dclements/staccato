package com.readytalk.staccato.database.migration.validation.javax;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;

/**
 * Validates that a date meets the date format criteria specified by the DateFormat constraint
 *
 * @author jhumphrey
 */
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

  private String dateFormat;

  public void initialize(DateFormat constraint) {
    dateFormat = constraint.dateFormat();
  }

  /**
   * Returns true if the following criteria are met:
   *
   * 1.  if the isoDate is null or empty string
   * 2.  if the iso date conforms to the DateFormat.dateFormat
   *
   * Returns false if the following criteria are met:
   *
   * 1.  if the iso date does not conform to the isoDate format
   *
   * @param isoDate the iso date to validate
   * @param context constraint context
   * @return true if valid, false otherwise
   */
  public boolean isValid(String isoDate, ConstraintValidatorContext context) {

    if (StringUtils.isEmpty(isoDate)) {
      return true;
    }

    // attempt to parse the iso date to the format defined in the DateFormat constraint.
    // if exception throws, then it's malformed so return invalid
    try {
      DateTimeFormat.forPattern(dateFormat).parseDateTime(isoDate);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
