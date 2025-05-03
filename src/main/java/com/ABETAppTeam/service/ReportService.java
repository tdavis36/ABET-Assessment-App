package com.ABETAppTeam.service;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.model.Report;
import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.repository.IFCARRepository;
import com.ABETAppTeam.repository.CourseRepository;
import com.ABETAppTeam.repository.OutcomeRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for generating reports based on FCAR data
 */
public class ReportService {

    private final IFCARRepository fcarRepository;
    private final CourseRepository courseRepository;
    private final OutcomeRepository outcomeRepository;
    private final LoggingService logger;

    /**
     * Constructor with default repositories
     */
    public ReportService() {
        this.fcarRepository = new FCARRepository();
        this.courseRepository = new CourseRepository();
        this.outcomeRepository = new OutcomeRepository();
        this.logger = LoggingService.getInstance();
    }

    /**
     * Constructor with dependency injection (for testing)
     *
     * @param fcarRepository The FCAR repository implementation
     */
    public ReportService(IFCARRepository fcarRepository) {
        this.fcarRepository = fcarRepository;
        this.courseRepository = new CourseRepository();
        this.outcomeRepository = new OutcomeRepository();
        this.logger = LoggingService.getInstance();
    }

    /**
     * Generate a report based on all completed FCARs
     *
     * @param reportTitle The title for the report
     * @return A report containing data from all completed FCARs
     */
    public Report generateFullReport(String reportTitle) {
        logger.info("Generating full report: {}", reportTitle);

        // Create a new report with a unique ID
        String reportId = "REP_" + UUID.randomUUID().toString().substring(0, 8);
        Report report = new Report(reportId, reportTitle);

        // Get all approved FCARs
        List<FCAR> completedFCARs = fcarRepository.findAll().stream()
                .filter(fcar -> "Approved".equals(fcar.getStatus()))
                .collect(Collectors.toList());

        // Add FCARs to the report
        report.setFcarList(completedFCARs);

        // Set the current semester and year for the report
        Calendar cal = Calendar.getInstance();
        report.setYear(cal.get(Calendar.YEAR));

        int month = cal.get(Calendar.MONTH);
        if (month <= 4) {
            report.setSemester("Spring");
        } else if (month <= 7) {
            report.setSemester("Summer");
        } else {
            report.setSemester("Fall");
        }

        // Add metadata
        report.addMetadata("totalFCARs", completedFCARs.size());
        report.addMetadata("courseCount", completedFCARs.stream().map(FCAR::getCourseCode).distinct().count());
        report.addMetadata("instructorCount", completedFCARs.stream().map(FCAR::getInstructorId).distinct().count());

        return report;
    }

    /**
     * Generate a report for a specific semester and year
     *
     * @param reportTitle The title for the report
     * @param semester The semester to filter by (e.g., "Fall", "Spring", "Summer")
     * @param year The year to filter by
     * @return A report containing filtered FCAR data
     */
    public Report generateSemesterReport(String reportTitle, String semester, int year) {
        logger.info("Generating semester report: {} for {} {}", reportTitle, semester, year);

        // Create a new report with a unique ID
        String reportId = "REP_" + semester + "_" + year + "_" + UUID.randomUUID().toString().substring(0, 5);
        Report report = new Report(reportId, reportTitle);

        // Set the semester and year
        report.setSemester(semester);
        report.setYear(year);

        // Get FCARs for the specified semester and year
        // Using the correct repository method to find by semester and year
        List<FCAR> semesterFCARs = fcarRepository.findBySemesterAndYear(semester, year);

        // Filter to only include approved FCARs
        List<FCAR> completedFCARs = semesterFCARs.stream()
                .filter(fcar -> "Approved".equals(fcar.getStatus()))
                .collect(Collectors.toList());

        // Add FCARs to the report
        report.setFcarList(completedFCARs);

        // Add metadata
        report.addMetadata("totalFCARs", completedFCARs.size());
        report.addMetadata("courseCount", completedFCARs.stream().map(FCAR::getCourseCode).distinct().count());
        report.addMetadata("instructorCount", completedFCARs.stream().map(FCAR::getInstructorId).distinct().count());

        return report;
    }

