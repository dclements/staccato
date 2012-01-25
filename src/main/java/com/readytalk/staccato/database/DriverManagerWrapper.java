package com.readytalk.staccato.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Wrapper class for DriverManager to facilitate testing. 
 * 
 * XXX: Long term solution here is handle this wrapper via dependency injection.
 */
public class DriverManagerWrapper {
	
	private DriverManagerWrapper() {
		
	}
	public static Connection getConnection(final String url, final String user, final String password) 
			throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
