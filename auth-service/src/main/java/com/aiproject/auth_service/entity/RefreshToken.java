package com.aiproject.auth_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token", columnList = "token")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Stores JWT refresh tokens for authenticated user sessions"
)
public class RefreshToken {

    /**
     * Primary key of refresh token table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Unique identifier of refresh token",
            example = "1"
    )
    private Long id;

    /**
     * JWT refresh token value.
     */
    @Column(nullable = false, unique = true, length = 500)
    @Schema(
            description = "JWT refresh token string",
            example = "eyJhbGciOiJIUzI1NiJ9.refresh.token"
    )
    private String token;

    /**
     * User associated with refresh token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(
            description = "User associated with refresh token"
    )
    private User user;

    /**
     * Expiration timestamp of refresh token.
     */
    @Column(nullable = false)
    @Schema(
            description = "Refresh token expiration timestamp",
            example = "2026-05-25T18:30:00Z"
    )
    private Instant expiryDate;

    /**
     * Indicates whether token has been revoked/logout.
     */
    @Column(nullable = false)
    @Schema(
            description = "Indicates whether refresh token is revoked",
            example = "false"
    )
    private boolean revoked;

    /**
     * Initializes default token state before persisting.
     */
    @PrePersist
    public void prePersist() {
        revoked = false;
    }

    /**
     * Helper method to check if token is expired.
     */
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    /**
     * Helper method to check if token is active.
     */
    public boolean isActive() {
        return !revoked && !isExpired();
    }
}