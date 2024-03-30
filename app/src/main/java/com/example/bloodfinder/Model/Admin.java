package com.example.bloodfinder.Model;

public class Admin {

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    private String AId;
    private String Name;
    private String City;
    private String phoneNumber;
    private String Email;
    private String Password;
    private String aId;
    private String imageUrl;
    private int usertype;

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public Admin() {
    }

    public Admin(String aId, String name, String city, String phoneNumber, String email, String password, int usertype) {
        AId = aId;
        Name = name;
        City = city;
        this.phoneNumber = phoneNumber;
        Email = email;
        Password = password;
        usertype = usertype;

        //String imageUrl
        //this.imageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
//    public String getaId() {
//        return aId;
//    }
//
//    public void setaId(String aId) {
//        this.aId = aId;
//    }


}
