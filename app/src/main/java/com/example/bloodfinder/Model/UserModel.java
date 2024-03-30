package com.example.bloodfinder.Model;

import java.util.Date;

public class UserModel {
    String name, email, phone, password, gander, bloodGroup, bGroup, city, imageUrl, verified, tokenId, uId, lastDonation;
    int usertype,donationNumber;
    //Date lastDonation;

    public UserModel() {
    }

    public UserModel(String name, String email, String phone, String password, String gander, String bloodGroup, String bGroup, String city, String imageUrl, String verified, int usertype, String tokenId, String uId, String lastDonation, int donationNumber) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.gander = gander;
       // this.bloodGroup = bloodGroup;
        this.bGroup = bGroup;
        this.city = city;
        this.imageUrl = imageUrl;
        this.verified = verified;
        usertype = usertype;
        this.tokenId = tokenId;
        this.uId = uId;
        this.lastDonation = lastDonation;
        this.donationNumber = donationNumber;
    }

    public int getDonationNumber() {
        return donationNumber;
    }

    public void setDonationNumber(int donationNumber) {
        this.donationNumber = donationNumber;
    }

   // public Date getLastDonation() {
     //   return lastDonation;
    //}
    public String getLastDonation() {
        return lastDonation;
    }


//    public void setLastDonation(Date lastDonation) {
//        this.lastDonation = lastDonation;
//    }
public void setLastDonation(String lastDonation) {
    this.lastDonation = lastDonation;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGander() {
        return gander;
    }

    public void setGander(String gander) {
        this.gander = gander;
    }
    public String getBloodGroup() {
        //return bloodGroup;
        return bGroup;
    }

   // public void setBloodGroup(String bloodGroup) {
   public void setBloodGroup(String bGroup) {
       // this.bloodGroup = bloodGroup;
        this.bGroup = bGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
    public int getUserType() {
        return usertype;
    }

    public void setUserType(int usertype) {
        this.usertype = usertype;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
