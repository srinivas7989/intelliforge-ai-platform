package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.dto.*;
import com.aiproject.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication APIs",
        description = "APIs for authentication, JWT tokens, password reset, and session management"
)
public class AuthController {

    private final AuthService authServ;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account using name, email, and password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authServ.register(request);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns JWT access and refresh tokens.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid email or password")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authServ.login(request));
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "Generates a new access token using a valid refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authServ.refresh(request));
    }

    @Operation(
            summary = "Logout user",
            description = "Revokes refresh token and logs out the current user session.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logged out successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authServ.logout(request);

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }

    @Operation(
            summary = "Forgot password",
            description = "Generates password reset token for a registered email address.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset token generated"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        return ResponseEntity.ok(
                authServ.forgotPassword(request)
        );
    }

    @Operation(
            summary = "Reset password",
            description = "Resets user password using a valid password reset token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        return ResponseEntity.ok(
                authServ.resetPassword(request)
        );
    }
}