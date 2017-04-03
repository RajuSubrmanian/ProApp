package com.inspirenetz.app.proapp.resources;

/**
 * Created by fayiz-ci on 15/6/16.
 */
public class RewardBalanceResource {

    private String rwd_name ;

    private String rwd_currency_id ;

    private double rwd_balance ;

    private String cashback_value ;

    public String getRwd_name() {
        return rwd_name;
    }

    public void setRwd_name(String rwd_name) {
        this.rwd_name = rwd_name;
    }

    public String getRwd_currency_id() {
        return rwd_currency_id;
    }

    public void setRwd_currency_id(String rwd_currency_id) {
        this.rwd_currency_id = rwd_currency_id;
    }

    public double getRwd_balance() {
        return rwd_balance;
    }

    public void setRwd_balance(double rwd_balance) {
        this.rwd_balance = rwd_balance;
    }

    public String getCashback_value() {
        return cashback_value;
    }

    public void setCashback_value(String cashback_value) {
        this.cashback_value = cashback_value;
    }
}
