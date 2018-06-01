package orderbooking.catalogue.responsedataset;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseItemDetailsDataset {

    String TAG=ResponseItemDetailsDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ResponseItemDetailsSubDataset result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseItemDetailsSubDataset getResult() {
        return result;
    }
    public void setResult(ResponseItemDetailsSubDataset result) {
        ResponseItemDetailsSubDataset dataset=new ResponseItemDetailsSubDataset();
        this.result = (result==null)?dataset:result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
