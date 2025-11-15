package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.dto.PerformanceIndicatorDTO;
import com.abetappteam.abetapp.entity.PerformanceIndicator;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.PerformanceIndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for PerformanceIndicator entity
 */
@Service
public class PerformanceIndicatorService
        extends BaseService<PerformanceIndicator, Long, PerformanceIndicatorRepository> {

    @Autowired
    public PerformanceIndicatorService(PerformanceIndicatorRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "PerformanceIndicator";
    }

    @Transactional
    public PerformanceIndicator createPerformanceIndicator(PerformanceIndicatorDTO dto) {
        // Check for duplicate indicator number for the same student outcome
        if (repository.existsByIndicatorNumberAndStudentOutcomeId(dto.getIndicatorNumber(),
                dto.getStudentOutcomeId())) {
            throw new ConflictException("Performance indicator with number '" + dto.getIndicatorNumber()
                    + "' already exists for this student outcome");
        }

        PerformanceIndicator indicator = new PerformanceIndicator();
        indicator.setDescription(dto.getDescription());
        indicator.setIndicatorNumber(dto.getIndicatorNumber());
        indicator.setStudentOutcomeId(dto.getStudentOutcomeId());
        indicator.setIsActive(true);

        logger.info("Creating new performance indicator: {} for student outcome {}", dto.getIndicatorNumber(),
                dto.getStudentOutcomeId());
        return repository.save(indicator);
    }

    @Transactional
    public PerformanceIndicator updatePerformanceIndicator(Long indicatorId, PerformanceIndicatorDTO dto) {
        PerformanceIndicator indicator = findById(indicatorId);

        // Check for duplicate indicator number if it's being changed
        if (!dto.getIndicatorNumber().equals(indicator.getIndicatorNumber())) {
            if (repository.existsByIndicatorNumberAndStudentOutcomeId(dto.getIndicatorNumber(),
                    indicator.getStudentOutcomeId())) {
                throw new ConflictException("Performance indicator with number '" + dto.getIndicatorNumber()
                        + "' already exists for this student outcome");
            }
            indicator.setIndicatorNumber(dto.getIndicatorNumber());
        }

        indicator.setDescription(dto.getDescription());
        logger.info("Updating performance indicator: {}", indicatorId);
        return repository.save(indicator);
    }

    @Transactional
    public void removePerformanceIndicator(Long indicatorId) {
        PerformanceIndicator indicator = findById(indicatorId);
        logger.info("Removing performance indicator: {} - {}", indicator.getIndicatorNumber(),
                indicator.getDescription());
        repository.delete(indicator);
    }

    @Transactional(readOnly = true)
    public List<PerformanceIndicator> getIndicatorsByStudentOutcome(Long studentOutcomeId) {
        return repository.findByStudentOutcomeId(studentOutcomeId);
    }

    @Transactional(readOnly = true)
    public Page<PerformanceIndicator> getIndicatorsByStudentOutcome(Long studentOutcomeId, Pageable pageable) {
        return repository.findByStudentOutcomeId(studentOutcomeId, pageable);
    }

    @Transactional(readOnly = true)
    public List<PerformanceIndicator> getActiveIndicatorsByStudentOutcome(Long studentOutcomeId) {
        return repository.findByStudentOutcomeIdAndIsActive(studentOutcomeId, true);
    }

    @Transactional(readOnly = true)
    public boolean existsByIndicatorNumberAndStudentOutcome(Integer indicatorNumber, Long studentOutcomeId) {
        return repository.existsByIndicatorNumberAndStudentOutcomeId(indicatorNumber, studentOutcomeId);
    }
}