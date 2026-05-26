package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for forgot password functionality"
)
public record ForgotPasswordRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 200, message = "Email must not exceed 200 characters")
        @Schema(
                description = "Registered email address of the user",
                example = "john@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email

) {
}