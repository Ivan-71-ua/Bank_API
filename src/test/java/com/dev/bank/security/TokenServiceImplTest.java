package com.dev.bank.security;

import com.dev.bank.models.User;
import com.dev.bank.security.client.TokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceImplTest {

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        String appName = "Test Bank API";
        String secret = "test-secret-key-for-bank-api-1234567890";
        long ttlSeconds = 3600;
        tokenService = new TokenServiceImpl(appName, secret, ttlSeconds);
    }

    @Test
    void testGenerateTokenContainsUserAndAppName() {
        User user = new User();
        user.setUsername("ivan");
        user.setEmail("ivan@test.com");

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        TokenData data = tokenService.parseToken(token);

        assertEquals("ivan", data.getUsername());
        assertEquals("ivan@test.com", data.getEmail());
        assertEquals("Test Bank API", data.getAppName());
        assertNotNull(data.getIssuedAt());
        assertNotNull(data.getExpiresAt());
        assertTrue(data.getIssuedAt().isBefore(data.getExpiresAt()));
        assertTrue(Instant.now().isBefore(data.getExpiresAt()));
    }

    @Test
    void testIsValidRightAfterGenerate() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@example.com");

        String token = tokenService.generateToken(user);

        boolean valid = tokenService.isValid(token);

        assertTrue(valid);
    }

    @Test
    void testInvalidateTokenMakesItInvalid() {
        User user = new User();
        user.setUsername("logoutUser");
        user.setEmail("logout@example.com");

        String token = tokenService.generateToken(user);
        assertTrue(tokenService.isValid(token));

        tokenService.invalidateToken(token);

        boolean validAfter = tokenService.isValid(token);

        assertFalse(validAfter);
    }
}
