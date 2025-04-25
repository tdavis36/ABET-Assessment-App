package com.ABETAppTeam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.ABETAppTeam.repository.OutcomeRepository;
import com.ABETAppTeam.repository.IndicatorRepository;
import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.model.Indicator;

/**
 * Controller for managing outcomes and indicators
 */
public class OutcomeController {

    private static OutcomeController instance;
    private final OutcomeRepository outcomeRepository;
    private final IndicatorRepository indicatorRepository;

    /**
     * Private constructor (singleton pattern)
     */
    private OutcomeController() {
        this.outcomeRepository = new OutcomeRepository();
        this.indicatorRepository = new IndicatorRepository();
    }

    /**
     * Get the singleton instance
     *
     * @return The singleton instance
     */
    public static synchronized OutcomeController getInstance() {
        if (instance == null) {
            instance = new OutcomeController();
        }
        return instance;
    }

    /**
     * Get all outcomes as a map
     *
     * @return Map of outcome IDs to descriptions
     */
    public Map<Integer, String> getAllOutcomeDescriptions() {
        Map<Integer, String> outcomeDescriptions = new HashMap<>();

        List<Outcome> outcomes = outcomeRepository.findAll();
        for (Outcome outcome : outcomes) {
            outcomeDescriptions.put(outcome.getId(), outcome.getDescription());
        }

        return outcomeDescriptions;
    }

    /**
     * Get all indicators grouped by outcome ID
     *
     * @return Map of outcome IDs to lists of indicator descriptions
     */
    public Map<Integer, List<String>> getAllIndicators() {
        Map<Integer, List<String>> indicators = new HashMap<>();

        List<Indicator> allIndicators = indicatorRepository.findAll();
        for (Indicator indicator : allIndicators) {
            int outcomeId = indicator.getOutcomeId();
            if (!indicators.containsKey(outcomeId)) {
                indicators.put(outcomeId, new ArrayList<>());
            }

            String formattedIndicator = String.format("%d.%d %s",
                    outcomeId, indicator.getNumber(), indicator.getDescription());
            indicators.get(outcomeId).add(formattedIndicator);
        }

        return indicators;
    }

    /**
     * Get outcomes for a specific course
     *
     * @param courseId Course ID
     * @return Map of course IDs to lists of outcome IDs
     */
    public Map<String, List<Integer>> getCourseOutcomes(String courseId) {
        Map<String, List<Integer>> courseOutcomes = new HashMap<>();

        List<Integer> outcomes = outcomeRepository.findByCourseId(courseId);
        if (!outcomes.isEmpty()) {
            courseOutcomes.put(courseId, outcomes);
        }

        return courseOutcomes;
    }

    /**
     * Get outcomes for all courses
     *
     * @return Map of course IDs to lists of outcome IDs
     */
    public Map<String, List<Integer>> getAllCourseOutcomes() {
        return outcomeRepository.findAllCourseOutcomes();
    }

    /**
     * Convert all outcome data to JSON format for use in JavaScript
     *
     * @return JSON string containing all outcome data
     */
    public String getOutcomeDataAsJson() {
        Map<Integer, String> outcomeDescriptions = getAllOutcomeDescriptions();
        Map<Integer, List<String>> indicators = getAllIndicators();
        Map<String, List<Integer>> courseOutcomes = getAllCourseOutcomes();

        StringBuilder jsonBuilder = new StringBuilder();

        // Build outcomeDescriptions JSON
        jsonBuilder.append("const outcomeDescriptions = {");
        for (Map.Entry<Integer, String> entry : outcomeDescriptions.entrySet()) {
            jsonBuilder.append(entry.getKey())
                    .append(": \"")
                    .append(escapeJsonString(entry.getValue()))
                    .append("\", ");
        }
        if (!outcomeDescriptions.isEmpty()) {
            jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length()); // Remove trailing comma and space
        }
        jsonBuilder.append("};\n\n");

        // Build indicators JSON
        jsonBuilder.append("const indicators = {");
        for (Map.Entry<Integer, List<String>> entry : indicators.entrySet()) {
            jsonBuilder.append(entry.getKey()).append(": [");
            for (String indicator : entry.getValue()) {
                jsonBuilder.append("\"")
                        .append(escapeJsonString(indicator))
                        .append("\", ");
            }
            if (!entry.getValue().isEmpty()) {
                jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length()); // Remove trailing comma and space
            }
            jsonBuilder.append("], ");
        }
        if (!indicators.isEmpty()) {
            jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length()); // Remove trailing comma and space
        }
        jsonBuilder.append("};\n\n");

        // Build courseOutcomes JSON
        jsonBuilder.append("const courseOutcomes = {");
        for (Map.Entry<String, List<Integer>> entry : courseOutcomes.entrySet()) {
            jsonBuilder.append("\"")
                    .append(escapeJsonString(entry.getKey()))
                    .append("\": [");
            for (Integer outcomeId : entry.getValue()) {
                jsonBuilder.append(outcomeId).append(", ");
            }
            if (!entry.getValue().isEmpty()) {
                jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length()); // Remove trailing comma and space
            }
            jsonBuilder.append("], ");
        }
        if (!courseOutcomes.isEmpty()) {
            jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length()); // Remove trailing comma and space
        }
        jsonBuilder.append("};");

        return jsonBuilder.toString();
    }

    /**
     * Escape special characters in JSON strings
     *
     * @param input Input string
     * @return Escaped string
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}