package com.ABETAppTeam.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Report class for storing aggregated data from FCARs, Indicators, Outcomes, etc.
 * This is a container that includes methods for analyzing report data.
 */
public class Report {

    private String reportId;              // Unique identifier for the report
    private String reportTitle;           // Title of the report
    private String semester;              // Semester (e.g., "Fall", "Spring", "Summer")
    private int year;                     // Year
    private List<FCAR> fcarList;          // FCARs included in this report
    private Date generatedDate;           // Date when the report was generated
    private Map<String, Object> metadata; // Additional metadata for the report

    /**
     * Constructor
     */
    public Report(String reportId, String reportTitle) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.fcarList = new ArrayList<>();
        this.generatedDate = new Date();
        this.metadata = new HashMap<>();
    }

    // Basic getters and setters

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<FCAR> getFcarList() {
        return fcarList;
    }

    public void setFcarList(List<FCAR> fcarList) {
        this.fcarList = fcarList;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    // Analysis methods that operate on the report's own data

    /**
     * Get the count of FCARs in this report
     *
     * @return The number of FCARs in the report
     */
    public int getFcarCount() {
        return fcarList.size();
    }

    /**
     * Get statistics on student outcomes across all FCARs in this report
     *
     * @return A map of outcome IDs to average achievement levels
     */
    public Map<String, Double> getOutcomeStatistics() {
        Map<String, List<Integer>> outcomeValues = new HashMap<>();

        // Collect all achievement levels for each outcome
        for (FCAR fcar : fcarList) {
            Map<String, Integer> outcomes = fcar.getStudentOutcomes();
            if (outcomes != null) {
                for (Map.Entry<String, Integer> entry : outcomes.entrySet()) {
                    String outcomeId = entry.getKey();
                    Integer value = entry.getValue();

                    outcomeValues.computeIfAbsent(outcomeId, k -> new ArrayList<>())
                            .add(value);
                }
            }
        }

        // Calculate average for each outcome
        Map<String, Double> outcomeAverages = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : outcomeValues.entrySet()) {
            String outcomeId = entry.getKey();
            List<Integer> values = entry.getValue();

            double average = values.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            outcomeAverages.put(outcomeId, average);
        }

        return outcomeAverages;
    }

    /**
     * Get course performance data aggregated by course
     *
     * @return A map of course codes to average outcome achievement levels
     */
    public Map<String, Double> getCoursePerformanceData() {
        Map<String, List<Double>> coursePerformance = new HashMap<>();

        // Group FCARs by course and calculate average performance
        for (FCAR fcar : fcarList) {
            String courseCode = fcar.getCourseCode();
            Map<String, Integer> outcomes = fcar.getStudentOutcomes();

            if (outcomes != null && !outcomes.isEmpty()) {
                double avgAchievement = outcomes.values().stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);

                coursePerformance.computeIfAbsent(courseCode, k -> new ArrayList<>())
                        .add(avgAchievement);
            }
        }

        // Calculate overall average for each course
        Map<String, Double> courseAverages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : coursePerformance.entrySet()) {
            String courseCode = entry.getKey();
            List<Double> values = entry.getValue();

            double average = values.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            courseAverages.put(courseCode, average);
        }

        return courseAverages;
    }

    /**
     * Generate a summary of all improvement actions from FCARs
     *
     * @return A list of all improvement actions with metadata
     */
    public List<Map<String, String>> getAllImprovementActions() {
        List<Map<String, String>> allActions = new ArrayList<>();

        for (FCAR fcar : fcarList) {
            Map<String, String> actions = fcar.getImprovementActions();
            if (actions != null && !actions.isEmpty()) {
                Map<String, String> actionWithMetadata = new HashMap<>();
                actionWithMetadata.putAll(actions);
                actionWithMetadata.put("courseCode", fcar.getCourseCode());
                actionWithMetadata.put("fcarId", String.valueOf(fcar.getFcarId()));
                actionWithMetadata.put("semester", fcar.getSemester() + " " + fcar.getYear());

                allActions.add(actionWithMetadata);
            }
        }

        return allActions;
    }

    /**
     * Get FCARs grouped by course
     *
     * @return A map of course codes to lists of FCARs
     */
    public Map<String, List<FCAR>> getFcarsByCourse() {
        return fcarList.stream()
                .collect(Collectors.groupingBy(FCAR::getCourseCode));
    }

    /**
     * Get FCARs grouped by instructor
     *
     * @return A map of instructor IDs to lists of FCARs
     */
    public Map<Integer, List<FCAR>> getFcarsByInstructor() {
        return fcarList.stream()
                .collect(Collectors.groupingBy(FCAR::getInstructorId));
    }
}