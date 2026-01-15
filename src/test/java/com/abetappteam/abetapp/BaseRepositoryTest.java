package com.abetappteam.abetapp;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for repository tests.
 * Uses @DataJpaTest for lightweight JPA testing with an H2 database.
 * <p>
 * All repository tests should extend this class.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    protected TestEntityManager entityManager;

    @BeforeEach
    public void baseSetUp() {
        // Common setup for all repository tests
        // Override this method in subclasses if needed
    }

    /**
     * Persist and flush entity to database
     */
    protected <T> T persistAndFlush(T entity) {
        entityManager.persistAndFlush(entity);
        return entity;
    }

    /**
     * Clear the persistence context
     */
    protected void clearContext() {
        entityManager.clear();
    }

    /**
     * Flush changes to database
     */
    protected void flush() {
        entityManager.flush();
    }
}