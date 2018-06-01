package com.singlagroup.datasets;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class TempDeviceRegistrationDataset {
    @SerializedName("TempID")
    private String TempID;

    public TempDeviceRegistrationDataset(String TempID) {

        this.TempID = TempID;
    }
    public String getTempID() {
        return TempID;
    }
    public void setTempID(String TempID) {
        this.TempID = TempID;
    }
}
