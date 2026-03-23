package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.CommitItemDto;
import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.Outcome;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.CommitItemRepository;
import com.ship.weeklycommits.repository.OutcomeRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CommitItemService {

    private final CommitItemRepository commitItemRepository;
    private final WeeklyCommitRepository weeklyCommitRepository;
    private final OutcomeRepository outcomeRepository;

    public CommitItemService(CommitItemRepository commitItemRepository,
                             WeeklyCommitRepository weeklyCommitRepository,
                             OutcomeRepository outcomeRepository) {
        this.commitItemRepository = commitItemRepository;
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.outcomeRepository = outcomeRepository;
    }

    public List<CommitItemDto> listItems(String userId, LocalDate weekStart) {
        WeeklyCommit wc = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Weekly commit not found"));
        return commitItemRepository.findByWeeklyCommitIdOrderBySortOrder(wc.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CommitItemDto addItem(String userId, String orgId, LocalDate weekStart, CommitItemDto dto) {
        WeeklyCommit wc = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Weekly commit not found"));
        requireStatus(wc, WeeklyCommitStatus.DRAFT, "add items");

        Outcome outcome = outcomeRepository.findById(dto.outcomeId())
                .orElseThrow(() -> new IllegalArgumentException("Outcome not found: " + dto.outcomeId()));

        CommitItem item = new CommitItem();
        item.setWeeklyCommit(wc);
        item.setTitle(dto.title());
        item.setDescription(dto.description());
        item.setOutcome(outcome);
        item.setUrgency(dto.urgency());
        item.setImportance(dto.importance());
        item.setSortOrder(dto.sortOrder());
        item.setCompletionStatus(dto.completionStatus() != null ? dto.completionStatus() : com.ship.weeklycommits.model.CompletionStatus.PENDING);
        item.setCompletionNotes(dto.completionNotes());

        if (dto.carriedFromId() != null) {
            CommitItem carriedFrom = commitItemRepository.findById(dto.carriedFromId())
                    .orElseThrow(() -> new IllegalArgumentException("Carried-from item not found: " + dto.carriedFromId()));
            item.setCarriedFrom(carriedFrom);
        }

        CommitItem saved = commitItemRepository.save(item);
        return toDto(saved);
    }

    @Transactional
    public CommitItemDto updateItem(String userId, LocalDate weekStart, UUID itemId, CommitItemDto dto) {
        WeeklyCommit wc = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Weekly commit not found"));
        requireStatus(wc, WeeklyCommitStatus.DRAFT, "update items");

        CommitItem item = commitItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Commit item not found: " + itemId));

        if (!item.getWeeklyCommit().getId().equals(wc.getId())) {
            throw new IllegalArgumentException("Item does not belong to this weekly commit");
        }

        Outcome outcome = outcomeRepository.findById(dto.outcomeId())
                .orElseThrow(() -> new IllegalArgumentException("Outcome not found: " + dto.outcomeId()));

        item.setTitle(dto.title());
        item.setDescription(dto.description());
        item.setOutcome(outcome);
        item.setUrgency(dto.urgency());
        item.setImportance(dto.importance());
        item.setSortOrder(dto.sortOrder());
        item.setCompletionStatus(dto.completionStatus() != null ? dto.completionStatus() : item.getCompletionStatus());
        item.setCompletionNotes(dto.completionNotes());

        if (dto.carriedFromId() != null) {
            CommitItem carriedFrom = commitItemRepository.findById(dto.carriedFromId())
                    .orElseThrow(() -> new IllegalArgumentException("Carried-from item not found: " + dto.carriedFromId()));
            item.setCarriedFrom(carriedFrom);
        } else {
            item.setCarriedFrom(null);
        }

        CommitItem saved = commitItemRepository.save(item);
        return toDto(saved);
    }

    @Transactional
    public void deleteItem(String userId, LocalDate weekStart, UUID itemId) {
        WeeklyCommit wc = weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("Weekly commit not found"));
        requireStatus(wc, WeeklyCommitStatus.DRAFT, "delete items");

        CommitItem item = commitItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Commit item not found: " + itemId));

        if (!item.getWeeklyCommit().getId().equals(wc.getId())) {
            throw new IllegalArgumentException("Item does not belong to this weekly commit");
        }

        commitItemRepository.delete(item);
    }

    private void requireStatus(WeeklyCommit wc, WeeklyCommitStatus required, String action) {
        if (wc.getStatus() != required) {
            throw new IllegalStateException(
                    "Cannot " + action + ": week is " + wc.getStatus() + ", must be " + required);
        }
    }

    private CommitItemDto toDto(CommitItem item) {
        return new CommitItemDto(
                item.getId(),
                item.getWeeklyCommit().getId(),
                item.getTitle(),
                item.getDescription(),
                item.getOutcome().getId(),
                item.getUrgency(),
                item.getImportance(),
                item.getSortOrder(),
                item.getCompletionStatus(),
                item.getCompletionNotes(),
                item.getCarriedFrom() != null ? item.getCarriedFrom().getId() : null,
                item.getVersion(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
