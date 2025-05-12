// Fixed IndicatorControllerTest.java
package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.AppUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IndicatorController class
 */
public class IndicatorControllerTest {

    @Mock
    private AppUtils appUtils;

    private IndicatorController indicatorController;

    // Store the original controller instance for restoration
    private IndicatorController originalInstance;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Save original singleton instance
        originalInstance = IndicatorController.getInstance();

        // Use reflection to reset the singleton instance and initialize a new one for testing
        try {
            // Reset the singleton instance
            java.lang.reflect.Field instanceField = IndicatorController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            // Reset the indicator counter if needed
            java.lang.reflect.Field counterField = IndicatorController.class.getDeclaredField("indicatorCounter");
            counterField.setAccessible(true);
            counterField.set(null, 1);

            // Add a custom implementation for the indicators map
            java.lang.reflect.Field indicatorsField = IndicatorController.class.getDeclaredField("indicators");
            indicatorsField.setAccessible(true);
            // Create a map that will actually store our test indicators
            Map<Integer, Indicator> indicatorsMap = new HashMap<>();
            indicatorsField.set(null, indicatorsMap);

            // Get a fresh instance
            indicatorController = IndicatorController.getInstance();

            // Ensure we're modifying the real map in the controller
            assertSame(indicatorsMap, indicatorsField.get(null));
        } catch (Exception e) {
            fail("Failed to reset controller instance: " + e.getMessage());
        }
    }

    @Test
    public void testGetInstance() {
        // Test the singleton pattern
        IndicatorController instance = IndicatorController.getInstance();
        assertNotNull(instance);

        IndicatorController instance2 = IndicatorController.getInstance();
        assertSame(instance, instance2);
    }

    @Test
    public void testCreateIndicator() {
        // Act
        int indicatorId = indicatorController.createIndicator("Test indicator", 80.0);

        // Assert
        assertTrue(indicatorId > 0);

        // Get the indicator to verify
        Indicator indicator = indicatorController.getIndicator(indicatorId);
        assertNotNull(indicator);
        assertEquals("Test indicator", indicator.getDescription());
    }

    @Test
    public void testCreateMultipleIndicators() {
        // Act - Create multiple indicators
        int id1 = indicatorController.createIndicator("First indicator", 70.0);
        int id2 = indicatorController.createIndicator("Second indicator", 75.0);
        int id3 = indicatorController.createIndicator("Third indicator", 85.0);

        // Assert - IDs should be sequential
        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(3, id3);

        // Verify indicators
        Indicator indicator1 = indicatorController.getIndicator(id1);
        assertNotNull(indicator1);
        assertEquals("First indicator", indicator1.getDescription());

        Indicator indicator2 = indicatorController.getIndicator(id2);
        assertNotNull(indicator2);
        assertEquals("Second indicator", indicator2.getDescription());

        Indicator indicator3 = indicatorController.getIndicator(id3);
        assertNotNull(indicator3);
        assertEquals("Third indicator", indicator3.getDescription());
    }

    @Test
    public void testGetIndicator() {
        // Arrange - Create an indicator
        int indicatorId = indicatorController.createIndicator("Test get indicator", 90.0);

        // Act
        Indicator indicator = indicatorController.getIndicator(indicatorId);

        // Assert
        assertNotNull(indicator);
        assertEquals(indicatorId, indicator.getIndicatorId());
        assertEquals("Test get indicator", indicator.getDescription());
    }

    @Test
    public void testGetNonExistentIndicator() {
        // Act
        Indicator indicator = indicatorController.getIndicator(999);

        // Assert
        assertNull(indicator);
    }

    @Test
    public void testUpdateIndicator() {
        // Arrange - Create an indicator
        int indicatorId = indicatorController.createIndicator("Original description", 80.0);
        Indicator indicator = indicatorController.getIndicator(indicatorId);
        assertNotNull(indicator, "Indicator should be created successfully");

        // Modify the indicator
        indicator.setDescription("Updated description");

        // Act
        boolean result = indicatorController.updateIndicator(indicator);

        // Assert
        assertTrue(result, "Update should return true on success");

        // Get the updated indicator
        Indicator updatedIndicator = indicatorController.getIndicator(indicatorId);
        assertNotNull(updatedIndicator, "Updated indicator should exist");
        assertEquals("Updated description", updatedIndicator.getDescription(),
                "Description should be updated");
    }

    @Test
    public void testUpdateNonExistentIndicator() {
        // Arrange
        Indicator nonExistentIndicator = new Indicator();
        nonExistentIndicator.setId(999);
        nonExistentIndicator.setDescription("Non-existent");

        // Act
        boolean result = indicatorController.updateIndicator(nonExistentIndicator);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testUpdateNullIndicator() {
        // Act
        boolean result = indicatorController.updateIndicator(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCacheAfterUpdate() {
        // Arrange - Create an indicator and get it to cache
        int indicatorId = indicatorController.createIndicator("Original", 75.0);
        Indicator firstGet = indicatorController.getIndicator(indicatorId);
        assertNotNull(firstGet, "First indicator retrieval should succeed");

        // Update indicator
        firstGet.setDescription("Updated through cache");
        boolean updateResult = indicatorController.updateIndicator(firstGet);
        assertTrue(updateResult, "Update should succeed");

        // Act - Get it again from cache
        Indicator secondGet = indicatorController.getIndicator(indicatorId);

        // Assert - Cache should be updated
        assertNotNull(secondGet, "Second indicator retrieval should succeed");
        assertEquals("Updated through cache", secondGet.getDescription(),
                "Description should match updated value");
        assertSame(firstGet, secondGet, "Should be the same object instance from cache");
    }

    @Test
    public void testIndicatorCacheKeyAccess() {
        // Arrange - Create indicators with specific IDs
        int id1 = indicatorController.createIndicator("Indicator 1", 70.0);
        int id2 = indicatorController.createIndicator("Indicator 2", 80.0);

        // Get one to load into cache
        Indicator indicator1 = indicatorController.getIndicator(id1);
        assertNotNull(indicator1, "First indicator should be retrieved successfully");

        // Act - Access indicators
        Indicator cachedIndicator = indicatorController.getIndicator(id1);
        Indicator uncachedIndicator = indicatorController.getIndicator(id2);

        // Access non-existent indicator
        Indicator nonExistentIndicator = indicatorController.getIndicator(999);

        // Assert
        assertNotNull(cachedIndicator, "Cached indicator should be retrievable");
        assertEquals("Indicator 1", cachedIndicator.getDescription(),
                "Cached indicator should have correct description");

        assertNotNull(uncachedIndicator, "Uncached indicator should be retrievable");
        assertEquals("Indicator 2", uncachedIndicator.getDescription(),
                "Uncached indicator should have correct description");

        assertNull(nonExistentIndicator, "Non-existent indicator should return null");
    }
}