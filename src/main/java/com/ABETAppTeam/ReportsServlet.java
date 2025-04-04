package com.ABETAppTeam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/ReportsServlet")
public class ReportsServlet extends HttpServlet {

    /**
     * Handles GET requests: retrieves all reports, processes any actions, and forwards to Reports.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check for any action parameters to generate or export reports
        String action = request.getParameter("action");
        if ("generateSemesterReport".equals(action)) {
            // Expecting parameters "semester" and "year"
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));
            Report report = ReportFactory.generateReportBySemester(semester, year);
            request.setAttribute("message", "Report generated for " + semester + " " + year + " (ID: " + report.getReportId() + ")");
        } else if ("generateYearReport".equals(action)) {
            // Expecting parameter "year"
            int year = Integer.parseInt(request.getParameter("year"));
            Report report = ReportFactory.generateReportByYear(year);
            request.setAttribute("message", "Report generated for year " + year + " (ID: " + report.getReportId() + ")");
        } else if ("exportReport".equals(action)) {
            // Expecting parameters "reportId" and "exportType" (either "pdf" or "csv")
            String reportId = request.getParameter("reportId");
            String exportType = request.getParameter("exportType");
            Report report = ReportFactory.getReport(reportId);
            boolean exportSuccess = false;
            if (report != null) {
                if ("pdf".equalsIgnoreCase(exportType)) {
                    exportSuccess = ReportFactory.exportReportToPDF(report, "export_" + reportId + ".pdf");
                } else if ("csv".equalsIgnoreCase(exportType)) {
                    exportSuccess = ReportFactory.exportReportToCSV(report, "export_" + reportId + ".csv");
                }
            }
            request.setAttribute("message", "Export " + (exportSuccess ? "successful" : "failed") + " for report " + reportId);
        }

        // Retrieve all reports from ReportFactory
        Map<String, Report> reportMap = ReportFactory.getAllReports();
        request.setAttribute("allReports", reportMap.values());

        // Forward to Reports.jsp (adjust the path if necessary)
        request.getRequestDispatcher("/WEB-INF/reports.jsp").forward(request, response);
    }

    /**
     * Handles POST requests by delegating to doGet.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
