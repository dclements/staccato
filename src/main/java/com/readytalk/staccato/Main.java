package com.readytalk.staccato;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.migration.guice.MigrationModule;

/**
 * Contains the main for running this application.
 */
public class Main {

	public final static Injector injector = Guice.createInjector(new MigrationModule());

	public static void main(String... args) {

		CommandLineService cls = injector.getInstance(CommandLineService.class);

		StaccatoOptions options = cls.parse(args);

		if (options != null) {
			Staccato staccato = injector.getInstance(Staccato.class);
			staccato.execute(options);
		}
	}
}