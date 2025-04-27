package com.ABETAppTeam.model;

/**
 * Model class for Student Learning Outcomes
 */
public class Outcome {
    private int outcomeId;
    private String outcomeNum;
    private String description;

    /**
     * Default constructor
     */
    public Outcome() {
    }

    /**
     * Constructor with parameters
     *
     * @param outcomeId   Outcome ID
     * @param outcomeNum  Outcome number
     * @param description Outcome description
     */
    public Outcome(int outcomeId, String outcomeNum, String description) {
        this.outcomeId = outcomeId;
        this.outcomeNum = outcomeNum;
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

    /**
     * Get the outcome number
     *
     * @return Outcome number
     */
    public String getOutcomeNum() {
        return outcomeNum;
    }

    /**
     * Set the outcome number
     *
     * @param outcomeNum Outcome number
     */
    public void setOutcomeNum(String outcomeNum) {
        this.outcomeNum = outcomeNum;
    }

    @Override
    public String toString() {
        return "Outcome [id=" + outcomeId + ", outcomeNum=" + outcomeNum + ", description=" + description + "]";
    }
}
