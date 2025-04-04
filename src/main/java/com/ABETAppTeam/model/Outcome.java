package com.ABETAppTeam.model;

/**
 * Model class for Student Learning Outcomes
 */
public class Outcome {
    private int id;
    private String description;

    /**
     * Default constructor
     */
    public Outcome() {
    }

    /**
     * Constructor with parameters
     *
     * @param id Outcome ID
     * @param description Outcome description
     */
    public Outcome(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Get the outcome ID
     *
     * @return Outcome ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the outcome ID
     *
     * @param id Outcome ID
     */
    public void setId(int id) {
        this.id = id;
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
        return "Outcome [id=" + id + ", description=" + description + "]";
    }
}