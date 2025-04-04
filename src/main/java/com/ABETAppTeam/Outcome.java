package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Outcome class represents a specific ABET outcome
 * that may have default indicators associated with it.
 */
public class Outcome {

    private String outcomeId;                 // e.g., "1", "2", "3", etc.
    private String description;               // Full description of the outcome
    private boolean isLegacy;                 // Flag indicating if the outcome is considered "legacy"
    private List<Indicator> defaultIndicators; // Indicators commonly associated with this outcome

    /**
     * Default constructor.
     */
    public Outcome() {
        this.defaultIndicators = new ArrayList<>();
    }

    /**
     * Parameterized constructor.
     * 
     * @param outcomeId    Unique identifier (e.g., "1" or "2")
     * @param description  Description of the outcome
     */
    public Outcome(String outcomeId, String description) {
        this.outcomeId = outcomeId;
        this.description = description;
        this.isLegacy = false;
        this.defaultIndicators = new ArrayList<>();
    }

    /**
     * Returns the unique outcome ID.
     */
    public String getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
    }

    /**
     * Returns the outcome description.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Indicates whether this outcome is legacy.
     */
    public boolean isLegacy() {
        return isLegacy;
    }

    public void setLegacy(boolean legacy) {
        isLegacy = legacy;
    }

    /**
     * Returns the default indicators associated with this outcome.
     */
    public List<Indicator> getAssociatedIndicators() {
        return defaultIndicators;
    }

    /**
     * Sets the default indicators for this outcome.
     */
    public void setAssociatedIndicators(List<Indicator> indicators) {
        this.defaultIndicators = indicators;
    }

    /**
     * Adds a single indicator to the list of default indicators.
     */
    public void addIndicator(Indicator indicator) {
        this.defaultIndicators.add(indicator);
    }
}
