package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class DivisionDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String DivisionID,DivisionName,UnderID,DateTime;
    int Active;

    public DivisionDataset(String DivisionID, String DivisionName, int Active,String UnderID,String DateTime) {
        this.DivisionID = DivisionID;
        this.DivisionName = DivisionName;
        this.Active = Active;
        this.UnderID = UnderID;
        this.DateTime = DateTime;
    }
    public String getDivisionID() {
        return DivisionID;
    }
    public void setDivisionID(String DivisionID) {
        this.DivisionID = DivisionID;
    }

    public String getDivisionName() {
        return DivisionName;
    }
    public void setDivisionName(String DivisionName) {
        this.DivisionName = DivisionName;
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

    public String getDateTime() {
        return DateTime;
    }
    public void setDateTime(String DateTime) {
        this.DateTime = DateTime;
    }
}
