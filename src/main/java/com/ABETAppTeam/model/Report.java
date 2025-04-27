package com.ABETAppTeam.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Report class for storing aggregated data from FCARs, Indicators, Outcomes, etc.
 * This is a simple container that can be expanded to include whatever information
 * you need for your final report (e.g., tables, statistics, etc.).
 */
public class Report {

    private String reportId;              // Unique identifier for the report
    private String reportTitle;           // Title of the report
    private String semester;              // Semester (e.g., "Fall", "Spring", "Summer")
    private int year;                     // Year
    private List<FCAR> fcarList;          // FCARs included in this report
    // You can add lists/maps for Indicators, Outcomes, etc.:
    // private List<Indicator> indicatorList;
    // private List<Outcome> outcomeList;
    // ... and any other relevant data

    /**
     * Constructor
     */
    public Report(String reportId, String reportTitle) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.fcarList = new ArrayList<>();
    }

    // Getters and setters

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

    // Additional fields for Indicators, Outcomes, etc., can go here.
}
