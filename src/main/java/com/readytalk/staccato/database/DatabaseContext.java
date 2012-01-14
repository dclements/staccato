package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple struct for modeling database meta-data.
 */
public class DatabaseContext {

	private Connection connection;
	private URI baseJdbcUri;
	private URI fullyQualifiedJdbcUri;
	private String username;
	private String password;
	private String dbName;
	private String rootDbName;
	private String superUser;
	private String superUserPwd;
	private DatabaseType databaseType;
	private final Map<String, Savepoint> txnSavepoints = new HashMap<String, Savepoint>();

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public URI getBaseJdbcUri() {
		return baseJdbcUri;
	}

	public void setBaseJdbcUri(URI baseJdbcUri) {
		this.baseJdbcUri = baseJdbcUri;
	}

	public URI getFullyQualifiedJdbcUri() {
		return fullyQualifiedJdbcUri;
	}

	public void setFullyQualifiedJdbcUri(URI fullyQualifiedJdbcUri) {
		this.fullyQualifiedJdbcUri = fullyQualifiedJdbcUri;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getRootDbName() {
		return rootDbName;
	}

	public void setRootDbName(String rootDbName) {
		this.rootDbName = rootDbName;
	}

	public String getSuperUserPwd() {
		return superUserPwd;
	}

	public void setSuperUserPwd(String superUserPwd) {
		this.superUserPwd = superUserPwd;
	}

	public String getSuperUser() {
		return superUser;
	}

	public void setSuperUser(String superUser) {
		this.superUser = superUser;
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	public Map<String, Savepoint> getTxnSavepoints() {
		return txnSavepoints;
	}
}