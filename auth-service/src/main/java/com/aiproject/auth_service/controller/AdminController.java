package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.entity.User;
import com.aiproject.auth_service.exception.ResourceNotFoundException;
import com.aiproject.auth_service.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(
        name = "Admin APIs",
        description = "Admin-only APIs for user management"
)
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserRepository userRepository;

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users with their account status and assigned roles. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
            }
    )
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream().map(user -> Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "enabled", user.isEnabled(),
                        "accountLocked", user.isAccountLocked(),
                        "roles", user.getRoles().stream()
                                .map(role -> role.getName())
                                .toList()
                )).toList()
        );
    }

    @Operation(
            summary = "Lock user account",
            description = "Locks a user account by user ID. Once locked, the user cannot log in. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User locked successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/users/{id}/lock")
    public ResponseEntity<?> lockUser(
            @Parameter(
                    description = "ID of the user to lock",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setAccountLocked(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User locked successfully"));
    }
}