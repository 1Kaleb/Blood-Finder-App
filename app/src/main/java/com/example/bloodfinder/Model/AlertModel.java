package com.example.bloodfinder.Model;

public class AlertModel {

    String name, bloodGroup,date, city, alertId, aId;

    public AlertModel() {
    }

    public AlertModel(String name, String bloodGroup, String date, String city, String alertId, String aId) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.date = date;
        this.city = city;
        this.alertId = alertId;
        this.aId = aId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }
}
