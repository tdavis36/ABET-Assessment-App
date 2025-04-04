package com.ABETAppTeam;

import java.util.HashMap;
import java.util.Map;

/**
 * OutcomeController class manages Outcome objects.
 * It follows a pattern similar to FCARController.
 */
public class OutcomeController {

    private static OutcomeController instance;

    // In-memory storage for Outcome objects
    private Map<String, Outcome> outcomeMap;

    // Attribute to store a currently selected outcome (optional usage)
    private String selectedOutcome;

    /**
     * Private constructor for singleton pattern.
     */
    private OutcomeController() {
        this.outcomeMap = new HashMap<>();
    }

    /**
     * Get the singleton instance of the OutcomeController.
     */
    public static synchronized OutcomeController getInstance() {
        if (instance == null) {
            instance = new OutcomeController();
        }
        return instance;
    }

    /**
     * Manages outcomes. 
     * This is a placeholder for higher-level logic such as
     * loading outcomes from a database, batch-updating, etc.
     */
    public void manageOutcomes() {
        // TODO: Implement your own logic for managing outcomes
        // e.g., bulk updates, checks, or other operations.
    }

    /**
     * Sets an outcome to be "legacy" (no longer current).
     * 
     * @param outcomeId The ID of the outcome to update
     */
    public void setLegacy(String outcomeId) {
        Outcome outcome = outcomeMap.get(outcomeId);
        if (outcome != null) {
            outcome.setLegacy(true);
        }
    }

    /**
     * Sets an outcome to be "current" (no longer legacy).
     * 
     * @param outcomeId The ID of the outcome to update
     */
    public void setCurrent(String outcomeId) {
        Outcome outcome = outcomeMap.get(outcomeId);
        if (outcome != null) {
            outcome.setLegacy(false);
        }
    }

    /**
     * Retrieves an outcome by ID.
     *
     * @param outcomeId The ID of the outcome to retrieve
     * @return The Outcome object, or null if not found
     */
    public Outcome getOutcome(String outcomeId) {
        return outcomeMap.get(outcomeId);
    }

    /**
     * Creates or updates an outcome in the map.
     *
     * @param outcome The outcome to store
     */
    public void saveOutcome(Outcome outcome) {
        if (outcome != null && outcome.getOutcomeId() != null) {
            outcomeMap.put(outcome.getOutcomeId(), outcome);
        }
    }

    /**
     * Deletes an outcome from the map.
     *
     * @param outcomeId The ID of the outcome to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteOutcome(String outcomeId) {
        if (outcomeMap.containsKey(outcomeId)) {
            outcomeMap.remove(outcomeId);
            return true;
        }
        return false;
    }

    /**
     * (Optional) Accessor for the currently selected outcome ID.
     */
    public String getSelectedOutcome() {
        return selectedOutcome;
    }

    /**
     * (Optional) Mutator for the currently selected outcome ID.
     */
    public void setSelectedOutcome(String outcomeId) {
        this.selectedOutcome = outcomeId;
    }

    /**
     * (Optional) Clear all outcomes (for testing or reset).
     */
    public void clearAllOutcomes() {
        outcomeMap.clear();
    }
}
