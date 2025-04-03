package com.ABETAppTeam;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

/**
 * SessionStorageHandler for managing session-based FCAR data.
 *
 * This class provides methods to store, retrieve, and clear FCAR data in the
 * session.
 *
 * <p>
 * Usage Example in a Servlet:
 * 
 * <pre>
 * // Store a newly created FCAR in session
 * FCARController controller = FCARController.getInstance();
 * String fcarId = controller.createFCAR("CS320", "Prof01", "Spring", 2025);
 * FCAR fcar = controller.getFCAR(fcarId);
 * SessionStorageHandler.storeFCAR(request.getSession(), fcarId, fcar);
 *
 * // Retrieve the FCAR by ID
 * FCAR retrieved = SessionStorageHandler.getFCAR(request.getSession(), fcarId);
 * </pre>
 */
public class SessionStorageHandler {

    private static final String FCAR_SESSION_KEY = "fcarData";

    /**
     * Stores FCAR data in the session.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID
     * @param fcar    The FCAR object to store
     */
    public static void storeFCAR(HttpSession session, String fcarId, FCAR fcar) {
        Map<String, FCAR> fcarData = getStoredFCARs(session);
        fcarData.put(fcarId, fcar);
        session.setAttribute(FCAR_SESSION_KEY, fcarData);
    }

    /**
     * Retrieves an FCAR from the session by its ID.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID
     * @return The retrieved FCAR object, or null if not found
     */
    public static FCAR getFCAR(HttpSession session, String fcarId) {
        Map<String, FCAR> fcarData = getStoredFCARs(session);
        return fcarData.get(fcarId);
    }

    /**
     * Retrieves all stored FCARs from the session.
     *
     * @param session The HttpSession object
     * @return A map of FCAR IDs to FCAR objects
     */
    @SuppressWarnings("unchecked")
    public static Map<String, FCAR> getStoredFCARs(HttpSession session) {
        Object data = session.getAttribute(FCAR_SESSION_KEY);
        if (data instanceof Map) {
            return (Map<String, FCAR>) data;
        }
        return new HashMap<>();
    }

    /**
     * Removes an FCAR from the session.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID to remove
     */
    public static void removeFCAR(HttpSession session, String fcarId) {
        Map<String, FCAR> fcarData = getStoredFCARs(session);
        fcarData.remove(fcarId);
        session.setAttribute(FCAR_SESSION_KEY, fcarData);
    }

    /**
     * Clears all FCAR data from the session.
     *
     * @param session The HttpSession object
     */
    public static void clearAllFCARs(HttpSession session) {
        session.removeAttribute(FCAR_SESSION_KEY);
    }
}
