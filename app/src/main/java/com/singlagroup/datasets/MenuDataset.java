package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class MenuDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String MainGroupID,MainGroupName;

    public MenuDataset() {

    }
    public MenuDataset(String MainGroupID, String MainGroupName) {

        this.MainGroupID = MainGroupID;
        this.MainGroupName = MainGroupName;
    }
    public String getMainGroupID() {
        return MainGroupID;
    }
    public void setMainGroupID(String MainGroupID) {
        this.MainGroupID = MainGroupID;
    }

    public String getMainGroupName() {
        return MainGroupName;
    }
    public void setMainGroupName(String MainGroupName) {
        this.MainGroupName = MainGroupName;
    }

}
