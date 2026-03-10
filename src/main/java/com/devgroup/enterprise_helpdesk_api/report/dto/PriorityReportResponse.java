package com.devgroup.enterprise_helpdesk_api.report.dto;

public class PriorityReportResponse {

    private String priority;
    private long totalTickets;

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public long getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

}
