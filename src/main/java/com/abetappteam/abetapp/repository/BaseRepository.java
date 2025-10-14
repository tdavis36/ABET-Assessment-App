package com.abetappteam.abetapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface that extends JPA functionality with common operations.
 * All entity repositories should extend this interface.
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Find entities by a field value (case-insensitive for strings)
     */
    List<T> findByField(String fieldName, Object value);

    /**
     * Find first entity by field value
     */
    Optional<T> findFirstByField(String fieldName, Object value);

    /**
     * Check if entity exists by field value
     */
    boolean existsByField(String fieldName, Object value);

    /**
     * Soft delete - mark as deleted without removing from database
     * Only if your entities support soft deletion
     */
    void softDelete(ID id);

    /**
     * Find all non-deleted entities (for soft delete support)
     */
    List<T> findAllActive();
}