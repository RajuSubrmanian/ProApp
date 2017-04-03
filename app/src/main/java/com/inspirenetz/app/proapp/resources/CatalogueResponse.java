package com.inspirenetz.app.proapp.resources;

import java.util.List;

/**
 * Created by raju on 2/16/17.
 */

public class CatalogueResponse extends BaseResource{

    private List<CatalogueResource> data;

    public List<CatalogueResource> getData() {
        return data;
    }

    public void setData(List<CatalogueResource> data) {
        this.data = data;
    }
}
