package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Example entity
 */
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {

    /**
     * Find example by name (case-insensitive)
     */
    Optional<Example> findByNameIgnoreCase(String name);

    /**
     * Find all active examples
     */
    List<Example> findByActiveTrue();

    /**
     * Find all inactive examples
     */
    List<Example> findByActiveFalse();

    /**
     * Check if example exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find examples by name containing (case-insensitive search)
     */
    List<Example> findByNameContainingIgnoreCase(String nameFragment);

    /**
     * Custom query example: Find active examples with name containing search term
     */
    @Query("SELECT e FROM Example e WHERE e.active = true AND LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Example> findActiveExamplesByNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Find all non-deleted examples (for soft delete support)
     */
    @Query("SELECT e FROM Example e WHERE e.deleted = false")
    List<Example> findAllActive();
}
