package com.readytalk.staccato.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.readytalk.staccato.database.migration.script.Script;

public class DatabaseServiceImpl implements DatabaseService {
	private static final Integer CONNECTION_TIMEOUT = 30;
	private static final Logger logger = Logger.getLogger(DatabaseServiceImpl.class);

	@Override
	public DatabaseContextBuilder getDatabaseContextBuilder() {
		return new DatabaseContextBuilder();
	}

	@Override
	public Connection connect(final URI jdbcUri, final String username, final String password,
							  final DatabaseType databaseType) {

		Connection connection;
		
		boolean loaded = DbUtils.loadDriver(databaseType.getDriver());
		
		if(!loaded) {
			throw new DatabaseException("SQL driver not found: " + String.valueOf(databaseType.getDriver()));
		}

		try {
			logger.info("Connecting to database: " + jdbcUri.toString());

			connection = DriverManagerWrapper.getConnection(jdbcUri.toString(), username, password);
			
		} catch (SQLException e) {
			throw new DatabaseException("SQL exception occurred when establishing connection to database: " + jdbcUri, e);
		}

		return connection;
	}
	
	@Override
	public Connection connect(final DatabaseContext context) {
		final Connection retval = this.connect(context.getFullyQualifiedJdbcUri(), context.getUsername(),
				context.getPassword(), context.getDatabaseType());
		context.setConnection(retval);
		return retval;
	}

	@Override
	public void disconnect(DatabaseContext context) {

		logger.info("Disconnecting database connection: " + String.valueOf(context.getFullyQualifiedJdbcUri()));

		final Connection connection = context.getConnection();
		try {
			if (connection != null && connection.isValid(CONNECTION_TIMEOUT)) {
				DbUtils.close(connection);
			}
		} catch (SQLException e1) {
			try {
				if (connection != null && !connection.isClosed()) {
					DbUtils.close(connection);
				}
			} catch (SQLException e2) {
				logger.warn("Unable to close database connection to: "
						+ String.valueOf(context.getFullyQualifiedJdbcUri()));
			}
		}
	}

	@Override
	public void startTransaction(final DatabaseContext context, final Script<?> script) {
		try {
			Connection connection = context.getConnection();
			connection.setAutoCommit(false);

			String savepointName = buildSavepointName(script);

			Savepoint savepoint = connection.setSavepoint(savepointName);
			context.getTxnSavepoints().put(savepointName, savepoint);
			
			logger.debug("started transaction with savepoint: " + savepointName);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to start transaction", e);
		}
	}

	@Override
	public void endTransaction(final DatabaseContext context, final Script<?> script) {
		try {
			Connection connection = context.getConnection();
			if (!connection.getAutoCommit()) {
				connection.commit();
				connection.setAutoCommit(true);

				String savepointName = buildSavepointName(script);

				try {
					connection.releaseSavepoint(context.getTxnSavepoints().get(savepointName));
				} catch (SQLException e) {
					logger.debug("Savepoint not found when ending transaction: " + savepointName);
				} finally {
					context.getTxnSavepoints().remove(savepointName);
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to end the transaction", e);
		}
	}

	/**
	 * Helper method for building a transaction savepoint name.
	 *
	 * @param script the script
	 * @return the savepoint name
	 */
	private String buildSavepointName(final Script<?> script) {
		return "transaction_savepoint_" + script.getFilename();
	}

	@Override
	public void rollback(final DatabaseContext context, final Script<?> script) {
		try {

			String savepointName = buildSavepointName(script);

			Connection connection = context.getConnection();
			if (!connection.getAutoCommit()) {
				logger.debug("rolling back transaction to savepoint: " + savepointName);
				connection.rollback(context.getTxnSavepoints().get(savepointName));
				//As of JDBC4 the savepoint is no longer valid, so we can remove it.
				context.getTxnSavepoints().remove(savepointName);
			} else {
				logger.warn("Attempted to rollback while autoCommit was set to true.");
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to rollback database", e);
		}
	}
}
