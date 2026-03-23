package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.DefiningObjectiveDto;
import com.ship.weeklycommits.dto.RallyCryDto;
import com.ship.weeklycommits.model.RallyCry;
import com.ship.weeklycommits.repository.DefiningObjectiveRepository;
import com.ship.weeklycommits.repository.OutcomeRepository;
import com.ship.weeklycommits.repository.RallyCryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RcdoServiceTest {

    @Mock
    private RallyCryRepository rallyCryRepository;

    @Mock
    private DefiningObjectiveRepository definingObjectiveRepository;

    @Mock
    private OutcomeRepository outcomeRepository;

    private RcdoService rcdoService;

    @BeforeEach
    void setUp() {
        rcdoService = new RcdoService(rallyCryRepository, definingObjectiveRepository, outcomeRepository);
    }

    @Test
    void listRallyCries_returnsActiveForOrg() {
        RallyCry rallyCry = new RallyCry();
        rallyCry.setId(UUID.randomUUID());
        rallyCry.setTitle("Q1 Rally Cry");
        rallyCry.setDescription("Focus on growth");
        rallyCry.setOrgId("org-1");
        rallyCry.setActive(true);
        rallyCry.setCreatedAt(OffsetDateTime.now());
        rallyCry.setUpdatedAt(OffsetDateTime.now());

        when(rallyCryRepository.findByOrgIdAndActiveTrue("org-1")).thenReturn(List.of(rallyCry));

        List<RallyCryDto> result = rcdoService.listRallyCries("org-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Q1 Rally Cry");
        assertThat(result.get(0).orgId()).isEqualTo("org-1");
        assertThat(result.get(0).active()).isTrue();
    }

    @Test
    void createRallyCry_setsOrgIdFromAuth() {
        RallyCryDto inputDto = new RallyCryDto("New Rally Cry", "Description");

        RallyCry saved = new RallyCry();
        saved.setId(UUID.randomUUID());
        saved.setTitle("New Rally Cry");
        saved.setDescription("Description");
        saved.setOrgId("org-42");
        saved.setActive(true);
        saved.setCreatedAt(OffsetDateTime.now());
        saved.setUpdatedAt(OffsetDateTime.now());

        when(rallyCryRepository.save(any(RallyCry.class))).thenReturn(saved);

        RallyCryDto result = rcdoService.createRallyCry("org-42", inputDto);

        assertThat(result.orgId()).isEqualTo("org-42");
        assertThat(result.title()).isEqualTo("New Rally Cry");
        assertThat(result.active()).isTrue();
    }

    @Test
    void createObjective_throwsWhenRallyCryNotFound() {
        UUID rallyCryId = UUID.randomUUID();
        when(rallyCryRepository.findById(rallyCryId)).thenReturn(Optional.empty());

        DefiningObjectiveDto dto = new DefiningObjectiveDto(
                null, rallyCryId, "Objective", "Desc", null, null, null);

        assertThatThrownBy(() -> rcdoService.createObjective(rallyCryId, "user-1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rally cry not found");
    }
}
