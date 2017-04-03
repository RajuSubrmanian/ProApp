package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by raju on 2/4/17.
 */

public class PromotionListResponse extends BaseResource {

    private List<PromotionListResource> data;

    public List<PromotionListResource> getData() {
        return data;
    }

    public void setData(List<PromotionListResource> data) {
        this.data = data;
    }
}
