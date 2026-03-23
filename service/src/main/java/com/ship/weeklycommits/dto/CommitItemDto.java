package com.ship.weeklycommits.dto;

import com.ship.weeklycommits.model.CompletionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommitItemDto(
    UUID id,
    UUID weeklyCommitId,
    @NotBlank String title,
    String description,
    @NotNull UUID outcomeId,
    @NotNull @Pattern(regexp = "HIGH|LOW") String urgency,
    @NotNull @Pattern(regexp = "HIGH|LOW") String importance,
    int sortOrder,
    CompletionStatus completionStatus,
    String completionNotes,
    UUID carriedFromId,
    int version,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
