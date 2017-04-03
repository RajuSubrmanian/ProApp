package com.inspirenetz.app.proapp.resources;

import java.io.Serializable;

/**
 * Created by fayiz-ci on 16/6/16.
 */
public class CustomerResource implements Serializable {


    private long customerno ;

    private String loyalty_id ;

    private String firstname ;

    private String lastname ;

    private String email ;

    private String mobile ;

    private String address;

    private String city ;

    private String pincode ;

    private String birthday ;

    private String anniversary ;

    private String referralCode;

    public long getCustomerno() {
        return customerno;
    }

    public void setCustomerno(long customerno) {
        this.customerno = customerno;
    }

    public String getLoyalty_id() {
        return loyalty_id;
    }

    public void setLoyalty_id(String loyalty_id) {
        this.loyalty_id = loyalty_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    @Override
    public String toString() {
        return "CustomerResource{" +
                "customerno=" + customerno +
                ", loyalty_id='" + loyalty_id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", pincode='" + pincode + '\'' +
                ", birthday='" + birthday + '\'' +
                ", anniversary='" + anniversary + '\'' +
                ", referralCode='" + referralCode + '\'' +
                '}';
    }
}
