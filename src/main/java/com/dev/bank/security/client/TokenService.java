package com.dev.bank.security.client;

import com.dev.bank.models.User;

public interface TokenService {

    String generateToken(User user);

    TokenData parseToken(String token);

    boolean isValid(String token);

    String refreshToken(String token);

    void invalidateToken(String token);
}