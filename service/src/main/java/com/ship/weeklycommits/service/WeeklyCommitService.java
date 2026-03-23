package com.ship.weeklycommits.service;

import com.ship.weeklycommits.model.OrgSettings;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.model.WeeklyCommitStatus;
import com.ship.weeklycommits.repository.OrgSettingsRepository;
import com.ship.weeklycommits.repository.WeeklyCommitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class WeeklyCommitService {

    private final WeeklyCommitRepository weeklyCommitRepository;
    private final OrgSettingsRepository orgSettingsRepository;

    public WeeklyCommitService(WeeklyCommitRepository weeklyCommitRepository,
                               OrgSettingsRepository orgSettingsRepository) {
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.orgSettingsRepository = orgSettingsRepository;
    }

    public WeeklyCommit getCurrentWeek(String userId, String orgId) {
        LocalDate today = resolveToday(orgId);
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return getOrCreateWeek(userId, orgId, monday);
    }

    public WeeklyCommit getWeek(String userId, String orgId, LocalDate weekStart) {
        return getOrCreateWeek(userId, orgId, weekStart);
    }

    @Transactional
    public WeeklyCommit getOrCreateWeek(String userId, String orgId, LocalDate monday) {
        return weeklyCommitRepository.findByUserIdAndWeekStartDate(userId, monday)
                .orElseGet(() -> {
                    WeeklyCommit wc = new WeeklyCommit();
                    wc.setUserId(userId);
                    wc.setOrgId(orgId);
                    wc.setWeekStartDate(monday);
                    wc.setWeekEndDate(monday.plusDays(6));
                    wc.setStatus(WeeklyCommitStatus.DRAFT);
                    return weeklyCommitRepository.save(wc);
                });
    }

    @Transactional
    public void transitionToLocked(String orgId) {
        List<WeeklyCommit> drafts = weeklyCommitRepository.findByOrgIdAndStatus(orgId, WeeklyCommitStatus.DRAFT);
        OffsetDateTime now = OffsetDateTime.now();
        for (WeeklyCommit wc : drafts) {
            wc.setStatus(WeeklyCommitStatus.LOCKED);
            wc.setLockedAt(now);
        }
        weeklyCommitRepository.saveAll(drafts);
    }

    @Transactional
    public void transitionToReconciling(String orgId) {
        List<WeeklyCommit> locked = weeklyCommitRepository.findByOrgIdAndStatus(orgId, WeeklyCommitStatus.LOCKED);
        for (WeeklyCommit wc : locked) {
            wc.setStatus(WeeklyCommitStatus.RECONCILING);
        }
        weeklyCommitRepository.saveAll(locked);
    }

    private LocalDate resolveToday(String orgId) {
        return orgSettingsRepository.findById(orgId)
                .map(OrgSettings::getTimezone)
                .map(tz -> LocalDate.now(ZoneId.of(tz)))
                .orElse(LocalDate.now(ZoneId.of("America/New_York")));
    }
}
