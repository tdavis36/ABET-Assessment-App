package com.ABETAppTeam.model;

/**
 * Model class for Student Learning Outcomes
 */
public class Outcome {
    private int outcomeId;
    private String description;

    /**
     * Default constructor
     */
    public Outcome() {
    }

    /**
     * Constructor with parameters
     *
     * @param outcomeId Outcome ID
     * @param description Outcome description
     */
    public Outcome(int outcomeId, String description) {
        this.outcomeId = outcomeId;
        this.description = description;
    }

    /**
     * Get the outcome ID
     *
     * @return Outcome ID
     */
    public int getId() {
        return outcomeId;
    }

    /**
     * Set the outcome ID
     *
     * @param outcomeId Outcome ID
     */
    public void setId(int outcomeId) {
        this.outcomeId = outcomeId;
    }

    /**
     * Get the outcome description
     *
     * @return Outcome description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the outcome description
     *
     * @param description Outcome description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Outcome [id=" + outcomeId + ", description=" + description + "]";
    }
}