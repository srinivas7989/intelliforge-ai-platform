package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for creating a new role"
)
public record CreateRoleRequest(

        @NotBlank(message = "Role name is required")
        @Size(max = 100, message = "Role name must not exceed 100 characters")
        @Pattern(
                regexp = "^ROLE_[A-Z_]+$",
                message = "Role name must follow format ROLE_NAME"
        )
        @Schema(
                description = "Unique role name",
                example = "ROLE_ADMIN",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name

) {
}