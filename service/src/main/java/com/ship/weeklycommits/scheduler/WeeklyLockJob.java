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
public class WeeklyLockJob {

    private static final Logger log = LoggerFactory.getLogger(WeeklyLockJob.class);

    private final OrgSettingsRepository orgSettingsRepository;
    private final WeeklyCommitService weeklyCommitService;

    public WeeklyLockJob(OrgSettingsRepository orgSettingsRepository,
                         WeeklyCommitService weeklyCommitService) {
        this.orgSettingsRepository = orgSettingsRepository;
        this.weeklyCommitService = weeklyCommitService;
    }

    @Scheduled(cron = "0 0 * * * MON")
    public void lockDrafts() {
        log.info("Starting weekly lock job");
        for (OrgSettings org : orgSettingsRepository.findAll()) {
            try {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(org.getTimezone()));
                if (now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() >= 8) {
                    weeklyCommitService.transitionToLocked(org.getOrgId());
                    log.info("Locked drafts for org {}", org.getOrgId());
                }
            } catch (Exception e) {
                log.error("Failed to lock drafts for org {}: {}", org.getOrgId(), e.getMessage(), e);
            }
        }
        log.info("Completed weekly lock job");
    }
}
