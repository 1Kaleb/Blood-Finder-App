package com.example.bloodfinder.Model;

public class HospitalModel {
    String name, email, phoneNumber, city, lat, lang,  userKey, mapCity, pushKey;

    public HospitalModel() {
    }

    public HospitalModel(String name, String email, String phoneNumber, String city, String lat, String lang, String userKey, String mapCity, String pushKey) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.lat = lat;
        this.lang = lang;
        this.userKey = userKey;
        this.mapCity = mapCity;
        this.pushKey = pushKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getMapCity() {
        return mapCity;
    }

    public void setMapCity(String mapCity) {
        this.mapCity = mapCity;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
