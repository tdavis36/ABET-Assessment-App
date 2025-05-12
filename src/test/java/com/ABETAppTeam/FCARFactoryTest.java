package com.ABETAppTeam;

import com.ABETAppTeam.model.FCAR;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FCARFactoryTest {

    @Test
    void testGetFCARInvalidId() {
        assertNull(FCARFactory.getFCAR("not-a-number"));
    }

    @Test
    void testDeleteFCARInvalidId() {
        assertFalse(FCARFactory.deleteFCAR("NaN"));
    }

    @Test
    void testGetFCARsByProfessorInvalid() {
        List<FCAR> result = FCARFactory.getFCARsByProfessor("NaN");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFCARsBySemesterEmpty() {
        List<FCAR> sem = FCARFactory.getFCARsBySemester("Fall", 2099);
        assertNotNull(sem);
        assertTrue(sem.isEmpty());
    }

    @Test
    void testGetAllFCARsAlias() {
        List<FCAR> all1 = FCARFactory.getAllFCARs();
        List<FCAR> all2 = FCARFactory.getAllFCARsAsList();
        assertSame(all1, all2);
    }

    @Test
    void testCreateThenRetrieveFCAR() {
        // createFCAR delegates to repository; if your H2/Testcontainers is up, this will persist
        FCAR created = FCARFactory.createFCAR("CS101", 42, "Spring", 2025);
        assertNotNull(created, "createFCAR should return a non‐null FCAR");
        FCAR fetched = FCARFactory.getFCAR(String.valueOf(created.getId()));
        assertNotNull(fetched, "Should retrieve the same FCAR by ID");
        assertEquals("CS101", fetched.getCourseCode());
        assertEquals(42, fetched.getProfessorId());
    }
}
