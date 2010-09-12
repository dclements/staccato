
package com.readytalk.staccato.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * This class models a simple three number version as well as any
 * free form version String. It has two modes of operation, strict
 * and relaxed.
 * </p>
 *
 * <h3>Relaxed mode</h3>
 * <p>
 * When this class is constructed in relaxed mode it tries everything
 * in its power to figure out what the heck a version string is and
 * will only fail if the version string has two delimiters next to each
 * other or begins/ends with a delimiter.
 * </p>
 *
 * <p>
 * The delimiters are always the . (dot) and - (dash) characters. When
 * in relaxed mode, it will walk over the Version string passed in and
 * stop at each delimiter. If the string since the last delimiter or the
 * beginning is a number, it will try to figure out which part the
 * version it is. This will either be a major, minor or patch part.
 * However, if it has parsed out a major, minor and patch version
 * already then it will assume that the rest of the version string is
 * additional information such as a snapshot, date or just some string.
 * If at any point it encounters a non-digit character other than a
 * delimiter, it immediately stops handling major, minor and patch
 * versions and assumes the rest of the string is additional information.
 * </p>
 *
 * <p>
 * Lastly, regardless of the mode, this class makes a slightly forceful
 * assumption that there is a difference between the two delimiters.
 * It assumes that once a - (dash) is encountered that the rest of the
 * version string is additional information. This is done so that short
 * hand versions can be used like these:
 * </p>
 *
 * <pre>
 * 1.0-snapshot
 * 1-branch_foo_bar
 * </pre>
 *
 * <h3>Strict mode</h3>
 * <p>
 * When this class is in strict mode, it follows the same handling as
 * relaxed mode, but before it begin parsing, it checks to ensure that
 * the version string matches this regular expression:
 * </p>
 *
 * <pre>
 * [0-9]+(?>\.[0-9]+){0,2}(?>-(?>snapshot|rc|beta|alpha|m)[0-9]*)?
 * </pre>
 *
 * <p>
 * This regular expression can also be described using this EBNF (roughly):
 * </p>
 *
 * <pre>major[.minor[.patch[-snapshot]]]</pre>
 *
 * <p>
 * The <b>major, minor, patch and snapshot</b> portions of the above
 * format must conform to this specificcation:
 * </p>
 *
 * <pre>
 * major = [0-9]+
 * minor = [0-9]+
 * patch = [0-9]+
 * snapshot = (snapshot|rc|beta|alpha|m)[0-9]*
 * </pre>
 *
 * <h3>Defaults</h3>
 * <p>
 * If the major, minor or patch are not specified they default to 0. This
 * means that the version string <b>1.0</b> is equal to <b>1.0.0</b>.
 * Likewise, the version string <b>foo</b> is equal to <b>0.0.0-foo</b>.
 * </p>
 *
 * <h3>Equals and comparisons</h3>
 * <p>
 * When comparing or testing versions for equality there are a few things to
 * keep in mind. First of all, equals matches the major, minor, patch and
 * additional information exactly. If there are any differences, the two
 * versions are not equal. However, since there are defaults for all of these
 * than two constructions using different Strings might yield equal versions.
 * </p>
 *
 * <p>
 * During comparisons, this class handles the major, minor and patch
 * comparisons using direct integer math, even if one or the other of
 * the version strings contains additional information. If the versions
 * match exactly using the major, minor and patch, then the additional
 * information is parsed to determine if it can be smartly compared.
 * In order to accomplish this it breaks the additional information into
 * a prefix and suffix such that the suffix is always an integer number.
 * If there is no suffix that contains only integers, than the suffix is
 * considered null and only the prefix is used.
 * </p>
 *
 * <p>
 * If the prefixes are equal than the suffixes (if they exist) are
 * compared using integer math. If the prefixes are not equal, then
 * a ranking system is tried. Be default this class supports these
 * ranked prefixes in this order:
 * </p>
 *
 * <pre>
 * snapshot
 * alpha
 * beta
 * m
 * rc
 *
 * m = Milestone
 * rc = Release candidate
 * </pre>
 *
 * <p>
 * If both of the prefixes are one of the following strings than
 * the ranks are compared using integer math (via an index into
 * an array that stores the rankings).
 * </p>
 *
 * <p>
 * If either of the prefixes is not one of the above ranked
 * prefixes, than the entire additional information from both
 * versions is compared alphabetically.
 * </p>
 *
 * <p>
 * NOTE: Negative values are not permitted using the integer constructor.
 * </p>
 *
 * @author Brian Pontarelli and James Humphrey
 * @since 1.0
 */
