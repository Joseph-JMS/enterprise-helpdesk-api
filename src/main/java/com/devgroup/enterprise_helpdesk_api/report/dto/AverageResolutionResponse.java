package com.devgroup.enterprise_helpdesk_api.report.dto;

public class AverageResolutionResponse {

    private String priority;
    private double averageHours;

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public double getAverageHours() {
        return averageHours;
    }
    public void setAverageHours(double averageHours) {
        this.averageHours = averageHours;
    }

}
