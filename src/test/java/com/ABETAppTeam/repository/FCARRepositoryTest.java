package com.ABETAppTeam.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.repository.FCARRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

@ExtendWith(MockitoExtension.class)
public class FCARRepositoryTest {

    @Mock
    private HikariDataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private FCARRepository fcarRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        // Replace the DataSourceFactory with our mock
        try (var staticMock = mockStatic(DataSourceFactory.class)) {
            staticMock.when(DataSourceFactory::getDataSource).thenReturn(mockDataSource);

            // Set up mock connection
            when(mockDataSource.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // Initialize repository with mock datasource
            fcarRepository = new FCARRepository();
        }
    }

    @Test
    @DisplayName("Test findById returns FCAR when found")
    public void testFindByIdFound() throws SQLException {
        // Arrange
        int fcarId = 1;
        setupMockResultSet(true);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Act
        FCAR result = fcarRepository.findById(fcarId);

        // Assert
        assertNotNull(result);
        assertEquals(fcarId, result.getFcarId());
        verify(mockPreparedStatement).setInt(1, fcarId);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Test findById returns null when not found")
    public void testFindByIdNotFound() throws SQLException {
        // Arrange
        int fcarId = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        FCAR result = fcarRepository.findById(fcarId);

        // Assert
        assertNull(result);
        verify(mockPreparedStatement).setInt(1, fcarId);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Test findAll returns list of FCARs")
    public void testFindAll() throws SQLException {
        // Arrange
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        setupMockResultSet(true);

        // Act
        List<FCAR> results = fcarRepository.findAll();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(mockStatement).executeQuery(anyString());
    }

    @Test
    @DisplayName("Test findByCourseCode returns correct FCARs")
    public void testFindByCourseCode() throws SQLException {
        // Arrange
        String courseCode = "CS101";
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        setupMockResultSet(true);

        // Act
        List<FCAR> results = fcarRepository.findByCourseCode(courseCode);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(courseCode, results.get(0).getCourseCode());
        verify(mockPreparedStatement).setString(1, courseCode);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Test findByInstructorId returns correct FCARs")
    public void testFindByInstructorId() throws SQLException {
        // Arrange
        int instructorId = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        setupMockResultSet(true);

        // Act
        List<FCAR> results = fcarRepository.findByInstructorId(instructorId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(instructorId, results.get(0).getInstructorId());
        verify(mockPreparedStatement).setInt(1, instructorId);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Test findBySemesterAndYear returns correct FCARs")
    public void testFindBySemesterAndYear() throws SQLException {
        // Arrange
        String semester = "Fall";
        int year = 2023;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        setupMockResultSet(true);

        // Act
        List<FCAR> results = fcarRepository.findBySemesterAndYear(semester, year);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(semester, results.get(0).getSemester());
        assertEquals(year, results.get(0).getYear());
        verify(mockPreparedStatement).setString(1, semester);
        verify(mockPreparedStatement).setInt(2, year);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Test save inserts new FCAR")
    public void testSaveNewFCAR() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(0);  // ID 0 means new FCAR
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Generated ID

        // Act
        FCAR result = fcarRepository.save(fcar);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).getGeneratedKeys();
    }

    @Test
    @DisplayName("Test save updates existing FCAR")
    public void testSaveExistingFCAR() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);  // ID > 0 means existing FCAR
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        FCAR result = fcarRepository.save(fcar);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Test update returns true when successful")
    public void testUpdateSuccess() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        // Act
        boolean result = fcarRepository.update(fcar);

        // Assert
        assertTrue(result);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Test update returns false when no rows affected")
    public void testUpdateNoRowsAffected() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = fcarRepository.update(fcar);

        // Assert
        assertFalse(result);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Test delete returns true when successful")
    public void testDeleteSuccess() throws SQLException {
        // Arrange
        int fcarId = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = fcarRepository.delete(fcarId);

        // Assert
        assertTrue(result);
        verify(mockPreparedStatement).setInt(1, fcarId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Test delete returns false when no rows affected")
    public void testDeleteNoRowsAffected() throws SQLException {
        // Arrange
        int fcarId = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = fcarRepository.delete(fcarId);

        // Assert
        assertFalse(result);
        verify(mockPreparedStatement).setInt(1, fcarId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Test error handling in findById")
    public void testFindByIdError() throws SQLException {
        // Arrange
        int fcarId = 1;
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Test exception"));

        // Act
        FCAR result = fcarRepository.findById(fcarId);

        // Assert
        assertNull(result);
        verify(mockPreparedStatement).setInt(1, fcarId);
    }

    @Test
    @DisplayName("Test loading assessment methods")
    public void testLoadingAssessmentMethods() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        PreparedStatement methodStmt = mock(PreparedStatement.class);
        ResultSet methodRS = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FCAR_Assessment_Methods"))).thenReturn(methodStmt);
        when(methodStmt.executeQuery()).thenReturn(methodRS);
        when(methodRS.next()).thenReturn(true, true, false);
        when(methodRS.getString("method_key")).thenReturn("key1", "key2");
        when(methodRS.getString("method_value")).thenReturn("value1", "value2");

        // Act - we'll use findById which should load additional data
        setupMockResultSet(true);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        FCAR result = fcarRepository.findById(1);

        // Assert
        assertNotNull(result);
        Map<String, String> methods = result.getAssessmentMethods();
        assertNotNull(methods);
        assertEquals(2, methods.size());
        assertEquals("value1", methods.get("key1"));
        assertEquals("value2", methods.get("key2"));
    }

    @Test
    @DisplayName("Test loading student outcomes")
    public void testLoadingStudentOutcomes() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        PreparedStatement outcomesStmt = mock(PreparedStatement.class);
        ResultSet outcomesRS = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FCAR_Student_Outcomes"))).thenReturn(outcomesStmt);
        when(outcomesStmt.executeQuery()).thenReturn(outcomesRS);
        when(outcomesRS.next()).thenReturn(true, false);
        when(outcomesRS.getString("outcome_key")).thenReturn("outcome1");
        when(outcomesRS.getInt("achievement_level")).thenReturn(4);

        // Act - we'll use findById which should load additional data
        setupMockResultSet(true);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        FCAR result = fcarRepository.findById(1);

        // Assert
        assertNotNull(result);
        Map<String, Integer> outcomes = result.getStudentOutcomes();
        assertNotNull(outcomes);
        assertEquals(1, outcomes.size());
        assertEquals(Integer.valueOf(4), outcomes.get("outcome1"));
    }

    @Test
    @DisplayName("Test loading improvement actions")
    public void testLoadingImprovementActions() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        PreparedStatement actionsStmt = mock(PreparedStatement.class);
        ResultSet actionsRS = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FCAR_Improvement_Actions"))).thenReturn(actionsStmt);
        when(actionsStmt.executeQuery()).thenReturn(actionsRS);
        when(actionsRS.next()).thenReturn(true, false);
        when(actionsRS.getString("action_key")).thenReturn("action1");
        when(actionsRS.getString("action_value")).thenReturn("value1");

        // Act - we'll use findById which should load additional data
        setupMockResultSet(true);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        FCAR result = fcarRepository.findById(1);

        // Assert
        assertNotNull(result);
        Map<String, String> actions = result.getImprovementActions();
        assertNotNull(actions);
        assertEquals(1, actions.size());
        assertEquals("value1", actions.get("action1"));
    }

    @Test
    @DisplayName("Test loading status")
    public void testLoadingStatus() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        PreparedStatement statusStmt = mock(PreparedStatement.class);
        ResultSet statusRS = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FCAR_Status"))).thenReturn(statusStmt);
        when(statusStmt.executeQuery()).thenReturn(statusRS);
        when(statusRS.next()).thenReturn(true);
        when(statusRS.getString("status")).thenReturn("Approved");
        when(statusRS.getString("comments")).thenReturn("Good work");

        // Act - we'll use findById which should load additional data
        setupMockResultSet(true);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        FCAR result = fcarRepository.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Approved", result.getStatus());
        assertEquals("Good work", result.getImprovementActions().get("statusComments"));
    }

    @Test
    @DisplayName("Test transaction handling in save")
    public void testTransactionHandlingInSave() throws SQLException {
        // Arrange
        FCAR fcar = createSampleFCAR(1);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Transaction test exception"));

        // Act
        FCAR result = fcarRepository.save(fcar);

        // Assert
        assertNull(result);
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).rollback();
        verify(mockConnection).setAutoCommit(true);
    }

    // Helper methods to set up the tests

    private void setupMockResultSet(boolean hasNext) throws SQLException {
        when(mockResultSet.next()).thenReturn(hasNext, false);

        if (hasNext) {
            // Set up all the FCAR field values
            when(mockResultSet.getInt("fcar_id")).thenReturn(1);
            when(mockResultSet.getString("course_code")).thenReturn("CS101");
            when(mockResultSet.getString("semester")).thenReturn("Fall");
            when(mockResultSet.getInt("year")).thenReturn(2023);
            when(mockResultSet.getInt("instructor_id")).thenReturn(1);
            when(mockResultSet.getTimestamp("date_filled")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getInt("outcome_id")).thenReturn(1);
            when(mockResultSet.getInt("indicator_id")).thenReturn(1);
            when(mockResultSet.getInt("goal_id")).thenReturn(1);
            when(mockResultSet.getInt("method_id")).thenReturn(1);
            when(mockResultSet.getString("method_desc")).thenReturn("Method description");
            when(mockResultSet.getInt("stud_expect_id")).thenReturn(1);
            when(mockResultSet.getString("summary_desc")).thenReturn("Summary description");
            when(mockResultSet.getInt("action_id")).thenReturn(1);
            when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

            // Handle cases where getInt returns NULL
            when(mockResultSet.wasNull()).thenReturn(false);
        }
    }

    private FCAR createSampleFCAR(int id) {
        FCAR fcar = new FCAR();
        fcar.setFcarId(id);
        fcar.setCourseCode("CS101");
        fcar.setSemester("Fall");
        fcar.setYear(2023);
        fcar.setInstructorId(1);
        fcar.setOutcomeId(1);
        fcar.setIndicatorId(1);
        fcar.setMethodDesc("Method description");
        fcar.setSummaryDesc("Summary description");
        fcar.setStatus("Draft");

        // Set up the collection fields
        Map<String, String> assessmentMethods = new HashMap<>();
        assessmentMethods.put("key1", "value1");
        fcar.setAssessmentMethods(assessmentMethods);

        Map<String, Integer> studentOutcomes = new HashMap<>();
        studentOutcomes.put("outcome1", 4);
        fcar.setStudentOutcomes(studentOutcomes);

        Map<String, String> improvementActions = new HashMap<>();
        improvementActions.put("action1", "value1");
        fcar.setImprovementActions(improvementActions);

        return fcar;
    }
}