public class Version implements Comparable<Version> {
  /**
   * Represents 0.0.0 version.
   */
  public static final Version ZERO = new Version(0, 0, 0);

  /**
   * Represents a regular expression string that will match against any Version.
   */
  public static final String REGULAR_EXPRESSION_STRING = "[0-9]+(?:\\.[0-9]+)?(?:\\.[0-9]+)?(?:-(?:alpha|ALPHA|beta|BETA|milestone|MILESTONE|rc|RC|snapshot|SNAPSHOT|ib|IB|a|A|b|B|m|M)[0-9]*)*";
  public static final Pattern REGULAR_EXPRESSION = Pattern.compile(REGULAR_EXPRESSION_STRING);

  /**
   * The rank ordering of the version additional values. This is currently in this order:
   * <pre>
   * snapshot
   * alpha
   * beta
   * m
   * rc
   * ib
   *
   * m = Milestone
   * rc = Release candidate
   * ib = Integration build
   * </pre>
   */
  public static final List<String> ADDITIONAL_RANKING = new ArrayList<String>(Arrays.asList("snaphsot", "alpha", "beta", "m", "rc", "ib"));

  /**
   * The format of the version string suffix (if specified) when Version is in strict mode.
   */
  public static final Pattern STRICT_FORMAT = Pattern.compile("[0-9]+(?:\\.[0-9]+)?(?:\\.[0-9]+)?(?:-(?:alpha|beta|milestone|rc|snapshot)[0-9]*)?(?:-ib[0-9]*)?");

  private final int major;
  private final int minor;
  private final int patch;
  private final String additional;

  /**
   * Constructs a version with the given major, minor and patch version numbers.
   *
   * @param   major The major version number.
   * @param   minor The minor version number.
   * @param   patch The patch version number.
   */
  public Version(int major, int minor, int patch) {
    if (major < 0 || minor < 0 || patch < 0) {
      throw new IllegalArgumentException("Majr, minor and patch must be positive integers");
    }

    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.additional = null;
  }

  /**
   * Constructs a version by parsing the given String.
   *
   * @param   version The version String to parse.
   * @throws IllegalArgumentException If the string starts or ends with a delimiter (. or -) or
   *          contains two delimiters in a row.
   */
  public Version(String version) {
    this(version, false);
  }

