package com.ABETAppTeam;

public class Indicator {
    private int indicatorId;
    private String description;
    private double targetGoal;

    /**
     * Default constructor
     */
    public Indicator() {
        // Optionally initialize default values here
    }

    /**
     * Parameterized constructor for Indicator
     *
     * @param indicatorId Unique identifier for the indicator
     * @param description Description of the indicator
     * @param targetGoal  Target goal for the indicator
     */
    public Indicator(int indicatorId, String description, double targetGoal) {
        this.indicatorId = indicatorId;
        this.description = description;
        this.targetGoal = targetGoal;
    }

    // Getters and setters

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetGoal() {
        return targetGoal;
    }

    public void setTargetGoal(double targetGoal) {
        this.targetGoal = targetGoal;
    }
}
