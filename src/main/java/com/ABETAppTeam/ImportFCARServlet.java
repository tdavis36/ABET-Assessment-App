package com.ABETAppTeam;

import com.ABETAppTeam.controller.CourseController;
import com.ABETAppTeam.controller.UserController;
import com.ABETAppTeam.model.*;
import com.ABETAppTeam.util.AppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for importing FCAR data from CSV files
 * This servlet handles file uploads and processes CSV data to create FCARs
 */
@WebServlet("/ImportFCARServlet")
@MultipartConfig
public class ImportFCARServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Handles POST requests for importing FCAR data
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        // Start request timing and logging
        String timerId = startRequest(request);

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Check if user is logged in and is an admin
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // Check if user is an admin
        if (!(currentUser instanceof Admin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return;
        }

        try {
            // Get the uploaded file
            Part filePart = request.getPart("fcarFile");
            if (filePart == null) {
                session.setAttribute("errorMessage", "No file was uploaded");
                response.sendRedirect(request.getContextPath() + "/SettingsServlet");
                return;
            }

            // Check if file has content
            if (filePart.getSize() == 0) {
                session.setAttribute("errorMessage", "Uploaded file is empty");
                response.sendRedirect(request.getContextPath() + "/SettingsServlet");
                return;
            }

            // Check if file is CSV
            String fileName = getFileName(filePart);
            if (!fileName.toLowerCase().endsWith(".csv")) {
                session.setAttribute("errorMessage", "Uploaded file is not a CSV file");
                response.sendRedirect(request.getContextPath() + "/SettingsServlet");
                return;
            }

            // Process the CSV file
            boolean hasHeaderRow = "on".equals(request.getParameter("headerRow"));
            List<FCAR> importedFCARs = processCSVFile(filePart, hasHeaderRow, currentUser);

            // Save the imported FCARs
            List<FCAR> savedFCARs = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (FCAR fcar : importedFCARs) {
                try {
                    FCAR savedFcar = FCARFactory.save(fcar);
                    if (savedFcar != null) {
                        savedFCARs.add(savedFcar);
                    } else {
                        errors.add("Failed to save FCAR for course " + fcar.getCourseCode());
                    }
                } catch (Exception e) {
                    errors.add("Error saving FCAR for course " + fcar.getCourseCode() + ": " + e.getMessage());
                    AppUtils.error("Error saving imported FCAR", e);
                }
            }

            // Set session attributes for success/error messages
            if (!savedFCARs.isEmpty()) {
                session.setAttribute("successMessage", "Successfully imported " + savedFCARs.size() + " FCARs");
            }

            if (!errors.isEmpty()) {
                session.setAttribute("errorMessage", "Errors occurred during import: " + String.join("; ", errors));
            }

            // Redirect back to the settings page
            response.sendRedirect(request.getContextPath() + "/SettingsServlet");

        } catch (Exception e) {
            // Log the error
            AppUtils.error("Error importing FCARs", e);
            session.setAttribute("errorMessage", "Error importing FCARs: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/SettingsServlet");
        } finally {
            // Finish request timing and cleanup
            finishRequest(timerId, request);
        }
    }

    /**
    * Processes the CSV file and creates FCAR objects
    * Enhanced version with better error handling and additional validation
    *
    * @param line The uploaded file part
    * @return Array of FCAR objects created from the CSV data
    */
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handle quotes
                if (inQuotes) {
                    // Check for escaped quotes (double quotes)
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        currentValue.append('"');
                        i++; // Skip the next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            } else if (c == ',' && !inQuotes) {
                // End of current value
                result.add(currentValue.toString().trim());
                currentValue.setLength(0); // Clear the buffer
            } else {
                // Regular character
                currentValue.append(c);
            }
        }

        // Add the last value
        result.add(currentValue.toString().trim());

        return result.toArray(new String[0]);
    }

    /**
     * Helper method to check if a semester value is valid
     */
    private boolean isValidSemester(String semester) {
        if (semester == null) return false;

        // Case-insensitive check for valid semesters
        String semesterLower = semester.toLowerCase();
        return semesterLower.equals("fall") ||
                semesterLower.equals("spring") ||
                semesterLower.equals("summer");
    }

    /**
     * Helper method to check if an instructor exists in the system
     */
    private boolean isValidInstructor(int instructorId) {
        try {
            User user = UserController.getInstance().getUserById(instructorId);
            return user instanceof Professor;
        } catch (Exception e) {
            AppUtils.error("Error checking instructor validity: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Helper method to check if a course exists in the system
     */
    private boolean isValidCourse(String courseCode) {
        try {
            CourseController controller = CourseController.getInstance();
            Course course = controller.getCourse(courseCode);
            return course != null;
        } catch (Exception e) {
            AppUtils.error("Error checking course validity: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Helper method to check if a course is assigned to an instructor
     */
    private boolean isCourseAssignedToInstructor(String courseCode, int instructorId) {
        try {
            UserController controller = UserController.getInstance();
            List<String> assignedCourses = controller.getProfessorCourses(instructorId);
            return assignedCourses != null && assignedCourses.contains(courseCode);
        } catch (Exception e) {
            AppUtils.error("Error checking course assignment: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Helper method to assign a course to an instructor
     */
    private void assignCourseToInstructor(String courseCode, int instructorId) {
        try {
            UserController controller = UserController.getInstance();

            // Get current assigned courses
            List<String> currentCourses = controller.getProfessorCourses(instructorId);
            if (currentCourses == null) {
                currentCourses = new ArrayList<>();
            }

            // Add the new course
            if (!currentCourses.contains(courseCode)) {
                currentCourses.add(courseCode);

                // Update assignments
                controller.assignCoursesToProfessor(instructorId, currentCourses);
            }
        } catch (Exception e) {
            AppUtils.error("Error assigning course to instructor: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes the CSV file and creates FCAR objects
     * Enhanced version with better error handling and additional validation
     *
     * @param filePart     The uploaded file part
     * @param hasHeaderRow Whether the CSV file has a header row
     * @param currentUser  The current user
     * @return List of FCAR objects created from the CSV data
     */
    List<FCAR> processCSVFile(Part filePart, boolean hasHeaderRow, User currentUser) throws IOException {
        List<FCAR> fcars = new ArrayList<>();
        Map<String, Integer> columnMap = new HashMap<>();
        StringBuilder errorLog = new StringBuilder();
        int successCount = 0;
        int errorCount = 0;

        try (InputStream inputStream = filePart.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Process header row if present
                if (lineNumber == 1 && hasHeaderRow) {
                    // Handle quoted CSV properly
                    String[] headers = parseCSVLine(line);
                    for (int i = 0; i < headers.length; i++) {
                        columnMap.put(headers[i].trim().toLowerCase(), i);
                    }
                    continue;
                }

                try {
                    // Process data row with proper CSV parsing
                    String[] values = parseCSVLine(line);

                    // If no header row, create default column mapping
                    if (columnMap.isEmpty()) {
                        for (int i = 0; i < values.length; i++) {
                            switch (i) {
                                case 0:
                                    columnMap.put("coursecode", i);
                                    break;
                                case 1:
                                    columnMap.put("instructorid", i);
                                    break;
                                case 2:
                                    columnMap.put("semester", i);
                                    break;
                                case 3:
                                    columnMap.put("year", i);
                                    break;
                                case 4:
                                    columnMap.put("outcomeid", i);
                                    break;
                                case 5:
                                    columnMap.put("indicatorid", i);
                                    break;
                                case 6:
                                    columnMap.put("workused", i);
                                    break;
                                case 7:
                                    columnMap.put("assessmentdescription", i);
                                    break;
                                case 8:
                                    columnMap.put("level1", i);
                                    break;
                                case 9:
                                    columnMap.put("level2", i);
                                    break;
                                case 10:
                                    columnMap.put("level3", i);
                                    break;
                                case 11:
                                    columnMap.put("targetgoal", i);
                                    break;
                                case 12:
                                    columnMap.put("summary", i);
                                    break;
                                case 13:
                                    columnMap.put("improvementactions", i);
                                    break;
                                default:
                                    columnMap.put("column" + i, i);
                            }
                        }
                    }

                    // Extract required fields
                    String courseCode = getValueFromRow(values, columnMap, "coursecode");
                    String instructorIdStr = getValueFromRow(values, columnMap, "instructorid");
                    String semester = getValueFromRow(values, columnMap, "semester");
                    String yearStr = getValueFromRow(values, columnMap, "year");

                    // Validate required fields
                    boolean hasErrors = false;
                    if (courseCode == null || courseCode.isEmpty()) {
                        errorLog.append("Row ").append(lineNumber).append(": Missing course code\n");
                        hasErrors = true;
                    }

                    if (instructorIdStr == null || instructorIdStr.isEmpty()) {
                        errorLog.append("Row ").append(lineNumber).append(": Missing instructor ID\n");
                        hasErrors = true;
                    }

                    if (semester == null || semester.isEmpty()) {
                        errorLog.append("Row ").append(lineNumber).append(": Missing semester\n");
                        hasErrors = true;
                    } else if (!isValidSemester(semester)) {
                        errorLog.append("Row ").append(lineNumber).append(": Invalid semester '").append(semester).append("' (must be Fall, Spring, or Summer)\n");
                        hasErrors = true;
                    }

                    if (yearStr == null || yearStr.isEmpty()) {
                        errorLog.append("Row ").append(lineNumber).append(": Missing year\n");
                        hasErrors = true;
                    }

                    if (hasErrors) {
                        errorCount++;
                        continue;
                    }

                    try {
                        // Parse numeric fields
                        int instructorId = Integer.parseInt(instructorIdStr);
                        int year = Integer.parseInt(yearStr);

                        // Validate instructor exists in the system
                        if (!isValidInstructor(instructorId)) {
                            errorLog.append("Row ").append(lineNumber).append(": Instructor ID ").append(instructorId).append(" not found in the system\n");
                            errorCount++;
                            continue;
                        }

                        // Validate course exists in the system
                        if (!isValidCourse(courseCode)) {
                            errorLog.append("Row ").append(lineNumber).append(": Course code ").append(courseCode).append(" not found in the system\n");
                            errorCount++;
                            continue;
                        }

                        // Check if course is assigned to instructor
                        if (!isCourseAssignedToInstructor(courseCode, instructorId)) {
                            // Assign the course to the instructor
                            assignCourseToInstructor(courseCode, instructorId);
                            AppUtils.info("Automatically assigned course {} to instructor {}", courseCode, instructorId);
                        }

                        // Create FCAR object
                        FCAR fcar = new FCAR(0, courseCode, instructorId, semester, year);

                        // Set optional fields
                        String outcomeIdStr = getValueFromRow(values, columnMap, "outcomeid");
                        if (outcomeIdStr != null && !outcomeIdStr.isEmpty()) {
                            try {
                                int outcomeId = Integer.parseInt(outcomeIdStr);
                                fcar.setOutcomeId(outcomeId);
                            } catch (NumberFormatException e) {
                                AppUtils.warn("Invalid outcomeId format in row {}: {}", lineNumber, outcomeIdStr);
                            }
                        }

                        String indicatorIdStr = getValueFromRow(values, columnMap, "indicatorid");
                        if (indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
                            try {
                                int indicatorId = Integer.parseInt(indicatorIdStr);
                                fcar.setIndicatorId(indicatorId);
                            } catch (NumberFormatException e) {
                                AppUtils.warn("Invalid indicatorId format in row {}: {}", lineNumber, indicatorIdStr);
                            }
                        }

                        // Set assessment methods
                        Map<String, String> assessmentMethods = new HashMap<>();

                        String workUsed = getValueFromRow(values, columnMap, "workused");
                        if (workUsed != null && !workUsed.isEmpty()) {
                            assessmentMethods.put("workUsed", workUsed);
                        }

                        String assessmentDescription = getValueFromRow(values, columnMap, "assessmentdescription");
                        if (assessmentDescription != null && !assessmentDescription.isEmpty()) {
                            assessmentMethods.put("assessmentDescription", assessmentDescription);
                        }

                        String level1Str = getValueFromRow(values, columnMap, "level1");
                        if (level1Str != null && !level1Str.isEmpty()) {
                            assessmentMethods.put("level1", level1Str);
                        }

                        String level2Str = getValueFromRow(values, columnMap, "level2");
                        if (level2Str != null && !level2Str.isEmpty()) {
                            assessmentMethods.put("level2", level2Str);
                        }

                        String level3Str = getValueFromRow(values, columnMap, "level3");
                        if (level3Str != null && !level3Str.isEmpty()) {
                            assessmentMethods.put("level3", level3Str);
                        }

                        String targetGoalStr = getValueFromRow(values, columnMap, "targetgoal");
                        if (targetGoalStr != null && !targetGoalStr.isEmpty()) {
                            assessmentMethods.put("targetGoal", targetGoalStr);
                        }

                        fcar.setAssessmentMethods(assessmentMethods);

                        // Set improvement actions
                        Map<String, String> improvementActions = new HashMap<>();

                        String summary = getValueFromRow(values, columnMap, "summary");
                        if (summary != null && !summary.isEmpty()) {
                            improvementActions.put("summary", summary);
                        }

                        String actions = getValueFromRow(values, columnMap, "improvementactions");
                        if (actions != null && !actions.isEmpty()) {
                            improvementActions.put("actions", actions);
                        }

                        fcar.setImprovementActions(improvementActions);

                        // Set status to Draft
                        fcar.setStatus("Draft");

                        // Add to list
                        fcars.add(fcar);
                        successCount++;

                    } catch (NumberFormatException e) {
                        errorLog.append("Row ").append(lineNumber).append(": Invalid number format: ").append(e.getMessage()).append("\n");
                        errorCount++;
                        AppUtils.warn("Skipping row {} due to invalid number format: {}", lineNumber, e.getMessage());
                    }
                } catch (Exception e) {
                    errorLog.append("Row ").append(lineNumber).append(": Unexpected error: ").append(e.getMessage()).append("\n");
                    errorCount++;
                    AppUtils.error("Error processing row {}: {}", lineNumber, e.getMessage(), e);
                }
            }

            // Log summary of import
            AppUtils.info("CSV import completed: {} successes, {} errors", successCount, errorCount);
            if (errorCount > 0) {
                AppUtils.warn("CSV import errors:\n{}", errorLog.toString());
            }
        }

        return fcars;
    }

    /**
     * Gets a value from a row based on the column name
     *
     * @param values The row values
     * @param columnMap The column mapping
     * @param columnName The column name
     * @return The value from the row, or null if not found
     */
    private String getValueFromRow (String[]values, Map < String, Integer > columnMap, String columnName){
        Integer index = columnMap.get(columnName.toLowerCase());
        if (index != null && index < values.length) {
            return values[index].trim();
        }
        return null;
    }
    /**
     * Extracts the file name from a Part
     *
     * @param part The Part containing the file
     * @return The file name
     */
    String getFileName(Part part){
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");

        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }

        return "unknown";
    }
}
