package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for resetting user password using reset token"
)
public record ResetPasswordRequest(

        @NotBlank(message = "Reset token is required")
        @Size(max = 1000, message = "Token is too long")
        @Schema(
                description = "Password reset token generated from forgot-password API",
                example = "d7a7a7f3-8d51-4d17-9d34-ef4abac12345",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String token,

        @NotBlank(message = "New password is required")
        @Size(
                min = 8,
                message = "Password must contain at least 8 characters"
        )
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
                message = """
                        Password must contain:
                        uppercase letter,
                        lowercase letter,
                        number,
                        special character
                        """
        )
        @Schema(
                description = "New password for the account",
                example = "NewPassword@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String newPassword

) {
}