package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for updating user profile information"
)
public record UpdateProfileRequest(

        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Name must contain between 2 and 150 characters"
        )
        @Schema(
                description = "Updated full name of the user",
                example = "John Doe Updated",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name

) {
}