package vn.iotstar.dating_fe.entities;

import java.util.Date;

public class Reports {
    private Long id;

    private Long reporter;
    private Long reported;
    private String reason;
    private Date reportAt;

    public Reports() {
    }

    public Reports(Long id, Long reporter, Long reported, String reason, Date reportAt) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.reportAt = reportAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporter() {
        return reporter;
    }

    public void setReporter(Long reporter) {
        this.reporter = reporter;
    }

    public Long getReported() {
        return reported;
    }

    public void setReported(Long reported) {
        this.reported = reported;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReportAt() {
        return reportAt;
    }

    public void setReportAt(Date reportAt) {
        this.reportAt = reportAt;
    }
}
