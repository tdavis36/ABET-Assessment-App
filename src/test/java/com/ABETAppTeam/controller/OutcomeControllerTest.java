// Fixed OutcomeControllerTest.java
package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.repository.IndicatorRepository;
import com.ABETAppTeam.repository.OutcomeRepository;
import com.ABETAppTeam.util.AppUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OutcomeController class
 */
public class OutcomeControllerTest {

    @Mock
    private OutcomeRepository outcomeRepository;

    @Mock
    private IndicatorRepository indicatorRepository;

    private OutcomeController outcomeController;

    private List<Outcome> testOutcomes;
    private List<Indicator> testIndicators;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a fresh controller instance
        outcomeController = new OutcomeController();

        // Try to set the singleton instance using reflection
        try {
            // Reset the singleton instance
            java.lang.reflect.Field instanceField = OutcomeController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, outcomeController);

            // Inject repository fields
            java.lang.reflect.Field outcomeRepoField = OutcomeController.class.getDeclaredField("outcomeRepository");
            outcomeRepoField.setAccessible(true);
            outcomeRepoField.set(outcomeController, outcomeRepository);

            java.lang.reflect.Field indicatorRepoField = OutcomeController.class.getDeclaredField("indicatorRepository");
            indicatorRepoField.setAccessible(true);
            indicatorRepoField.set(outcomeController, indicatorRepository);
        } catch (Exception e) {
            fail("Failed to set up controller instance: " + e.getMessage());
        }

        // Create test outcomes
        testOutcomes = new ArrayList<>();

        Outcome outcome1 = new Outcome();
        outcome1.setId(1);
        outcome1.setOutcomeNum("1");
        outcome1.setDescription("Analyze complex computing problems");
        testOutcomes.add(outcome1);

        Outcome outcome2 = new Outcome();
        outcome2.setId(2);
        outcome2.setOutcomeNum("2");
        outcome2.setDescription("Design computing-based solutions");
        testOutcomes.add(outcome2);

        // Create test indicators
        testIndicators = new ArrayList<>();

        Indicator indicator1_1 = new Indicator();
        indicator1_1.setId(1);
        indicator1_1.setOutcomeId(1);
        indicator1_1.setNumber(1);
        indicator1_1.setDescription("Student can correctly interpret a computational problem");
        testIndicators.add(indicator1_1);

        Indicator indicator1_2 = new Indicator();
        indicator1_2.setId(2);
        indicator1_2.setOutcomeId(1);
        indicator1_2.setNumber(2);
        indicator1_2.setDescription("Student can analyze a computational problem");
        testIndicators.add(indicator1_2);

        Indicator indicator2_1 = new Indicator();
        indicator2_1.setId(3);
        indicator2_1.setOutcomeId(2);
        indicator2_1.setNumber(1);
        indicator2_1.setDescription("Student can identify appropriate technologies");
        testIndicators.add(indicator2_1);

        // Set up default behavior for frequently used mocks
        when(outcomeRepository.findAll()).thenReturn(testOutcomes);
        when(indicatorRepository.findAll()).thenReturn(testIndicators);

        // Make getOutcomeDataAsJson work
        when(outcomeRepository.findAllCourseOutcomes()).thenReturn(
                Map.of("CS101", Arrays.asList(1, 2))
        );
    }

    @Test
    public void testGetInstance() {
        // Test the singleton pattern
        OutcomeController instance = OutcomeController.getInstance();
        assertNotNull(instance);

        OutcomeController instance2 = OutcomeController.getInstance();
        assertSame(instance, instance2);
    }

    @Test
    public void testGetAllOutcomeDescriptions() {
        // Arrange
        when(outcomeRepository.findAll()).thenReturn(testOutcomes);

        // Act
        Map<Integer, String> descriptions = outcomeController.getAllOutcomeDescriptions();

        // Assert
        assertNotNull(descriptions);
        assertEquals(2, descriptions.size());
        assertEquals("Analyze complex computing problems", descriptions.get(1));
        assertEquals("Design computing-based solutions", descriptions.get(2));
        verify(outcomeRepository).findAll();
    }

    @Test
    public void testGetAllIndicators() {
        // Arrange
        when(indicatorRepository.findAll()).thenReturn(testIndicators);

        // Act
        Map<Integer, List<String>> indicators = outcomeController.getAllIndicators();

        // Assert
        assertNotNull(indicators);
        assertEquals(2, indicators.size());

        List<String> outcome1Indicators = indicators.get(1);
        assertNotNull(outcome1Indicators);
        assertEquals(2, outcome1Indicators.size());
        assertTrue(outcome1Indicators.get(0).contains("1.1"));
        assertTrue(outcome1Indicators.get(1).contains("1.2"));

        List<String> outcome2Indicators = indicators.get(2);
        assertNotNull(outcome2Indicators);
        assertEquals(1, outcome2Indicators.size());
        assertTrue(outcome2Indicators.get(0).contains("2.1"));

        verify(indicatorRepository).findAll();
    }

    @Test
    public void testGetCourseOutcomes() {
        // Arrange
        String courseId = "CS101";
        List<Integer> outcomes = Arrays.asList(1, 2);
        when(outcomeRepository.findByCourseId(courseId)).thenReturn(outcomes);

        // Act
        Map<String, List<Integer>> courseOutcomes = outcomeController.getCourseOutcomes(courseId);

        // Assert
        assertNotNull(courseOutcomes);
        assertEquals(1, courseOutcomes.size());

        List<Integer> courseOutcomeIds = courseOutcomes.get(courseId);
        assertNotNull(courseOutcomeIds);
        assertEquals(2, courseOutcomeIds.size());
        assertEquals(1, courseOutcomeIds.get(0));
        assertEquals(2, courseOutcomeIds.get(1));

        verify(outcomeRepository).findByCourseId(courseId);
    }

    @Test
    public void testGetCourseOutcomesEmpty() {
        // Arrange
        String courseId = "CS999";
        when(outcomeRepository.findByCourseId(courseId)).thenReturn(new ArrayList<>());

        // Act
        Map<String, List<Integer>> courseOutcomes = outcomeController.getCourseOutcomes(courseId);

        // Assert
        assertNotNull(courseOutcomes);
        assertEquals(0, courseOutcomes.size());

        verify(outcomeRepository).findByCourseId(courseId);
    }

    @Test
    public void testGetAllCourseOutcomes() {
        // Arrange
        Map<String, List<Integer>> mockCourseOutcomes = new HashMap<>();
        mockCourseOutcomes.put("CS101", Arrays.asList(1, 2));
        mockCourseOutcomes.put("CS102", Arrays.asList(2, 3));
        when(outcomeRepository.findAllCourseOutcomes()).thenReturn(mockCourseOutcomes);

        // Act
        Map<String, List<Integer>> courseOutcomes = outcomeController.getAllCourseOutcomes();

        // Assert
        assertNotNull(courseOutcomes);
        assertEquals(2, courseOutcomes.size());

        List<Integer> cs101Outcomes = courseOutcomes.get("CS101");
        assertNotNull(cs101Outcomes);
        assertEquals(2, cs101Outcomes.size());

        List<Integer> cs102Outcomes = courseOutcomes.get("CS102");
        assertNotNull(cs102Outcomes);
        assertEquals(2, cs102Outcomes.size());

        verify(outcomeRepository).findAllCourseOutcomes();
    }

    @Test
    public void testGetOutcomeDataAsJson() {
        // Arrange
        when(outcomeRepository.findAll()).thenReturn(testOutcomes);
        when(indicatorRepository.findAll()).thenReturn(testIndicators);

        Map<String, List<Integer>> mockCourseOutcomes = new HashMap<>();
        mockCourseOutcomes.put("CS101", Arrays.asList(1, 2));
        when(outcomeRepository.findAllCourseOutcomes()).thenReturn(mockCourseOutcomes);

        // Act
        String json = outcomeController.getOutcomeDataAsJson();

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("outcomeDescriptions"), "JSON should contain outcome descriptions");
        assertTrue(json.contains("outcomeNumbers"), "JSON should contain outcome numbers");
        assertTrue(json.contains("indicators"), "JSON should contain indicators");
        assertTrue(json.contains("courseOutcomes"), "JSON should contain course outcomes");

        // Check specific content
        assertTrue(json.contains("\"1\":\"Analyze complex computing problems\""),
                "JSON should contain outcome 1 description");
        assertTrue(json.contains("\"2\":\"Design computing-based solutions\""),
                "JSON should contain outcome 2 description");
        assertTrue(json.contains("\"CS101\":[1,2]"), "JSON should contain CS101 outcomes");

        verify(outcomeRepository).findAll();
        verify(indicatorRepository).findAll();
        verify(outcomeRepository).findAllCourseOutcomes();
    }

    @Test
    public void testGetAllOutcomesAndIndicatorsForForm() {
        // Arrange
        when(outcomeRepository.findAll()).thenReturn(testOutcomes);
        when(indicatorRepository.findAll()).thenReturn(testIndicators);

        // Act
        Map<String, Object> result = outcomeController.getAllOutcomesAndIndicatorsForForm();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        List<Outcome> outcomes = (List<Outcome>) result.get("outcomes");
        assertNotNull(outcomes);
        assertEquals(2, outcomes.size());

        Map<Integer, List<Indicator>> indicatorsByOutcome = (Map<Integer, List<Indicator>>) result.get("indicatorsByOutcome");
        assertNotNull(indicatorsByOutcome);
        assertEquals(2, indicatorsByOutcome.size());

        List<Indicator> outcome1Indicators = indicatorsByOutcome.get(1);
        assertNotNull(outcome1Indicators);
        assertEquals(2, outcome1Indicators.size());

        List<Indicator> outcome2Indicators = indicatorsByOutcome.get(2);
        assertNotNull(outcome2Indicators);
        assertEquals(1, outcome2Indicators.size());

        verify(outcomeRepository).findAll();
        verify(indicatorRepository).findAll();
    }

    @Test
    public void testGetOutcomes() {
        // Arrange
        when(outcomeRepository.findAll()).thenReturn(testOutcomes);

        // Act
        List<Outcome> outcomes = outcomeController.getOutcomes();

        // Assert
        assertNotNull(outcomes);
        assertEquals(2, outcomes.size());
        assertEquals(1, outcomes.get(0).getId());
        assertEquals(2, outcomes.get(1).getId());
        verify(outcomeRepository).findAll();
    }

    @Test
    public void testGetIndicatorsByOutcome() {
        // Arrange
        when(indicatorRepository.findAll()).thenReturn(testIndicators);

        // Act
        Map<Integer, List<Indicator>> indicatorsByOutcome = outcomeController.getIndicatorsByOutcome();

        // Assert
        assertNotNull(indicatorsByOutcome);
        assertEquals(2, indicatorsByOutcome.size());

        List<Indicator> outcome1Indicators = indicatorsByOutcome.get(1);
        assertNotNull(outcome1Indicators);
        assertEquals(2, outcome1Indicators.size());
        assertEquals(1, outcome1Indicators.get(0).getIndicatorId());
        assertEquals(2, outcome1Indicators.get(1).getIndicatorId());

        List<Indicator> outcome2Indicators = indicatorsByOutcome.get(2);
        assertNotNull(outcome2Indicators);
        assertEquals(1, outcome2Indicators.size());
        assertEquals(3, outcome2Indicators.get(0).getIndicatorId());

        verify(indicatorRepository).findAll();
    }
}