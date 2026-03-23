package com.ship.weeklycommits.dto;

import com.ship.weeklycommits.model.CompletionStatus;
import jakarta.validation.constraints.NotNull;

public record ReconcileRequest(
    @NotNull CompletionStatus completionStatus,
    String completionNotes
) {}
