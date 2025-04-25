package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;
import java.util.List;

/**
 * Interface for FCAR repository operations
 * This interface defines the operations for FCAR persistence
 */
public interface IFCARRepository {
    /**
     * Find an FCAR by its ID
     *
     * @param fcarId The ID of the FCAR to find
     * @return The FCAR if found, null otherwise
     */
    FCAR findById(int fcarId);

    /**
     * Find all FCARs
     *
     * @return List of all FCARs
     */
    List<FCAR> findAll();

    /**
     * Find FCARs for a specific course
     *
     * @param courseCode The course code
     * @return List of FCARs for the course
     */
    List<FCAR> findByCourseCode(String courseCode);

    /**
     * Find FCARs created by a specific instructor
     *
     * @param instructorId The instructor ID
     * @return List of FCARs created by the instructor
     */
    List<FCAR> findByInstructorId(int instructorId);

    /**
     * Save a new FCAR or update an existing one
     *
     * @param fcar The FCAR to save
     * @return The saved FCAR with its ID (if new), or null if the save failed
     */
    FCAR save(FCAR fcar);

    /**
     * Update an existing FCAR
     *
     * @param fcar The FCAR to update
     * @return true if the update was successful, false otherwise
     */
    boolean update(FCAR fcar);

    /**
     * Delete an FCAR
     *
     * @param fcarId The ID of the FCAR to delete
     * @return true if the deletion was successful, false otherwise
     */
    boolean delete(int fcarId);

    /**
     * Find FCARs for a specific semester and year
     *
     * @param semester The semester (e.g., "Fall", "Spring", "Summer")
     * @param year The year
     * @return List of FCARs for the specified semester and year
     */
    List<FCAR> findBySemesterAndYear(String semester, int year);
}