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

	public void setConnection(final Connection _connection) {
		this.connection = _connection;
	}

	public URI getBaseJdbcUri() {
		return baseJdbcUri;
	}

	public void setBaseJdbcUri(final URI _baseJdbcUri) {
		this.baseJdbcUri = _baseJdbcUri;
	}

	public URI getFullyQualifiedJdbcUri() {
		return fullyQualifiedJdbcUri;
	}

	public void setFullyQualifiedJdbcUri(final URI _fullyQualifiedJdbcUri) {
		this.fullyQualifiedJdbcUri = _fullyQualifiedJdbcUri;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String _password) {
		this.password = _password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String _username) {
		this.username = _username;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(final String _dbName) {
		this.dbName = _dbName;
	}

	public String getRootDbName() {
		return rootDbName;
	}

	public void setRootDbName(final String _rootDbName) {
		this.rootDbName = _rootDbName;
	}

	public String getSuperUserPwd() {
		return superUserPwd;
	}

	public void setSuperUserPwd(final String _superUserPwd) {
		this.superUserPwd = _superUserPwd;
	}

	public String getSuperUser() {
		return superUser;
	}

	public void setSuperUser(final String _superUser) {
		this.superUser = _superUser;
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(final DatabaseType _databaseType) {
		this.databaseType = _databaseType;
	}

	public Map<String, Savepoint> getTxnSavepoints() {
		return txnSavepoints;
	}
}
