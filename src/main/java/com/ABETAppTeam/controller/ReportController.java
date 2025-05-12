package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Report;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.service.ReportService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for generating and managing reports
 * This class is responsible for handling report generation requests and preparing
 * report data for views
 */
public class ReportController {
    // Singleton instance
    private static ReportController instance;

    // Service instances
    private final ReportService reportService;
    private final LoggingService logger;

    /**
     * Private constructor for a singleton pattern
     */
    private ReportController() {
        this.reportService = new ReportService();
        this.logger = LoggingService.getInstance();
        logger.debug("ReportController initialized");
    }

    /**
     * Get the singleton instance
     *
     * @return The ReportController instance
     */
    public static synchronized ReportController getInstance() {
        if (instance == null) {
            instance = new ReportController();
        }
        return instance;
    }

    /**
     * Generate data for a full report
     *
     * @param reportTitle The title for the report
     * @return Map containing the report and related data for the view
     */
    public Map<String, Object> generateFullReportData(String reportTitle) {
        logger.debug("Generating full report data with title: {}", reportTitle);

        Map<String, Object> reportData = new HashMap<>();

        // Use the ReportService to generate the report
        Report report = reportService.generateFullReport(reportTitle);

        // Add the report to the data map
        reportData.put("report", report);

        // Add metadata
        reportData.put("generatedAt", new Date());
        reportData.put("reportType", "Full Report");

        // Add course performance data
        reportData.put("coursePerformance", report.getCoursePerformanceData());

        // Add outcome statistics
        reportData.put("outcomeStatistics", report.getOutcomeStatistics());

        return reportData;
    }

    /**
     * Generate data for a semester report
     *
     * @param reportTitle The title for the report
     * @param semester The semester (e.g., "Fall", "Spring", "Summer")
     * @param year The year
     * @return Map containing the report and related data for the view
     */
    // In ReportController.java

    public Map<String, Object> generateSemesterReportData(String reportTitle, String semester, int year) {
        logger.debug("Generating semester report data: {} for {} {}", reportTitle, semester, year);

        Map<String, Object> reportData = new HashMap<>();

        // Use the ReportService to generate the report
        Report report = reportService.generateSemesterReport(reportTitle, semester, year);

        // Add the report to the data map
        reportData.put("report", report);

        // Add metadata
        reportData.put("generatedAt", new Date());
        reportData.put("reportType", "Semester Report");
        reportData.put("semester", semester);
        reportData.put("year", year);

        // Add course performance data
        reportData.put("coursePerformance", report.getCoursePerformanceData());

        // Add outcome statistics
        reportData.put("outcomeStatistics", report.getOutcomeStatistics());

        return reportData;
    }

    /**
     * Generate data for course-based reports
     *
     * @param reportTitle The title for the report
     * @return Map containing course reports and related data for the view
     */
    public Map<String, Object> generateCourseReportsData(String reportTitle) {
        logger.debug("Generating course-based reports data with title: {}", reportTitle);

        Map<String, Object> reportData = new HashMap<>();

        // Use the ReportService to generate the report
        Map<String, Report> courseReports = reportService.generateCourseBasedReports(reportTitle);

        // Add the reports to the data map
        reportData.put("courseReports", courseReports);

        // Add metadata
        reportData.put("generatedAt", new Date());
        reportData.put("reportType", "Course-Based Reports");
        reportData.put("courseCodes", new ArrayList<>(courseReports.keySet()));

        // For each course, add its performance data and outcome statistics
        Map<String, Map<String, Double>> allCourseStats = new HashMap<>();
        Map<String, Map<String, Double>> allOutcomeStats = new HashMap<>();

        for (Map.Entry<String, Report> entry : courseReports.entrySet()) {
            String courseCode = entry.getKey();
            Report report = entry.getValue();

            allCourseStats.put(courseCode, report.getCoursePerformanceData());
            allOutcomeStats.put(courseCode, report.getOutcomeStatistics());
        }

        reportData.put("allCoursePerformance", allCourseStats);
        reportData.put("allOutcomeStatistics", allOutcomeStats);

        return reportData;
    }

