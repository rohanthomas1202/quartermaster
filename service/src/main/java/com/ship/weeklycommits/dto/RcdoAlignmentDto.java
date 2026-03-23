package com.ship.weeklycommits.dto;

import java.util.List;

public record RcdoAlignmentDto(
    List<RallyCryCommitCount> rallyCries, int totalCommits
) {
    public record RallyCryCommitCount(
        String rallyCryId, String rallyCryTitle,
        int commitCount, double percentage
    ) {}
}
