package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.ReconcileRequest;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReconciliationService {

    private final WeeklyCommitRepository weeklyCommitRepository;
    private final CommitItemRepository commitItemRepository;
    private final WeeklyCommitService weeklyCommitService;

    public ReconciliationService(WeeklyCommitRepository weeklyCommitRepository,
                                  CommitItemRepository commitItemRepository,
                                  WeeklyCommitService weeklyCommitService) {
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.commitItemRepository = commitItemRepository;
        this.weeklyCommitService = weeklyCommitService;
    }

    @Transactional
    public CommitItem reconcileItem(String userId, LocalDate weekStart, UUID itemId, ReconcileRequest request) {
        WeeklyCommit week = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Week not found"));

        if (week.getStatus() != WeeklyCommitStatus.RECONCILING) {
            throw new IllegalStateException("week is " + week.getStatus());
        }

        CommitItem item = commitItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getWeeklyCommit().getId().equals(week.getId())) {
            throw new IllegalArgumentException("Item does not belong to this week");
        }

        item.setCompletionStatus(request.completionStatus());
        item.setCompletionNotes(request.completionNotes());

        return commitItemRepository.save(item);
    }

    @Transactional
    public WeeklyCommit submitReconciliation(String userId, String orgId, LocalDate weekStart) {
        WeeklyCommit week = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Week not found"));

        if (week.getStatus() != WeeklyCommitStatus.RECONCILING) {
            throw new IllegalStateException("week is " + week.getStatus());
        }

        week.setStatus(WeeklyCommitStatus.RECONCILED);
        week.setReconciledAt(OffsetDateTime.now());
        weeklyCommitRepository.save(week);

        List<CommitItem> incompleteItems = commitItemRepository.findByWeeklyCommitIdAndCompletionStatusIn(
                week.getId(), List.of(CompletionStatus.PARTIAL, CompletionStatus.NOT_DONE));

        if (!incompleteItems.isEmpty()) {
            LocalDate nextWeekStart = weekStart.plusWeeks(1);
            WeeklyCommit nextWeek = weeklyCommitService.getOrCreateWeek(userId, orgId, nextWeekStart);

            for (CommitItem item : incompleteItems) {
                CommitItem carried = new CommitItem();
                carried.setWeeklyCommit(nextWeek);
                carried.setTitle(item.getTitle());
                carried.setDescription(item.getDescription());
                carried.setOutcome(item.getOutcome());
                carried.setUrgency(item.getUrgency());
                carried.setImportance(item.getImportance());
                carried.setSortOrder(item.getSortOrder());
                carried.setCarriedFrom(item);
                commitItemRepository.save(carried);
            }
        }

        return week;
    }
}
