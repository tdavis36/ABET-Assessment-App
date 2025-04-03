package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FCARController class for the ABET Assessment Application
 * This class serves as a controller for FCAR (Faculty Course Assessment Report)
 * operations. It provides methods for creating, retrieving, updating, and
 * managing FCARs, acting as an intermediary between the UI and the FCAR data.
 * This class combines the functionality of the previous FCARController and
 * FCARFactory
 * to remove redundancy.
 */
public class FCARController {
    // Singleton instance
    private static FCARController instance;

    // Store created FCARs in memory (in a real application, this would be a
    // database)
    private final Map<String, FCAR> fcarMap;

    // Cache for recently accessed FCARs
    private final Map<String, FCAR> fcarCache;

    /**
     * Private constructor for a singleton pattern
     */
    private FCARController() {
        this.fcarMap = new HashMap<>();
        this.fcarCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of the FCARController
     * 
     * @return The FCARController instance
     */
    public static synchronized FCARController getInstance() {
        if (instance == null) {
            instance = new FCARController();
        }
        return instance;
    }

    /**
     * Create a new FCAR
     * 
     * @param courseId    ID of the course
     * @param professorId ID of the professor creating the FCAR
     * @param semester    Semester (e.g., "Fall", "Spring", "Summer")
     * @param year        Year
     * @return The ID of the created FCAR, or null if creation failed
     */
    public String createFCAR(String courseId, String professorId, String semester, int year) {
        try {
            // Generate a unique ID for the FCAR
            String fcarId = "FCAR-" + UUID.randomUUID();

            // Create a new FCAR object
            FCAR fcar = new FCAR(fcarId, courseId, professorId, semester, year);

            // Store the FCAR in the map
            this.fcarMap.put(fcarId, fcar);

            // Also store in cache
            this.fcarCache.put(fcarId, fcar);

            return fcarId;
        } catch (Exception e) {
            // Log the error
            System.err.println("Error creating FCAR: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get an FCAR by its ID
     * 
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public FCAR getFCAR(String fcarId) {
        // Check the cache first
        if (this.fcarCache.containsKey(fcarId)) {
            return this.fcarCache.get(fcarId);
        }

        // If not in cache, get from the main storage
        FCAR fcar = this.fcarMap.get(fcarId);

        // If found, add to cache
        if (fcar != null) {
            this.fcarCache.put(fcarId, fcar);
        }

        return fcar;
    }

    /**
     * Update an existing FCAR
     * 
     * @param fcar The FCAR object to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateFCAR(FCAR fcar) {
        try {
            if (fcar == null || fcar.getFcarId() == null) {
                return false;
            }

            // Check if the FCAR exists
            if (!this.fcarMap.containsKey(fcar.getFcarId())) {
                return false;
            }

            // Update the FCAR in the map
            this.fcarMap.put(fcar.getFcarId(), fcar);

            // Update the cache
            this.fcarCache.put(fcar.getFcarId(), fcar);

            return true;
        } catch (Exception e) {
            // Log the error
            System.err.println("Error updating FCAR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an FCAR by its ID
     * 
     * @param fcarId ID of the FCAR to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteFCAR(String fcarId) {
        try {
            if (fcarId == null || !this.fcarMap.containsKey(fcarId)) {
                return false;
            }

            // Remove the FCAR from the map
            this.fcarMap.remove(fcarId);

            // Remove from cache
            this.fcarCache.remove(fcarId);

            return true;
        } catch (Exception e) {
            // Log the error
            System.err.println("Error deleting FCAR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Submit an FCAR for review
     * 
     * @param fcarId ID of the FCAR to submit
     * @return true if the submission was successful, false otherwise
     */
    public boolean submitFCAR(String fcarId) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        boolean result = fcar.submit();

        // Update the FCAR if submission was successful
        if (result) {
            this.updateFCAR(fcar);
        }

        return result;
    }

    /**
     * Approve an FCAR
     * 
     * @param fcarId ID of the FCAR to approve
     * @return true if the approval was successful, false otherwise
     */
    public boolean approveFCAR(String fcarId) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        boolean result = fcar.approve();

        // Update the FCAR if approval was successful
        if (result) {
            this.updateFCAR(fcar);
        }

        return result;
    }

    /**
     * Reject an FCAR
     * 
     * @param fcarId ID of the FCAR to reject
     * @return true if the rejection was successful, false otherwise
     */
    public boolean rejectFCAR(String fcarId) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        boolean result = fcar.reject();

        // Update the FCAR if rejection was successful
        if (result) {
            this.updateFCAR(fcar);
        }

        return result;
    }

    /**
     * Return an FCAR to draft status
     *
     * @param fcarId ID of the FCAR to return to draft
     */
    public void returnFCARToDraft(String fcarId) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return;
        }

