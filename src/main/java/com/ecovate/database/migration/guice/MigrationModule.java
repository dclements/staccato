package com.ecovate.database.migration.guice;

import javax.validation.Validation;
import javax.validation.Validator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author jhumphrey
 */
public class MigrationModule extends AbstractModule {

  @Override
  protected void configure() {

  }

  @Provides
  public Validator provideValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }
}