  /**
   * Constructs a version by parsing the given String.
   *
   * <p>
   * If this is set into strict mode (NOT the default), than the version string must conform to
   * This pattern:
   * </p>
   *
   * <pre>major[.minor[.patch[-snapshot]]]</pre>
   *
   * <p>
   * The <b>major, minor, patch and snapshot</b> portions of the above format must conform to this
   * specificcation:
   * </p>
   *
   * <pre>
   * major = [0-9]+
   * minor = [0-9]+
   * patch = [0-9]+
   * snapshot = (snapshot|rc|beta|alpha|m)[0-9]*
   * </pre>
   *
   * @param   version The version String to parse.
   * @param   strict If this is true, the version string must conform to the format above.
   * @throws IllegalArgumentException If the string starts or ends with a delimiter (. or -) or
   *          contains two delimiters in a row. Or the strict mode is turned on and the version
   *          doesn't conform to the format above.
   */
  public Version(String version, boolean strict) {
    if (strict && !STRICT_FORMAT.matcher(version.toLowerCase()).matches()) {
      throw new IllegalArgumentException("Invalid strict version string [" + version + "]");
    }

    char start = version.charAt(0);
    char end = version.charAt(version.length() - 1);
    if (start == '.' || start == '-' || end == '.' || end == '-') {
      throw new IllegalArgumentException("Version strings should not begin or end with . or - [" +
        version + "] is invalid.");
    }

    StringBuilder word = new StringBuilder();
    StringBuilder num = new StringBuilder();
    Integer major = null;
    Integer minor = null;
    Integer patch = null;
    String additional = null;
    boolean doneNumbers = false;
    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i);
      if (doneNumbers) {
        word.append(c);
      } else {
        if (c == '.' || c == '-') {
          if (version.charAt(i - 1) == '.' || version.charAt(i - 1) == '-') {
            throw new IllegalArgumentException("Version strings should not have two" +
              " delimiters next to each other. [" + version + "] is invalid.");
          }

          if (num.length() > 0) {
            if (major == null) {
              major = Integer.parseInt(num.toString());
            } else if (minor == null) {
              minor = Integer.parseInt(num.toString());
            } else if (patch == null) {
              patch = Integer.parseInt(num.toString());
              doneNumbers = true;
            }

            num.setLength(0);
          }

          // Just by convention, let's assume the rest of the version string is additional
          if (c == '-') {
            doneNumbers = true;
          }
        } else if (Character.isDigit(c)) {
          num.append(c);
        } else {
          doneNumbers = true;
          word.append(num).append(c);
        }
      }
    }

    if (!doneNumbers && num.length() > 0) {
      if (major == null) {
        major = Integer.parseInt(num.toString());
      } else if (minor == null) {
        minor = Integer.parseInt(num.toString());
      } else if (patch == null) {
        patch = Integer.parseInt(num.toString());
      }
    }

    if (word.length() > 0) {
      additional = word.toString();
    }

    this.major = major != null ? major : 0;
    this.minor = minor != null ? minor : 0;
    this.patch = patch != null ? patch : 0;
    this.additional = additional;
  }

  /**
   * @return The major version number.
   */
  public int getMajor() {
    return major;
  }

  /**
   * @return The minor version number.
   */
  public int getMinor() {
    return minor;
  }

  /**
   * @return The patch version number.
   */
  public int getPatch() {
    return patch;
  }

  /**
   * @return Any additional version information on the end of the version String. For example,
   *          1.0.2-RC4 the additional would be RC4.
   */
  public String getAdditional() {
    return additional;
  }

  /**
   * @return True if this Version is a major, false for all other types of versions.
   */
  public boolean isMajorVersion() {
    return minor == 0 && patch == 0 && additional == null;
  }

  /**
   * @return True if this Version is a minor, false for all other types of versions.
   */
  public boolean isMinorVersion() {
    return minor > 0 && patch == 0 && additional == null;
  }

  /**
   * @return True if this Version is a patch, false for all other types of versions.
   */
  public boolean isPatchVersion() {
    return patch > 0 && additional == null;
  }

  /**
   * @return True if this Version is a snapshot, false for all other types of versions.
   */
  public boolean isSnapshot() {
    return additional != null;
  }

  /**
   * Compares the given Object with this Version for equality.
   *
   * @param   other The object to compare with this Version for equality.
   * @return True if they are both Versions and equal, false otherwise.
   */

  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;

    Version version = (Version) other;

    if (major != version.major) return false;
    if (minor != version.minor) return false;
    if (patch != version.patch) return false;
    if (additional != null ? !additional.equals(version.additional) : version.additional != null)
      return false;

    return true;
  }

  /**
   * @return A valid hashcode for the Version.
   */

  public int hashCode() {
    int result;
    result = major;
    result = 31 * result + minor;
    result = 31 * result + patch;
    result = 31 * result + (additional != null ? additional.hashCode() : 0);
    return result;
  }

  /**
   * Returns the value of the comparison between this Version and the given Object. This throws an
   * exception if the object given is not a Version.
   *
   * @param   other The other Object to compare against.
   * @return A positive integer if this Version is larger than the given version. Zero if the given
   *          Version is the exact same as this Version. A negative integer is this Version is smaller
   *          that the given Version.
   * @throws IllegalArgumentException If the given Object is not a Version.
   */
  public int compareTo(Version other) {
    if (major != other.major) {
      return major - other.major;
    }

    if (minor != other.minor) {
      return minor - other.minor;
    }

    if (patch != other.patch) {
      return patch - other.patch;
    }

    // Try to figure out the additional stuff by checking if there is a number at the end
    if (additional != null && other.additional != null) {
      char[] ca = additional.toLowerCase().toCharArray();
      char[] caOther = other.additional.toLowerCase().toCharArray();

      List<Pair<String, Long>> values = decodeAdditional(ca);
      List<Pair<String, Long>> valuesOther = decodeAdditional(caOther);

      for (int i = 0; i < values.size(); i++) {
        // This happens with 1.0-RC4-B2 and 1.0-RC4 and the shorter one should win
        if (i == valuesOther.size()) {
          return -1;
        }

        Pair<String, Long> value = values.get(i);
        Pair<String, Long> valueOther = valuesOther.get(i);
        if (value.first.equals(valueOther.first)) {
          if (value.second != null && valueOther.second != null) {
            int result = value.second.compareTo(valueOther.second);
            if (result == 0) {
              continue;
            }

            return result;
          } else if (value.second == null && valueOther.second != null) {
            return 1;
          } else if (value.second != null && valueOther.second == null) {
            return -1;
          }
        } else {
          // Mis-matched versions, tough decision, but I think the ordering goes alpha, beta,
          // M, RC
          int rank = ADDITIONAL_RANKING.indexOf(value.first);
          int rankOther = ADDITIONAL_RANKING.indexOf(valueOther.first);

          if (rank == -1 || rankOther == -1) {
            // Now we really have no option but to compare the additional strings lexically
            return additional.compareTo(other.additional);
          }

          return rank - rankOther;
        }
      }

      // If we got here, then the match and either the other is longer, in which case this one
      // wins, or they are equal and 0 will be returned at the end
      if (valuesOther.size() > values.size()) {
        return 1;
      }
    } else if (additional == null && other.additional != null) {
      return 1;
    } else if (additional != null && other.additional == null) {
      return -1;
    }

    return 0;
  }

  private List<Pair<String, Long>> decodeAdditional(char[] ca) {
    StringBuilder str = new StringBuilder();
    StringBuilder num = new StringBuilder();
    List<Pair<String, Long>> list = new ArrayList<Pair<String, Long>>();
    boolean insideDigits = false;
    for (char c : ca) {
      boolean digit = Character.isDigit(c);
      if (digit) {
        insideDigits = true;
        num.append(c);
      } else {
        // Clear it out since that last digits was embedded
        if (insideDigits) {
          Long l = null;
          if (num.length() > 0) {
            l = Long.valueOf(num.toString());
          }

          list.add(new Pair<String, Long>(str.toString(), l));

          num = new StringBuilder();
          str = new StringBuilder();
        }

        insideDigits = false;

        if (c != '-') {
          str.append(c);
        }
      }
    }

    if (num.length() > 0 || str.length() > 0) {
      Long l = null;
      if (num.length() > 0) {
        l = Long.valueOf(num.toString());
      }

      list.add(new Pair<String, Long>(str.toString(), l));
    }

    return list;
  }

  /**
   * Converts the version number to a string suitable for debugging.
   *
   * @return A String of the version number.
   */
  public String toString() {
    return "" + major + "." + minor + "." + patch + (additional != null ? "-" + additional : "");
  }

  /**
   * @return A Version instance that is a major version of the same major as this Version. If
   *          this is 2.4.7 this will return 2.0.
   */
  public Version toMajor() {
    return new Version(major, 0, 0);
  }

  /**
   * @return A Version instance that is a minor version of the same minor as this Version. If
   *          this is 2.4.7 this will return 2.4.
   */
  public Version toMinor() {
    return new Version(major, minor, 0);
  }

  /**
   * @return A Version instance that is a patch version of the same patch as this Version. If
   *          this is 2.4.7-RC4 this will return 2.4.7.
   */
  public Version toPatch() {
    return new Version(major, minor, patch);
  }
}