    /**
     * Generate reports aggregated by course
     *
     * @param reportTitle The title for the report
     * @return A map of course codes to reports
     */
    public Map<String, Report> generateCourseBasedReports(String reportTitle) {
        logger.info("Generating course-based reports: {}", reportTitle);

        Map<String, Report> courseReports = new HashMap<>();

        // Get all approved FCARs
        List<FCAR> completedFCARs = fcarRepository.findAll().stream()
                .filter(fcar -> "Approved".equals(fcar.getStatus()))
                .collect(Collectors.toList());

        // Group FCARs by course code
        Map<String, List<FCAR>> fcarsByCourse = completedFCARs.stream()
                .collect(Collectors.groupingBy(FCAR::getCourseCode));

        // Create a report for each course
        for (Map.Entry<String, List<FCAR>> entry : fcarsByCourse.entrySet()) {
            String courseCode = entry.getKey();
            List<FCAR> courseFCARs = entry.getValue();

            String reportId = "REP_" + courseCode + "_" + UUID.randomUUID().toString().substring(0, 5);
            Report courseReport = new Report(reportId, reportTitle + " - " + courseCode);
            courseReport.setFcarList(courseFCARs);

            // Get course information
            Course course = courseRepository.findByCourseCode(courseCode);
            if (course != null) {
                courseReport.addMetadata("courseName", course.getCourseName());
                courseReport.addMetadata("courseDescription", course.getDescription());
                courseReport.addMetadata("learningOutcomes", course.getLearningOutcomes());
            }

            courseReports.put(courseCode, courseReport);
        }

        return courseReports;
    }

