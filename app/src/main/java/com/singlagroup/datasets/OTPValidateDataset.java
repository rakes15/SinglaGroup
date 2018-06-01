package com.singlagroup.datasets;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class OTPValidateDataset {
    @SerializedName("Status")
    private int Status;

    public OTPValidateDataset(int Status) {

        this.Status = Status;
    }
    public int getStatus() {
        return Status;
    }
    public void setStatus(int Status) {
        this.Status = Status;
    }
}
