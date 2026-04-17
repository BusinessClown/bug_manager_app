// not my own code 
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

//  Simple password hashing utility using SHA-256 + a random salt.

//  If you add the BCrypt library to your project (e.g. jbcrypt-0.4.jar),
//  replace the body of hash() and verify() with:
//    hash()   -> BCrypt.hashpw(plainPassword, BCrypt.gensalt())
//    verify() -> BCrypt.checkpw(plainPassword, storedHash)

//  The stored format is:  base64(salt) + ":" + base64(hash)

public class PasswordUtil {

	private static final int SALT_BYTES = 16;

	// Hashes a plain-text password and returns the storable string.
	public static String hash(String plainPassword) {
		byte[] salt = new byte[SALT_BYTES];
		new SecureRandom().nextBytes(salt);
		byte[] hash = sha256(salt, plainPassword);
		return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
	}

//	 Returns true if plainPassword matches the storedHash produced by hash().
//	 Also handles legacy plain-text passwords that were stored before hashing
//	 was introduced so existing accounts still work after the upgrade.

	public static boolean verify(String plainPassword, String storedHash) {
//		 Legacy plain-text password (no ":" separator) — accept it as-is
		if (!storedHash.contains(":")) {
			return plainPassword.equals(storedHash);
		}
		String[] parts = storedHash.split(":", 2);
		byte[] salt = Base64.getDecoder().decode(parts[0]);
		byte[] expected = Base64.getDecoder().decode(parts[1]);
		byte[] actual = sha256(salt, plainPassword);
//		 Constant-time comparison to prevent timing attacks
		return MessageDigest.isEqual(expected, actual);
	}

	private static byte[] sha256(byte[] salt, String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			return md.digest(password.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available", e);
		}
	}
}