package com.ship.weeklycommits.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RallyCryDto(
        UUID id,
        @NotBlank String title,
        String description,
        String orgId,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public RallyCryDto(String title, String description) {
        this(null, title, description, null, false, null, null);
    }
}
