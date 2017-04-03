package com.inspirenetz.app.proapp.resources;

/**
 * Created by raju on 2/27/17.
 */

public class CatalogueCartItemResource {

    private String catProductNo;

    private CatalogueResource catalogueResource;

    private int qty;

    private String cartOperation;

    private String status;

    public String getCatProductNo() {
        return catProductNo;
    }

    public void setCatProductNo(String catProductNo) {
        this.catProductNo = catProductNo;
    }

    public CatalogueResource getCatalogueResource() {
        return catalogueResource;
    }

    public void setCatalogueResource(CatalogueResource catalogueResource) {
        this.catalogueResource = catalogueResource;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCartOperation() {
        return cartOperation;
    }

    public void setCartOperation(String cartOperation) {
        this.cartOperation = cartOperation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
