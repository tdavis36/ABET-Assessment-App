package com.ABETAppTeam.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hash a password for storing in the database
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Verify password against stored hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}