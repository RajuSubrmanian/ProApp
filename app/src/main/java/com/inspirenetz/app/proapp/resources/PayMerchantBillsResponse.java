package com.inspirenetz.app.proapp.resources;

/**
 * Created by raju on 2/24/17.
 */

public class PayMerchantBillsResponse extends BaseResource {

    private PayWithPointsResponse data;

    public PayWithPointsResponse getData() {
        return data;
    }

    public void setData(PayWithPointsResponse data) {
        this.data = data;
    }
}
