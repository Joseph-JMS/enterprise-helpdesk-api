package com.devgroup.enterprise_helpdesk_api.report.dto;

public class TechnicianReportResponse {

    private String technicianUsername;
    private long assignedTickets;
    private long resolvedTickets;

    public String getTechnicianUsername() {
        return technicianUsername;
    }
    public void setTechnicianUsername(String technicianUsername) {
        this.technicianUsername = technicianUsername;
    }
    public long getAssignedTickets() {
        return assignedTickets;
    }
    public void setAssignedTickets(long assignedTickets) {
        this.assignedTickets = assignedTickets;
    }
    public long getResolvedTickets() {
        return resolvedTickets;
    }
    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

}
