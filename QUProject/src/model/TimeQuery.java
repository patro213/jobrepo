package model;

import java.util.Date;

public class TimeQuery {
    private String objectType;
    private String serviceId;
    private String queryTypeId;
    private String responseType;
    private Date dateFrom;
    private Date dateTo;
    private double time;

    public TimeQuery() {

    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getQueryTypeId() {
        return queryTypeId;
    }

    public void setQueryTypeId(String queryTypeId) {
        this.queryTypeId = queryTypeId;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

}
