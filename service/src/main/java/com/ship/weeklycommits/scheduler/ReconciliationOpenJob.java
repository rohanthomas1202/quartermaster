package com.ship.weeklycommits.scheduler;

import com.ship.weeklycommits.model.OrgSettings;
import com.ship.weeklycommits.repository.OrgSettingsRepository;
import com.ship.weeklycommits.service.WeeklyCommitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ReconciliationOpenJob {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationOpenJob.class);

    private final OrgSettingsRepository orgSettingsRepository;
    private final WeeklyCommitService weeklyCommitService;

    public ReconciliationOpenJob(OrgSettingsRepository orgSettingsRepository,
                                  WeeklyCommitService weeklyCommitService) {
        this.orgSettingsRepository = orgSettingsRepository;
        this.weeklyCommitService = weeklyCommitService;
    }

    @Scheduled(cron = "0 0 * * * FRI")
    public void openReconciliation() {
        log.info("Starting reconciliation open job");
        for (OrgSettings org : orgSettingsRepository.findAll()) {
            try {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(org.getTimezone()));
                if (now.getDayOfWeek() == DayOfWeek.FRIDAY && now.getHour() >= 17) {
                    weeklyCommitService.transitionToReconciling(org.getOrgId());
                    log.info("Opened reconciliation for org {}", org.getOrgId());
                }
            } catch (Exception e) {
                log.error("Failed to open reconciliation for org {}: {}", org.getOrgId(), e.getMessage(), e);
            }
        }
        log.info("Completed reconciliation open job");
    }
}
