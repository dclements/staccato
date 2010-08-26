package com.readytalk.staccato.database;

import org.testng.annotations.BeforeClass;

import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author jhumphrey
 */
public class BaseTest {

  protected Injector injector;

  @BeforeClass()
  public void initGuice() {
    injector = Guice.createInjector(new MigrationModule());
    injector.injectMembers(this);
  }
}
