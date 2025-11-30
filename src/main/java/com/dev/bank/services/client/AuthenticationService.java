package com.dev.bank.services.client;

import com.dev.bank.models.request.AuthLoginRequest;
import com.dev.bank.models.request.AuthRegisterRequest;
import com.dev.bank.models.response.AuthLoginResponse;
import com.dev.bank.models.response.AuthRegisterResponse;
import com.dev.bank.models.response.BaseResponse;

public interface AuthenticationService {
    AuthLoginResponse login(AuthLoginRequest request);

    AuthRegisterResponse register(AuthRegisterRequest request);

    AuthLoginResponse refreshToken(String token);

    BaseResponse logout(String token);
}