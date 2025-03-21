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

                + "    <!-- Add fields for student outcomes -->\n"
                + "    <label for=\"outcome1\">Outcome 1:</label>\n"
                + "    <input type=\"number\" name=\"outcome1\" value=\"4\"><br>\n\n"
                + "    <label for=\"outcome2\">Outcome 2:</label>\n"
                + "    <input type=\"number\" name=\"outcome2\" value=\"3\"><br>\n\n"

                + "    <!-- Add fields for assessment methods -->\n"
                + "    <label for=\"method1\">Method 1:</label>\n"
                + "    <input type=\"text\" name=\"method1\" value=\"Project-based assessment\"><br>\n\n"
                + "    <label for=\"method2\">Method 2:</label>\n"
                + "    <input type=\"text\" name=\"method2\" value=\"Midterm exam\"><br>\n\n"

                + "    <!-- Add fields for improvement actions -->\n"
                + "    <label for=\"action1\">Action 1:</label>\n"
                + "    <input type=\"text\" name=\"action1\" value=\"Revise lecture slides for clarity\"><br>\n\n"
                + "    <label for=\"action2\">Action 2:</label>\n"
                + "    <input type=\"text\" name=\"action2\" value=\"Provide weekly practice quizzes\"><br>\n\n"

                + "    <input type=\"hidden\" name=\"action\" value=\"submitFakeFCAR\">\n"
                + "    <input type=\"submit\" value=\"Submit\">\n"
                + "</form>";

    }
}
