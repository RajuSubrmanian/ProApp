package com.inspirenetz.app.proapp.resources;

/**
 * Created by raju on 2/3/17.
 */

public class CustomerRewardBalanceResponse extends BaseResource{

    private RewardBalanceResource data;

    public RewardBalanceResource getData() {
        return data;
    }

    public void setData(RewardBalanceResource data) {
        this.data = data;
    }
}
