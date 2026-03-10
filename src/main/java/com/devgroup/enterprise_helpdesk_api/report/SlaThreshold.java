package com.devgroup.enterprise_helpdesk_api.report;

import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketPriority;

public enum SlaThreshold {

    CRITICAL(4),
    HIGH(8),
    MEDIUM(24),
    LOW(72);

    private final int hours;

    SlaThreshold(int hours) {
        this.hours = hours;
    }

    public static int getHours(TicketPriority priority) {
        return switch (priority) {
            case CRITICAL -> CRITICAL.hours;
            case HIGH -> HIGH.hours;
            case MEDIUM -> MEDIUM.hours;
            case LOW -> LOW.hours;
        };
    }

}
