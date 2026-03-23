package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.RcdoAlignmentDto;
import com.ship.weeklycommits.dto.TeamSummaryDto;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import com.ship.weeklycommits.model.DefiningObjective;
import com.ship.weeklycommits.model.Outcome;
import com.ship.weeklycommits.model.RallyCry;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ManagerDashboardService {

    private final WeeklyCommitRepository weeklyCommitRepository;
    private final CommitItemRepository commitItemRepository;

    public ManagerDashboardService(WeeklyCommitRepository weeklyCommitRepository,
                                    CommitItemRepository commitItemRepository) {
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.commitItemRepository = commitItemRepository;
    }

    public TeamSummaryDto getTeamSummary(String orgId, LocalDate weekStart) {
        List<WeeklyCommit> commits = weeklyCommitRepository.findByOrgIdAndWeekStartDate(orgId, weekStart);

        int reconciledCount = 0;
        int reconcilingCount = 0;
        int lockedCount = 0;
        int draftCount = 0;
        int totalCompleted = 0;
        int totalItems = 0;
        List<TeamSummaryDto.MemberSummary> members = new ArrayList<>();

        for (WeeklyCommit wc : commits) {
            switch (wc.getStatus()) {
                case RECONCILED -> reconciledCount++;
                case RECONCILING -> reconcilingCount++;
                case LOCKED -> lockedCount++;
                case DRAFT -> draftCount++;
            }

            List<CommitItem> items = commitItemRepository.findByWeeklyCommitIdOrderBySortOrder(wc.getId());
            int completed = 0;
            int partial = 0;
            int notDone = 0;

            for (CommitItem item : items) {
                switch (item.getCompletionStatus()) {
                    case COMPLETED -> completed++;
                    case PARTIAL -> partial++;
                    case NOT_DONE -> notDone++;
                    default -> { /* PENDING - not counted */ }
                }
            }

            totalCompleted += completed;
            totalItems += items.size();

            members.add(new TeamSummaryDto.MemberSummary(
                wc.getUserId(),
                wc.getStatus().name(),
                completed,
                partial,
                notDone,
                items.size()
            ));
        }

        double avgCompletionPercent = totalItems > 0
            ? BigDecimal.valueOf(totalCompleted * 100.0 / totalItems)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue()
            : 0.0;

        return new TeamSummaryDto(
            commits.size(),
            reconciledCount,
            reconcilingCount,
            lockedCount,
            draftCount,
            avgCompletionPercent,
            members
        );
    }

    public RcdoAlignmentDto getRcdoAlignment(String orgId, LocalDate weekStart) {
        List<WeeklyCommit> commits = weeklyCommitRepository.findByOrgIdAndWeekStartDate(orgId, weekStart);

        Map<String, RallyCryInfo> rallyCryMap = new LinkedHashMap<>();
        int totalCommits = 0;

        for (WeeklyCommit wc : commits) {
            List<CommitItem> items = commitItemRepository.findByWeeklyCommitIdOrderBySortOrder(wc.getId());
            for (CommitItem item : items) {
                Outcome outcome = item.getOutcome();
                DefiningObjective defObj = outcome.getDefiningObjective();
                RallyCry rallyCry = defObj.getRallyCry();
                String rallyCryId = rallyCry.getId().toString();

                rallyCryMap.computeIfAbsent(rallyCryId,
                    k -> new RallyCryInfo(rallyCryId, rallyCry.getTitle()));
                rallyCryMap.get(rallyCryId).count++;
                totalCommits++;
            }
        }

        final int total = totalCommits;
        List<RcdoAlignmentDto.RallyCryCommitCount> rallyCries = rallyCryMap.values().stream()
            .map(info -> new RcdoAlignmentDto.RallyCryCommitCount(
                info.id,
                info.title,
                info.count,
                total > 0
                    ? BigDecimal.valueOf(info.count * 100.0 / total)
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue()
                    : 0.0
            ))
            .toList();

        return new RcdoAlignmentDto(rallyCries, totalCommits);
    }

    private static class RallyCryInfo {
        final String id;
        final String title;
        int count;

        RallyCryInfo(String id, String title) {
            this.id = id;
            this.title = title;
            this.count = 0;
        }
    }
}
