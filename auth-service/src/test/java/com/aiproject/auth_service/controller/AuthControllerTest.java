package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.dto.*;
import com.aiproject.auth_service.exception.BadRequestException;
import com.aiproject.auth_service.exception.UnauthorizedException;
import com.aiproject.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuthController authController = new AuthController(authService);

    @Test
    void register_ShouldReturnSuccessMessage() {
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "Password@123"
        );

        doNothing().when(authService).register(request);

        var response = authController.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Map.of("message", "User registered successfully"), response.getBody());
        verify(authService).register(request);
    }

    @Test
    void register_ShouldThrowBadRequest_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "Password@123"
        );

        doThrow(new BadRequestException("Email already registered"))
                .when(authService).register(request);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authController.register(request)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(authService).register(request);
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest(
                "john@example.com",
                "Password@123"
        );

        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                900
        );

        when(authService.login(request)).thenReturn(authResponse);

        var response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("access-token", response.getBody().accessToken());
        assertEquals("refresh-token", response.getBody().refreshToken());
        assertEquals("Bearer", response.getBody().tokenType());
        assertEquals(900, response.getBody().expiresIn());

        verify(authService).login(request);
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest(
                "john@example.com",
                "wrong-password"
        );

        when(authService.login(request))
                .thenThrow(new UnauthorizedException("Invalid email or password"));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authController.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(authService).login(request);
    }

    @Test
    void refresh_ShouldReturnNewAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

        AuthResponse authResponse = new AuthResponse(
                "new-access-token",
                "refresh-token",
                "Bearer",
                900
        );

        when(authService.refresh(request)).thenReturn(authResponse);

        var response = authController.refresh(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("new-access-token", response.getBody().accessToken());
        verify(authService).refresh(request);
    }

    @Test
    void refresh_ShouldThrowUnauthorized_WhenTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

        when(authService.refresh(request))
                .thenThrow(new UnauthorizedException("Invalid refresh token"));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authController.refresh(request)
        );

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(authService).refresh(request);
    }

    @Test
    void logout_ShouldReturnSuccessMessage() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

        doNothing().when(authService).logout(request);

        var response = authController.logout(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Map.of("message", "Logged out successfully"), response.getBody());
        verify(authService).logout(request);
    }

    @Test
    void logout_ShouldThrowUnauthorized_WhenTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

        doThrow(new UnauthorizedException("Invalid refresh token"))
                .when(authService).logout(request);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authController.logout(request)
        );

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(authService).logout(request);
    }
}