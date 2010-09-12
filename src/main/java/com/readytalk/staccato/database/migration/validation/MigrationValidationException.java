package com.readytalk.staccato.database.migration.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown on script invalidation
 *
 * @author jhumphrey
 */
public class MigrationValidationException extends RuntimeException {

  List<Violation> violations = new ArrayList<Violation>();
  String message;

  public MigrationValidationException(String message, List<Violation> violations) {
    this.message = message;
    for (Violation violation : violations) {
      addViolation(violation);
    }
  }

  public MigrationValidationException(String message, Violation violation) {
    this.message = message;
    addViolation(violation);
  }

  private void addViolation(Violation violation) {
    violations.add(violation);
  }

  public static class Violation {
    public String message;
    public String propertyName;
    public Object propertyValue;
  }

  @Override
  public String getMessage() {
    StringBuilder builder = new StringBuilder();
    builder.append("The following migration violations occurred:\n");
    for (Violation violation : violations) {
      builder.append("Property: ").append(violation.propertyName).append("\n");
      builder.append("Value: ").append(violation.propertyValue).append("\n");
      builder.append("Reason: ").append(violation.message).append("\n");
    }
    return builder.toString();
  }

  /**
   * Returns a list of violations
   *
   * @return the violation list
   */
  public List<Violation> getViolations() {
    return violations;
  }
}