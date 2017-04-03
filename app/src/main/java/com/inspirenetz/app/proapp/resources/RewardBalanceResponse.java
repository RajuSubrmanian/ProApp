package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by fayiz-ci on 15/6/16.
 */
public class RewardBalanceResponse extends BaseResource{

    private List<RewardBalanceResource> data ;

    private List<RewardBalanceResource> balance ;

    public List<RewardBalanceResource> getData() {
        return data;
    }

    public void setData(List<RewardBalanceResource> data) {
        this.data = data;
    }

    public List<RewardBalanceResource> getBalance() {
        return balance;
    }

    public void setBalance(List<RewardBalanceResource> balance) {
        this.balance = balance;
    }


}
