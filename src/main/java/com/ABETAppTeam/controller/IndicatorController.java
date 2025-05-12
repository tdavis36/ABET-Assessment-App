package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * IndicatorController class for the ABET Assessment Application.
 * This class serves as a controller for Indicator operations.
 * It provides methods for creating, retrieving, and updating Indicators,
 * mirroring the functionality of your FCARController.
 */
public class IndicatorController {
    // Singleton instance
    private static IndicatorController instance;

    // In-memory storage for Indicators (in a real application, this might be a database)
    private final Map<Integer, Indicator> indicatorMap;

    // Cache for recently accessed Indicators
    private final Map<Integer, Indicator> indicatorCache;

    // Counter for generating unique indicator IDs
    private int indicatorCounter = 1;

    /**
     * Private constructor for the singleton pattern.
     */
    private IndicatorController() {
        this.indicatorMap = new HashMap<>();
        this.indicatorCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of the IndicatorController.
     *
     * @return The IndicatorController instance.
     */
    public static synchronized IndicatorController getInstance() {
        if (instance == null) {
            instance = new IndicatorController();
        }
        return instance;
    }

    /**
     * Create a new Indicator.
     *
     * @param description Description of the indicator.
     * @param targetGoal  Target goal for the indicator.
     * @return The ID of the created indicator, or -1 if creation failed.
     */
    public int createIndicator(String description, double targetGoal) {
        try {
            // Generate a unique ID
            int indicatorId = indicatorCounter++;

            // Create a new Indicator object
            Indicator indicator = new Indicator(indicatorId, description, targetGoal);

            // Store the Indicator in the main map
            this.indicatorMap.put(indicatorId, indicator);

            // Also store in the cache
            this.indicatorCache.put(indicatorId, indicator);

            return indicatorId;
        } catch (Exception e) {
            AppUtils.error("Error creating Indicator: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * Retrieve an Indicator by its ID.
     *
     * @param indicatorId The ID of the Indicator to retrieve.
     * @return The Indicator object, or null if not found.
     */
    public Indicator getIndicator(int indicatorId) {
        // Check the cache first
        if (this.indicatorCache.containsKey(indicatorId)) {
            return this.indicatorCache.get(indicatorId);
        }

        // If not in cache, retrieve from the main storage
        Indicator indicator = this.indicatorMap.get(indicatorId);

        // If found, add it to the cache
        if (indicator != null) {
            this.indicatorCache.put(indicatorId, indicator);
        }

        return indicator;
    }

    /**
     * Update an existing Indicator.
     *
     * @param indicator The Indicator object with updated data.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateIndicator(Indicator indicator) {
        if (indicator == null || !this.indicatorMap.containsKey(indicator.getIndicatorId())) {
            return false;
        }

        // Update the Indicator in both the main map and the cache
        this.indicatorMap.put(indicator.getIndicatorId(), indicator);
        this.indicatorCache.put(indicator.getIndicatorId(), indicator);
        return true;
    }
}
