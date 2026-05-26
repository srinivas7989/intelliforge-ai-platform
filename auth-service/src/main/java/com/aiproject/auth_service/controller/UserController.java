package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.dto.ChangePasswordRequest;
import com.aiproject.auth_service.dto.UpdateProfileRequest;
import com.aiproject.auth_service.entity.User;
import com.aiproject.auth_service.exception.BadRequestException;
import com.aiproject.auth_service.exception.ResourceNotFoundException;
import com.aiproject.auth_service.exception.UnauthorizedException;
import com.aiproject.auth_service.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "User APIs",
        description = "Authenticated user profile and account management APIs"
)
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Get current user",
            description = "Returns profile information of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current user fetched successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "roles", user.getRoles().stream()
                        .map(role -> role.getName())
                        .toList()
        ));
    }

    @Operation(
            summary = "Update current user profile",
            description = "Updates profile details of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
            }
    )
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {

        User user = getCurrentUser(authentication);

        user.setName(request.name());

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "Profile updated successfully")
        );
    }

    @Operation(
            summary = "Change password",
            description = "Changes password after validating the old password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Old password is incorrect or invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
            }
    )
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        User user = getCurrentUser(authentication);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }

    @Operation(
            summary = "Delete current user account",
            description = "Deletes the currently authenticated user's account permanently.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {

        User user = getCurrentUser(authentication);

        userRepository.delete(user);

        return ResponseEntity.ok(
                Map.of("message", "Account deleted successfully")
        );
    }

    /**
     * Resolves authenticated user from Spring Security context.
     */
    private User getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException(
                    "Unauthorized: Missing or invalid token"
            );
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }
}