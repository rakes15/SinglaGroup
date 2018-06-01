package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class GodownDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String GodownID,GodownName,UnderID;
    int Active;

    public GodownDataset(String GodownID, String GodownName, int Active, String UnderID) {
        this.GodownID = GodownID;
        this.GodownName = GodownName;
        this.Active = Active;
        this.UnderID = UnderID;
    }
    public String getGodownID() {
        return GodownID;
    }
    public void setGodownID(String GodownID) {
        this.GodownID = GodownID;
    }

    public String getGodownName() {
        return GodownName;
    }
    public void setGodownName(String GodownName) {
        this.GodownName = GodownName;
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
