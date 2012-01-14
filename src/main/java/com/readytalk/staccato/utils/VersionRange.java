package com.readytalk.staccato.utils;

/**
 * <p>
 * This class models a version range that starts at one version and
 * continues to another version, possibly unbounded. The start version
 * is always required. The end version is not required and this denotes
 * an unbounded range where in any version greater than or equal to the
 * start version is incleded in the range. The end version is always
 * exclusive.
 * </p>
 *
 * <p>
 * The String constructor parses the String using a little String magic
 * to pull out one or two version numbers. These are valid formats:
 * </p>
 *
 * <table>
 * <tr><th>Value</th><th>Meaning</th></tr>
 * <tr><td>*</td><td>all versions starting at 0.0.0 and going until infinity</td></tr>
 * <tr><td>2</td><td>starts at 2.0.0 and ends at 2.0.0</td></tr>
 * <tr><td>2.*</td><td>starts at 2.0.0 and ends at the highest value of 2 major line</td></tr>
 * <tr><td>2-3</td><td>starts at 2.0.0 and ends at 3.0</td></tr>
 * <tr><td>2-</td><td>starts at 2.0.0 and ends at infinity</td></tr>
 * <tr><td>2.0-3.1</td><td>starts at 2.0.0 and ends at 3.1</td></tr>
 * <tr><td>-3.1</td><td>starts at 0.0.0 and ends at 3.1</td></tr>
 * <tr><td>2.*-3.1</td><td>not allowed</td></tr>
 * <tr><td>2*</td><td>not allowed</td></tr>
 * <tr><td>*3</td><td>not allowed</td></tr>
 * <tr><td>*.3</td><td>not allowed</td></tr>
 * </table>
 * </p>
 * 
 * @since 1.0
 */
public class VersionRange {
	private final Version start;
	private final Version end;

	/**
	 * Constructs a new VersionRange that starts at one Version and ends another.
	 *
	 * @param   start The start Version number.
	 * @param   end (Optional) The end Version number.
	 * @throws IllegalArgumentException If start is null or greater than end.
	 */
	public VersionRange(Version start, Version end) {
		if (start == null) {
			throw new IllegalArgumentException("start version cannot be null");
		}

		if (end != null && start.compareTo(end) > 0) {
			throw new IllegalArgumentException("start version must be less than end version. [" +
					start + "] is not less than [" + end + "]");
		}

		this.start = start;
		this.end = end;
	}

	/**
	 * Constructs a version range by parsing the given String.
	 *
	 * @param   versionRange The version range String to parse.
	 * @throws NumberFormatException If any part of the version range String is not an integer value.
	 * @throws IllegalArgumentException If the version range String is invalid in the ways shown in
	 *          the class comment.
	 */
	public VersionRange(String versionRange) {
		if (versionRange == null) {
			throw new IllegalArgumentException("versionRange string cannot be null");
		}

		// Look for - first, should be only one or zero
		int dash = versionRange.indexOf('-');
		if (dash != -1) {
			if (versionRange.indexOf('-', dash + 1) != -1) {
				throw new IllegalArgumentException("Invalid version range string [" + versionRange + "]");
			}

			if (dash == 0) {
				start = Version.ZERO;
			} else {
				String version1 = versionRange.substring(0, dash);
				start = new Version(version1, true);
			}

			if (dash != versionRange.length() - 1) {
				String version2 = versionRange.substring(dash + 1);
				end = new Version(version2, true);
			} else {
				end = null;
			}
		} else {
			int star = versionRange.indexOf('*');
			if (star != -1) {
				if (star == 0 && versionRange.length() == 1) {
					start = Version.ZERO;
					end = null;
				} else {
					if (versionRange.indexOf('*', star + 1) != -1 || star == 0 ||
							versionRange.charAt(star - 1) != '.') {
						throw new IllegalArgumentException("Invalid version range string [" + versionRange + "]");
					}

					String version1 = versionRange.substring(0, star - 1);
					start = new Version(version1, true);
					int dot = version1.indexOf('.');
					if (dot == -1) {
						end = new Version(start.getMajor(), Integer.MAX_VALUE, Integer.MAX_VALUE);
					} else {
						dot = version1.indexOf('.', dot + 1); // If two periods, patch (not allowed)
						if (dot == -1) {
							end = new Version(start.getMajor(), start.getMinor(), Integer.MAX_VALUE);
						} else {
							// Patch (not allowed)
							throw new IllegalArgumentException("Invalid version range string [" + versionRange + "]");
						}
					}
				}
			} else {
				start = new Version(versionRange, true);
				end = start;
			}
		}
	}

	/**
	 * @return The starting version number.
	 */
	public Version getStart() {
		return start;
	}

	/**
	 * @return The ending version number.
	 */
	public Version getEnd() {
		return end;
	}

	/**
	 * Compares the given Object with this Version for equality.
	 *
	 * @param   other The object to compare with this Version for equality.
	 * @return True if they are both Versions and equal, false otherwise.
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		final VersionRange version = (VersionRange) other;

		if (!start.equals(version.start)) {
			return false;
		}
		if (end != null && !end.equals(version.end)) {
			return false;
		}

		return true;
	}

	/**
	 * @return A valid hashcode for the Version.
	 */
	public int hashCode() {
		int result;
		result = start.hashCode();
		if (end != null) {
			result = 29 * result + end.hashCode();
		}
		return result;
	}

	/**
	 * Returns whether or not the given version is in this range.
	 *
	 * @param   v The version to check it is in this range.
	 * @return True if the version is in the range, false otherwise.
	 */
	public boolean isInRange(Version v) {
		return start.compareTo(v) <= 0 && (end == null || end.compareTo(v) >= 0);
	}

	/**
	 * Returns the highest version that is in the range.
	 *
	 * @param   va An array of Versions out of which this method returns the highest version in the
	 *          version range.
	 * @return The highest version in the range or null if none are in the range.
	 */
	public Version highestVersion(Version[] va) {
		Version highest = null;
		for (int i = 0; i < va.length; i++) {
			Version version = va[i];
			if (isInRange(version)) {
				if (highest != null && version.compareTo(highest) > 0) {
					highest = version;
				} else if (highest == null) {
					highest = version;
				}
			}
		}

		return highest;
	}

	/**
	 * Converts the version number to a string suitable for debugging.
	 *
	 * @return A String of the version number.
	 */
	public String toString() {
		return start.toString() + "-" + ((end == null) ? "infinity" : end.toString());
	}
}