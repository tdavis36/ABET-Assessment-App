package com.ABETAppTeam;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.util.AppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
     * 
     * @param filePart The uploaded file part
     * @param hasHeaderRow Whether the CSV file has a header row
     * @param currentUser The current user
     * @return List of FCAR objects created from the CSV data
     */
    private List<FCAR> processCSVFile(Part filePart, boolean hasHeaderRow, User currentUser) throws IOException {
        List<FCAR> fcars = new ArrayList<>();
        Map<String, Integer> columnMap = new HashMap<>();

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
                    String[] headers = line.split(",");
                    for (int i = 0; i < headers.length; i++) {
                        columnMap.put(headers[i].trim().toLowerCase(), i);
                    }
                    continue;
                }

                // Process data row
                String[] values = line.split(",");

                // If no header row, create default column mapping
                if (columnMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        switch (i) {
                            case 0: columnMap.put("coursecode", i); break;
                            case 1: columnMap.put("instructorid", i); break;
                            case 2: columnMap.put("semester", i); break;
                            case 3: columnMap.put("year", i); break;
                            case 4: columnMap.put("outcomeid", i); break;
                            case 5: columnMap.put("indicatorid", i); break;
                            case 6: columnMap.put("workused", i); break;
                            case 7: columnMap.put("assessmentdescription", i); break;
                            case 8: columnMap.put("level1", i); break;
                            case 9: columnMap.put("level2", i); break;
                            case 10: columnMap.put("level3", i); break;
                            case 11: columnMap.put("targetgoal", i); break;
                            case 12: columnMap.put("summary", i); break;
                            case 13: columnMap.put("improvementactions", i); break;
                            default: columnMap.put("column" + i, i);
                        }
                    }
                }

                // Extract required fields
                String courseCode = getValueFromRow(values, columnMap, "coursecode");
                String instructorIdStr = getValueFromRow(values, columnMap, "instructorid");
                String semester = getValueFromRow(values, columnMap, "semester");
                String yearStr = getValueFromRow(values, columnMap, "year");

                // Validate required fields
                if (courseCode == null || courseCode.isEmpty() ||
                    instructorIdStr == null || instructorIdStr.isEmpty() ||
                    semester == null || semester.isEmpty() ||
                    yearStr == null || yearStr.isEmpty()) {
                    AppUtils.warn("Skipping row {} due to missing required fields", lineNumber);
                    continue;
                }

                try {
                    int instructorId = Integer.parseInt(instructorIdStr);
                    int year = Integer.parseInt(yearStr);

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

                } catch (NumberFormatException e) {
                    AppUtils.warn("Skipping row {} due to invalid number format: {}", lineNumber, e.getMessage());
                }
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
    private String getValueFromRow(String[] values, Map<String, Integer> columnMap, String columnName) {
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
    private String getFileName(Part part) {
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
