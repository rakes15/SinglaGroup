package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseLogInUserMainDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ResponseLogInUserSubDataset result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseLogInUserSubDataset getResult() {
        return result;
    }
    public void setResult(ResponseLogInUserSubDataset result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
