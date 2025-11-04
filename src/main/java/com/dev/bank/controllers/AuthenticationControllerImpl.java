package com.dev.bank.controllers;

import com.dev.bank.controllers.client.AuthenticationController;
import com.dev.bank.models.request.AuthLoginRequest;
import com.dev.bank.models.request.AuthRegisterRequest;
import com.dev.bank.models.response.AuthLoginResponse;
import com.dev.bank.models.response.AuthRegisterResponse;
import com.dev.bank.services.client.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationControllerImpl implements AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        return service.login(request);
    }

    @Override
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        return service.register(request);
    }
}