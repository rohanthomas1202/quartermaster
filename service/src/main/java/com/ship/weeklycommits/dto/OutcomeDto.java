package com.ship.weeklycommits.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OutcomeDto(
        UUID id,
        UUID definingObjectiveId,
        @NotBlank String title,
        String description,
        String measurableTarget,
        String currentValue,
        String ownerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
