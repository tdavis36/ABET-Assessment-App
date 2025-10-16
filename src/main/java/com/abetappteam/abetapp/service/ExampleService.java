package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.ExampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Example entity
 */
@Service
public class ExampleService extends BaseService<Example, Long, ExampleRepository> {

    @Autowired
    public ExampleService(ExampleRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Example";
    }

    /**
     * Create a new example from DTO
     */
    @Transactional
    public Example create(ExampleDTO dto) {
        // Check for duplicate name
        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Example with name '" + dto.getName() + "' already exists");
        }

        Example example = new Example();
        example.setName(dto.getName());
        example.setDescription(dto.getDescription());
        example.setActive(dto.getActive() != null ? dto.getActive() : true);

        logger.info("Creating new example: {}", dto.getName());
        return repository.save(example);
    }

    /**
     * Update an existing example
     */
    @Transactional
    public Example update(Long id, ExampleDTO dto) {
        Example example = findById(id);

        // Check for duplicate name (excluding current example)
        repository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("Example with name '" + dto.getName() + "' already exists");
            }
        });

        example.setName(dto.getName());
        example.setDescription(dto.getDescription());
        if (dto.getActive() != null) {
            example.setActive(dto.getActive());
        }

        logger.info("Updating example: {}", id);
        return repository.save(example);
    }

    /**
     * Find example by name
     */
    @Transactional(readOnly = true)
    public Example findByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Example not found with name: " + name));
    }

    /**
     * Find all active examples
     */
    @Transactional(readOnly = true)
    public List<Example> findAllActive() {
        return repository.findByActiveTrue();
    }

    /**
     * Find all inactive examples
     */
    @Transactional(readOnly = true)
    public List<Example> findAllInactive() {
        return repository.findByActiveFalse();
    }

    /**
     * Search examples by name
     */
    @Transactional(readOnly = true)
    public List<Example> searchByName(String searchTerm) {
        return repository.findByNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Activate an example
     */
    @Transactional
    public Example activate(Long id) {
        Example example = findById(id);
        example.setActive(true);
        logger.info("Activating example: {}", id);
        return repository.save(example);
    }

    /**
     * Deactivate an example
     */
    @Transactional
    public Example deactivate(Long id) {
        Example example = findById(id);
        example.setActive(false);
        logger.info("Deactivating example: {}", id);
        return repository.save(example);
    }
}