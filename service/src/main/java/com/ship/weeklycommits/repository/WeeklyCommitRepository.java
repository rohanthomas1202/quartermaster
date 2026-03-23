package com.ship.weeklycommits.repository;

import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeeklyCommitRepository extends JpaRepository<WeeklyCommit, UUID> {
    Optional<WeeklyCommit> findByUserIdAndWeekStartDate(String userId, LocalDate weekStartDate);
    List<WeeklyCommit> findByOrgIdAndWeekStartDate(String orgId, LocalDate weekStartDate);
    List<WeeklyCommit> findByOrgIdAndStatus(String orgId, WeeklyCommitStatus status);
}
