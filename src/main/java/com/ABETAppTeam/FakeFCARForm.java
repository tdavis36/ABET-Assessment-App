package com.ABETAppTeam;

/**
 * FakeFCARForm class for generating a mock FCAR and a sample HTML form
 * for demonstration or testing purposes.
 */
public class FakeFCARForm {

    /**
     * Creates and returns a new FCAR object with pre-filled sample data.
     *
     * @return A sample FCAR object.
     */
    public static FCAR createFakeFCAR() {
        // Example data for demonstration
        String courseId = "CS320";
        String professorId = "Prof01";
        String semester = "Spring";
        int year = 2025;

        // Use the FCARFactory to create a new FCAR
        FCAR fcar = FCARFactory.createFCAR(courseId, professorId, semester, year);

        // Add some sample student outcomes
        fcar.addStudentOutcome("Outcome1", 4);
        fcar.addStudentOutcome("Outcome2", 3);

        // Add assessment methods
        fcar.addAssessmentMethod("Method1", "Project-based assessment");
        fcar.addAssessmentMethod("Method2", "Midterm exam");

        // Add improvement actions
        fcar.addImprovementAction("Action1", "Revise lecture slides for clarity");
        fcar.addImprovementAction("Action2", "Provide weekly practice quizzes");

        return fcar;
    }

    /**
     * Returns a basic HTML form as a String for demo/testing.
     * You can embed this form in a JSP or servlet response to simulate
     * an FCAR input form for user submission.
     *
     * @return A String containing basic HTML form markup.
     */
    public static String getFakeFormHTML() {
        return "<form action=\"/submitFakeFCAR\" method=\"post\">\n"
                + "    <label for=\"courseId\">Course ID:</label>\n"
                + "    <input type=\"text\" name=\"courseId\" value=\"CS320\"><br>\n\n"

                + "    <label for=\"professorId\">Professor ID:</label>\n"
                + "    <input type=\"text\" name=\"professorId\" value=\"Prof01\"><br>\n\n"

                + "    <label for=\"semester\">Semester:</label>\n"
                + "    <input type=\"text\" name=\"semester\" value=\"Spring\"><br>\n\n"

                + "    <label for=\"year\">Year:</label>\n"
                + "    <input type=\"number\" name=\"year\" value=\"2025\"><br>\n\n"

                + "    <!-- You can add more fields here for outcomes, methods, actions, etc. -->\n\n"

                + "    <button type=\"submit\">Submit</button>\n"
                + "</form>";
    }
}
