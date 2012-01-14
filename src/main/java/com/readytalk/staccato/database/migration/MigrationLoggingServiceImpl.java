package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseException;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.SQLUtils;

public class MigrationLoggingServiceImpl implements MigrationLoggingService {

	private static final Logger logger = Logger.getLogger(MigrationLoggingServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createVersionsTable(DatabaseContext context) {

		if (!versionTableExists(context)) {

			URL url = null;
			String sqlFile = null;

			switch (context.getDatabaseType()) {
			case MYSQL:
				sqlFile = "mysql-staccato-migrations.sql";
				url = this.getClass().getClassLoader().getResource(sqlFile);
				break;
			case POSTGRESQL:
				sqlFile = "postgresql-staccato-migrations.sql";
				url = this.getClass().getClassLoader().getResource(sqlFile);
				break;
			}

			if (url == null) {
				throw new DatabaseException("Unable to create the " + MIGRATION_VERSIONS_TABLE + ".  Cannot locate the sql file: " + sqlFile);
			}

			try {
				logger.debug("Creating table: " + MIGRATION_VERSIONS_TABLE);
				SQLUtils.executeSQLFile(context.getConnection(), url);
			} catch (SQLException e) {
				throw new DatabaseException("Unable to execute mysql script: " + url.toExternalForm(), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean versionTableExists(DatabaseContext context) {
		try {
			if (context.getConnection().isClosed()) {
				throw new DatabaseException("Connection is closed to: " + context.getFullyQualifiedJdbcUri());
			}
		} catch(final SQLException e) {
			throw new DatabaseException("Unable to determine connection status for: " + context.getFullyQualifiedJdbcUri(), e);
		}

		final Connection conn = context.getConnection();
		ResultSet rs = null;
		try {
			logger.debug("Checking for existence of table: " + MIGRATION_VERSIONS_TABLE);
			rs = SQLUtils.execute(conn, "select * from " + MIGRATION_VERSIONS_TABLE);
			return true;
		} catch(final SQLException e) {
			return false;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
			} catch(final SQLException sqe) {
				logger.log(Level.WARN, "Exception trying to close ResultSet.", sqe);
			}
		}
	}

	@Override
	public void log(DatabaseContext databaseContext, DynamicLanguageScript<?> script, Class<? extends Annotation> workflowStep, Migration migrationAnnotation) {

		// create the migration versions table
		createVersionsTable(databaseContext);

		try {
			String filename = script.getFilename();

			String date;

			switch (databaseContext.getDatabaseType()) {
			case MYSQL:
				date = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(script.getScriptDate());
				break;
			default:
				date = script.getScriptDate().toString();
				break;
			}

			String hash = script.getSHA1Hash();

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("INSERT INTO ").append(MIGRATION_VERSIONS_TABLE).append(" ");
			sqlBuilder.append("(database_version, script_date, script_hash, script_filename, workflow_step) ");
			sqlBuilder.append("values('");
			sqlBuilder.append(migrationAnnotation.databaseVersion()).append("', '");
			sqlBuilder.append(date).append("', '");
			sqlBuilder.append(hash).append("', '");
			sqlBuilder.append(filename).append("', '");
			sqlBuilder.append(workflowStep.getSimpleName());
			sqlBuilder.append("')");

			logger.debug("Logged migration to: " + MIGRATION_VERSIONS_TABLE);

			SQLUtils.execute(databaseContext.getConnection(), sqlBuilder.toString());
		} catch (SQLException e) {
			throw new MigrationException("Unable to execute query", e);
		}

	}
}
