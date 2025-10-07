package com.abetappteam.abetapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for service unit tests.
 * Uses Mockito for mocking dependencies.
 *
 * All service unit tests should extend this class.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @BeforeEach
    public void baseSetUp() {
        // Common setup for all service tests
        // Override this method in subclasses if needed
    }
}