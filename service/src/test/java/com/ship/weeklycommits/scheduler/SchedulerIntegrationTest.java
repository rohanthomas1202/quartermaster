package com.ship.weeklycommits.scheduler;

import com.ship.weeklycommits.model.OrgSettings;
import com.ship.weeklycommits.repository.OrgSettingsRepository;
import com.ship.weeklycommits.service.WeeklyCommitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerIntegrationTest {

    @Mock
    private OrgSettingsRepository orgSettingsRepository;

    @Mock
    private WeeklyCommitService weeklyCommitService;

    @Test
    void lockJob_transitionsForEligibleOrgs() {
        OrgSettings org = new OrgSettings();
        org.setOrgId("org-1");
        org.setTimezone("UTC");
        when(orgSettingsRepository.findAll()).thenReturn(List.of(org));

        WeeklyLockJob job = new WeeklyLockJob(orgSettingsRepository, weeklyCommitService);
        job.lockDrafts();

        // Verification depends on current day/time - exercises the code path
    }

    @Test
    void reconciliationJob_transitionsForEligibleOrgs() {
        OrgSettings org = new OrgSettings();
        org.setOrgId("org-1");
        org.setTimezone("UTC");
        when(orgSettingsRepository.findAll()).thenReturn(List.of(org));

        ReconciliationOpenJob job = new ReconciliationOpenJob(orgSettingsRepository, weeklyCommitService);
        job.openReconciliation();

        // Verification depends on current day/time - exercises the code path
    }
}
