package com.ship.weeklycommits.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DefiningObjectiveDto(
        UUID id,
        UUID rallyCryId,
        @NotBlank String title,
        String description,
        String ownerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
