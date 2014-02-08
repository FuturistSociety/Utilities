/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHasher {

	private MessageDigest md;

	/**
	 * Default constructor to create a SHA-512 message digest.
	 */
	public StringHasher() {
		this("SHA-512");
	}

	/**
	 * Constructor to create a message digest using the given algorithm.
	 * @param hashAlgorithm name of the hashing algorithm to use.
	 * @see http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#Architecture
	 */
	public StringHasher(String hashAlgorithm) {
		try {
			md = MessageDigest.getInstance(hashAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the hashed value of the String given.
	 * @param input the String to hash.
	 * @return the hash.
	 */
	public String hash(String input) {

		md.update(input.getBytes());

		byte byteData[] = md.digest();

		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
