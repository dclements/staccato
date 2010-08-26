package com.ecovate.database;

import org.testng.annotations.BeforeClass;

import com.ecovate.database.migration.guice.MigrationModule;
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
