package com.ship.weeklycommits.repository;

import com.ship.weeklycommits.model.CommitItem;
import com.ship.weeklycommits.model.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommitItemRepository extends JpaRepository<CommitItem, UUID> {
    List<CommitItem> findByWeeklyCommitIdOrderBySortOrder(UUID weeklyCommitId);
    List<CommitItem> findByWeeklyCommitIdAndCompletionStatusIn(UUID weeklyCommitId, List<CompletionStatus> statuses);
}
