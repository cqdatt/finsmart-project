package com.finsmart.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 * Uses SHA-256 with random salt for secure password storage
 * 
 * Note: For production, consider using BCrypt or Argon2
 */
public class HashPassword {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;  // 128 bits
    private static final int ITERATIONS = 10000;
    
    /**
     * Hash a password with a randomly generated salt
     * @param plainPassword the raw password to hash
     * @return formatted string: "salt:hash" (both Base64 encoded)
     */
    public static String hash(String plainPassword) {
        try {
            // Generate random salt
            byte[] salt = generateSalt();
            
            // Hash password with salt
            byte[] hash = hashWithSalt(plainPassword.toCharArray(), salt, ITERATIONS);
            
            // Return formatted: salt:hash (Base64 encoded)
            return Base64.getEncoder().encodeToString(salt) + ":" + 
                   Base64.getEncoder().encodeToString(hash);
                   
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param plainPassword the raw password to check
     * @param storedHash the previously stored hash in format "salt:hash"
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String plainPassword, String storedHash) {
        try {
            // Split stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the input password with the same salt
            byte[] actualHash = hashWithSalt(plainPassword.toCharArray(), salt, ITERATIONS);
            
            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(expectedHash, actualHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate a cryptographically secure random salt
     * @return byte array of random salt
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hash password with salt using PBKDF2-like approach
     * @param password password characters
     * @param salt salt bytes
     * @param iterations number of hashing iterations
     * @return hashed bytes
     */
    private static byte[] hashWithSalt(char[] password, byte[] salt, int iterations) 
            throws NoSuchAlgorithmException {
        
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        
        // Initial hash of password + salt
        md.update(salt);
        byte[] hash = md.digest(bytesFromChars(password));
        
        // Multiple iterations for added security
        for (int i = 0; i < iterations - 1; i++) {
            md.reset();
            hash = md.digest(hash);
        }
        
        return hash;
    }
    
    /**
     * Convert char array to byte array (UTF-8)
     */
    private static byte[] bytesFromChars(char[] chars) {
        return new String(chars).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    
    /**
     * Simple test method (for development only)
     */
    public static void main(String[] args) {
        String password = "Test@123";
        String hashed = hash(password);
        System.out.println("Hashed: " + hashed);
        System.out.println("Verify correct: " + verify(password, hashed));
        System.out.println("Verify wrong: " + verify("WrongPass", hashed));
    }
}