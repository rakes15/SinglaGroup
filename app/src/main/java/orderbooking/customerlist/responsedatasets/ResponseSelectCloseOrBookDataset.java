package orderbooking.customerlist.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseSelectCloseOrBookDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ArrayList<SelectCustomerForOrderDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<SelectCustomerForOrderDataset> getResult() {
        return result;
    }
    public void setResult(ArrayList<SelectCustomerForOrderDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
