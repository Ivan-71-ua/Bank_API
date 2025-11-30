package com.dev.bank.security;

import com.dev.bank.security.client.RequireToken;
import com.dev.bank.security.client.TokenData;
import com.dev.bank.security.client.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TokenAuthInterceptor.class);

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;

        boolean needToken =
                method.hasMethodAnnotation(RequireToken.class) ||
                        method.getBeanType().isAnnotationPresent(RequireToken.class);

        if (!needToken) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (isBlank(header)) {
            header = request.getHeader("X-Auth-Token");
        }

        if (isBlank(header)) {
            log.warn("Запит без токена на захищений ендпойнт");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Потрібен токен авторизації");
            return false;
        }

        String token = header.trim();
        String lower = token.toLowerCase();
        if (lower.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        if (!tokenService.isValid(token)) {
            log.warn("Невалідний або прострочений токен");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Невалідний або прострочений токен");
            return false;
        }

        TokenData data = tokenService.parseToken(token);
        request.setAttribute("tokenData", data);
        request.setAttribute("authToken", token);

        log.info("Успішна перевірка токена для користувача {}", data.getUsername());
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}