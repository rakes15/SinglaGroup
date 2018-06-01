package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;
import com.singlagroup.datasets.CityDataset;

import java.util.ArrayList;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseCityListDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ArrayList<CityDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<CityDataset> getResult() {
        return result;
    }
    public void setResult(ArrayList<CityDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
