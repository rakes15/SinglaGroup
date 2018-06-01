package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ShowroomDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String ShowroomID,ShowroomName,UnderID;
    int Active;

    public ShowroomDataset(String ShowroomID, String ShowroomName, int Active, String UnderID) {
        this.ShowroomID = ShowroomID;
        this.ShowroomName = ShowroomName;
        this.Active = Active;
        this.UnderID = UnderID;
    }
    public String getShowroomID() {
        return ShowroomID;
    }
    public void setShowroomID(String ShowroomID) {
        this.ShowroomID = ShowroomID;
    }

    public String getShowroomName() {
        return ShowroomName;
    }
    public void setShowroomName(String ShowroomName) {
        this.ShowroomName = ShowroomName;
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
