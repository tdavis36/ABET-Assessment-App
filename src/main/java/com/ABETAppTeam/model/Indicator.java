package com.ABETAppTeam.model;

/**
 * Model class for outcome indicators
 */
public class Indicator {
    private int indicatorId;
    private int outcomeId;
    private int indicatorNumber;
    private String description;

    /**
     * Default constructor
     */
    public Indicator() {
    }

    /**
     * Constructor with parameters
     *
     * @param id Indicator ID
     * @param outcomeId Outcome ID that this indicator belongs to
     * @param number Indicator number within the outcome (e.g., 1 for indicator 1.1)
     * @param description Indicator description
     */
    public Indicator(int indicatorId, int outcomeId, int indicatorNumber, String description) {
        this.indicatorId = indicatorId;
        this.outcomeId = outcomeId;
        this.indicatorNumber = indicatorNumber;
        this.description = description;
    }

    public Indicator(int indicatorId, String description, double targetGoal) {
    }

    /**
     * Get the indicator ID
     *
     * @return Indicator ID
     */
    public int getIndicatorId() {
        return indicatorId;
    }

    /**
     * Set the indicator ID
     *
     * @param indicatorId Indicator ID
     */
    public void setId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    /**
     * Get the outcome ID that this indicator belongs to
     *
     * @return Outcome ID
     */
    public int getOutcomeId() {
        return outcomeId;
    }

    /**
     * Set the outcome ID that this indicator belongs to
     *
     * @param outcomeId Outcome ID
     */
    public void setOutcomeId(int outcomeId) {
        this.outcomeId = outcomeId;
    }

    /**
     * Get the indicator number within the outcome (e.g., 1 for indicator 1.1)
     *
     * @return Indicator number
     */
    public int getNumber() {
        return indicatorNumber;
    }

    /**
     * Set the indicator number within the outcome
     *
     * @param number Indicator number
     */
    public void setNumber(int indicatorNumber) {
        this.indicatorNumber = indicatorNumber;
    }

    /**
     * Get the indicator description
     *
     * @return Indicator description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the indicator description
     *
     * @param description Indicator description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Indicator [id=" + indicatorId + ", outcomeId=" + outcomeId + ", number=" + indicatorNumber + ", description=" + description + "]";
    }
}