package com.dev.bank.controllers.client;

import com.dev.bank.models.request.AuthLoginRequest;
import com.dev.bank.models.request.AuthRegisterRequest;
import com.dev.bank.models.response.AuthLoginResponse;
import com.dev.bank.models.response.AuthRegisterResponse;
import com.dev.bank.models.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/auth") //http://localhost:8081/auth/
public interface AuthenticationController {
    @PostMapping("/login")
    AuthLoginResponse login(@RequestBody AuthLoginRequest request);

    @PostMapping("/register")
    AuthRegisterResponse register(@RequestBody AuthRegisterRequest request);

    @PostMapping("/refresh")
    AuthLoginResponse refreshToken(HttpServletRequest request);

    @PostMapping("/invalidate")
    BaseResponse invalidateToken(HttpServletRequest request);
}