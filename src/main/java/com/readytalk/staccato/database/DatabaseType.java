package com.readytalk.staccato.database;

import java.net.URI;

import com.readytalk.staccato.database.migration.MigrationException;

/**
 * Represents a database type
 */
public enum DatabaseType {
	MYSQL("mysql", "com.mysql.jdbc.Driver", "mysql"),
	POSTGRESQL("postgresql", "org.postgresql.Driver", "postgres"),
	HSQLDB("hsqldb", "org.hsqldb.jdbcDriver", "hsqldb");

	private String type;
	private String driver;
	private String root;

	DatabaseType(final String _type, final String _driver, final String _root) {
		this.type = _type;
		this.driver = _driver;
		this.root = _root;
	}

	/**
	 * Returns the jdbc driver
	 *
	 * @return the jdbc driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Returns the database type
	 *
	 * @return the database type
	 */
	public String getType() {
		return type;
	}

	public String getRoot() {
		return root;
	}

	/**
	 * Convenience method for returning a type from a JDBC URI
	 *
	 * @param uri JDBC URI
	 * @return a database type
	 */
	public static DatabaseType getTypeFromJDBCUri(final URI uri) {
		try {
			return DatabaseType.valueOf(uri.getSchemeSpecificPart().split(":")[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new MigrationException("Staccato currently does not provide support for the following jdbc uri: " + uri);
		}
	}

}