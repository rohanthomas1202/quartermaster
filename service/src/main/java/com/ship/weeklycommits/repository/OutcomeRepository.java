package com.ship.weeklycommits.repository;

import com.ship.weeklycommits.model.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutcomeRepository extends JpaRepository<Outcome, UUID> {
    List<Outcome> findByDefiningObjectiveId(UUID definingObjectiveId);
}
