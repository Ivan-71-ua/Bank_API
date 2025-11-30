package com.dev.bank.security;

import com.dev.bank.models.User;
import com.dev.bank.security.client.TokenData;
import com.dev.bank.security.client.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final String appName;
    private final long ttlSeconds;
    private final SecretKey key;
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public TokenServiceImpl(
            @Value("${app.name}") String appName,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds}") long ttlSeconds
    ) {
        this.appName = appName;
        this.ttlSeconds = ttlSeconds;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        log.info("Генерація токена для користувача {}", user.getUsername());

        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(appName)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("email", user.getEmail())
                .signWith(key)
                .compact();
    }

    @Override
    public TokenData parseToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        Claims claims = jws.getPayload();

        TokenData data = new TokenData();
        data.setUsername(claims.getSubject());
        data.setEmail(claims.get("email", String.class));
        data.setAppName(claims.getIssuer());

        Date issued = claims.getIssuedAt();
        Date exp = claims.getExpiration();

        if (issued != null) {
            data.setIssuedAt(issued.toInstant());
        }
        if (exp != null) {
            data.setExpiresAt(exp.toInstant());
        }

        return data;
    }

    @Override
    public boolean isValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (blacklist.containsKey(token)) {
            log.warn("Спроба використати інвалідований токен");
            return false;
        }
        try {
            TokenData data = parseToken(token);
            Instant now = Instant.now();
            Instant expiresAt = data.getExpiresAt();
            if (expiresAt == null) {
                return false;
            }
            boolean ok = now.isBefore(expiresAt);
            if (!ok) {
                log.warn("Термін дії токена вичерпано для користувача {}", data.getUsername());
            }
            return ok;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Помилка перевірки токена: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String refreshToken(String token) {
        if (!isValid(token)) {
            log.warn("Неможливо оновити невалідний токен");
            return null;
        }
        TokenData data = parseToken(token);

        User user = new User();
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());

        blacklist.put(token, Instant.now());
        log.info("Оновлення токена для користувача {}", data.getUsername());

        return generateToken(user);
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        blacklist.put(token, Instant.now());
        log.info("Токен додано до чорного списку");
    }
}
