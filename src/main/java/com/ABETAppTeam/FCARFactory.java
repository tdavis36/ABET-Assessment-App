package com.ABETAppTeam;

import java.util.*;

/**
 * FCARFactory class for the ABET Assessment Application
 *
 * This class is responsible for creating and managing FCAR (Faculty Course
 * Assessment Report) objects. It follows the Factory design pattern to
 * centralize FCAR creation logic.
 */
public class FCARFactory {
    // Store created FCARs in memory (in a real application, this would be a database)
    private static Map<String, FCAR> fcarMap = new HashMap<>();

    /**
     * Create a new FCAR for a course.
     *
     * @param courseId    ID of the course
     * @param professorId ID of the professor creating the FCAR
     * @param semester    Semester (e.g., "Fall", "Spring", "Summer")
     * @param year        Year
     * @return The created FCAR object
     */
    public static FCAR createFCAR(String courseId, String professorId, String semester, int year) {
        // Generate a unique ID for the FCAR
        String fcarId = "FCAR-" + UUID.randomUUID().toString();

        // Create a new FCAR object
        FCAR fcar = new FCAR(fcarId, courseId, professorId, semester, year);

        // Store the FCAR in the map
        fcarMap.put(fcarId, fcar);

        return fcar;
    }

    /**
     * Create or update an FCAR, depending on whether it already has an ID.
     * This method is often expected by a "SessionStorageHandler" or similar code.
     *
     * @param fcar The FCAR to save (create or update)
     * @return The saved FCAR, possibly with a newly assigned ID
     */
    public static FCAR save(FCAR fcar) {
        if (fcar == null) {
            return null;
        }

        // If there's no FCAR ID or it's blank, treat this as a new FCAR
        if (fcar.getFcarId() == null || fcar.getFcarId().trim().isEmpty()) {
            return createFCAR(fcar.getCourseId(), fcar.getProfessorId(),
                    fcar.getSemester(), fcar.getYear());
        } else {
            // Otherwise, update the existing FCAR
            updateFCAR(fcar);
            return fcar;
        }
    }

    /**
     * Get an FCAR by its ID.
     *
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public static FCAR getFCAR(String fcarId) {
        return fcarMap.get(fcarId);
    }

    /**
     * Update an existing FCAR.
     *
     * @param fcar The FCAR object to update
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateFCAR(FCAR fcar) {
        if (fcar == null || fcar.getFcarId() == null) {
            return false;
        }

        // Check if the FCAR exists
        if (!fcarMap.containsKey(fcar.getFcarId())) {
            return false;
        }

        // Update the FCAR in the map
        fcarMap.put(fcar.getFcarId(), fcar);
        return true;
    }

    /**
     * A simpler "update" method name, often expected by session or service layers.
     * Delegates to updateFCAR(...) for the actual update.
     *
     * @param fcar The FCAR object to update
     * @return true if the update was successful, false otherwise
     */
    public static boolean update(FCAR fcar) {
        return updateFCAR(fcar);
    }

    /**
     * Delete an FCAR by its ID.
     *
     * @param fcarId ID of the FCAR to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean deleteFCAR(String fcarId) {
        if (fcarId == null || !fcarMap.containsKey(fcarId)) {
            return false;
        }

        // Remove the FCAR from the map
        fcarMap.remove(fcarId);
        return true;
    }

    /**
     * A simpler "delete" method name, often expected by session or service layers.
     * Delegates to deleteFCAR(...) for the actual deletion.
     *
     * @param fcarId ID of the FCAR to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean delete(String fcarId) {
        return deleteFCAR(fcarId);
    }

    /**
     * Returns all FCARs as a Map of <fcarId, FCAR>.
     */
    public static Map<String, FCAR> getAllFCARs() {
        return fcarMap;
    }

    /**
     * Helper method: Returns all FCARs as a List.
     */
    public static List<FCAR> getAllFCARsAsList() {
        return new ArrayList<>(fcarMap.values());
    }

    /**
     * Get all FCARs for a course.
     *
     * @param courseId ID of the course
     * @return Map of FCAR IDs to FCAR objects for the course
     */
    public static Map<String, FCAR> getFCARsForCourse(String courseId) {
        Map<String, FCAR> result = new HashMap<>();
        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            if (entry.getValue().getCourseId().equals(courseId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Get all FCARs created by a professor.
     *
     * @param professorId ID of the professor
     * @return Map of FCAR IDs to FCAR objects created by the professor
     */
    public static Map<String, FCAR> getFCARsByProfessor(String professorId) {
        Map<String, FCAR> result = new HashMap<>();
        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            if (entry.getValue().getProfessorId().equals(professorId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Get all FCARs for a specific semester and year.
     *
     * @param semester Semester (e.g., "Fall", "Spring", "Summer")
     * @param year     Year
     * @return Map of FCAR IDs to FCAR objects for the semester and year
     */
    public static Map<String, FCAR> getFCARsBySemester(String semester, int year) {
        Map<String, FCAR> result = new HashMap<>();
        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            FCAR fcar = entry.getValue();
            if (fcar.getSemester().equals(semester) && fcar.getYear() == year) {
                result.put(entry.getKey(), fcar);
            }
        }
        return result;
    }
}