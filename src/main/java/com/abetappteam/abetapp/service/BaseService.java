package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.entity.BaseEntity;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base service class providing common CRUD operations.
 * All service classes should extend this.
 *
 * @param <T> Entity type extending BaseEntity
 * @param <ID> Primary key type
 * @param <R> Repository type
 */
public abstract class BaseService<T extends BaseEntity, ID, R extends JpaRepository<T, ID>> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    /**
     * Get the entity name for error messages
     */
    protected abstract String getEntityName();

    /**
     * Find entity by ID
     */
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " not found with id: " + id));
    }

    /**
     * Find entity by ID, return Optional
     */
    @Transactional(readOnly = true)
    public Optional<T> findByIdOptional(ID id) {
        return repository.findById(id);
    }

    /**
     * Get all entities
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    /**
     * Get paginated entities
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Save entity
     */
    @Transactional
    public T save(T entity) {
        logger.debug("Saving {}: {}", getEntityName(), entity);
        return repository.save(entity);
    }

    /**
     * Delete entity by ID
     */
    @Transactional
    public void delete(ID id) {
        T entity = findById(id);
        logger.debug("Deleting {}: {}", getEntityName(), id);
        repository.delete(entity);
    }

    /**
     * Delete entity
     */
    @Transactional
    public void delete(T entity) {
        logger.debug("Deleting {}: {}", getEntityName(), entity.getId());
        repository.delete(entity);
    }

    /**
     * Soft delete (if entity supports it)
     */
    @Transactional
    public void softDelete(ID id) {
        T entity = findById(id);
        entity.markAsDeleted();
        repository.save(entity);
        logger.debug("Soft deleted {}: {}", getEntityName(), id);
    }

    /**
     * Check if entity exists by ID
     */
    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    /**
     * Count all entities
     */
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    /**
     * Save all entities
     */
    @Transactional
    public List<T> saveAll(List<T> entities) {
        logger.debug("Saving {} {}s", entities.size(), getEntityName());
        return repository.saveAll(entities);
    }

    /**
     * Delete all entities
     */
    @Transactional
    public void deleteAll() {
        logger.warn("Deleting all {}s", getEntityName());
        repository.deleteAll();
    }
}