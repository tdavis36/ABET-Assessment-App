package com.ABETAppTeam;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to initialize test data for the ABET Assessment Application
 */
@WebServlet("/InitTestDataServlet")
public class InitTestDataServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        /**
         * @see HttpServlet#HttpServlet()
         */
        public InitTestDataServlet() {
                super();
        }

        /**
         * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
         *      response)
         */
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                // Get controllers
                FCARController fcarController = FCARController.getInstance();
                DisplaySystemController displayController = DisplaySystemController.getInstance();

                // Create test users
                Professor smith = new Professor("Smith", "smith", "password", "smith@university.edu", "John", "Smith",
                                "Computer Science", "Room 101", "555-1234");
                smith.addCourseId("CS101");
                smith.addCourseId("CS201");
                smith.addCourseId("CS320");

                Professor johnson = new Professor("Johnson", "johnson", "password", "johnson@university.edu", "Emily",
                                "Johnson",
                                "Computer Science", "Room 102", "555-5678");
                johnson.addCourseId("CS330");
                johnson.addCourseId("CS335");

                // Add users to cache
                displayController.addUserToCache(smith);
                displayController.addUserToCache(johnson);

                // Create test courses
                Course cs101 = new Course("CS101", "CS101", "Introduction to Programming",
                                "Intro to programming concepts",
                                "Smith", "Spring", 2025);
                Course cs201 = new Course("CS201", "CS201", "Data Structures",
                                "Study of data structures and algorithms",
                                "Smith", "Spring", 2025);
                Course cs320 = new Course("CS320", "CS320", "Software Engineering",
                                "Software development methodologies",
                                "Smith", "Spring", 2025);
                Course cs330 = new Course("CS330", "CS330", "Computer Architecture", "Study of computer organization",
                                "Johnson", "Spring", 2025);
                Course cs335 = new Course("CS335", "CS335", "Operating Systems", "OS concepts and implementation",
                                "Johnson",
                                "Spring", 2025);

                // Add courses to cache
                displayController.addCourseToCache(cs101);
                displayController.addCourseToCache(cs201);
                displayController.addCourseToCache(cs320);
                displayController.addCourseToCache(cs330);
                displayController.addCourseToCache(cs335);

                // Create test FCARs
                String fcar1Id = fcarController.createFCAR("CS101", "Smith", "Spring", 2025);
                String fcar2Id = fcarController.createFCAR("CS201", "Smith", "Spring", 2025);
                String fcar3Id = fcarController.createFCAR("CS330", "Johnson", "Spring", 2025);

                // Add some data to FCARs
                FCAR fcar1 = fcarController.getFCAR(fcar1Id);
                if (fcar1 != null) {
                        // CS101 has outcomes 1 and 6
                        fcar1.addAssessmentMethod("selectedOutcomes", "1,6");
                        fcar1.addAssessmentMethod("indicator_1.1", "selected");
                        fcar1.addAssessmentMethod("indicator_1.2", "selected");
                        fcar1.addAssessmentMethod("indicator_6.1", "selected");
                        fcar1.addAssessmentMethod("indicator_6.2", "selected");

                        fcar1.addAssessmentMethod("targetGoal", "70");
                        fcar1.addAssessmentMethod("workUsed", "Final Project");
                        fcar1.addAssessmentMethod("assessmentDescription",
                                        "Students were asked to develop a simple program.");
                        fcar1.addAssessmentMethod("level3", "15");
                        fcar1.addAssessmentMethod("level2", "5");
                        fcar1.addAssessmentMethod("level1", "2");
                        fcar1.addAssessmentMethod("totalStudents", "22");
                        fcar1.addAssessmentMethod("studentsMetTarget", "15");
                        fcar1.addAssessmentMethod("percentageMetTarget", "68.18");
                        fcar1.addAssessmentMethod("targetMet", "false");
                        fcar1.addImprovementAction("summary",
                                        "Most students performed well, but some struggled with basic concepts.");
                        fcar1.addImprovementAction("actions", "Will provide more hands-on exercises next semester.");
                        fcarController.updateFCAR(fcar1);
                }

                FCAR fcar2 = fcarController.getFCAR(fcar2Id);
                if (fcar2 != null) {
                        // CS201 has outcomes 1, 2, and 6
                        fcar2.addAssessmentMethod("selectedOutcomes", "1,2,6");
                        fcar2.addAssessmentMethod("indicator_1.1", "selected");
                        fcar2.addAssessmentMethod("indicator_1.3", "selected");
                        fcar2.addAssessmentMethod("indicator_2.1", "selected");
                        fcar2.addAssessmentMethod("indicator_2.2", "selected");
                        fcar2.addAssessmentMethod("indicator_6.1", "selected");

                        fcar2.addAssessmentMethod("targetGoal", "75");
                        fcar2.addAssessmentMethod("workUsed", "Midterm Exam");
                        fcar2.addAssessmentMethod("assessmentDescription",
                                        "Students were asked to implement data structures.");
                        fcar2.addAssessmentMethod("level3", "18");
                        fcar2.addAssessmentMethod("level2", "4");
                        fcar2.addAssessmentMethod("level1", "1");
                        fcar2.addAssessmentMethod("totalStudents", "23");
                        fcar2.addAssessmentMethod("studentsMetTarget", "18");
                        fcar2.addAssessmentMethod("percentageMetTarget", "78.26");
                        fcar2.addAssessmentMethod("targetMet", "true");
                        fcar2.addImprovementAction("summary", "Students performed well overall.");
                        fcar2.addImprovementAction("actions",
                                        "Will introduce more complex data structures next semester.");
                        fcarController.updateFCAR(fcar2);
                }

                FCAR fcar3 = fcarController.getFCAR(fcar3Id);
                if (fcar3 != null) {
                        // CS330 has outcomes 2 and 4
                        fcar3.addAssessmentMethod("selectedOutcomes", "2,4");
                        fcar3.addAssessmentMethod("indicator_2.3", "selected");
                        fcar3.addAssessmentMethod("indicator_2.4", "selected");
                        fcar3.addAssessmentMethod("indicator_4.1", "selected");
                        fcar3.addAssessmentMethod("indicator_4.2", "selected");

                        fcar3.addAssessmentMethod("targetGoal", "80");
                        fcar3.addAssessmentMethod("workUsed", "Final Exam");
                        fcar3.addAssessmentMethod("assessmentDescription",
                                        "Students were asked to analyze ethical issues in computing.");
                        fcar3.addAssessmentMethod("level3", "12");
                        fcar3.addAssessmentMethod("level2", "6");
                        fcar3.addAssessmentMethod("level1", "2");
                        fcar3.addAssessmentMethod("totalStudents", "20");
                        fcar3.addAssessmentMethod("studentsMetTarget", "12");
                        fcar3.addAssessmentMethod("percentageMetTarget", "60.00");
                        fcar3.addAssessmentMethod("targetMet", "false");
                        fcar3.addImprovementAction("summary", "Students need more practice with ethical analysis.");
                        fcar3.addImprovementAction("actions", "Will incorporate more case studies next semester.");
                        fcarController.updateFCAR(fcar3);
                }

                // Add FCARs to professors
                smith.addFcarId(fcar1Id);
                smith.addFcarId(fcar2Id);
                johnson.addFcarId(fcar3Id);

                // Update users in cache
                displayController.addUserToCache(smith);
                displayController.addUserToCache(johnson);

                // Redirect to professor page
                response.getWriter().println("Test data initialized successfully!");
                response.getWriter()
                                .println("<a href='" + request.getContextPath()
                                                + "/ProfessorServlet'>Go to Professor Dashboard</a>");
        }
}