        boolean result = fcar.returnToDraft();

        // Update the FCAR if the operation was successful
        if (result) {
            this.updateFCAR(fcar);
        }

    }

    /**
     * Add a student outcome to an FCAR
     * 
     * @param fcarId           ID of the FCAR
     * @param outcomeId        ID of the outcome
     * @param achievementLevel Achievement level (1-5)
     * @return true if the operation was successful, false otherwise
     */
    public boolean addStudentOutcome(String fcarId, String outcomeId, int achievementLevel) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        fcar.addStudentOutcome(outcomeId, achievementLevel);
        return this.updateFCAR(fcar);
    }

    /**
     * Add an assessment method to an FCAR
     * 
     * @param fcarId      ID of the FCAR
     * @param methodId    ID of the method
     * @param description Description of the method
     * @return true if the operation was successful, false otherwise
     */
    public boolean addAssessmentMethod(String fcarId, String methodId, String description) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        fcar.addAssessmentMethod(methodId, description);
        return this.updateFCAR(fcar);
    }

    /**
     * Add an improvement action to an FCAR
     * 
     * @param fcarId      ID of the FCAR
     * @param actionId    ID of the action
     * @param description Description of the action
     * @return true if the operation was successful, false otherwise
     */
    public boolean addImprovementAction(String fcarId, String actionId, String description) {
        FCAR fcar = this.getFCAR(fcarId);

        if (fcar == null) {
            return false;
        }

        fcar.addImprovementAction(actionId, description);
        return this.updateFCAR(fcar);
    }

    /**
     * Get all FCARs for a course
     * 
     * @param courseId ID of the course
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseId) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : this.fcarMap.entrySet()) {
            if (entry.getValue().getCourseId().equals(courseId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        // Update the cache with the retrieved FCARs
        this.fcarCache.putAll(result);

        return new ArrayList<>(result.values());
    }

    /**
     * Get all FCARs created by a professor
     * 
     * @param professorId ID of the professor
     * @return List of FCARs created by the professor
     */
    public List<FCAR> getFCARsByProfessor(String professorId) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : this.fcarMap.entrySet()) {
            if (entry.getValue().getProfessorId().equals(professorId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        // Update the cache with the retrieved FCARs
        this.fcarCache.putAll(result);

        return new ArrayList<>(result.values());
    }

    /**
     * Get all FCARs for a specific semester and year
     * 
     * @param semester Semester (e.g., "Fall", "Spring", "Summer")
     * @param year     Year
     * @return List of FCARs for the semester and year
     */
    public List<FCAR> getFCARsBySemester(String semester, int year) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : this.fcarMap.entrySet()) {
            FCAR fcar = entry.getValue();
            if (fcar.getSemester().equals(semester) && fcar.getYear() == year) {
                result.put(entry.getKey(), fcar);
            }
        }

        // Update the cache with the retrieved FCARs
        this.fcarCache.putAll(result);

        return new ArrayList<>(result.values());
    }

    /**
     * Get all FCARs
     * 
     * @return List of all FCAR objects
     */
    public List<FCAR> getAllFCARs() {
        return new ArrayList<>(this.fcarMap.values());
    }

    /**
     * Clear the FCAR cache
     */
    public void clearCache() {
        this.fcarCache.clear();
    }
}
