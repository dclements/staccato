package com.readytalk.staccato;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.readytalk.staccato.database.migration.guice.MigrationModule;

public class GuiceTest {
	private final Module [] modules = new Module [] {new MigrationModule()};
	
	private final Injector injector = Guice.createInjector(new MigrationModule());
	
	protected Injector getInjector() {
		return injector;
	}
	
	/**
	 * XXX: This is a bit of a hack to allow the easy injection of spies. Eventually will
	 * want to build or find a more convenient framework for these things. 
	 * @param overrides
	 * @return
	 */
	protected Injector createOverride(Module ... overrides) {
		return Guice.createInjector(Modules.override(modules).with(overrides));
	}
}
