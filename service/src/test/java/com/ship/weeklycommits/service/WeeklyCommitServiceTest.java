package com.ship.weeklycommits.service;

import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.OrgSettingsRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyCommitServiceTest {

    @Mock
    private WeeklyCommitRepository weeklyCommitRepository;

    @Mock
    private OrgSettingsRepository orgSettingsRepository;

    @Captor
    private ArgumentCaptor<WeeklyCommit> weeklyCommitCaptor;

    @Captor
    private ArgumentCaptor<List<WeeklyCommit>> weeklyCommitListCaptor;

    private WeeklyCommitService weeklyCommitService;

    @BeforeEach
    void setUp() {
        weeklyCommitService = new WeeklyCommitService(weeklyCommitRepository, orgSettingsRepository);
    }

    @Test
    void getOrCreateWeek_createsNewDraftWhenNotFound() {
        LocalDate monday = LocalDate.of(2026, 3, 23);
        when(weeklyCommitRepository.findByUserIdAndWeekStartDate("user-1", monday))
                .thenReturn(Optional.empty());
        when(weeklyCommitRepository.save(any(WeeklyCommit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeeklyCommit result = weeklyCommitService.getOrCreateWeek("user-1", "org-1", monday);

        assertThat(result.getStatus()).isEqualTo(WeeklyCommitStatus.DRAFT);
        assertThat(result.getWeekStartDate()).isEqualTo(monday);
        assertThat(result.getWeekEndDate()).isEqualTo(monday.plusDays(6));

        verify(weeklyCommitRepository).save(weeklyCommitCaptor.capture());
        WeeklyCommit saved = weeklyCommitCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getOrgId()).isEqualTo("org-1");
    }

    @Test
    void getOrCreateWeek_returnsExistingWhenFound() {
        LocalDate monday = LocalDate.of(2026, 3, 23);
        WeeklyCommit existing = new WeeklyCommit();
        existing.setUserId("user-1");
        existing.setOrgId("org-1");
        existing.setWeekStartDate(monday);
        existing.setWeekEndDate(monday.plusDays(6));
        existing.setStatus(WeeklyCommitStatus.DRAFT);

        when(weeklyCommitRepository.findByUserIdAndWeekStartDate("user-1", monday))
                .thenReturn(Optional.of(existing));

        WeeklyCommit result = weeklyCommitService.getOrCreateWeek("user-1", "org-1", monday);

        assertThat(result).isSameAs(existing);
        verify(weeklyCommitRepository, never()).save(any());
    }

    @Test
    void transitionToLocked_changesDraftToLocked() {
        WeeklyCommit draft = new WeeklyCommit();
        draft.setStatus(WeeklyCommitStatus.DRAFT);
        draft.setOrgId("org-1");

        when(weeklyCommitRepository.findByOrgIdAndStatus("org-1", WeeklyCommitStatus.DRAFT))
                .thenReturn(List.of(draft));

        weeklyCommitService.transitionToLocked("org-1");

        verify(weeklyCommitRepository).saveAll(weeklyCommitListCaptor.capture());
        List<WeeklyCommit> saved = weeklyCommitListCaptor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getStatus()).isEqualTo(WeeklyCommitStatus.LOCKED);
        assertThat(saved.get(0).getLockedAt()).isNotNull();
    }

    @Test
    void transitionToReconciling_changesLockedToReconciling() {
        WeeklyCommit locked = new WeeklyCommit();
        locked.setStatus(WeeklyCommitStatus.LOCKED);
        locked.setOrgId("org-1");

        when(weeklyCommitRepository.findByOrgIdAndStatus("org-1", WeeklyCommitStatus.LOCKED))
                .thenReturn(List.of(locked));

        weeklyCommitService.transitionToReconciling("org-1");

        verify(weeklyCommitRepository).saveAll(weeklyCommitListCaptor.capture());
        List<WeeklyCommit> saved = weeklyCommitListCaptor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getStatus()).isEqualTo(WeeklyCommitStatus.RECONCILING);
    }
}
