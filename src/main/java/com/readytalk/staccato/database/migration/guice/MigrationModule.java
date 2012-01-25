package com.readytalk.staccato.database.migration.guice;

import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.MigrationRuntimeFactory;
import com.readytalk.staccato.database.migration.MigrationRuntimeImpl;
import com.readytalk.staccato.database.migration.workflow.WorkflowContext;
import com.readytalk.staccato.database.migration.workflow.WorkflowContextFactory;
import com.readytalk.staccato.database.migration.workflow.WorkflowContextImpl;

public class MigrationModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
			.implement(MigrationRuntime.class, MigrationRuntimeImpl.class)
			.build(MigrationRuntimeFactory.class));
		install(new FactoryModuleBuilder()
			.implement(WorkflowContext.class, WorkflowContextImpl.class)
			.build(WorkflowContextFactory.class));
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
