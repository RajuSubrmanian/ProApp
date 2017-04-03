package com.inspirenetz.app.proapp.resources;

/**
 * Created by fayiz-ci on 15/6/16.
 */
public class UserAuthenticationResponse extends BaseResource {

    private UserInfoResource data ;

    public UserInfoResource getData() {
        return data;
    }

    public void setData(UserInfoResource data) {
        this.data = data;
    }
}
