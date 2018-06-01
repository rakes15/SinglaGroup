package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;
import com.singlagroup.datasets.CompanyDataset;
import com.singlagroup.datasets.DivisionEmpDataset;

import java.util.ArrayList;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseDivisionListDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ArrayList<CompanyDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<CompanyDataset> getResult() {
        return result;
    }
    public void setResult(ArrayList<CompanyDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
