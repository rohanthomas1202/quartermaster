package com.ship.weeklycommits.service;

import com.ship.weeklycommits.dto.DefiningObjectiveDto;
import com.ship.weeklycommits.dto.OutcomeDto;
import com.ship.weeklycommits.dto.RallyCryDto;
import com.ship.weeklycommits.model.DefiningObjective;
import com.ship.weeklycommits.model.Outcome;
import com.ship.weeklycommits.model.RallyCry;
import com.ship.weeklycommits.repository.DefiningObjectiveRepository;
import com.ship.weeklycommits.repository.OutcomeRepository;
import com.ship.weeklycommits.repository.RallyCryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RcdoService {

    private final RallyCryRepository rallyCryRepository;
    private final DefiningObjectiveRepository definingObjectiveRepository;
    private final OutcomeRepository outcomeRepository;

    public RcdoService(RallyCryRepository rallyCryRepository,
                       DefiningObjectiveRepository definingObjectiveRepository,
                       OutcomeRepository outcomeRepository) {
        this.rallyCryRepository = rallyCryRepository;
        this.definingObjectiveRepository = definingObjectiveRepository;
        this.outcomeRepository = outcomeRepository;
    }

    public List<RallyCryDto> listRallyCries(String orgId) {
        return rallyCryRepository.findByOrgIdAndActiveTrue(orgId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public RallyCryDto createRallyCry(String orgId, RallyCryDto dto) {
        RallyCry entity = new RallyCry();
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setOrgId(orgId);
        entity.setActive(true);
        RallyCry saved = rallyCryRepository.save(entity);
        return toDto(saved);
    }

    public List<DefiningObjectiveDto> listObjectives(UUID rallyCryId) {
        return definingObjectiveRepository.findByRallyCryId(rallyCryId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public DefiningObjectiveDto createObjective(UUID rallyCryId, String userId, DefiningObjectiveDto dto) {
        RallyCry rallyCry = rallyCryRepository.findById(rallyCryId)
                .orElseThrow(() -> new IllegalArgumentException("Rally cry not found: " + rallyCryId));

        DefiningObjective entity = new DefiningObjective();
        entity.setRallyCry(rallyCry);
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setOwnerId(userId);
        DefiningObjective saved = definingObjectiveRepository.save(entity);
        return toDto(saved);
    }

    public List<OutcomeDto> listOutcomes(UUID objectiveId) {
        return outcomeRepository.findByDefiningObjectiveId(objectiveId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public OutcomeDto createOutcome(UUID objectiveId, String userId, OutcomeDto dto) {
        DefiningObjective objective = definingObjectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Defining objective not found: " + objectiveId));

        Outcome entity = new Outcome();
        entity.setDefiningObjective(objective);
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setMeasurableTarget(dto.measurableTarget());
        entity.setCurrentValue(dto.currentValue());
        entity.setOwnerId(userId);
        Outcome saved = outcomeRepository.save(entity);
        return toDto(saved);
    }

    private RallyCryDto toDto(RallyCry entity) {
        return new RallyCryDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getOrgId(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private DefiningObjectiveDto toDto(DefiningObjective entity) {
        return new DefiningObjectiveDto(
                entity.getId(),
                entity.getRallyCry().getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private OutcomeDto toDto(Outcome entity) {
        return new OutcomeDto(
                entity.getId(),
                entity.getDefiningObjective().getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getMeasurableTarget(),
                entity.getCurrentValue(),
                entity.getOwnerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
