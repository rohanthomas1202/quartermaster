package com.ship.weeklycommits.repository;

import com.ship.weeklycommits.model.RallyCry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RallyCryRepository extends JpaRepository<RallyCry, UUID> {
    List<RallyCry> findByOrgIdAndActiveTrue(String orgId);
}
