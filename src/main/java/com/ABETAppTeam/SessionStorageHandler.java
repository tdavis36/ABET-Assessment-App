package com.ABETAppTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

/**
 * SessionStorageHandler for managing session-based FCAR data.
 *
 * This class serves as a session-based cache that delegates to FCARController
 * for all operations. It ensures that any changes made to FCARs are properly
 * synchronized with the main storage in FCARController.
 *
 * <p>
 * Usage Example in a Servlet:
 * 
 * <pre>
 * // Store a reference to a newly created FCAR in session
 * FCARController controller = FCARController.getInstance();
 * String fcarId = controller.createFCAR("CS320", "Prof01", "Spring", 2025);
 * SessionStorageHandler.cacheFCAR(request.getSession(), fcarId);
 *
 * // Retrieve the FCAR by ID (will use the controller as the source of truth)
 * FCAR retrieved = SessionStorageHandler.getFCAR(request.getSession(), fcarId);
 * </pre>
 */
public class SessionStorageHandler {

    private static final String FCAR_SESSION_KEY = "fcarCachedIds";

    /**
     * Caches an FCAR reference in the session.
     * This doesn't store the actual FCAR object, just its ID for later retrieval
     * from FCARController.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID to cache
     */
    public static void cacheFCAR(HttpSession session, String fcarId) {
        @SuppressWarnings("unchecked")
        List<String> cachedIds = (List<String>) session.getAttribute(FCAR_SESSION_KEY);
        if (cachedIds == null) {
            cachedIds = new java.util.ArrayList<>();
        }

        if (!cachedIds.contains(fcarId)) {
            cachedIds.add(fcarId);
        }

        session.setAttribute(FCAR_SESSION_KEY, cachedIds);
    }

    /**
     * Retrieves an FCAR by its ID.
     * This delegates to FCARController to ensure we always get the most up-to-date
     * version.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID
     * @return The retrieved FCAR object, or null if not found
     */
    public static FCAR getFCAR(HttpSession session, String fcarId) {
        // Always delegate to FCARController for the actual FCAR data
        return FCARController.getInstance().getFCAR(fcarId);
    }

    /**
     * Retrieves all FCARs cached in the session.
     * This delegates to FCARController to ensure we always get the most up-to-date
     * versions.
     *
     * @param session The HttpSession object
     * @return A map of FCAR IDs to FCAR objects
     */
    @SuppressWarnings("unchecked")
    public static Map<String, FCAR> getCachedFCARs(HttpSession session) {
        List<String> cachedIds = (List<String>) session.getAttribute(FCAR_SESSION_KEY);
        Map<String, FCAR> result = new HashMap<>();

        if (cachedIds != null) {
            FCARController controller = FCARController.getInstance();
            for (String id : cachedIds) {
                FCAR fcar = controller.getFCAR(id);
                if (fcar != null) {
                    result.put(id, fcar);
                }
            }
        }

        return result;
    }

    /**
     * Removes an FCAR reference from the session cache.
     * This doesn't delete the actual FCAR from FCARController.
     *
     * @param session The HttpSession object
     * @param fcarId  The FCAR ID to remove from cache
     */
    public static void uncacheFCAR(HttpSession session, String fcarId) {
        @SuppressWarnings("unchecked")
        List<String> cachedIds = (List<String>) session.getAttribute(FCAR_SESSION_KEY);

        if (cachedIds != null) {
            cachedIds.remove(fcarId);
            session.setAttribute(FCAR_SESSION_KEY, cachedIds);
        }
    }

    /**
     * Clears all FCAR references from the session cache.
     * This doesn't delete the actual FCARs from FCARController.
     *
     * @param session The HttpSession object
     */
    public static void clearCache(HttpSession session) {
        session.removeAttribute(FCAR_SESSION_KEY);
    }

    /**
     * Updates an FCAR in the main storage.
     * This delegates to FCARController to ensure proper synchronization.
     *
     * @param session The HttpSession object
     * @param fcar    The FCAR object to update
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateFCAR(HttpSession session, FCAR fcar) {
        if (fcar == null || fcar.getFcarId() == null) {
            return false;
        }

        // Ensure this FCAR is cached in the session
        cacheFCAR(session, fcar.getFcarId());

        // Delegate to FCARController for the actual update
        return FCARController.getInstance().updateFCAR(fcar);
    }
}
