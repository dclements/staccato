package com.readytalk.staccato.database.migration.script;

import java.net.URL;

/**
 * Models a script.
 */
public interface Script<T> extends Comparable<T> {

	/**
	 * Returns the filename including the file extension
	 *
	 * @return the script filename
	 */
	String getFilename();

	/**
	 * Returns the url to the script
	 *
	 * @return the script url
	 */
	URL getUrl();
}
