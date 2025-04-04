package com.ABETAppTeam.model;

/**
 * Model class for outcome indicators
 */
public class Indicator {
    private int id;
    private int outcomeId;
    private int number;
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
    public Indicator(int id, int outcomeId, int number, String description) {
        this.id = id;
        this.outcomeId = outcomeId;
        this.number = number;
        this.description = description;
    }

    public Indicator(int indicatorId, String description, double targetGoal) {
    }

    /**
     * Get the indicator ID
     *
     * @return Indicator ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the indicator ID
     *
     * @param id Indicator ID
     */
    public void setId(int id) {
        this.id = id;
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
        return number;
    }

    /**
     * Set the indicator number within the outcome
     *
     * @param number Indicator number
     */
    public void setNumber(int number) {
        this.number = number;
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
        return "Indicator [id=" + id + ", outcomeId=" + outcomeId + ", number=" + number + ", description=" + description + "]";
    }

    public Integer getIndicatorId() {
        return id;
    }
}