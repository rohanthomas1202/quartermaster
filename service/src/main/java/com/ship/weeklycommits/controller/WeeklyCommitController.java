package com.ship.weeklycommits.controller;

import com.ship.weeklycommits.config.AuthHeaderFilter;
import com.ship.weeklycommits.dto.CommitItemDto;
import com.ship.weeklycommits.dto.ReconcileRequest;
import com.ship.weeklycommits.model.WeeklyCommit;
import com.ship.weeklycommits.service.CommitItemService;
import com.ship.weeklycommits.service.ReconciliationService;
import com.ship.weeklycommits.service.WeeklyCommitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/weekly-commits")
public class WeeklyCommitController {

    private final WeeklyCommitService weeklyCommitService;
    private final CommitItemService commitItemService;
    private final ReconciliationService reconciliationService;

    public WeeklyCommitController(WeeklyCommitService weeklyCommitService,
                                   CommitItemService commitItemService,
                                   ReconciliationService reconciliationService) {
        this.weeklyCommitService = weeklyCommitService;
        this.commitItemService = commitItemService;
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/current")
    public WeeklyCommitResponse getCurrent(HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        WeeklyCommit wc = weeklyCommitService.getCurrentWeek(userId, orgId);
        List<CommitItemDto> items = commitItemService.listItems(userId, wc.getWeekStartDate());
        return new WeeklyCommitResponse(wc, items);
    }

    @GetMapping("/{weekStart}")
    public WeeklyCommitResponse getWeek(@PathVariable LocalDate weekStart, HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        WeeklyCommit wc = weeklyCommitService.getWeek(userId, orgId, weekStart);
        List<CommitItemDto> items = commitItemService.listItems(userId, weekStart);
        return new WeeklyCommitResponse(wc, items);
    }

    @PostMapping("/{weekStart}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CommitItemDto addItem(@PathVariable LocalDate weekStart,
                                  HttpServletRequest request,
                                  @Valid @RequestBody CommitItemDto dto) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        return commitItemService.addItem(userId, orgId, weekStart, dto);
    }

    @PutMapping("/{weekStart}/items/{itemId}")
    public CommitItemDto updateItem(@PathVariable LocalDate weekStart,
                                     @PathVariable UUID itemId,
                                     HttpServletRequest request,
                                     @Valid @RequestBody CommitItemDto dto) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        return commitItemService.updateItem(userId, weekStart, itemId, dto);
    }

    @DeleteMapping("/{weekStart}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable LocalDate weekStart,
                            @PathVariable UUID itemId,
                            HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        commitItemService.deleteItem(userId, weekStart, itemId);
    }

    @PutMapping("/{weekStart}/items/{itemId}/reconcile")
    public void reconcileItem(@PathVariable LocalDate weekStart,
                               @PathVariable UUID itemId,
                               HttpServletRequest request,
                               @Valid @RequestBody ReconcileRequest reconcileRequest) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        reconciliationService.reconcileItem(userId, weekStart, itemId, reconcileRequest);
    }

    @PostMapping("/{weekStart}/submit-reconciliation")
    public void submitReconciliation(@PathVariable LocalDate weekStart, HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        reconciliationService.submitReconciliation(userId, orgId, weekStart);
    }

    public record WeeklyCommitResponse(
            UUID id,
            String userId,
            String orgId,
            LocalDate weekStartDate,
            LocalDate weekEndDate,
            String status,
            int version,
            List<CommitItemDto> items
    ) {
        public WeeklyCommitResponse(WeeklyCommit wc, List<CommitItemDto> items) {
            this(
                    wc.getId(),
                    wc.getUserId(),
                    wc.getOrgId(),
                    wc.getWeekStartDate(),
                    wc.getWeekEndDate(),
                    wc.getStatus().name(),
                    wc.getVersion(),
                    items
            );
        }
    }
}
