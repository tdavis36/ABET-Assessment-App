package com.ABETAppTeam;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

public class SessionStorageHandler {

    // Key used to store the FCAR cache in the session.
    private static final String FCAR_CACHE_SESSION_KEY = "fcarCache";

    /**
     * Retrieves FCARs from the session cache.
     * If the cache is missing, it loads FCARs from FCARFactory,
     * converts the result to a List, and caches it.
     *
     * @param session the current HttpSession.
     * @return a List of FCAR objects.
     */
    public List<FCAR> getFCARs(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<FCAR> cachedFCARs = (List<FCAR>) session.getAttribute(FCAR_CACHE_SESSION_KEY);
        if (cachedFCARs == null) {
            // Load FCARs from primary storage (FCARFactory)
            cachedFCARs = FCARFactory.getAllFCARsAsList();
            session.setAttribute(FCAR_CACHE_SESSION_KEY, cachedFCARs);
        }
        return cachedFCARs;
    }

    /**
     * Saves a new FCAR using FCARFactory and invalidates the session cache.
     *
     * @param session the current HttpSession.
     * @param fcar    the FCAR to save.
     * @return the saved FCAR.
     */
    public FCAR saveFCAR(HttpSession session, FCAR fcar) {
        FCAR savedFCAR = FCARFactory.save(fcar);
        invalidateCache(session);
        return savedFCAR;
    }

    /**
     * Updates an existing FCAR using FCARFactory and invalidates the session cache.
     *
     * @param session the current HttpSession.
     * @param fcar    the FCAR to update.
     * @return true if update succeeded, false otherwise.
     */
    public boolean updateFCAR(HttpSession session, FCAR fcar) {
        boolean success = FCARFactory.update(fcar);
        if (success) {
            invalidateCache(session);
        }
        return success;
    }

    /**
     * Deletes an FCAR using FCARFactory and invalidates the session cache.
     *
     * @param session the current HttpSession.
     * @param fcarId  the ID of the FCAR to delete.
     * @return true if deletion succeeded, false otherwise.
     */
    public boolean deleteFCAR(HttpSession session, int fcarId) {
        boolean success = FCARFactory.delete(String.valueOf(fcarId));
        if (success) {
            invalidateCache(session);
        }
        return success;
    }

    /**
     * Invalidates the session cache by removing the cached FCAR list.
     *
     * @param session the current HttpSession.
     */
    public void invalidateCache(HttpSession session) {
        session.removeAttribute(FCAR_CACHE_SESSION_KEY);
    }
}