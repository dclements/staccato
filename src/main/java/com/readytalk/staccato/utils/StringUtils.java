package com.readytalk.staccato.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jhumphrey
 */
public class StringUtils {

  /**
   * Converts a string into a UTF-8 SHA hash
   *
   * @param string the string
   * @return the hash
   * @throws NoSuchAlgorithmException if SHA1 doesn't exist
   * @throws UnsupportedEncodingException if UTF-8 doesn't exist
   */
  public static String SHA1Hash(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {

    MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
    messageDigest.reset();
    messageDigest.update(string.getBytes("UTF-8"));
    byte[] digest = messageDigest.digest();
    BigInteger bigInt = new BigInteger(1, digest);
    String hashtext = bigInt.toString(16);
    while (hashtext.length() < 32) {
      hashtext = "0" + hashtext;
    }

    return hashtext;
  }
}
