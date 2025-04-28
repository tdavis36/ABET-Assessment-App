package com.ABETAppTeam.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.util.TestDatabaseSetup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutcomeRepository Tests")
class OutcomeRepositoryTest {

    @Mock
    private com.zaxxer.hikari.HikariDataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private OutcomeRepository outcomeRepository;

    private static TestDatabaseSetup dbSetup;

    @BeforeAll
    static void setupTestDatabase() throws SQLException {
        dbSetup = new TestDatabaseSetup();
        dbSetup.setupTestDatabase();

        // Set up test data in the database
        try (Connection conn = dbSetup.getConnection()) {
            // Clean existing data
            conn.createStatement().execute("DELETE FROM Indicator");
            conn.createStatement().execute("DELETE FROM Course_Outcome");
            conn.createStatement().execute("DELETE FROM Outcome");

            // Insert test outcomes
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Outcome (outcome_id, outcome_num, outcome_desc) VALUES (?, ?, ?)")) {
                // Test outcome 1
                stmt.setInt(1, 1);
                stmt.setString(2, "a");
                stmt.setString(3, "An ability to apply knowledge of computing");
                stmt.addBatch();

                // Test outcome 2
                stmt.setInt(1, 2);
                stmt.setString(2, "b");
                stmt.setString(3, "An ability to analyze a problem");
                stmt.addBatch();

                stmt.executeBatch();
            }

            // Insert test indicators
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Indicator (indicator_id, outcome_id, indicator_num, indicator_desc) " +
                            "VALUES (?, ?, ?, ?)")) {
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

            // Insert test course-outcome mappings
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Course_Outcome (course_code, outcome_id) VALUES (?, ?)")) {
                stmt.setString(1, "CS101");
                stmt.setInt(2, 1);
                stmt.addBatch();

                stmt.setString(1, "CS101");
                stmt.setInt(2, 2);
                stmt.addBatch();

                stmt.setString(1, "CS201");
                stmt.setInt(2, 1);
                stmt.addBatch();

                stmt.executeBatch();
            }
        }
    }

    @AfterAll
    static void teardownTestDatabase() throws SQLException {
        if (dbSetup != null) {
            dbSetup.teardownTestDatabase();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        // Set up mock behavior
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Nested
    @DisplayName("CSV Loading Tests")
    class CsvLoadingTests {

        @Test
        @DisplayName("Should load outcomes from CSV file")
        void loadFromCSV() {
            // Use the actual implementation for this test
            OutcomeRepository realRepo = new OutcomeRepository();

            // Act
            List<Outcome> outcomes = realRepo.loadFromCSV();

            // Assert
            assertNotNull(outcomes, "Outcomes list should not be null");
            assertFalse(outcomes.isEmpty(), "Outcomes list should not be empty");

            // Check structure of outcomes and indicators
            for (Outcome outcome : outcomes) {
                assertNotNull(outcome.getOutcomeNum(), "Outcome number should not be null");
                assertNotNull(outcome.getDescription(), "Outcome description should not be null");

                List<Indicator> indicators = outcome.getIndicators();
                assertNotNull(indicators, "Indicators list should not be null");
                assertFalse(indicators.isEmpty(), "Indicators list should not be empty");

                for (Indicator indicator : indicators) {
                    assertNotNull(indicator.getId(), "Indicator ID should not be null");
                    assertNotNull(indicator.getDescription(), "Indicator description should not be null");
                }
            }
        }

        @Test
        @DisplayName("Should correctly parse CSV lines with quotes and commas")
        void parseCSVLine() throws Exception {
            OutcomeRepository realRepo = new OutcomeRepository();

            // Call the parseCSVLine method through reflection
            java.lang.reflect.Method parseMethod = OutcomeRepository.class.getDeclaredMethod("parseCSVLine", String.class);
            parseMethod.setAccessible(true);

            // Test with complex CSV line
            String testLine = "\"An ability to apply knowledge\",\"Indicator 1\",\"Indicator 2,with comma\"";
            String[] result = (String[]) parseMethod.invoke(realRepo, testLine);

            // Assert
            assertEquals(3, result.length, "Should parse 3 fields");
            assertEquals("An ability to apply knowledge", result[0], "First field should be correctly parsed");
            assertEquals("Indicator 1", result[1], "Second field should be correctly parsed");
            assertEquals("Indicator 2,with comma", result[2], "Third field with comma should be correctly parsed");
        }

        @Test
        @DisplayName("Should handle empty CSV file gracefully")
        void handleEmptyCsvFile() throws Exception {
            // Create a mock OutcomeRepository that reads from an empty file
            OutcomeRepository spyRepo = spy(new OutcomeRepository());

            // Mock the getClass().getClassLoader().getResourceAsStream method
            InputStream emptyStream = new ByteArrayInputStream("Header1,Header2\n".getBytes());
            doReturn(emptyStream).when(spyRepo).getResourceAsStream(anyString());

            // Act
            List<Outcome> outcomes = spyRepo.loadFromCSV();

            // Assert
            assertNotNull(outcomes, "Outcomes list should not be null even with empty file");
            assertTrue(outcomes.isEmpty(), "Outcomes list should be empty with empty file");
        }

        @Test
        @DisplayName("Should handle malformed CSV file")
        void handleMalformedCsvFile() throws Exception {
            // Create a mock OutcomeRepository that reads from a malformed file
            OutcomeRepository spyRepo = spy(new OutcomeRepository());

            // Mock the getClass().getClassLoader().getResourceAsStream method
            String malformedContent = "Header1,Header2\nMalformed row without, enough, closing quotes\"";
            InputStream malformedStream = new ByteArrayInputStream(malformedContent.getBytes());
            doReturn(malformedStream).when(spyRepo).getResourceAsStream(anyString());

            // Act
            List<Outcome> outcomes = spyRepo.loadFromCSV();

            // Assert
            assertNotNull(outcomes, "Outcomes list should not be null even with malformed file");
            // The outcome might be parsed incorrectly but should not throw exceptions
        }
    }

    @Nested
    @DisplayName("Database Operations Tests")
    class DatabaseOperationsTests {

        @Test
        @DisplayName("Should find outcome by ID")
        void findById() throws SQLException {
            // Arrange
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("outcome_id")).thenReturn(1);
            when(mockResultSet.getString("outcome_num")).thenReturn("a");
            when(mockResultSet.getString("outcome_desc")).thenReturn("Test Outcome");

            // Act
            Outcome outcome = outcomeRepository.findById(1);

            // Assert
            assertNotNull(outcome, "Outcome should not be null");
            assertEquals(1, outcome.getId(), "Outcome ID should match");
            assertEquals("a", outcome.getOutcomeNum(), "Outcome number should match");
            assertEquals("Test Outcome", outcome.getDescription(), "Outcome description should match");

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should find all outcomes")
        void findAll() throws SQLException {
            // Arrange
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getInt("outcome_id")).thenReturn(1, 2);
            when(mockResultSet.getString("outcome_num")).thenReturn("a", "b");
            when(mockResultSet.getString("outcome_desc")).thenReturn("Outcome 1", "Outcome 2");

            // Mock behavior for indicators query
            PreparedStatement mockIndicatorStmt = mock(PreparedStatement.class);
            ResultSet mockIndicatorRs = mock(ResultSet.class);
            when(mockConnection.prepareStatement(contains("FROM Indicator"))).thenReturn(mockIndicatorStmt);
            when(mockIndicatorStmt.executeQuery()).thenReturn(mockIndicatorRs);
            // No indicators returned for simplicity
            when(mockIndicatorRs.next()).thenReturn(false);

            // Act
            List<Outcome> outcomes = outcomeRepository.findAll();

            // Assert
            assertNotNull(outcomes, "Outcomes list should not be null");
            assertEquals(2, outcomes.size(), "Should find 2 outcomes");
            assertEquals(1, outcomes.get(0).getId(), "First outcome ID should match");
            assertEquals(2, outcomes.get(1).getId(), "Second outcome ID should match");

            // Verify interactions
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should find outcomes by course ID")
        void findByCourseId() throws SQLException {
            // Arrange
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getInt("outcome_id")).thenReturn(1, 2);

            // Act
            List<Integer> outcomes = outcomeRepository.findByCourseId("CS101");

            // Assert
            assertNotNull(outcomes, "Outcomes list should not be null");
            assertEquals(2, outcomes.size(), "Should find 2 outcomes");
            assertEquals(1, outcomes.get(0), "First outcome ID should match");
            assertEquals(2, outcomes.get(1), "Second outcome ID should match");

            // Verify interactions
            verify(mockPreparedStatement).setString(1, "CS101");
            verify(mockPreparedStatement).executeQuery();
        }

        @Test
        @DisplayName("Should handle SQL exception when finding by ID")
        void handleSqlExceptionFindById() throws SQLException {
            // Arrange
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Test exception"));

            // Act
            Outcome outcome = outcomeRepository.findById(1);

            // Assert
            assertNull(outcome, "Outcome should be null when exception occurs");
        }

        @Test
        @DisplayName("Should save a new outcome")
        void saveOutcome() throws SQLException {
            // Arrange
            Outcome outcomeToSave = new Outcome();
            outcomeToSave.setOutcomeNum("c");
            outcomeToSave.setDescription("New outcome");

            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(3); // Generated ID

            // Act
            Outcome savedOutcome = outcomeRepository.save(outcomeToSave);

            // Assert
            assertNotNull(savedOutcome, "Saved outcome should not be null");
            assertEquals(3, savedOutcome.getId(), "Saved outcome should have generated ID");
            assertEquals("c", savedOutcome.getOutcomeNum(), "Outcome number should be preserved");
            assertEquals("New outcome", savedOutcome.getDescription(), "Outcome description should be preserved");

            // Verify interactions
            verify(mockPreparedStatement).setString(1, "c");
            verify(mockPreparedStatement).setString(2, "New outcome");
            verify(mockPreparedStatement).executeUpdate();
            verify(mockPreparedStatement).getGeneratedKeys();
        }

        @Test
        @DisplayName("Should update an existing outcome")
        void updateOutcome() throws SQLException {
            // Arrange
            Outcome outcomeToUpdate = new Outcome();
            outcomeToUpdate.setId(1);
            outcomeToUpdate.setOutcomeNum("a");
            outcomeToUpdate.setDescription("Updated description");

            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean updated = outcomeRepository.update(outcomeToUpdate);

            // Assert
            assertTrue(updated, "Outcome should be updated successfully");

            // Verify interactions
            verify(mockPreparedStatement).setString(1, "a");
            verify(mockPreparedStatement).setString(2, "Updated description");
            verify(mockPreparedStatement).setInt(3, 1);
            verify(mockPreparedStatement).executeUpdate();
        }

        @Test
        @DisplayName("Should delete an outcome")
        void deleteOutcome() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean deleted = outcomeRepository.delete(1);

            // Assert
            assertTrue(deleted, "Outcome should be deleted successfully");

            // Verify interactions
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).executeUpdate();
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        private OutcomeRepository realRepo;

        @BeforeEach
        void setUp() {
            realRepo = new OutcomeRepository();
        }

        @Test
        @DisplayName("Should find outcomes by IDs from database")
        void findByIdFromDatabase() {
            // Act
            Outcome outcome = realRepo.findById(1);

            // Assert
            assertNotNull(outcome, "Outcome should be found in database");
            assertEquals(1, outcome.getId(), "Outcome ID should match");
            assertEquals("a", outcome.getOutcomeNum(), "Outcome number should match");
            assertEquals(
                    "An ability to apply knowledge of computing",
                    outcome.getDescription(),
                    "Outcome description should match"
            );

            // Check indicators
            List<Indicator> indicators = outcome.getIndicators();
            assertNotNull(indicators, "Indicators should not be null");
            assertEquals(2, indicators.size(), "Outcome should have 2 indicators");

            // Verify each indicator's ID and description
            Map<Integer, String> expected = Map.of(
                    1, "Apply mathematical principles",
                    2, "Use computing techniques"
            );
            for (Indicator ind : indicators) {
                assertTrue(
                        expected.containsKey(ind.getId()),
                        "Unexpected indicator id: " + ind.getId()
                );
                assertEquals(
                        expected.get(ind.getId()),
                        ind.getDescription(),
                        "Indicator description should match for ID " + ind.getId()
                );
            }
        }
    }
}
