package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class CityDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String CityID,CityName,StateID;

    public CityDataset() {

    }
    public CityDataset(String CityID, String CityName, String StateID) {

        this.CityID = CityID;
        this.CityName = CityName;
        this.StateID = StateID;
    }
    public String getCityID() {
        return CityID;
    }
    public void setCityID(String CityID) {
        this.CityID = CityID;
    }

    public String getCityName() {
        return CityName;
    }
    public void setCityName(String CityName) {
        this.CityName = CityName;
    }

    public String getStateID() {
        return StateID;
    }
    public void setStateID(String StateID) {
        this.StateID = StateID;
    }

}
