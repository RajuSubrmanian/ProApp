package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by raju on 2/7/17.
 */

public class CustomerActivityListResponse extends BaseResource {

    private List<CustomerActivityListResource> data;

    public List<CustomerActivityListResource> getData() {
        return data;
    }

    public void setData(List<CustomerActivityListResource> data) {
        this.data = data;
    }
}
