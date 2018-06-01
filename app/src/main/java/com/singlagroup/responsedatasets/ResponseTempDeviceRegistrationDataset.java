package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;
import com.singlagroup.datasets.TempDeviceRegistrationDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseTempDeviceRegistrationDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private TempDeviceRegistrationDataset result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public TempDeviceRegistrationDataset getResult() {
        return result;
    }
    public void setResult(TempDeviceRegistrationDataset result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
