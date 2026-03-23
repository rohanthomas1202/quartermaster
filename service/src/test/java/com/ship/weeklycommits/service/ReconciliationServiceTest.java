package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.ReconcileRequest;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import com.ship.weeklycommits.model.Outcome;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    private WeeklyCommitRepository weeklyCommitRepository;

    @Mock
    private CommitItemRepository commitItemRepository;

    @Mock
    private WeeklyCommitService weeklyCommitService;

    @Captor
    private ArgumentCaptor<CommitItem> commitItemCaptor;

    private ReconciliationService reconciliationService;

    @BeforeEach
    void setUp() {
        reconciliationService = new ReconciliationService(
                weeklyCommitRepository, commitItemRepository, weeklyCommitService);
    }

    @Test
    void reconcileItem_setsCompletionStatus() {
        LocalDate weekStart = LocalDate.of(2026, 3, 23);
        UUID weekId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        WeeklyCommit week = new WeeklyCommit();
        week.setId(weekId);
        week.setStatus(WeeklyCommitStatus.RECONCILING);

        CommitItem item = new CommitItem();
        item.setId(itemId);
        item.setWeeklyCommit(week);
        item.setCompletionStatus(CompletionStatus.PENDING);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate("user-1", weekStart))
                .thenReturn(Optional.of(week));
        when(commitItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(commitItemRepository.save(any(CommitItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReconcileRequest request = new ReconcileRequest(CompletionStatus.COMPLETED, "Done well");

        reconciliationService.reconcileItem("user-1", weekStart, itemId, request);

        verify(commitItemRepository).save(commitItemCaptor.capture());
        CommitItem saved = commitItemCaptor.getValue();
        assertThat(saved.getCompletionStatus()).isEqualTo(CompletionStatus.COMPLETED);
        assertThat(saved.getCompletionNotes()).isEqualTo("Done well");
    }

    @Test
    void reconcileItem_rejectsWhenNotReconciling() {
        LocalDate weekStart = LocalDate.of(2026, 3, 23);
        UUID itemId = UUID.randomUUID();

        WeeklyCommit week = new WeeklyCommit();
        week.setStatus(WeeklyCommitStatus.DRAFT);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate("user-1", weekStart))
                .thenReturn(Optional.of(week));

        ReconcileRequest request = new ReconcileRequest(CompletionStatus.COMPLETED, null);

        assertThatThrownBy(() -> reconciliationService.reconcileItem("user-1", weekStart, itemId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("week is DRAFT");
    }

    @Test
    void submitReconciliation_carriesForwardIncompleteItems() {
        LocalDate weekStart = LocalDate.of(2026, 3, 23);
        UUID weekId = UUID.randomUUID();

        WeeklyCommit week = new WeeklyCommit();
        week.setId(weekId);
        week.setUserId("user-1");
        week.setOrgId("org-1");
        week.setWeekStartDate(weekStart);
        week.setStatus(WeeklyCommitStatus.RECONCILING);

        Outcome outcome = new Outcome();
        outcome.setId(UUID.randomUUID());

        CommitItem partialItem = new CommitItem();
        partialItem.setId(UUID.randomUUID());
        partialItem.setWeeklyCommit(week);
        partialItem.setTitle("Incomplete task");
        partialItem.setDescription("Some description");
        partialItem.setOutcome(outcome);
        partialItem.setUrgency("HIGH");
        partialItem.setImportance("HIGH");
        partialItem.setSortOrder(1);
        partialItem.setCompletionStatus(CompletionStatus.PARTIAL);

        WeeklyCommit nextWeek = new WeeklyCommit();
        nextWeek.setId(UUID.randomUUID());
        nextWeek.setUserId("user-1");
        nextWeek.setOrgId("org-1");
        nextWeek.setWeekStartDate(weekStart.plusWeeks(1));

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate("user-1", weekStart))
                .thenReturn(Optional.of(week));
        when(weeklyCommitRepository.save(any(WeeklyCommit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commitItemRepository.findByWeeklyCommitIdAndCompletionStatusIn(
                weekId, List.of(CompletionStatus.PARTIAL, CompletionStatus.NOT_DONE)))
                .thenReturn(List.of(partialItem));
        when(weeklyCommitService.getOrCreateWeek("user-1", "org-1", weekStart.plusWeeks(1)))
                .thenReturn(nextWeek);
        when(commitItemRepository.save(any(CommitItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reconciliationService.submitReconciliation("user-1", "org-1", weekStart);

        // Verify week set to RECONCILED
        assertThat(week.getStatus()).isEqualTo(WeeklyCommitStatus.RECONCILED);
        assertThat(week.getReconciledAt()).isNotNull();

        // Verify carried item created in next week
        verify(commitItemRepository).save(commitItemCaptor.capture());
        CommitItem carried = commitItemCaptor.getValue();
        assertThat(carried.getTitle()).isEqualTo("Incomplete task");
        assertThat(carried.getCarriedFrom()).isEqualTo(partialItem);
        assertThat(carried.getWeeklyCommit()).isEqualTo(nextWeek);
    }
}
