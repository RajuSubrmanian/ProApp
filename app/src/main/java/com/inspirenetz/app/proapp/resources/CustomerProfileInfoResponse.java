package com.inspirenetz.app.proapp.resources;

/**
 * Created by raju on 2/6/17.
 */

public class CustomerProfileInfoResponse extends BaseResource {

    private CustomerProfileInfoResource data;

    public CustomerProfileInfoResource getData() {
        return data;
    }

    public void setData(CustomerProfileInfoResource data) {
        this.data = data;
    }
}
