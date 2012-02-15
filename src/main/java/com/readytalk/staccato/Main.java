package com.readytalk.staccato;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.migration.guice.MigrationModule;

/**
 * Contains the main for running this application.
 */
public class Main {
	
	private Main() {
		super();
	}

	public static void main(String... args) {
		final Injector injector = Guice.createInjector(new MigrationModule());

		final CommandLineService cls = injector.getInstance(CommandLineService.class);

		final StaccatoOptions options = cls.parse(args);
		
		//Null return represents the help dialogue.
		if (options != null) {
			StaccatoExecutor staccato = injector.getInstance(StaccatoExecutor.class);
			staccato.execute(options);
		}
	}
}
