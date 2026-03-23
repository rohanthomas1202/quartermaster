package com.ship.weeklycommits.controller;

import com.ship.weeklycommits.config.AuthHeaderFilter;
import com.ship.weeklycommits.dto.RcdoAlignmentDto;
import com.ship.weeklycommits.dto.TeamSummaryDto;
import com.ship.weeklycommits.service.ManagerDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/manager")
public class ManagerController {

    private final ManagerDashboardService managerDashboardService;

    public ManagerController(ManagerDashboardService managerDashboardService) {
        this.managerDashboardService = managerDashboardService;
    }

    @GetMapping("/team-summary")
    public TeamSummaryDto getTeamSummary(@RequestParam LocalDate weekStart,
                                          HttpServletRequest request) {
        if (!isManager(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Manager or admin role required");
        }
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        return managerDashboardService.getTeamSummary(orgId, weekStart);
    }

    @GetMapping("/rcdo-alignment")
    public RcdoAlignmentDto getRcdoAlignment(@RequestParam LocalDate weekStart,
                                               HttpServletRequest request) {
        if (!isManager(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Manager or admin role required");
        }
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        return managerDashboardService.getRcdoAlignment(orgId, weekStart);
    }

    private boolean isManager(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthHeaderFilter.USER_ROLE_ATTR);
        return "manager".equals(role) || "admin".equals(role);
    }
}
