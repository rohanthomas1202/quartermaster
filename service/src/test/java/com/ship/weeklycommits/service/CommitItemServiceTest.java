package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.CommitItemDto;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import com.ship.weeklycommits.model.Outcome;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.OutcomeRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommitItemServiceTest {

    @Mock
    private CommitItemRepository commitItemRepository;

    @Mock
    private WeeklyCommitRepository weeklyCommitRepository;

    @Mock
    private OutcomeRepository outcomeRepository;

    private CommitItemService commitItemService;

    private static final LocalDate WEEK_START = LocalDate.of(2026, 3, 23);
    private static final String USER_ID = "user-1";
    private static final String ORG_ID = "org-1";

    @BeforeEach
    void setUp() {
        commitItemService = new CommitItemService(commitItemRepository, weeklyCommitRepository, outcomeRepository);
    }

    @Test
    void addItem_succeedsInDraftState() {
        UUID weeklyCommitId = UUID.randomUUID();
        UUID outcomeId = UUID.randomUUID();

        WeeklyCommit wc = new WeeklyCommit();
        wc.setId(weeklyCommitId);
        wc.setUserId(USER_ID);
        wc.setOrgId(ORG_ID);
        wc.setWeekStartDate(WEEK_START);
        wc.setStatus(WeeklyCommitStatus.DRAFT);

        Outcome outcome = new Outcome();
        outcome.setId(outcomeId);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate(USER_ID, WEEK_START))
                .thenReturn(Optional.of(wc));
        when(outcomeRepository.findById(outcomeId))
                .thenReturn(Optional.of(outcome));
        when(commitItemRepository.save(any(CommitItem.class)))
                .thenAnswer(invocation -> {
                    CommitItem item = invocation.getArgument(0);
                    item.setId(UUID.randomUUID());
                    return item;
                });

        CommitItemDto dto = new CommitItemDto(
                null, null, "Ship login page", "Build the login UI",
                outcomeId, "HIGH", "LOW", 1,
                CompletionStatus.PENDING, null, null, 0, null, null
        );

        CommitItemDto result = commitItemService.addItem(USER_ID, ORG_ID, WEEK_START, dto);

        assertThat(result.title()).isEqualTo("Ship login page");
        assertThat(result.urgency()).isEqualTo("HIGH");
        verify(commitItemRepository).save(any(CommitItem.class));
    }

    @Test
    void addItem_rejectsWhenLocked() {
        UUID outcomeId = UUID.randomUUID();

        WeeklyCommit wc = new WeeklyCommit();
        wc.setId(UUID.randomUUID());
        wc.setUserId(USER_ID);
        wc.setStatus(WeeklyCommitStatus.LOCKED);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate(USER_ID, WEEK_START))
                .thenReturn(Optional.of(wc));

        CommitItemDto dto = new CommitItemDto(
                null, null, "Some item", null,
                outcomeId, "HIGH", "HIGH", 1,
                CompletionStatus.PENDING, null, null, 0, null, null
        );

        assertThatThrownBy(() -> commitItemService.addItem(USER_ID, ORG_ID, WEEK_START, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("week is LOCKED");

        verify(commitItemRepository, never()).save(any());
    }

    @Test
    void deleteItem_rejectsWhenLocked() {
        UUID itemId = UUID.randomUUID();

        WeeklyCommit wc = new WeeklyCommit();
        wc.setId(UUID.randomUUID());
        wc.setUserId(USER_ID);
        wc.setStatus(WeeklyCommitStatus.LOCKED);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate(USER_ID, WEEK_START))
                .thenReturn(Optional.of(wc));

        assertThatThrownBy(() -> commitItemService.deleteItem(USER_ID, WEEK_START, itemId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("week is LOCKED");

        verify(commitItemRepository, never()).delete(any());
    }
}
