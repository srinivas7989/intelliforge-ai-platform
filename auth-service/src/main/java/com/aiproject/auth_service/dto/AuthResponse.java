package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Authentication response containing JWT tokens and token metadata"
)
public record AuthResponse(

        @Schema(
                description = "JWT access token used for authenticated API requests",
                example = "eyJhbGciOiJIUzI1NiJ9.access.token"
        )
        String accessToken,

        @Schema(
                description = "JWT refresh token used to generate new access tokens",
                example = "eyJhbGciOiJIUzI1NiJ9.refresh.token"
        )
        String refreshToken,

        @Schema(
                description = "Authentication token type",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Access token expiration time in seconds",
                example = "900"
        )
        long expiresIn

) {
}