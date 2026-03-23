package com.ship.weeklycommits.repository;

import com.ship.weeklycommits.model.OrgSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgSettingsRepository extends JpaRepository<OrgSettings, String> {
}
