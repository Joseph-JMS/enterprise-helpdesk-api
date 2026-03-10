package com.devgroup.enterprise_helpdesk_api.report.dto;

public class SummaryResponse {

    private long totalTickets;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;
    private long cancelled;

    public long getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }
    public long getOpen() {
        return open;
    }
    public void setOpen(long open) {
        this.open = open;
    }
    public long getInProgress() {
        return inProgress;
    }
    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }
    public long getResolved() {
        return resolved;
    }
    public void setResolved(long resolved) {
        this.resolved = resolved;
    }
    public long getClosed() {
        return closed;
    }
    public void setClosed(long closed) {
        this.closed = closed;
    }
    public long getCancelled() {
        return cancelled;
    }
    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }

}
