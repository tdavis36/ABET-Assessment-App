package com.abetappteam.abetapp;

import com.abetappteam.abetapp.config.TestConfig;
import com.abetappteam.abetapp.security.JwtUtil;
import com.abetappteam.abetapp.util.TestEntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Base class for repository tests.
 * Uses @DataJpaTest for lightweight JPA testing with an H2 database.
 * <p>
 * All repository tests should extend this class.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class BaseRepositoryTest {

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected TestEntityHelper entityHelper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    public void baseSetUp() {
        // Common setup for all repository tests
        // Override this method in subclasses if needed
    }

    /**
     * Utility method for convenience to flush and clear persistence context.
     */
    protected void flushAndClear() {
        entityHelper.flushAndClear();
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