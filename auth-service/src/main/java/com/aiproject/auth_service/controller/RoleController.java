package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.dto.AssignRoleRequest;
import com.aiproject.auth_service.dto.CreateRoleRequest;
import com.aiproject.auth_service.entity.Role;
import com.aiproject.auth_service.entity.User;
import com.aiproject.auth_service.exception.BadRequestException;
import com.aiproject.auth_service.exception.ResourceNotFoundException;
import com.aiproject.auth_service.repository.RoleRepository;
import com.aiproject.auth_service.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Role APIs",
        description = "Admin APIs for creating roles and assigning roles to users"
)
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Operation(
            summary = "Create role",
            description = "Creates a new role in the system. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role created successfully"),
                    @ApiResponse(responseCode = "400", description = "Role already exists or invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
            }
    )
    @PostMapping
    public ResponseEntity<?> createRole(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new BadRequestException("Role already exists");
        }

        Role role = Role.builder()
                .name(request.name())
                .build();

        roleRepository.save(role);

        return ResponseEntity.ok(
                Map.of("message", "Role created successfully")
        );
    }

    @Operation(
            summary = "Assign role to user",
            description = "Assigns an existing role to an existing user using userId and roleId. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
                    @ApiResponse(responseCode = "404", description = "User or role not found")
            }
    )
    @PostMapping("/assign")
    public ResponseEntity<?> assignRole(
            @Valid @RequestBody AssignRoleRequest request
    ) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.userId()
                ));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found with id: " + request.roleId()
                ));

        if (user.getRoles().contains(role)) {
            throw new BadRequestException("User already has this role");
        }

        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "Role assigned successfully")
        );
    }
}