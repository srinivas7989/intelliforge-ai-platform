package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for refreshing JWT access token"
)
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        @Size(max = 1000, message = "Refresh token is too long")
        @Schema(
                description = "JWT refresh token used to generate a new access token",
                example = "eyJhbGciOiJIUzI1NiJ9.refresh.token",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken

) {
}