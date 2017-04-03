package com.inspirenetz.app.proapp.resources;

/**
 * Created by fayiz-ci on 23/6/16.
 */
public class BaseResource {

    private String status ;

    private String errorcode ;

    private String errordesc;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }
}
