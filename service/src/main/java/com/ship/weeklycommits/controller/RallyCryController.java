package com.ship.weeklycommits.controller;

import com.ship.weeklycommits.config.AuthHeaderFilter;
import com.ship.weeklycommits.dto.DefiningObjectiveDto;
import com.ship.weeklycommits.dto.OutcomeDto;
import com.ship.weeklycommits.dto.RallyCryDto;
import com.ship.weeklycommits.service.RcdoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RallyCryController {

    private final RcdoService rcdoService;

    public RallyCryController(RcdoService rcdoService) {
        this.rcdoService = rcdoService;
    }

    @GetMapping("/rally-cries")
    public List<RallyCryDto> listRallyCries(HttpServletRequest request) {
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        return rcdoService.listRallyCries(orgId);
    }

    @PostMapping("/rally-cries")
    @ResponseStatus(HttpStatus.CREATED)
    public RallyCryDto createRallyCry(HttpServletRequest request, @Valid @RequestBody RallyCryDto dto) {
        String orgId = (String) request.getAttribute(AuthHeaderFilter.ORG_ID_ATTR);
        return rcdoService.createRallyCry(orgId, dto);
    }

    @GetMapping("/rally-cries/{id}/objectives")
    public List<DefiningObjectiveDto> listObjectives(@PathVariable UUID id) {
        return rcdoService.listObjectives(id);
    }

    @PostMapping("/rally-cries/{id}/objectives")
    @ResponseStatus(HttpStatus.CREATED)
    public DefiningObjectiveDto createObjective(@PathVariable UUID id,
                                                 HttpServletRequest request,
                                                 @Valid @RequestBody DefiningObjectiveDto dto) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        return rcdoService.createObjective(id, userId, dto);
    }

    @GetMapping("/objectives/{id}/outcomes")
    public List<OutcomeDto> listOutcomes(@PathVariable UUID id) {
        return rcdoService.listOutcomes(id);
    }

    @PostMapping("/objectives/{id}/outcomes")
    @ResponseStatus(HttpStatus.CREATED)
    public OutcomeDto createOutcome(@PathVariable UUID id,
                                     HttpServletRequest request,
                                     @Valid @RequestBody OutcomeDto dto) {
        String userId = (String) request.getAttribute(AuthHeaderFilter.USER_ID_ATTR);
        return rcdoService.createOutcome(id, userId, dto);
    }
}
