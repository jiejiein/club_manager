package com.club;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    
    @Test
    public void testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "666666";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("========================================");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("Matches test: " + encoder.matches(rawPassword, encodedPassword));
        System.out.println("========================================");
    }
}
