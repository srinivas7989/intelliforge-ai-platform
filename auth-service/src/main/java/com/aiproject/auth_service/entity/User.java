package com.aiproject.auth_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Represents an authenticated platform user"
)
public class User {

    /**
     * Primary key of users table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Unique identifier of user",
            example = "1"
    )
    private Long id;

    /**
     * Full name of the user.
     */
    @NotBlank
    @Column(nullable = false, length = 150)
    @Schema(
            description = "Full name of user",
            example = "John Doe"
    )
    private String name;

    /**
     * Unique email address used for login.
     */
    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 200)
    @Schema(
            description = "Unique email address of user",
            example = "john@example.com"
    )
    private String email;

    /**
     * BCrypt hashed password.
     */
    @NotBlank
    @Column(nullable = false, length = 500)
    @Schema(
            description = "Encrypted password hash"
    )
    private String password;

    /**
     * Indicates whether account is enabled.
     */
    @Column(nullable = false)
    @Schema(
            description = "Whether account is enabled",
            example = "true"
    )
    private boolean enabled;

    /**
     * Indicates whether account is locked.
     */
    @Column(nullable = false)
    @Schema(
            description = "Whether account is locked",
            example = "false"
    )
    private boolean accountLocked;

    /**
     * Timestamp when account was created.
     */
    @Column(nullable = false, updatable = false)
    @Schema(
            description = "Account creation timestamp",
            example = "2026-05-25T10:15:30"
    )
    private LocalDateTime createdAt;

    /**
     * User roles for authorization.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    @Schema(
            description = "Roles assigned to user"
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Initializes default values before persisting.
     */
    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();

        if (!enabled) {
            enabled = true;
        }

        if (accountLocked) {
            accountLocked = false;
        }
    }

    /**
     * Helper method to check if account is active.
     */
    public boolean isAccountActive() {
        return enabled && !accountLocked;
    }

    /**
     * Helper method to add role safely.
     */
    public void addRole(Role role) {
        roles.add(role);
    }
}