// Fixed ReportFactoryTest.java
package com.ABETAppTeam;

import com.ABETAppTeam.model.Report;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportFactoryTest {

    @BeforeEach
    void clearReports() {
        // clear the in-memory reportMap
        ReportFactory.getAllReports().clear();
    }

    @Test
    void testGenerateAndRetrieveSemesterReport() {
        // generate one report
        Report rpt = ReportFactory.generateReportBySemester("Fall", 2023);
        assertNotNull(rpt, "Generated report should not be null");

        // work with the actual return type - Map<String, Report>
        Map<String, Report> all = ReportFactory.getAllReports();
        assertEquals(1, all.size(), "Should have exactly one report stored");

        // grab it out of the map by its ID
        assertTrue(all.containsKey(rpt.getReportId()), "Map should contain the report with the generated ID");
        Report fetched = all.get(rpt.getReportId());
        assertSame(rpt, fetched, "Retrieved report should be the same instance");
    }

    @Test
    void testGenerateReportByYear() {
        Report rpt = ReportFactory.generateReportByYear(2022);
        assertNotNull(rpt, "Generated report should not be null");
        assertNull(rpt.getSemester(), "Semester should be null for yearly report");
        assertEquals(2022, rpt.getYear(), "Year should match");
        assertSame(rpt, ReportFactory.getReport(rpt.getReportId()),
                "Retrieved report should be the same instance");
    }

    @Test
    void testGetReportNotFound() {
        assertNull(ReportFactory.getReport("NON_EXISTENT"),
                "Non-existent report ID should return null");
    }

    @Test
    void testExportReportToPDFandCSV() {
        // null report or path => false
        assertFalse(ReportFactory.exportReportToPDF(null, "a.pdf"),
                "Null report should return false");
        assertFalse(ReportFactory.exportReportToPDF(new Report("X","T"), null),
                "Null path should return false");
        assertTrue(ReportFactory.exportReportToPDF(new Report("X","T"), "out.pdf"),
                "Valid report and path should return true");

        assertFalse(ReportFactory.exportReportToCSV(null, "a.csv"),
                "Null report should return false");
        assertFalse(ReportFactory.exportReportToCSV(new Report("X","T"), null),
                "Null path should return false");
        assertTrue(ReportFactory.exportReportToCSV(new Report("X","T"), "out.csv"),
                "Valid report and path should return true");
    }

    @Test
    void testGetAllReports() {
        // initially empty
        Map<String, Report> empty = ReportFactory.getAllReports();
        assertTrue(empty.isEmpty(), "Initial reports map should be empty");

        // generate one report
        Report rpt = ReportFactory.generateReportBySemester("Spring", 2025);
        assertNotNull(rpt, "Generated report should not be null");

        // verify via Map
        Map<String, Report> all = ReportFactory.getAllReports();
        assertEquals(1, all.size(), "Should have one report after generation");
        assertTrue(all.containsKey(rpt.getReportId()), "Map should contain the report with the generated ID");
        assertSame(rpt, all.get(rpt.getReportId()), "Retrieved report should be the same instance");
    }

    @Test
    void testGenerateMultipleReportsStoresAll() {
        // first report
        Report rpt1 = ReportFactory.generateReportBySemester("Fall", 2023);
        assertNotNull(rpt1, "First generated report should not be null");

        // second report
        Report rpt2 = ReportFactory.generateReportByYear(2024);
        assertNotNull(rpt2, "Second generated report should not be null");

        // verify via Map
        Map<String, Report> all = ReportFactory.getAllReports();
        assertEquals(2, all.size(), "Should store both reports");
        assertSame(rpt1, all.get(rpt1.getReportId()), "First report should be retrievable by ID");
        assertSame(rpt2, all.get(rpt2.getReportId()), "Second report should be retrievable by ID");
    }
}