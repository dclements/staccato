package com.readytalk.staccato.database;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;

public class DatabaseContextBuilder implements Builder<DatabaseContext> {

	private final DatabaseContext ctx = new DatabaseContext();
	
	/**
	 * Returns the database context.
	 *
	 * @return {@link com.readytalk.staccato.database.DatabaseContext}
	 */
	public DatabaseContext build() {
		
		this.fullyQualifiedJdbcUriStr(ctx.getBaseJdbcUri(), ctx.getDbName());
		
		if(StringUtils.isEmpty(ctx.getSuperUser())) {
			ctx.setSuperUser(ctx.getDatabaseType().getRoot());
		}
		
		if (StringUtils.isEmpty(ctx.getRootDbName())) {
			ctx.setRootDbName(ctx.getDatabaseType().getRoot());
		} 
		
		return ctx;
	}
	
	public DatabaseContextBuilder jdbcUri(String jdbcUri) {
		ctx.setBaseJdbcUri(URI.create(jdbcUri));
		
		return this;
	}
	
	private DatabaseContextBuilder fullyQualifiedJdbcUriStr(URI jdbcUri, String dbName) {
		String fullyQualifiedJdbcUriStr = jdbcUri.toString();
		
		if (fullyQualifiedJdbcUriStr.endsWith("/")) {
			fullyQualifiedJdbcUriStr += dbName;
		} else {
			fullyQualifiedJdbcUriStr += "/" + dbName;
		}
		
		final URI fullyQualifiedJdbcUri = URI.create(fullyQualifiedJdbcUriStr);
		
		ctx.setFullyQualifiedJdbcUri(fullyQualifiedJdbcUri);
		
		DatabaseType databaseType = DatabaseType.getTypeFromJDBCUri(fullyQualifiedJdbcUri);
		ctx.setDatabaseType(databaseType);
		
		return this;
	}
	
	public DatabaseContextBuilder dbName(String dbName) {
		ctx.setDbName(dbName);
		
		return this;
	}
	
	public DatabaseContextBuilder username(String username) {
		ctx.setUsername(username);
		
		return this;
	}
	
	public DatabaseContextBuilder password(String password) {
		ctx.setPassword(password);
		
		return this;
	}
	
	public DatabaseContextBuilder superUser(String superUser) {
		ctx.setSuperUser(superUser);
		
		return this;
	}
	
	public DatabaseContextBuilder superUserPwd(String superUserPwd) {
		if (StringUtils.isEmpty(superUserPwd)) {
			ctx.setSuperUserPwd("");
		} else {
			ctx.setSuperUserPwd(superUserPwd);
		}
		
		return this;
	}
	
	public DatabaseContextBuilder rootDbName(String rootDbName) {
		ctx.setRootDbName(rootDbName);
		
		return this;
	}

	/**
	 * Convenience method for setting the base JDBC context.
	 *
	 * @param jdbcUri the base jdbc uri
	 * @param dbName the database name
	 * @param username the database username
	 * @param password the database password
	 * @param superUser the db super user
	 * @param superUserPwd the db super user pwd
	 * @return this builder
	 */

	public DatabaseContextBuilder setContext(String jdbcUri, String dbName, String username, String password, String superUser, String superUserPwd, String rootDbName) {

		return this.jdbcUri(jdbcUri).dbName(dbName).username(username).password(password).superUser(superUser).superUserPwd(superUserPwd).rootDbName(rootDbName);
	}
}
