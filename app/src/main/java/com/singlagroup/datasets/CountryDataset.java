package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class CountryDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String CountryID,CountryName;

    public CountryDataset(String CountryID, String CountryName) {

        this.CountryID = CountryID;
        this.CountryID = CountryName;
    }
    public String getCountryID() {
        return CountryID;
    }
    public void setCountryID(String CountryID) {
        this.CountryID = CountryID;
    }

    public String getCountryName() {
        return CountryName;
    }
    public void setCountryName(String CountryName) {
        this.CountryName = CountryName;
    }

}