    /**
     * Generate a report focused on specific outcomes
     *
     * @param reportTitle The title for the report
     * @param outcomeIds List of outcome IDs to focus on
     * @return A report focused on the specified outcomes
     */
    public Report generateOutcomeReport(String reportTitle, List<String> outcomeIds) {
        logger.info("Generating outcome report: {} for outcomes: {}", reportTitle, outcomeIds);

        String reportId = "REP_OUT_" + UUID.randomUUID().toString().substring(0, 8);
        Report report = new Report(reportId, reportTitle);

        // Get all approved FCARs that contain any of the specified outcomes
        List<FCAR> relevantFCARs = fcarRepository.findAll().stream()
                .filter(fcar -> "Approved".equals(fcar.getStatus()))
                .filter(fcar -> {
                    Map<String, Integer> outcomes = fcar.getStudentOutcomes();
                    if (outcomes == null) return false;

                    // Check if any of the specified outcomes are in this FCAR
                    return outcomeIds.stream()
                            .anyMatch(outcomes::containsKey);
                })
                .collect(Collectors.toList());

        report.setFcarList(relevantFCARs);

        // Get outcome descriptions for metadata
        Map<String, String> outcomeDescriptions = new HashMap<>();
        for (String outcomeId : outcomeIds) {
            try {
                int id = Integer.parseInt(outcomeId);
                Outcome outcome = outcomeRepository.findById(id);
                if (outcome != null) {
                    outcomeDescriptions.put(outcomeId, outcome.getDescription());
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid outcome ID: {}", outcomeId);
            }
        }

        report.addMetadata("outcomeDescriptions", outcomeDescriptions);
        report.addMetadata("targetOutcomes", outcomeIds);

        // Set current semester/year
        Calendar cal = Calendar.getInstance();
        report.setYear(cal.get(Calendar.YEAR));

        int month = cal.get(Calendar.MONTH);
        if (month <= 4) {
            report.setSemester("Spring");
        } else if (month <= 7) {
            report.setSemester("Summer");
        } else {
            report.setSemester("Fall");
        }

        return report;
    }

    /**
     * Generate a trend report across multiple years
     *
     * @param reportTitle The title for the report
     * @param startYear The starting year
     * @param endYear The ending year (inclusive)
     * @return A report containing trend analysis
     */
    public Report generateTrendReport(String reportTitle, int startYear, int endYear) {
        logger.info("Generating trend report: {} from {} to {}", reportTitle, startYear, endYear);

        String reportId = "REP_TREND_" + startYear + "_" + endYear + "_" + UUID.randomUUID().toString().substring(0, 5);
        Report report = new Report(reportId, reportTitle);

        // Get all approved FCARs within the year range
        List<FCAR> relevantFCARs = fcarRepository.findAll().stream()
                .filter(fcar -> "Approved".equals(fcar.getStatus()))
                .filter(fcar -> fcar.getYear() >= startYear && fcar.getYear() <= endYear)
                .collect(Collectors.toList());

        report.setFcarList(relevantFCARs);
        report.addMetadata("startYear", startYear);
        report.addMetadata("endYear", endYear);

        // Group FCARs by year for trend analysis
        Map<Integer, List<FCAR>> fcarsByYear = relevantFCARs.stream()
                .collect(Collectors.groupingBy(FCAR::getYear));

        // Calculate yearly statistics
        Map<Integer, Map<String, Double>> yearlyOutcomeStats = new HashMap<>();
        for (int year = startYear; year <= endYear; year++) {
            List<FCAR> yearFCARs = fcarsByYear.getOrDefault(year, Collections.emptyList());

            // Skip years with no data
            if (yearFCARs.isEmpty()) {
                continue;
            }

            // Create a temporary report for this year to use its analysis methods
            Report yearReport = new Report("temp", "temp");
            yearReport.setFcarList(yearFCARs);

            // Get outcome statistics for this year
            Map<String, Double> outcomeStats = yearReport.getOutcomeStatistics();
            yearlyOutcomeStats.put(year, outcomeStats);
        }

        report.addMetadata("yearlyOutcomeStatistics", yearlyOutcomeStats);

        return report;
    }

    /**
     * Export report to a specific format
     *
     * @param report The report to export
     * @param format The format to export to (e.g., "PDF", "HTML", "CSV")
     * @return A string representation of the report in the specified format
     */
    public String exportReport(Report report, String format) {
        logger.info("Exporting report {} in {} format", report.getReportId(), format);

        switch (format.toUpperCase()) {
            case "CSV":
                return exportToCSV(report);
            case "HTML":
                return exportToHTML(report);
            case "PDF":
                return "PDF export not implemented yet";
            default:
                return exportToPlainText(report);
        }
    }

    /**
     * Export report to CSV format
     */
    private String exportToCSV(Report report) {
        StringBuilder sb = new StringBuilder();

        // CSV Header
        sb.append("Report ID,Report Title,Semester,Year,FCAR ID,Course Code,Instructor ID,");
        sb.append("Status,Outcome ID,Achievement Level\n");

        // CSV Data
        for (FCAR fcar : report.getFcarList()) {
            for (Map.Entry<String, Integer> outcome : fcar.getStudentOutcomes().entrySet()) {
                sb.append(report.getReportId()).append(",");
                sb.append(escapeCSV(report.getReportTitle())).append(",");
                sb.append(report.getSemester()).append(",");
                sb.append(report.getYear()).append(",");
                sb.append(fcar.getFcarId()).append(",");
                sb.append(fcar.getCourseCode()).append(",");
                sb.append(fcar.getInstructorId()).append(",");
                sb.append(fcar.getStatus()).append(",");
                sb.append(outcome.getKey()).append(",");
                sb.append(outcome.getValue()).append("\n");
            }
        }

        return sb.toString();
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Export report to HTML format
     */
    private String exportToHTML(Report report) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html>\n<head>\n");
        sb.append("<title>").append(report.getReportTitle()).append("</title>\n");
        sb.append("<style>body{font-family:Arial,sans-serif;margin:20px;} table{border-collapse:collapse;width:100%;margin-bottom:20px;} ");
        sb.append("th,td{border:1px solid #ddd;padding:8px;text-align:left;} th{background-color:#f2f2f2;} ");
        sb.append("h1,h2,h3{color:#333;} .summary{background-color:#f9f9f9;padding:15px;border-radius:5px;margin:10px 0;}</style>\n");
        sb.append("</head>\n<body>\n");

        // Report header
        sb.append("<h1>").append(report.getReportTitle()).append("</h1>\n");
        sb.append("<div class='summary'>\n");
        sb.append("<p><strong>Report ID:</strong> ").append(report.getReportId()).append("</p>\n");
        sb.append("<p><strong>Semester:</strong> ").append(report.getSemester()).append(" ").append(report.getYear()).append("</p>\n");
        sb.append("<p><strong>Generated:</strong> ").append(report.getGeneratedDate()).append("</p>\n");
        sb.append("<p><strong>Total FCARs:</strong> ").append(report.getFcarCount()).append("</p>\n");
        sb.append("</div>\n");

        // Course performance section
        sb.append("<h2>Course Performance</h2>\n");
        sb.append("<table>\n<tr><th>Course Code</th><th>Average Achievement</th></tr>\n");

        Map<String, Double> coursePerformance = report.getCoursePerformanceData();
        for (Map.Entry<String, Double> entry : coursePerformance.entrySet()) {
            sb.append("<tr><td>").append(entry.getKey()).append("</td>");
            sb.append("<td>").append(String.format("%.2f", entry.getValue())).append("</td></tr>\n");
        }

        sb.append("</table>\n");

        // Outcome statistics section
        sb.append("<h2>Student Outcome Statistics</h2>\n");
        sb.append("<table>\n<tr><th>Outcome ID</th><th>Average Achievement</th></tr>\n");

        Map<String, Double> outcomeStats = report.getOutcomeStatistics();
        for (Map.Entry<String, Double> entry : outcomeStats.entrySet()) {
            sb.append("<tr><td>").append(entry.getKey()).append("</td>");
            sb.append("<td>").append(String.format("%.2f", entry.getValue())).append("</td></tr>\n");
        }

        sb.append("</table>\n");

        // FCAR list section
        sb.append("<h2>Included FCARs</h2>\n");
        sb.append("<table>\n<tr><th>FCAR ID</th><th>Course</th><th>Instructor ID</th><th>Semester</th><th>Year</th></tr>\n");

        for (FCAR fcar : report.getFcarList()) {
            sb.append("<tr><td>").append(fcar.getFcarId()).append("</td>");
            sb.append("<td>").append(fcar.getCourseCode()).append("</td>");
            sb.append("<td>").append(fcar.getInstructorId()).append("</td>");
            sb.append("<td>").append(fcar.getSemester()).append("</td>");
            sb.append("<td>").append(fcar.getYear()).append("</td></tr>\n");
        }

        sb.append("</table>\n");

        // Improvement actions section
        sb.append("<h2>Improvement Actions</h2>\n");
        sb.append("<table>\n<tr><th>Course</th><th>FCAR ID</th><th>Semester</th><th>Action ID</th><th>Description</th></tr>\n");

        List<Map<String, String>> actions = report.getAllImprovementActions();
        for (Map<String, String> action : actions) {
            for (Map.Entry<String, String> entry : action.entrySet()) {
                if (entry.getKey().equals("courseCode") || entry.getKey().equals("fcarId") || entry.getKey().equals("semester")) {
                    continue;
                }

                sb.append("<tr><td>").append(action.get("courseCode")).append("</td>");
                sb.append("<td>").append(action.get("fcarId")).append("</td>");
                sb.append("<td>").append(action.get("semester")).append("</td>");
                sb.append("<td>").append(entry.getKey()).append("</td>");
                sb.append("<td>").append(entry.getValue()).append("</td></tr>\n");
            }
        }

        sb.append("</table>\n");

        // End HTML document
        sb.append("</body>\n</html>");

        return sb.toString();
    }

    /**
     * Export report to plain text format
     */
    private String exportToPlainText(Report report) {
        StringBuilder sb = new StringBuilder();

        sb.append("REPORT: ").append(report.getReportTitle()).append("\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append("Report ID: ").append(report.getReportId()).append("\n");
        sb.append("Semester: ").append(report.getSemester()).append(" ").append(report.getYear()).append("\n");
        sb.append("Generated: ").append(report.getGeneratedDate()).append("\n");
        sb.append("Total FCARs: ").append(report.getFcarCount()).append("\n\n");

        // Course performance
        sb.append("COURSE PERFORMANCE\n");
        sb.append("-".repeat(80)).append("\n");
        Map<String, Double> coursePerformance = report.getCoursePerformanceData();
        for (Map.Entry<String, Double> entry : coursePerformance.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            sb.append(String.format("%.2f", entry.getValue())).append("\n");
        }
        sb.append("\n");

        // Outcome statistics
        sb.append("STUDENT OUTCOME STATISTICS\n");
        sb.append("-".repeat(80)).append("\n");
        Map<String, Double> outcomeStats = report.getOutcomeStatistics();
        for (Map.Entry<String, Double> entry : outcomeStats.entrySet()) {
            sb.append("Outcome ").append(entry.getKey()).append(": ");
            sb.append(String.format("%.2f", entry.getValue())).append("\n");
        }
        sb.append("\n");

        // FCARs included
        sb.append("FCARS INCLUDED IN THIS REPORT\n");
        sb.append("-".repeat(80)).append("\n");
        for (FCAR fcar : report.getFcarList()) {
            sb.append("FCAR #").append(fcar.getFcarId())
                    .append(" | Course: ").append(fcar.getCourseCode())
                    .append(" | Instructor: ").append(fcar.getInstructorId())
                    .append(" | Semester: ").append(fcar.getSemester()).append(" ").append(fcar.getYear())
                    .append("\n");
        }
        sb.append("\n");

        // Improvement actions
        sb.append("IMPROVEMENT ACTIONS\n");
        sb.append("-".repeat(80)).append("\n");
        List<Map<String, String>> actions = report.getAllImprovementActions();
        for (Map<String, String> action : actions) {
            sb.append("Course: ").append(action.get("courseCode"))
                    .append(" | FCAR #").append(action.get("fcarId"))
                    .append(" | ").append(action.get("semester")).append("\n");

            for (Map.Entry<String, String> entry : action.entrySet()) {
                if (entry.getKey().equals("courseCode") || entry.getKey().equals("fcarId") || entry.getKey().equals("semester")) {
                    continue;
                }
                sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}