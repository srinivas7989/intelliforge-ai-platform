package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for changing user password"
)
public record ChangePasswordRequest(

        @NotBlank(message = "Old password is required")
        @Schema(
                description = "Current password of the authenticated user",
                example = "OldPassword@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must contain at least 8 characters")
        @Schema(
                description = "New password to replace the old password",
                example = "NewPassword@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String newPassword

) {
}