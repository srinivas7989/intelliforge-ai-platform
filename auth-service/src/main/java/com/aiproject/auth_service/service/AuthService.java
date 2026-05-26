package com.aiproject.auth_service.service;

import com.aiproject.auth_service.dto.*;
import jakarta.validation.Valid;

public interface AuthService {

    void register(@Valid RegisterRequest request);

    AuthResponse login(@Valid LoginRequest request);

    AuthResponse refresh(@Valid RefreshTokenRequest request);

    void logout(@Valid RefreshTokenRequest request);


    Object forgotPassword(@Valid ForgotPasswordRequest request);

    Object resetPassword(@Valid ResetPasswordRequest request);
}
