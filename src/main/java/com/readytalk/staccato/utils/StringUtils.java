package com.readytalk.staccato.utils;

public class StringUtils {

	/**
	 * Truncates a str to the given length and adds a '...' on the end
	 *
	 * @param length the length to truncate at
	 * @param str the str
	 * @return the truncated str
	 */
	public static String truncate(int length, String str) {
		String truncatedStr = str;

		if (str.length() > length) {
			truncatedStr = str.substring(0, 100);
		}
		return truncatedStr;
	}
}
