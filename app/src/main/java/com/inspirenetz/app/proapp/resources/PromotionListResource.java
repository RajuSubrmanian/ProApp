package com.inspirenetz.app.proapp.resources;

import java.io.Serializable;

/**
 * Created by raju on 2/4/17.
 */

public class PromotionListResource implements Serializable {

    private String prmId;

    private String prmMerchantNo;

    private String prmName;

    private String prmShortDescription;

    private String prmLongDescription;

    private String prmMoreDetails;

    private String prmImage;

    private String prmExpiryDate;

    private String prmNumViews;

    private String prmImagePath;

    private String prmClaimExpiryDays;

    public String getPrmId() {
        return prmId;
    }

    public void setPrmId(String prmId) {
        this.prmId = prmId;
    }

    public String getPrmMerchantNo() {
        return prmMerchantNo;
    }

    public void setPrmMerchantNo(String prmMerchantNo) {
        this.prmMerchantNo = prmMerchantNo;
    }

    public String getPrmName() {
        return prmName;
    }

    public void setPrmName(String prmName) {
        this.prmName = prmName;
    }

    public String getPrmLongDescription() {
        return prmLongDescription;
    }

    public void setPrmLongDescription(String prmLongDescription) {
        this.prmLongDescription = prmLongDescription;
    }

    public String getPrmMoreDetails() {
        return prmMoreDetails;
    }

    public void setPrmMoreDetails(String prmMoreDetails) {
        this.prmMoreDetails = prmMoreDetails;
    }

    public String getPrmImage() {
        return prmImage;
    }

    public void setPrmImage(String prmImage) {
        this.prmImage = prmImage;
    }

    public String getPrmExpiryDate() {
        return prmExpiryDate;
    }

    public void setPrmExpiryDate(String prmExpiryDate) {
        this.prmExpiryDate = prmExpiryDate;
    }

    public String getPrmNumViews() {
        return prmNumViews;
    }

    public void setPrmNumViews(String prmNumViews) {
        this.prmNumViews = prmNumViews;
    }

    public String getPrmImagePath() {
        return prmImagePath;
    }

    public void setPrmImagePath(String prmImagePath) {
        this.prmImagePath = prmImagePath;
    }

    public String getPrmShortDescription() {
        return prmShortDescription;
    }

    public void setPrmShortDescription(String prmShortDescription) {
        this.prmShortDescription = prmShortDescription;
    }

    public String getPrmClaimExpiryDays() {
        return prmClaimExpiryDays;
    }

    public void setPrmClaimExpiryDays(String prmClaimExpiryDays) {
        this.prmClaimExpiryDays = prmClaimExpiryDays;
    }
}
