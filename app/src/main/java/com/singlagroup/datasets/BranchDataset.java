package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class BranchDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String BranchID,BranchName,UnderID;
    int Active;

    public BranchDataset(String BranchID, String BranchName, int Active,String UnderID) {
        this.BranchID = BranchID;
        this.BranchName = BranchName;
        this.Active = Active;
        this.UnderID = UnderID;
    }
    public String getBranchID() {
        return BranchID;
    }
    public void setBranchID(String BranchID) {
        this.BranchID = BranchID;
    }

    public String getBranchName() {
        return BranchName;
    }
    public void setBranchName(String BranchName) {
        this.BranchName = BranchName;
    }

    public int getActive() {
        return Active;
    }
    public void setActive(int Active) {
        this.Active = Active;
    }

    public String getUnderID() {
        return UnderID;
    }
    public void setUnderID(String UnderID) {
        this.UnderID = UnderID;
    }
}
