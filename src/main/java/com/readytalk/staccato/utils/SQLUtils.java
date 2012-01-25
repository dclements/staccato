package com.readytalk.staccato.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class SQLUtils {

	private static final Logger logger = Logger.getLogger(SQLUtils.class);

	/**
	 * Executes sql.
	 *
	 * @param connection the connection
	 * @param sql the sql
	 * @return result set
	 * @throws SQLException if error during sql execution
	 */
	public static ResultSet execute(final Connection connection, final String sql) throws SQLException {

		logger.trace("Executing sql: \n" + sql + "\n");

		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();

			boolean isResultSet = st.execute(sql);
			if (isResultSet) {
				rs = st.getResultSet();
			}

		} catch (final SQLException e) {
			throw new SQLException("Error occurred while executing sql: " + sql, e);
		}

		return rs;
	}

	/**
	 * Executes sql file.
	 *
	 * @param connection the sql connection
	 * @param url the url
	 * @return the result set
	 * @throws SQLException if errors during execution
	 */
	public static ResultSet executeSQLFile(final Connection connection, final URL url) throws SQLException {

		logger.trace("Executing sql file url: " + url.toExternalForm());

		ResultSet rs;
		
		InputStream is = null;
		try {
			is = url.openStream();
			String sql = IOUtils.toString(is);

			rs = execute(connection, sql);
		} catch (IOException e) {
			throw new SQLException("Unable to read script: " + url.toExternalForm(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return rs;
	}
}
