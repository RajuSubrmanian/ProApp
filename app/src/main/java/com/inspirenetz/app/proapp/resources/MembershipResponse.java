package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by Raju on 24-Aug-16.
 */
public class MembershipResponse extends BaseResource{

    private List<MembershipResource> data;

    public List<MembershipResource> getData() {
        return data;
    }

    public void setData(List<MembershipResource> data) {
        this.data = data;
    }
}
