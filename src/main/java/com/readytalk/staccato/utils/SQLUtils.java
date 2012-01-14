package com.readytalk.staccato.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class SQLUtils {

	public static final Logger logger = Logger.getLogger(SQLUtils.class);

	/**
	 * Executes sql
	 *
	 * @param connection the connection
	 * @param sql the sql
	 * @return result set
	 * @throws SQLException if error during sql execution
	 */
	public static ResultSet execute(Connection connection, String sql) throws SQLException {

		logger.trace("Executing sql: \n" + sql + "\n");

		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();

			boolean isResultSet = st.execute(sql);
			if (isResultSet) {
				rs = st.getResultSet();
			}

		} catch (Exception e) {
			throw new SQLException("Error occurred while executing sql: " + sql, e);
		}

		return rs;
	}

	/**
	 * Executes sql file
	 *
	 * @param connection the sql connection
	 * @param url the url
	 * @return the result set
	 * @throws SQLException if errors during execution
	 */
	public static ResultSet executeSQLFile(Connection connection, URL url) throws SQLException {

		logger.trace("Executing sql file url: " + url.toExternalForm());

		ResultSet rs;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				stringBuilder.append(inputLine).append("\n");
			}
			in.close();

			rs = execute(connection, stringBuilder.toString());

		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Unable to read script: " + url.toExternalForm(), e);
		}

		return rs;
	}
}
