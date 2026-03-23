package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.TeamSummaryDto;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagerDashboardServiceTest {

    @Mock
    private WeeklyCommitRepository weeklyCommitRepository;

    @Mock
    private CommitItemRepository commitItemRepository;

    private ManagerDashboardService managerDashboardService;

    private static final LocalDate WEEK_START = LocalDate.of(2026, 3, 23);
    private static final String ORG_ID = "org-1";

    @BeforeEach
    void setUp() {
        managerDashboardService = new ManagerDashboardService(weeklyCommitRepository, commitItemRepository);
    }

    @Test
    void getTeamSummary_aggregatesCorrectly() {
        // user-1: RECONCILED with 2 items (1 COMPLETED, 1 PARTIAL)
        UUID wcId1 = UUID.randomUUID();
        WeeklyCommit wc1 = new WeeklyCommit();
        wc1.setId(wcId1);
        wc1.setUserId("user-1");
        wc1.setOrgId(ORG_ID);
        wc1.setWeekStartDate(WEEK_START);
        wc1.setStatus(WeeklyCommitStatus.RECONCILED);

        CommitItem item1 = new CommitItem();
        item1.setId(UUID.randomUUID());
        item1.setCompletionStatus(CompletionStatus.COMPLETED);

        CommitItem item2 = new CommitItem();
        item2.setId(UUID.randomUUID());
        item2.setCompletionStatus(CompletionStatus.PARTIAL);

        // user-2: LOCKED with 0 items
        UUID wcId2 = UUID.randomUUID();
        WeeklyCommit wc2 = new WeeklyCommit();
        wc2.setId(wcId2);
        wc2.setUserId("user-2");
        wc2.setOrgId(ORG_ID);
        wc2.setWeekStartDate(WEEK_START);
        wc2.setStatus(WeeklyCommitStatus.LOCKED);

        when(weeklyCommitRepository.findByOrgIdAndWeekStartDate(ORG_ID, WEEK_START))
            .thenReturn(List.of(wc1, wc2));
        when(commitItemRepository.findByWeeklyCommitIdOrderBySortOrder(wcId1))
            .thenReturn(List.of(item1, item2));
        when(commitItemRepository.findByWeeklyCommitIdOrderBySortOrder(wcId2))
            .thenReturn(List.of());

        TeamSummaryDto result = managerDashboardService.getTeamSummary(ORG_ID, WEEK_START);

        assertThat(result.totalMembers()).isEqualTo(2);
        assertThat(result.reconciledCount()).isEqualTo(1);
        assertThat(result.lockedCount()).isEqualTo(1);
        assertThat(result.reconcilingCount()).isEqualTo(0);
        assertThat(result.draftCount()).isEqualTo(0);
        assertThat(result.avgCompletionPercent()).isEqualTo(50.0);
        assertThat(result.members()).hasSize(2);

        TeamSummaryDto.MemberSummary member1 = result.members().get(0);
        assertThat(member1.userId()).isEqualTo("user-1");
        assertThat(member1.status()).isEqualTo("RECONCILED");
        assertThat(member1.completedCount()).isEqualTo(1);
        assertThat(member1.partialCount()).isEqualTo(1);
        assertThat(member1.notDoneCount()).isEqualTo(0);
        assertThat(member1.totalItems()).isEqualTo(2);

        TeamSummaryDto.MemberSummary member2 = result.members().get(1);
        assertThat(member2.userId()).isEqualTo("user-2");
        assertThat(member2.status()).isEqualTo("LOCKED");
        assertThat(member2.totalItems()).isEqualTo(0);
    }
}
