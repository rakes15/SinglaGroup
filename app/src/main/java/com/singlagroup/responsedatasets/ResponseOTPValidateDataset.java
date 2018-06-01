package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;
import com.singlagroup.datasets.OTPValidateDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseOTPValidateDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private OTPValidateDataset result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public OTPValidateDataset getResult() {
        return result;
    }
    public void setResult(OTPValidateDataset result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
