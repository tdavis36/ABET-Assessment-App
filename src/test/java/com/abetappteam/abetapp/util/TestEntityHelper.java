package com.abetappteam.abetapp.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility helper for JPA-based tests to manage persistence context safely.
 * Prevents stale version conflicts and ensures clean entity state between operations.
 */
@Component
public class TestEntityHelper {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Flushes all pending changes and clears the persistence context.
     * This ensures entities are detached, forcing reloading of updated versions from DB.
     */
    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Refreshes the given entity directly from the database.
     * @param entity The managed entity to refresh
     */
    @Transactional
    public void refresh(Object entity) {
        entityManager.refresh(entity);
    }
}
