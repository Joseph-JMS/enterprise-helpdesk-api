package com.devgroup.enterprise_helpdesk_api.report.dto;

public class SlaReportResponse {

    private String priority;
    private int thresholdHours;
    private long totalTickets;
    private long withinSla;
    private long breachedSla;
    private double compliancePercentage;

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public int getThresholdHours() {
        return thresholdHours;
    }
    public void setThresholdHours(int thresholdHours) {
        this.thresholdHours = thresholdHours;
    }
    public long getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }
    public long getWithinSla() {
        return withinSla;
    }
    public void setWithinSla(long withinSla) {
        this.withinSla = withinSla;
    }
    public long getBreachedSla() {
        return breachedSla;
    }
    public void setBreachedSla(long breachedSla) {
        this.breachedSla = breachedSla;
    }
    public double getCompliancePercentage() {
        return compliancePercentage;
    }
    public void setCompliancePercentage(double compliancePercentage) {
        this.compliancePercentage = compliancePercentage;
    }

}
