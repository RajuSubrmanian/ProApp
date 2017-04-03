package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by raju on 2/26/17.
 */

public class RedemptionPartnerResponse extends BaseResource {

    private List<RedemptionPartnerResource> data;

    public List<RedemptionPartnerResource> getData() {
        return data;
    }

    public void setData(List<RedemptionPartnerResource> data) {
        this.data = data;
    }
}
