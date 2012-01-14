package com.readytalk.staccato.database.migration.guice;

import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MigrationModule extends AbstractModule {

	@Override
	protected void configure() {

	}

	@Provides
	public Validator provideValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Provides
	public CommandLineParser provideParser() {
		return new BasicParser();
	}

	@Provides
	public HelpFormatter provideHelpFormatter() {
		return new HelpFormatter();
	}
}