package com.ship.weeklycommits.dto;

import java.util.List;

public record TeamSummaryDto(
    int totalMembers, int reconciledCount, int reconcilingCount,
    int lockedCount, int draftCount, double avgCompletionPercent,
    List<MemberSummary> members
) {
    public record MemberSummary(
        String userId, String status, int completedCount,
        int partialCount, int notDoneCount, int totalItems
    ) {}
}
