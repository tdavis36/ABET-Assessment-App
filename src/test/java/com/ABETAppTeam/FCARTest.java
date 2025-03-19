package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FCARTest {

    @Test
    void testGetAssessmentMethodsAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id200", "course200", "professor200", "Fall", 2024);

        // Act
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAssessmentMethodsAfterAddingSingleMethod() {
        // Arrange
        FCAR fcar = new FCAR("id201", "course201", "professor201", "Spring", 2025);
        fcar.addAssessmentMethod("Method1", "Description1");

        // Act
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Description1", result.get("Method1"));
    }

    @Test
    void testGetAssessmentMethodsAfterAddingMultipleMethods() {
        // Arrange
        FCAR fcar = new FCAR("id202", "course202", "professor202", "Summer", 2026);
        fcar.addAssessmentMethod("Method1", "Description1");
        fcar.addAssessmentMethod("Method2", "Description2");

        // Act
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Description1", result.get("Method1"));
        assertEquals("Description2", result.get("Method2"));
    }

    @Test
    void testAddAssessmentMethodWhenMethodIdIsNull() {
        // Arrange
        FCAR fcar = new FCAR("id203", "course203", "professor203", "Winter", 2024);

        // Act
        fcar.addAssessmentMethod(null, "Description1");

        // Assert
        assertNull(fcar.getAssessmentMethods().get(null));
        assertTrue(fcar.getAssessmentMethods().isEmpty());
    }

    @Test
    void testAddAssessmentMethodWhenDescriptionIsNull() {
        // Arrange
        FCAR fcar = new FCAR("id204", "course204", "professor204", "Spring", 2025);

        // Act
        fcar.addAssessmentMethod("Method1", null);

        // Assert
        assertEquals(1, fcar.getAssessmentMethods().size());
        assertNull(fcar.getAssessmentMethods().get("Method1"));
    }

    @Test
    void testSetStudentOutcomesWithValidMap() {
        // Arrange
        FCAR fcar = new FCAR("id54", "course54", "professor54", "Fall", 2025);
        Map<String, Integer> newOutcomes = Map.of("Outcome1", 85, "Outcome2", 90);

        // Act
        fcar.setStudentOutcomes(newOutcomes);
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertEquals(2, result.size());
        assertEquals(85, result.get("Outcome1"));
        assertEquals(90, result.get("Outcome2"));
    }

    @Test
    void testSetStudentOutcomesWithEmptyMap() {
        // Arrange
        FCAR fcar = new FCAR("id55", "course55", "professor55", "Spring", 2025);
        Map<String, Integer> emptyOutcomes = Map.of();

        // Act
        fcar.setStudentOutcomes(emptyOutcomes);
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testSetStudentOutcomesWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id56", "course56", "professor56", "Winter", 2024);

        // Act
        fcar.setStudentOutcomes(null);
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetSemesterAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id34", "course34", "professor34", "Spring", 2024);

        // Act
        String result = fcar.getSemester();

        // Assert
        assertEquals("Spring", result);
    }

    @Test
    void testSetSemesterWithValidSemester() {
        // Arrange
        FCAR fcar = new FCAR("id35", "course35", "professor35", "Summer", 2023);

        // Act
        fcar.setSemester("Fall");
        String result = fcar.getSemester();

        // Assert
        assertEquals("Fall", result);
    }

    @Test
    void testSetSemesterWithNullValue() {
        // Arrange
        FCAR fcar = new FCAR("id36", "course36", "professor36", "Winter", 2023);

        // Act
        fcar.setSemester(null);
        String result = fcar.getSemester();

        // Assert
        assertNull(result);
    }

    /**
     * The FCAR class represents a Faculty Course Assessment Report that can transition through
     * various states such as "Draft", "Submitted", etc. The `submit` method transitions the
     * FCAR from "Draft" to "Submitted" if it is in the correct starting state.
     */

    @Test
    void testSubmitFromDraftToSubmitted() {
        // Arrange
        FCAR fcar = new FCAR("id1", "course1", "professor1", "Fall", 2023);
        fcar.setStatus("Draft");

        // Act
        boolean result = fcar.submit();

        // Assert
        assertTrue(result);
        assertEquals("Submitted", fcar.getStatus());
    }

    @Test
    void testSubmitWhenAlreadySubmitted() {
        // Arrange
        FCAR fcar = new FCAR("id2", "course2", "professor2", "Spring", 2023);
        fcar.setStatus("Submitted");

        // Act
        boolean result = fcar.submit();

        // Assert
        assertFalse(result);
        assertEquals("Submitted", fcar.getStatus());
    }

    @Test
    void testSubmitWhenApproved() {
        // Arrange
        FCAR fcar = new FCAR("id3", "course3", "professor3", "Winter", 2023);
        fcar.setStatus("Approved");

        // Act
        boolean result = fcar.submit();

        // Assert
        assertFalse(result);
        assertEquals("Approved", fcar.getStatus());
    }

    @Test
    void testSubmitWhenRejected() {
        // Arrange
        FCAR fcar = new FCAR("id4", "course4", "professor4", "Summer", 2023);
        fcar.setStatus("Rejected");

        // Act
        boolean result = fcar.submit();

        // Assert
        assertFalse(result);
        assertEquals("Rejected", fcar.getStatus());
    }

    @Test
    void testApproveFromSubmittedToApproved() {
        // Arrange
        FCAR fcar = new FCAR("id5", "course5", "professor5", "Fall", 2023);
        fcar.setStatus("Submitted");

        // Act
        boolean result = fcar.approve();

        // Assert
        assertTrue(result);
        assertEquals("Approved", fcar.getStatus());
    }

    @Test
    void testApproveWhenInDraft() {
        // Arrange
        FCAR fcar = new FCAR("id6", "course6", "professor6", "Spring", 2023);
        fcar.setStatus("Draft");

        // Act
        boolean result = fcar.approve();

        // Assert
        assertFalse(result);
        assertEquals("Draft", fcar.getStatus());
    }

    @Test
    void testApproveWhenAlreadyApproved() {
        // Arrange
        FCAR fcar = new FCAR("id7", "course7", "professor7", "Winter", 2023);
        fcar.setStatus("Approved");

        // Act
        boolean result = fcar.approve();

        // Assert
        assertFalse(result);
        assertEquals("Approved", fcar.getStatus());
    }

    @Test
    void testApproveWhenRejected() {
        // Arrange
        FCAR fcar = new FCAR("id8", "course8", "professor8", "Summer", 2023);
        fcar.setStatus("Rejected");

        // Act
        boolean result = fcar.approve();

        // Assert
        assertFalse(result);
        assertEquals("Rejected", fcar.getStatus());
    }

    @Test
    void testRejectFromSubmittedToRejected() {
        // Arrange
        FCAR fcar = new FCAR("id9", "course9", "professor9", "Fall", 2023);
        fcar.setStatus("Submitted");

        // Act
        boolean result = fcar.reject();

        // Assert
        assertTrue(result);
        assertEquals("Rejected", fcar.getStatus());
    }

    @Test
    void testRejectWhenInDraft() {
        // Arrange
        FCAR fcar = new FCAR("id10", "course10", "professor10", "Spring", 2023);
        fcar.setStatus("Draft");

        // Act
        boolean result = fcar.reject();

        // Assert
        assertFalse(result);
        assertEquals("Draft", fcar.getStatus());
    }

    @Test
    void testRejectWhenAlreadyRejected() {
        // Arrange
        FCAR fcar = new FCAR("id11", "course11", "professor11", "Winter", 2023);
        fcar.setStatus("Rejected");

        // Act
        boolean result = fcar.reject();

        // Assert
        assertFalse(result);
        assertEquals("Rejected", fcar.getStatus());
    }

    @Test
    void testRejectWhenAlreadyApproved() {
        // Arrange
        FCAR fcar = new FCAR("id12", "course12", "professor12", "Summer", 2023);
        fcar.setStatus("Approved");

        // Act
        boolean result = fcar.reject();

        // Assert
        assertFalse(result);
        assertEquals("Approved", fcar.getStatus());
    }

    @Test
    void testReturnToDraftFromSubmitted() {
        // Arrange
        FCAR fcar = new FCAR("id13", "course13", "professor13", "Fall", 2023);
        fcar.setStatus("Submitted");

        // Act
        boolean result = fcar.returnToDraft();

        // Assert
        assertTrue(result);
        assertEquals("Draft", fcar.getStatus());
    }

    @Test
    void testReturnToDraftFromRejected() {
        // Arrange
        FCAR fcar = new FCAR("id14", "course14", "professor14", "Spring", 2023);
        fcar.setStatus("Rejected");

        // Act
        boolean result = fcar.returnToDraft();

        // Assert
        assertTrue(result);
        assertEquals("Draft", fcar.getStatus());
    }

    @Test
    void testReturnToDraftWhenInDraft() {
        // Arrange
        FCAR fcar = new FCAR("id15", "course15", "professor15", "Winter", 2023);
        fcar.setStatus("Draft");

        // Act
        boolean result = fcar.returnToDraft();

        // Assert
        assertFalse(result);
        assertEquals("Draft", fcar.getStatus());
    }

    @Test
    void testReturnToDraftWhenApproved() {
        // Arrange
        FCAR fcar = new FCAR("id16", "course16", "professor16", "Summer", 2023);
        fcar.setStatus("Approved");

        // Act
        boolean result = fcar.returnToDraft();

        // Assert
        assertFalse(result);
        assertEquals("Approved", fcar.getStatus());
    }

    @Test
    void testGetFcarIdAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id17", "course17", "professor17", "Fall", 2023);

        // Act
        String result = fcar.getFcarId();

        // Assert
        assertEquals("id17", result);
    }

    @Test
    void testGetFcarIdAfterSetFcarIdUpdate() {
        // Arrange
        FCAR fcar = new FCAR("id18", "course18", "professor18", "Spring", 2023);
        fcar.setFcarId("updatedId18");

        // Act
        String result = fcar.getFcarId();

        // Assert
        assertEquals("updatedId18", result);
    }

    @Test
    void testSetFcarIdWithValidNewId() {
        // Arrange
        FCAR fcar = new FCAR("id19", "course19", "professor19", "Fall", 2023);

        // Act
        fcar.setFcarId("newId19");
        String result = fcar.getFcarId();

        // Assert
        assertEquals("newId19", result);
    }

    @Test
    void testSetFcarIdWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id20", "course20", "professor20", "Winter", 2023);

        // Act
        fcar.setFcarId(null);
        String result = fcar.getFcarId();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCourseIdAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id21", "course21", "professor21", "Fall", 2023);

        // Act
        String result = fcar.getCourseId();

        // Assert
        assertEquals("course21", result);
    }

    @Test
    void testSetCourseIdWithValidNewCourseId() {
        // Arrange
        FCAR fcar = new FCAR("id22", "course22", "professor22", "Spring", 2023);

        // Act
        fcar.setCourseId("updatedCourse22");
        String result = fcar.getCourseId();

        // Assert
        assertEquals("updatedCourse22", result);
    }

    @Test
    void testSetCourseIdWithEmptyString() {
        // Arrange
        FCAR fcar = new FCAR("id24", "course24", "professor24", "Winter", 2024);

        // Act
        fcar.setCourseId("");
        String result = fcar.getCourseId();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testSetCourseIdWithWhitespace() {
        // Arrange
        FCAR fcar = new FCAR("id25", "course25", "professor25", "Fall", 2024);

        // Act
        fcar.setCourseId("   ");
        String result = fcar.getCourseId();

        // Assert
        assertEquals("   ", result);
    }

    @Test
    void testSetCourseIdWithSpecialCharacters() {
        // Arrange
        FCAR fcar = new FCAR("id26", "course26", "professor26", "Spring", 2024);

        // Act
        fcar.setCourseId("@#$%&*!123");
        String result = fcar.getCourseId();

        // Assert
        assertEquals("@#$%&*!123", result);
    }

    @Test
    void testSetCourseIdWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id23", "course23", "professor23", "Winter", 2023);

        // Act
        fcar.setCourseId(null);
        String result = fcar.getCourseId();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetProfessorIdAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id27", "course27", "professor27", "Spring", 2024);

        // Act
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("professor27", result);
    }

    @Test
    void testGetProfessorIdAfterSetProfessorIdUpdate() {
        // Arrange
        FCAR fcar = new FCAR("id28", "course28", "professor28", "Fall", 2023);
        fcar.setProfessorId("updatedProfessor28");

        // Act
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("updatedProfessor28", result);
    }

    @Test
    void testSetProfessorIdWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id29", "course29", "professor29", "Winter", 2023);

        // Act
        fcar.setProfessorId(null);
        String result = fcar.getProfessorId();

        // Assert
        assertNull(result);
    }

    @Test
    void testSetProfessorIdWithValidNewProfessorId() {
        // Arrange
        FCAR fcar = new FCAR("id30", "course30", "professor30", "Fall", 2023);

        // Act
        fcar.setProfessorId("newProfessor30");
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("newProfessor30", result);
    }

    @Test
    void testSetProfessorIdWithEmptyString() {
        // Arrange
        FCAR fcar = new FCAR("id31", "course31", "professor31", "Spring", 2024);

        // Act
        fcar.setProfessorId("");
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testSetProfessorIdWithWhitespace() {
        // Arrange
        FCAR fcar = new FCAR("id32", "course32", "professor32", "Fall", 2024);

        // Act
        fcar.setProfessorId("   ");
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("   ", result);
    }

    @Test
    void testSetProfessorIdWithSpecialCharacters() {
        // Arrange
        FCAR fcar = new FCAR("id33", "course33", "professor33", "Spring", 2024);

        // Act
        fcar.setProfessorId("@#$%&*!123");
        String result = fcar.getProfessorId();

        // Assert
        assertEquals("@#$%&*!123", result);
    }

    @Test
    void testGetYearAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id37", "course37", "professor37", "Fall", 2025);

        // Act
        int result = fcar.getYear();

        // Assert
        assertEquals(2025, result);
    }

    @Test
    void testGetYearAfterSetYearUpdate() {
        // Arrange
        FCAR fcar = new FCAR("id38", "course38", "professor38", "Spring", 2024);
        fcar.setYear(2026);

        // Act
        int result = fcar.getYear();

        // Assert
        assertEquals(2026, result);
    }

    @Test
    void testSetYearToValidValue() {
        // Arrange
        FCAR fcar = new FCAR("id39", "course39", "professor39", "Spring", 2024);

        // Act
        fcar.setYear(2025);
        int result = fcar.getYear();

        // Assert
        assertEquals(2025, result);
    }

    @Test
    void testSetYearToPastYear() {
        // Arrange
        FCAR fcar = new FCAR("id40", "course40", "professor40", "Fall", 2024);

        // Act
        fcar.setYear(2000);
        int result = fcar.getYear();

        // Assert
        assertEquals(2000, result);
    }

    @Test
    void testSetYearToCurrentYear() {
        // Arrange
        FCAR fcar = new FCAR("id41", "course41", "professor41", "Summer", 2023);

        // Act
        fcar.setYear(2023);
        int result = fcar.getYear();

        // Assert
        assertEquals(2023, result);
    }

    @Test
    void testSetYearToFutureYear() {
        // Arrange
        FCAR fcar = new FCAR("id42", "course42", "professor42", "Winter", 2024);

        // Act
        fcar.setYear(2050);
        int result = fcar.getYear();

        // Assert
        assertEquals(2050, result);
    }

    @Test
    void testSetYearToInvalidValue() {
        // Arrange
        FCAR fcar = new FCAR("id43", "course43", "professor43", "Spring", 2024);

        // Act
        fcar.setYear(-100);
        int result = fcar.getYear();

        // Assert
        assertEquals(-100, result); // Assume no validation in setter
    }

    @Test
    void testGetStatusAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id44", "course44", "professor44", "Fall", 2023);

        // Act
        String result = fcar.getStatus();

        // Assert
        assertEquals("Draft", result);
    }

    @Test
    void testGetStatusAfterSetStatusUpdate() {
        // Arrange
        FCAR fcar = new FCAR("id45", "course45", "professor45", "Spring", 2024);
        fcar.setStatus("Submitted");

        // Act
        String result = fcar.getStatus();

        // Assert
        assertEquals("Submitted", result);
    }

    @Test
    void testSetStatusToNull() {
        // Arrange
        FCAR fcar = new FCAR("id46", "course46", "professor46", "Winter", 2025);

        // Act
        fcar.setStatus(null);
        String result = fcar.getStatus();

        // Assert
        assertNull(result);
    }

    @Test
    void testSetStatusToValidValue() {
        // Arrange
        FCAR fcar = new FCAR("id47", "course47", "professor47", "Spring", 2024);

        // Act
        fcar.setStatus("Submitted");
        String result = fcar.getStatus();

        // Assert
        assertEquals("Submitted", result);
    }

    @Test
    void testSetStatusToInvalidValue() {
        // Arrange
        FCAR fcar = new FCAR("id48", "course48", "professor48", "Fall", 2023);

        // Act
        fcar.setStatus("InvalidStatus");
        String result = fcar.getStatus();

        // Assert
        assertEquals("InvalidStatus", result); // Assume no validation on setter
    }

    @Test
    void testSetStatusBackToDraftFromApproved() {
        // Arrange
        FCAR fcar = new FCAR("id49", "course49", "professor49", "Winter", 2023);
        fcar.setStatus("Approved");

        // Act
        fcar.setStatus("Draft");
        String result = fcar.getStatus();

        // Assert
        assertEquals("Draft", result);
    }

    @Test
    void testSetStatusWithEdgeCaseString() {
        // Arrange
        FCAR fcar = new FCAR("id50", "course50", "professor50", "Summer", 2025);

        // Act
        fcar.setStatus("   ");
        String result = fcar.getStatus();

        // Assert
        assertEquals("   ", result);
    }

    @Test
    void testGetStudentOutcomesAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id51", "course51", "professor51", "Fall", 2024);

        // Act
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetStudentOutcomesAfterAddingSingleOutcome() {
        // Arrange
        FCAR fcar = new FCAR("id52", "course52", "professor52", "Spring", 2025);
        fcar.addStudentOutcome("Outcome1", 80);

        // Act
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertEquals(1, result.size());
        assertEquals(80, result.get("Outcome1"));
    }

    @Test
    void testGetStudentOutcomesAfterAddingMultipleOutcomes() {
        // Arrange
        FCAR fcar = new FCAR("id53", "course53", "professor53", "Summer", 2026);
        fcar.addStudentOutcome("Outcome1", 75);
        fcar.addStudentOutcome("Outcome2", 90);

        // Act
        Map<String, Integer> result = fcar.getStudentOutcomes();

        // Assert
        assertEquals(2, result.size());
        assertEquals(75, result.get("Outcome1"));
        assertEquals(90, result.get("Outcome2"));
    }

    @Test
    void testAddStudentOutcomeWithNegativeAchievementLevel() {
        // Arrange
        FCAR fcar = new FCAR("id100", "course100", "professor100", "Fall", 2024);

        // Act
        fcar.addStudentOutcome("Outcome1", -10);

        // Assert
        assertEquals(-10, fcar.getStudentOutcomes().get("Outcome1"));
    }

    @Test
    void testAddStudentOutcomeWithDuplicateOutcomeId() {
        // Arrange
        FCAR fcar = new FCAR("id101", "course101", "professor101", "Spring", 2024);
        fcar.addStudentOutcome("Outcome1", 80);

        // Act
        fcar.addStudentOutcome("Outcome1", 95);

        // Assert
        assertEquals(95, fcar.getStudentOutcomes().get("Outcome1"));
        assertEquals(1, fcar.getStudentOutcomes().size());
    }

    @Test
    void testAddStudentOutcomeWithInvalidOutcomeId() {
        // Arrange
        FCAR fcar = new FCAR("id102", "course102", "professor102", "Winter", 2024);

        // Act
        fcar.addStudentOutcome(null, 100);
        fcar.addStudentOutcome("", 100);

        // Assert
        assertNull(fcar.getStudentOutcomes().get(null));
        assertEquals(100, fcar.getStudentOutcomes().get(""));
        assertEquals(1, fcar.getStudentOutcomes().size());
    }

    @Test
    void testSetAssessmentMethodsWithValidMap() {
        // Arrange
        FCAR fcar = new FCAR("id301", "course301", "professor301", "Fall", 2024);
        Map<String, String> newMethods = Map.of("Method1", "Description1", "Method2", "Description2");

        // Act
        fcar.setAssessmentMethods(newMethods);
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Description1", result.get("Method1"));
        assertEquals("Description2", result.get("Method2"));
    }

    @Test
    void testSetAssessmentMethodsOverwriteExisting() {
        // Arrange
        FCAR fcar = new FCAR("id302", "course302", "professor302", "Spring", 2025);
        fcar.addAssessmentMethod("OldMethod", "OldDescription");
        Map<String, String> newMethods = Map.of("NewMethod1", "NewDescription1", "NewMethod2", "NewDescription2");

        // Act
        fcar.setAssessmentMethods(newMethods);
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertEquals(2, result.size());
        assertNull(result.get("OldMethod"));
        assertEquals("NewDescription1", result.get("NewMethod1"));
        assertEquals("NewDescription2", result.get("NewMethod2"));
    }

    @Test
    void testSetAssessmentMethodsWithEmptyMap() {
        // Arrange
        FCAR fcar = new FCAR("id303", "course303", "professor303", "Summer", 2025);
        Map<String, String> emptyMethods = Map.of();

        // Act
        fcar.setAssessmentMethods(emptyMethods);
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testSetAssessmentMethodsWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id304", "course304", "professor304", "Winter", 2026);

        // Act
        fcar.setAssessmentMethods(null);
        Map<String, String> result = fcar.getAssessmentMethods();

        // Assert
        assertNull(result);
    }

    @Test
    void testAddAssessmentMethodWithValidInput() {
        // Arrange
        FCAR fcar = new FCAR("id305", "course305", "professor305", "Fall", 2024);

        // Act
        fcar.addAssessmentMethod("Method1", "Description1");

        // Assert
        assertEquals(1, fcar.getAssessmentMethods().size());
        assertEquals("Description1", fcar.getAssessmentMethods().get("Method1"));
    }

    @Test
    void testAddAssessmentMethodOverwriteExistingMethod() {
        // Arrange
        FCAR fcar = new FCAR("id306", "course306", "professor306", "Spring", 2025);
        fcar.addAssessmentMethod("Method1", "Description1");

        // Act
        fcar.addAssessmentMethod("Method1", "UpdatedDescription");

        // Assert
        assertEquals(1, fcar.getAssessmentMethods().size());
        assertEquals("UpdatedDescription", fcar.getAssessmentMethods().get("Method1"));
    }

    @Test
    void testAddAssessmentMethodWithNullValues() {
        // Arrange
        FCAR fcar = new FCAR("id307", "course307", "professor307", "Summer", 2025);

        // Act
        fcar.addAssessmentMethod(null, null);

        // Assert
        assertTrue(fcar.getAssessmentMethods().isEmpty());
        assertNull(fcar.getAssessmentMethods().get(null));
    }

    @Test
    void testGetImprovementActionsAfterInitialization() {
        // Arrange
        FCAR fcar = new FCAR("id400", "course400", "professor400", "Fall", 2023);

        // Act
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetImprovementActionsAfterAddingSingleAction() {
        // Arrange
        FCAR fcar = new FCAR("id401", "course401", "professor401", "Spring", 2024);
        fcar.addImprovementAction("Action1", "Description1");

        // Act
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Description1", result.get("Action1"));
    }

    @Test
    void testGetImprovementActionsAfterAddingMultipleActions() {
        // Arrange
        FCAR fcar = new FCAR("id402", "course402", "professor402", "Summer", 2025);
        fcar.addImprovementAction("Action1", "Description1");
        fcar.addImprovementAction("Action2", "Description2");

        // Act
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Description1", result.get("Action1"));
        assertEquals("Description2", result.get("Action2"));
    }

    @Test
    void testAddImprovementActionOverwriteExistingKey() {
        // Arrange
        FCAR fcar = new FCAR("id403", "course403", "professor403", "Fall", 2024);
        fcar.addImprovementAction("Action1", "Description1");

        // Act
        fcar.addImprovementAction("Action1", "UpdatedDescription");

        // Assert
        assertEquals(1, fcar.getImprovementActions().size());
        assertEquals("UpdatedDescription", fcar.getImprovementActions().get("Action1"));
    }

    @Test
    void testSetImprovementActionsWithValidMap() {
        // Arrange
        FCAR fcar = new FCAR("id405", "course405", "professor405", "Fall", 2023);
        Map<String, String> improvementActions = Map.of("Action1", "Description1", "Action2", "Description2");

        // Act
        fcar.setImprovementActions(improvementActions);
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Description1", result.get("Action1"));
        assertEquals("Description2", result.get("Action2"));
    }

    @Test
    void testSetImprovementActionsOverwriteExistingActions() {
        // Arrange
        FCAR fcar = new FCAR("id406", "course406", "professor406", "Spring", 2024);
        fcar.addImprovementAction("OldAction", "OldDescription");
        Map<String, String> newImprovementActions = Map.of("NewAction1", "NewDescription1", "NewAction2", "NewDescription2");

        // Act
        fcar.setImprovementActions(newImprovementActions);
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(2, result.size());
        assertNull(result.get("OldAction"));
        assertEquals("NewDescription1", result.get("NewAction1"));
        assertEquals("NewDescription2", result.get("NewAction2"));
    }

    @Test
    void testSetImprovementActionsWithEmptyMap() {
        // Arrange
        FCAR fcar = new FCAR("id407", "course407", "professor407", "Summer", 2025);
        Map<String, String> emptyActions = Map.of();

        // Act
        fcar.setImprovementActions(emptyActions);
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testSetImprovementActionsWithNull() {
        // Arrange
        FCAR fcar = new FCAR("id408", "course408", "professor408", "Winter", 2026);

        // Act
        fcar.setImprovementActions(null);
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertNull(result);
    }

    @Test
    void testAddImprovementActionWithValidInput() {
        // Arrange
        FCAR fcar = new FCAR("testId1", "testCourse1", "testProfessor1", "Fall", 2023);

        // Act
        fcar.addImprovementAction("Action1", "Description1");
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Description1", result.get("Action1"));
    }

    @Test
    void testAddImprovementActionOverwriteExistingAction() {
        // Arrange
        FCAR fcar = new FCAR("testId2", "testCourse2", "testProfessor2", "Spring", 2024);
        fcar.addImprovementAction("Action1", "Description1");

        // Act
        fcar.addImprovementAction("Action1", "UpdatedDescription");
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertEquals(1, result.size());
        assertEquals("UpdatedDescription", result.get("Action1"));
    }

    @Test
    void testAddImprovementActionWithNullValues() {
        // Arrange
        FCAR fcar = new FCAR("testId3", "testCourse3", "testProfessor3", "Winter", 2025);

        // Act
        fcar.addImprovementAction(null, null);
        Map<String, String> result = fcar.getImprovementActions();

        // Assert
        assertTrue(result.isEmpty());
    }
}