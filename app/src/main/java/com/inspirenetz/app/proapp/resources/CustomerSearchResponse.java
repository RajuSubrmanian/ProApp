package com.inspirenetz.app.proapp.resources;

/**
 * Created by fayiz-ci on 16/6/16.
 */
public class CustomerSearchResponse extends BaseResource {


    private CustomerResource data;

    public CustomerResource getData() {
        return data;
    }

    public void setData(CustomerResource data) {
        this.data = data;
    }
}