    /**
     * Generate data for an outcome-focused report
     *
     * @param reportTitle The title for the report
     * @param outcomeIds List of outcome IDs to focus on
     * @return Map containing the report and related data for the view
     */
    public Map<String, Object> generateOutcomeReportData(String reportTitle, List<String> outcomeIds) {
        logger.debug("Generating outcome report data: {} for outcomes: {}", reportTitle, outcomeIds);

        Map<String, Object> reportData = new HashMap<>();

        // Use the ReportService to generate the report
        Report report = reportService.generateOutcomeReport(reportTitle, outcomeIds);

        // Add the report to the data map
        reportData.put("report", report);

        // Add metadata
        reportData.put("generatedAt", new Date());
        reportData.put("reportType", "Outcome-Focused Report");
        reportData.put("targetOutcomes", outcomeIds);

        // Add outcome descriptions from metadata
        Map<String, String> outcomeDescriptions =
                (Map<String, String>) report.getMetadata().get("outcomeDescriptions");
        reportData.put("outcomeDescriptions", outcomeDescriptions);

        // Add statistics
        reportData.put("outcomeStatistics", report.getOutcomeStatistics());

        return reportData;
    }

    /**
     * Generate data for a trend report
     *
     * @param reportTitle The title for the report
     * @param startYear The starting year
     * @param endYear The ending year (inclusive)
     * @return Map containing the report and related data for the view
     */
    public Map<String, Object> generateTrendReportData(String reportTitle, int startYear, int endYear) {
        logger.debug("Generating trend report data: {} from {} to {}", reportTitle, startYear, endYear);

        Map<String, Object> reportData = new HashMap<>();

        // Use the ReportService to generate the report
        Report report = reportService.generateTrendReport(reportTitle, startYear, endYear);

        // Add the report to the data map
        reportData.put("report", report);

        // Add metadata
        reportData.put("generatedAt", new Date());
        reportData.put("reportType", "Trend Report");
        reportData.put("startYear", startYear);
        reportData.put("endYear", endYear);

        // Add yearly statistics from metadata
        Map<Integer, Map<String, Double>> yearlyStats =
                (Map<Integer, Map<String, Double>>) report.getMetadata().get("yearlyOutcomeStatistics");
        reportData.put("yearlyOutcomeStatistics", yearlyStats);

        return reportData;
    }

    /**
     * Export a report to a specific format
     *
     * @param reportId The ID of the report to export
     * @param format The format to export to ("PDF", "HTML", "CSV")
     * @return The exported report content
     */
    public String exportReport(String reportId, String format) {
        // Implementation would need to retrieve the report by ID first
        // For now, assuming we're exporting a report object passed in

        logger.debug("Request to export report ID {} to {} format", reportId, format);

        // Generate a new report for demonstration (in real app, would retrieve from storage)
        Report report = reportService.generateFullReport("Exported Report");

        return reportService.exportReport(report, format);
    }

    /**
     * Get a list of available report types
     *
     * @return List of report type names
     */
    public List<String> getAvailableReportTypes() {
        return Arrays.asList(
                "Full Report",
                "Semester Report",
                "Course Report",
                "Outcome Report",
                "Trend Report"
        );
    }

    /**
     * Get available semesters from FCARs for reporting
     *
     * @return List of semester names
     */
    public List<String> getAvailableSemesters() {
        return Arrays.asList("Fall", "Spring", "Summer");
    }

    /**
     * Get available years from FCARs for reporting
     *
     * @return List of years
     */
    public List<Integer> getAvailableYears() {
        // In a real implementation, this would query the database for actual years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();

        // Include the last 5 years
        for (int i = 0; i < 5; i++) {
            years.add(currentYear - i);
        }

        return years;
    }

    /**
     * Generate a dashboard with report statistics
     *
     * @return Map containing report statistics
     */
    public Map<String, Object> generateReportDashboard() {
        logger.debug("Generating report dashboard");

        Map<String, Object> dashboardData = new HashMap<>();

        // Get the current date and time
        Date now = new Date();
        dashboardData.put("generatedAt", now);

        // Add available report types
        dashboardData.put("reportTypes", getAvailableReportTypes());

        // Add available semesters and years
        dashboardData.put("semesters", getAvailableSemesters());
        dashboardData.put("years", getAvailableYears());

        return dashboardData;
    }
}