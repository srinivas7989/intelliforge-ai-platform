package com.aiproject.auth_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(name = "idx_role_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Represents a user role used for authorization and access control"
)
public class Role {

    /**
     * Primary key of role table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Unique identifier of role",
            example = "1"
    )
    private Long id;

    /**
     * Unique role name.
     * Examples: ROLE_USER, ROLE_ADMIN
     */
    @NotBlank
    @Column(unique = true, nullable = false, length = 100)
    @Schema(
            description = "Unique role name",
            example = "ROLE_ADMIN"
    )
    private String name;
}