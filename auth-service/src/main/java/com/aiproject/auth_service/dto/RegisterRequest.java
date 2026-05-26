package com.aiproject.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request payload for user registration"
)
public record RegisterRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 150,
                message = "Name must contain between 2 and 150 characters")
        @Schema(
                description = "Full name of the user",
                example = "John Doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 200,
                message = "Email must not exceed 200 characters")
        @Schema(
                description = "Unique email address of the user",
                example = "john@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8,
                message = "Password must contain at least 8 characters")
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
                description = "User account password",
                example = "Password@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String password

) {
}