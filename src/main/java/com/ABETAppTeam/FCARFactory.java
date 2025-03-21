package com.ABETAppTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FCARFactory class for the ABET Assessment Application
 * 
 * This class is responsible for creating and managing FCAR (Faculty Course
 * Assessment Report) objects. It follows the Factory design pattern to
 * centralize FCAR creation logic.
 */
public class FCARFactory {
    // Store created FCARs in memory (in a real application, this would be a
    // database)
    private static Map<String, FCAR> fcarMap = new HashMap<>();

    /**
     * Create a new FCAR for a course
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
     * Get an FCAR by its ID
     * 
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public static FCAR getFCAR(String fcarId) {
        return fcarMap.get(fcarId);
    }

    /**
     * Update an existing FCAR
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
     * Delete an FCAR by its ID
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
     * Get all FCARs for a course
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
     * Get all FCARs created by a professor
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
     * Get all FCARs for a specific semester and year
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

