package com.ABETAppTeam;

import java.util.*;
import java.util.UUID;

/**
 * ReportFactory class for generating and exporting reports that aggregate data
 * from FCARs, Indicators, and Outcomes.
 *
 * This class follows a pattern similar to FCARFactory. It provides:
 *  - Methods to create or retrieve reports by semester/year
 *  - Placeholder export methods (PDF/CSV) for future implementation
 */
public class ReportFactory {

    // In-memory storage of generated reports (in a real app, this might be a database)
    private static Map<String, Report> reportMap = new HashMap<>();

    /**
     * Generate a new report for a specific semester and year.
     *
     * @param semester Semester (e.g., "Fall", "Spring", "Summer")
     * @param year     Year
     * @return A newly generated Report object
     */
    public static Report generateReportBySemester(String semester, int year) {
        // Generate a unique ID for the report
        String reportId = "REPORT-" + UUID.randomUUID().toString();
        String reportTitle = "Semester Report: " + semester + " " + year;

        // Create a new Report object
        Report report = new Report(reportId, reportTitle);
        report.setSemester(semester);
        report.setYear(year);

        // 1. Retrieve all FCARs for the given semester/year
        Map<String, FCAR> fcarMap = (Map<String, FCAR>) FCARFactory.getFCARsBySemester(semester, year);
        // 2. Convert map values to a list and set them in the report
        List<FCAR> fcarList = new ArrayList<>(fcarMap.values());
        report.setFcarList(fcarList);

        // 3. (Future) Retrieve Indicators, Outcomes, etc., for the same semester/year

        // Store the report in memory
        reportMap.put(reportId, report);

        return report;
    }

    /**
     * Generate a new report for an entire year (all semesters).
     *
     * @param year Year
     * @return A newly generated Report object
     */
    public static Report generateReportByYear(int year) {
        // Generate a unique ID for the report
        String reportId = "REPORT-" + UUID.randomUUID().toString();
        String reportTitle = "Yearly Report: " + year;

        // Create a new Report object
        Report report = new Report(reportId, reportTitle);
        report.setYear(year);

        // 1. Retrieve all FCARs, then filter by year
        List<FCAR> allFcars = FCARFactory.getAllFCARsAsList();
        List<FCAR> filteredFcars = new ArrayList<>();
        for (FCAR fcar : allFcars) {
            if (fcar.getYear() == year) {
                filteredFcars.add(fcar);
            }
        }
        report.setFcarList(filteredFcars);

        // 2. (Future) Retrieve Indicators, Outcomes, etc., for that entire year

        // Store the report in memory
        reportMap.put(reportId, report);

        return report;
    }

    /**
     * Retrieve an existing report by its ID.
     *
     * @param reportId The ID of the report to retrieve
     * @return The Report object, or null if not found
     */
    public static Report getReport(String reportId) {
        return reportMap.get(reportId);
    }

    /**
     * Export a given report to PDF format.
     * (Placeholder method: implement your PDF library logic here)
     *
     * @param report   The Report to export
     * @param filePath The file path (or file name) where the PDF should be saved
     * @return true if the export was successful, false otherwise
     */
    public static boolean exportReportToPDF(Report report, String filePath) {
        if (report == null || filePath == null) {
            return false;
        }
        System.out.println("Exporting report to PDF (placeholder) at " + filePath);
        return true;
    }

    /**
     * Export a given report to CSV format.
     * (Placeholder method: implement your CSV generation logic here)
     *
     * @param report   The Report to export
     * @param filePath The file path (or file name) where the CSV should be saved
     * @return true if the export was successful, false otherwise
     */
    public static boolean exportReportToCSV(Report report, String filePath) {
        if (report == null || filePath == null) {
            return false;
        }
        System.out.println("Exporting report to CSV (placeholder) at " + filePath);
        return true;
    }

    /**
     * Optional: Return all generated reports in memory.
     */
    public static Map<String, Report> getAllReports() {
        return reportMap;
    }
}
