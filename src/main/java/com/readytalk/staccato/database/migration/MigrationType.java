package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;

import com.readytalk.staccato.database.migration.annotation.DataUp;
import com.readytalk.staccato.database.migration.annotation.PostUp;
import com.readytalk.staccato.database.migration.annotation.PreUp;
import com.readytalk.staccato.database.migration.annotation.SchemaUp;

/**
 * Models migration workflows.  Execution starts from the first index in the workflowSteps array so order matters
 *
 * @author jhumphrey
 */
@SuppressWarnings("unchecked")
public enum MigrationType {
  SCHEMA_UP(new Class[]{SchemaUp.class}),
  SCHEMA_DATA_UP(new Class[]{SchemaUp.class}),
  DATA_UP(new Class[]{DataUp.class}),
  PRE_UP(new Class[]{PreUp.class}),
  POST_UP(new Class[]{PostUp.class}),
  UP(new Class[]{PreUp.class, SchemaUp.class, DataUp.class, PostUp.class}),
  NEW(new Class[]{PreUp.class, SchemaUp.class, DataUp.class, PostUp.class});

  private Class<? extends Annotation>[] workflowSteps;

  MigrationType(Class<? extends Annotation>[] workflowSteps) {
    this.workflowSteps = workflowSteps;
  }

  public Class<? extends Annotation>[] getWorkflowSteps() {
    return workflowSteps;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Class<? extends Annotation> workflowStep : workflowSteps) {
      builder.append(workflowStep.getSimpleName()).append(" ");
    }
    return builder.toString();
  }

  public static String description() {
    StringBuilder builder = new StringBuilder();
    for (MigrationType migrationType : MigrationType.values()) {
      builder.append(migrationType.name()).append(": ").append(migrationType.toString()).append("\n");
    }

    return builder.toString();
  }
}
