package com.devgroup.enterprise_helpdesk_api.report.dto;

public class CategoryReportResponse {

    private String categoryName;
    private long totalTickets;

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public long getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

}
