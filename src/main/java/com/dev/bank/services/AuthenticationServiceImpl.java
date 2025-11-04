package com.dev.bank.services;

import com.dev.bank.models.User;
import com.dev.bank.models.request.AuthLoginRequest;
import com.dev.bank.models.request.AuthRegisterRequest;
import com.dev.bank.models.response.AuthLoginResponse;
import com.dev.bank.models.response.AuthRegisterResponse;
import com.dev.bank.services.client.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final Map<String, User> users = new HashMap<>();

    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_RE = Pattern.compile("^\\+?[0-9\\-\\s]{7,20}$");

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        log.info("LOGIN attempt: {}", request.getUsername());
        AuthLoginResponse resp = new AuthLoginResponse();

        if (blank(request.getUsername()) || blank(request.getPassword())) {
            resp.setSuccess(false);
            resp.setMessage("username and password are required");
            return resp;
        }
        User u = users.get(request.getUsername());
        if (u == null) {
            resp.setSuccess(false);
            resp.setMessage("user not found");
            return resp;
        }
        if (!u.getPassword().equals(request.getPassword())) {
            resp.setSuccess(false);
            resp.setMessage("invalid credentials");
            return resp;
        }
        resp.setSuccess(true);
        resp.setMessage("login success");
        resp.setToken("demo-token-" + System.currentTimeMillis());
        log.info("LOGIN success: {}", request.getUsername());
        return resp;
    }

    @Override
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        log.info("REGISTER attempt: {} / {}", request.getUsername(), request.getEmail());
        AuthRegisterResponse resp = new AuthRegisterResponse();

        if (blank(request.getUsername()) || blank(request.getPassword())
                || blank(request.getEmail()) || request.getBirthday() == null) {
            resp.setSuccess(false);
            resp.setMessage("username, password, email, birthday are required");
            return resp;
        }
        if (!EMAIL_RE.matcher(request.getEmail()).matches()) {
            resp.setSuccess(false);
            resp.setMessage("invalid email");
            return resp;
        }
        LocalDate b = request.getBirthday();
        if (b.isAfter(LocalDate.now())) {
            resp.setSuccess(false);
            resp.setMessage("birthday cannot be in the future");
            return resp;
        }
        String phone = request.getPhoneNumber();
        if (phone != null && !phone.isEmpty() && !PHONE_RE.matcher(phone).matches()) {
            resp.setSuccess(false);
            resp.setMessage("invalid phoneNumber");
            return resp;
        }
        if (users.containsKey(request.getUsername())) {
            resp.setSuccess(false);
            resp.setMessage("username already taken");
            return resp;
        }

        users.put(request.getUsername(), new User(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getBirthday(),
                request.getPhoneNumber()
        ));
        resp.setSuccess(true);
        resp.setMessage("registration success");
        log.info("REGISTER success: {}", request.getUsername());
        return resp;
    }

    private boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
