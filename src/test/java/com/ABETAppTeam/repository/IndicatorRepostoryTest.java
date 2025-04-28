package com.ABETAppTeam.repository;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.TestDatabaseSetup;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IndicatorRepository
 */
public class IndicatorRepositoryTest {
    private IndicatorRepository indicatorRepository;
    private static TestDatabaseSetup dbSetup;

    @BeforeAll
    public static void setupDatabase() throws SQLException {
        // Initialize test database
        dbSetup = new TestDatabaseSetup();
        dbSetup.setupTestDatabase();
    }

    @AfterAll
    public static void teardownDatabase() throws SQLException {
        // Clean up test database
        if (dbSetup != null) {
            dbSetup.teardownTestDatabase();
        }
    }

    @BeforeEach
    public void setup() throws SQLException {
        // Reset test data before each test
        MockitoAnnotations.openMocks(this);
        indicatorRepository = new IndicatorRepository();
        resetTestData();
    }

    private void resetTestData() throws SQLException {
        // Clear existing data
        try (Connection conn = dbSetup.getConnection()) {
            // Delete existing records
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Indicator")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Outcome")) {
                stmt.executeUpdate();
            }

            // Insert test outcomes
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Outcome (outcome_id, outcome_num, outcome_desc) VALUES (?, ?, ?)")) {
                // Test outcome 1
                stmt.setInt(1, 1);
                stmt.setString(2, "a");
                stmt.setString(3, "An ability to apply knowledge");
                stmt.addBatch();

                // Test outcome 2
                stmt.setInt(1, 2);
                stmt.setString(2, "b");
                stmt.setString(3, "An ability to analyze");
                stmt.addBatch();

                stmt.executeBatch();
            }

            // Insert test indicators
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Indicator (indicator_id, outcome_id, indicator_num, indicator_desc) VALUES (?, ?, ?, ?)")) {
                // Indicators for outcome 1
                stmt.setInt(1, 1);
                stmt.setInt(2, 1);
                stmt.setInt(3, 1);
                stmt.setString(4, "Apply mathematical principles");
                stmt.addBatch();

                stmt.setInt(1, 2);
                stmt.setInt(2, 1);
                stmt.setInt(3, 2);
                stmt.setString(4, "Use computing techniques");
                stmt.addBatch();

                // Indicators for outcome 2
                stmt.setInt(1, 3);
                stmt.setInt(2, 2);
                stmt.setInt(3, 1);
                stmt.setString(4, "Identify problem requirements");
                stmt.addBatch();

                stmt.executeBatch();
            }
        }
    }

    @Test
    public void testFindById() {
        // Test finding an existing indicator
        Indicator indicator = indicatorRepository.findById(1);

        assertNotNull(indicator);
        assertEquals(1, indicator.getIndicatorId());
        assertEquals(1, indicator.getOutcomeId());
        assertEquals(1, indicator.getNumber());
        assertEquals("Apply mathematical principles", indicator.getDescription());

        // Test finding a non-existent indicator
        Indicator nonExistentIndicator = indicatorRepository.findById(999);
        assertNull(nonExistentIndicator);
    }

    @Test
    public void testFindAll() {
        List<Indicator> indicators = indicatorRepository.findAll();

        assertNotNull(indicators);
        assertEquals(3, indicators.size());

        // Verify order and content
        assertEquals(1, indicators.get(0).getIndicatorId());
        assertEquals(2, indicators.get(1).getIndicatorId());
        assertEquals(3, indicators.get(2).getIndicatorId());
    }

    @Test
    public void testFindByOutcomeId() {
        // Test outcome with multiple indicators
        List<Indicator> outcome1Indicators = indicatorRepository.findByOutcomeId(1);
        assertNotNull(outcome1Indicators);
        assertEquals(2, outcome1Indicators.size());

        // Test outcome with one indicator
        List<Indicator> outcome2Indicators = indicatorRepository.findByOutcomeId(2);
        assertNotNull(outcome2Indicators);
        assertEquals(1, outcome2Indicators.size());
        assertEquals(3, outcome2Indicators.get(0).getIndicatorId());

        // Test non-existent outcome
        List<Indicator> nonExistentIndicators = indicatorRepository.findByOutcomeId(999);
        assertNotNull(nonExistentIndicators);
        assertTrue(nonExistentIndicators.isEmpty());
    }

    @Test
    public void testSave() {
        // Create a new indicator
        Indicator indicator = new Indicator();
        indicator.setOutcomeId(2);
        indicator.setNumber(2);
        indicator.setDescription("New test indicator");

        // Save it
        Indicator savedIndicator = indicatorRepository.save(indicator);

        // Verify it was saved with an ID
        assertNotNull(savedIndicator);
        assertTrue(savedIndicator.getIndicatorId() > 0);

        // Verify we can retrieve it
        Indicator retrievedIndicator = indicatorRepository.findById(savedIndicator.getIndicatorId());
        assertNotNull(retrievedIndicator);
        assertEquals(2, retrievedIndicator.getOutcomeId());
        assertEquals(2, retrievedIndicator.getNumber());
        assertEquals("New test indicator", retrievedIndicator.getDescription());
    }

    @Test
    public void testUpdate() {
        // Get an existing indicator
        Indicator indicator = indicatorRepository.findById(1);
        assertNotNull(indicator);

        // Modify it
        String newDescription = "Updated indicator description";
        indicator.setDescription(newDescription);

        // Update it
        boolean updated = indicatorRepository.update(indicator);
        assertTrue(updated);

        // Verify the update
        Indicator updatedIndicator = indicatorRepository.findById(1);
        assertNotNull(updatedIndicator);
        assertEquals(newDescription, updatedIndicator.getDescription());
    }

    @Test
    public void testDelete() {
        // Delete an existing indicator
        boolean deleted = indicatorRepository.delete(1);
        assertTrue(deleted);

        // Verify it's gone
        Indicator deletedIndicator = indicatorRepository.findById(1);
        assertNull(deletedIndicator);

        // Try to delete non-existent indicator
        boolean nonExistentDeleted = indicatorRepository.delete(999);
        assertFalse(nonExistentDeleted);
    }

    @Test
    public void testNonExistentUpdate() {
        // Try to update a non-existent indicator
        Indicator nonExistentIndicator = new Indicator();
        nonExistentIndicator.setIndicatorId(999);
        nonExistentIndicator.setOutcomeId(1);
        nonExistentIndicator.setNumber(1);
        nonExistentIndicator.setDescription("This shouldn't work");

        boolean updated = indicatorRepository.update(nonExistentIndicator);
        assertFalse(updated);
    }

    @Test
    public void testInvalidSave() {
        // Try to save with an invalid outcome ID
        Indicator invalidIndicator = new Indicator();
        invalidIndicator.setOutcomeId(999); // Non-existent outcome
        invalidIndicator.setNumber(1);
        invalidIndicator.setDescription("This shouldn't work due to foreign key constraint");

        Indicator savedIndicator = indicatorRepository.save(invalidIndicator);
        assertNull(savedIndicator);
    }
}