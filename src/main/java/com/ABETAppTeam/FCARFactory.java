package com.ABETAppTeam;

import com.ABETAppTeam.repository.IFCARRepository;
import com.ABETAppTeam.repository.FCARRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * FCARFactory class for the ABET Assessment Application
 * If you want to keep the "factory" name, it now just delegates to the repository
 * for all persistence. Otherwise, you can remove it if it's no longer necessary.
 */
public abstract class FCARFactory {
    // Reference the repository (could also be injected?)
    private static final IFCARRepository repository = new FCARRepository();

    // Create & save a new FCAR
    public static FCAR createFCAR(String courseId, String professorId, String semester, int year) {
        // fcarId is null => the repository will assign an auto-generated ID
        FCAR fcar = new FCAR(null, courseId, professorId, semester, year);
        return repository.save(fcar);
    }

    // "Save" can either insert or update, depending on whether FCAR has an ID
    public static FCAR save(FCAR fcar) {
        return repository.save(fcar);
    }

    public static FCAR getFCAR(String fcarId) {
        // parse string ID
        try {
            int id = Integer.parseInt(fcarId);
            return repository.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean updateFCAR(FCAR fcar) {
        return repository.update(fcar);
    }

    public static boolean deleteFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return repository.delete(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Example "getAll"
    public static List<FCAR> getAllFCARs() {
        return repository.findAll();
    }

    // If you had a method that got FCARs by course or by professor:
    public static List<FCAR> getFCARsForCourse(String courseId) {
        return repository.findByCourseCode(courseId);
    }

    public static List<FCAR> getFCARsByProfessor(String professorId) {
        try {
            int id = Integer.parseInt(professorId);
            return repository.findByInstructorId(id);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    /**
     * Get FCARs for a specific semester and year
     *
     * @param semester The semester (e.g., "Fall", "Spring", "Summer")
     * @param year The year
     * @return List of FCARs for the specified semester and year
     */
    public static List<FCAR> getFCARsBySemester(String semester, int year) {
        List<FCAR> allFCARs = getAllFCARs();
        List<FCAR> semesterFCARs = new ArrayList<>();

        for (FCAR fcar : allFCARs) {
            if (fcar.getSemester().equals(semester) && fcar.getYear() == year) {
                semesterFCARs.add(fcar);
            }
        }

        return semesterFCARs;
    }

    /**
     * Get all FCARs as a list (alias for getAllFCARs for compatibility)
     *
     * @return List of all FCARs
     */
    public static List<FCAR> getAllFCARsAsList() {
        return getAllFCARs();
    }

    protected abstract IFCARRepository getRepository();
    //  Add more here as necessary
}