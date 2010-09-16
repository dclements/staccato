package com.readytalk.staccato.database;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

/**
 * @author jhumphrey
 */
public class DatabaseContextBuilder {

  private DatabaseContext ctx = new DatabaseContext();

  /**
   * Returns the database context
   *
   * @return {@link com.readytalk.staccato.database.DatabaseContext}
   */
  public DatabaseContext build() {
    return ctx;
  }

  /**
   * Sets the base jdbc context
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

    String fullyQualifiedJdbcUriStr = jdbcUri.toString();

    if (fullyQualifiedJdbcUriStr.endsWith("/")) {
      fullyQualifiedJdbcUriStr += dbName;
    } else {
      fullyQualifiedJdbcUriStr += "/" + dbName;
    }

    URI fullyQualifiedJdbcUri = URI.create(fullyQualifiedJdbcUriStr);

    ctx.setBaseJdbcUri(URI.create(jdbcUri));
    ctx.setFullyQualifiedJdbcUri(fullyQualifiedJdbcUri);
    ctx.setDbName(dbName);
    ctx.setUsername(username);
    ctx.setPassword(password);

    DatabaseType databaseType = DatabaseType.getTypeFromJDBCUri(fullyQualifiedJdbcUri);
    ctx.setDatabaseType(databaseType);

    if (StringUtils.isEmpty(rootDbName)) {
      ctx.setRootDbName(databaseType.getRoot());
    } else {
      ctx.setRootDbName(rootDbName);
    }

    if (StringUtils.isEmpty(superUser)) {
      ctx.setSuperUser(databaseType.getRoot());
    } else {
      ctx.setSuperUser(superUser);
    }

    ctx.setSuperUserPwd(superUserPwd);

    return this;
  }
}
