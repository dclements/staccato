package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.readytalk.staccato.database.DatabaseType;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to define Migration scripts. Scripts that wish to
 * be included in the migration execution must be configured with this annotated.
 *
 * This is a class level annotation
 */
@Inherited
@Target({TYPE})
@Retention(RUNTIME)
public @interface Migration {

	final boolean scriptVersionStrictMode = true;
	final boolean databaseVersionStrictMode = true;

	/**
	 * The script date
	 *
	 * @return the script date
	 */
	String scriptDate();

	/**
	 * The database version
	 *
	 * @return the database version
	 */
	String databaseVersion();

	/**
	 * An informative description describing the migration script
	 *
	 * @return the description of the migration script
	 */
	String description();

	/**
	 * Informs the system which database type the script belongs to.
	 * If undefined, the system will assume to queue the script for execution
	 *
	 * @return the database type
	 */
	DatabaseType databaseType();

	/**
	 * A version of the script template used to create the migration script
	 *
	 * @return the hash of the script template
	 */
	String scriptVersion();
}
