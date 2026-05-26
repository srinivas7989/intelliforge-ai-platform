package com.aiproject.auth_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Stores password reset tokens for forgot-password functionality"
)
public class PasswordResetToken {

    /**
     * Primary key of password reset token table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Unique identifier of the password reset token",
            example = "1"
    )
    private Long id;

    /**
     * Unique token sent to user for password reset.
     */
    @Column(nullable = false, unique = true, length = 500)
    @Schema(
            description = "Unique password reset token",
            example = "d7a7a7f3-8d51-4d17-9d34-ef4abac12345"
    )
    private String token;

    /**
     * User associated with this reset token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(
            description = "User associated with this password reset token"
    )
    private User user;

    /**
     * Expiration timestamp of reset token.
     */
    @Column(nullable = false)
    @Schema(
            description = "Token expiry timestamp",
            example = "2026-05-25T18:30:00Z"
    )
    private Instant expiryDate;

    /**
     * Indicates whether token is already used.
     */
    @Column(nullable = false)
    @Schema(
            description = "Indicates whether the token has already been used",
            example = "false"
    )
    private boolean used;

    /**
     * Automatically initialize token state before insert.
     */
    @PrePersist
    public void prePersist() {
        if (used == false) {
            used = false;
        }
    }
}