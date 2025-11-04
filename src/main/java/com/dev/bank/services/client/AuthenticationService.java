package com.dev.bank.services.client;

import com.dev.bank.models.request.AuthLoginRequest;
import com.dev.bank.models.request.AuthRegisterRequest;
import com.dev.bank.models.response.AuthLoginResponse;
import com.dev.bank.models.response.AuthRegisterResponse;

public interface AuthenticationService {
    AuthLoginResponse login(AuthLoginRequest request);
    AuthRegisterResponse register(AuthRegisterRequest request);
}