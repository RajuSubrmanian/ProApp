package com.inspirenetz.app.proapp.resources;

/**
 * Created by fayiz-ci on 15/6/16.
 */
public class UserInfoResource {

    private String usrLoginId ;

    private String usrFName ;

    private String usrLName ;

    private int usrStatus;

    private long usrLocation ;

    private String merchantName;

    private long usrMerchantNo ;

    private int usrUserType ;


    public String getUsrLoginId() {
        return usrLoginId;
    }

    public void setUsrLoginId(String usrLoginId) {
        this.usrLoginId = usrLoginId;
    }

    public String getUsrFName() {
        return usrFName;
    }

    public void setUsrFName(String usrFName) {
        this.usrFName = usrFName;
    }

    public String getUsrLName() {
        return usrLName;
    }

    public void setUsrLName(String usrLName) {
        this.usrLName = usrLName;
    }

    public int getUsrStatus() {
        return usrStatus;
    }

    public void setUsrStatus(int usrStatus) {
        this.usrStatus = usrStatus;
    }

    public long getUsrLocation() {
        return usrLocation;
    }

    public void setUsrLocation(long usrLocation) {
        this.usrLocation = usrLocation;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public long getUsrMerchantNo() {
        return usrMerchantNo;
    }

    public void setUsrMerchantNo(long usrMerchantNo) {
        this.usrMerchantNo = usrMerchantNo;
    }

    public int getUsrUserType() {
        return usrUserType;
    }

    public void setUsrUserType(int usrUserType) {
        this.usrUserType = usrUserType;
    }
}
