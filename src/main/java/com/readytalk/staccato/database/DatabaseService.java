package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.migration.script.Script;

@ImplementedBy(DatabaseServiceImpl.class)
public interface DatabaseService {

	/**
	 * Used for building the database context
	 *
	 * @return database context builder
	 */
	DatabaseContextBuilder getDatabaseContextBuilder();

	/**
	 * Connects to the database and returns a jdbc connection
	 * @param jdbcUri the jdbc uri
	 * @param username the username
	 * @param password the password
	 * @param databaseType the database type
	 * @return returns a connection
	 */
	Connection connect(URI jdbcUri, String username, String password, DatabaseType databaseType);
	
	/**
	 * Connects to the database, returning the jdbc connection
	 * and adding it to the supplied context.
	 * @param context The database context.
	 * @return A JDBC connection to a database.
	 */
	Connection connect(DatabaseContext context);

	/**
	 * Disconnects from the database using the context specified
	 *
	 * @param context the database context
	 */
	void disconnect(DatabaseContext context);

	/**
	 * Starts a database transaction
	 *
	 * @param context the database context
	 * @param script the script creating a transaction for
	 */
	void startTransaction(DatabaseContext context, Script<?> script);

	/**
	 * Ends the transaction
	 *
	 * @param context the database connection
	 * @param script the script ending the transaction for
	 */
	void endTransaction(DatabaseContext context, Script<?> script);

	/**
	 * Rollsback any queries in the connection
	 *
	 * @param context the database connection
	 * @param script the script rolling back
	 */
	void rollback(DatabaseContext context, Script<?> script);
}
