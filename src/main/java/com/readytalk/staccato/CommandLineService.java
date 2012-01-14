package com.readytalk.staccato;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;

/**
 * Interface for Staccato command line.
 */
@ImplementedBy(CommandLineServiceImpl.class)
public interface CommandLineService {

	/**
	 * Called to parse the command line arguments
	 *
	 * @param args the command line arguments
	 * @throws MigrationValidationException if there are errors when parsing the command line arguments
	 * @return staccato options
	 */
	StaccatoOptions parse(String... args) throws MigrationValidationException;
}
