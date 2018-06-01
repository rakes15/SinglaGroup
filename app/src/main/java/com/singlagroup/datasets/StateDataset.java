package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class StateDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String StateID,StateName,CountryID;

    public StateDataset() {

    }
    public StateDataset(String StateID, String StateName, String CountryID) {

        this.StateID = StateID;
        this.StateName = StateName;
        this.CountryID = CountryID;
    }
    public String getStateID() {
        return StateID;
    }
    public void setStateID(String StateID) {
        this.StateID = StateID;
    }

    public String getStateName() {
        return StateName;
    }
    public void setStateName(String StateName) {
        this.StateName = StateName;
    }

    public String getCountryID() {
        return CountryID;
    }
    public void setCountryID(String CountryID) {
        this.CountryID = CountryID;
    }

}
