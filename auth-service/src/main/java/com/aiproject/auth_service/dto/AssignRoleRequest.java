package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request payload for assigning a role to a user"
)
public record AssignRoleRequest(

        @NotNull(message = "User ID is required")
        @Schema(
                description = "ID of the user receiving the role",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long userId,

        @NotNull(message = "Role ID is required")
        @Schema(
                description = "ID of the role being assigned",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long roleId

) {
}