package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import inventory.analysis.catalogue.adapter.RecyclerColorOptionDataSet;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseColorOptionDataset {

    String TAG= ResponseColorOptionDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<RecyclerColorOptionDataSet> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<RecyclerColorOptionDataSet> getResult() {
        return result;
    }
    public void setResult(List<RecyclerColorOptionDataSet> result) {
        List<RecyclerColorOptionDataSet> dataset=new ArrayList<RecyclerColorOptionDataSet>();
        this.result = (result==null)?dataset:result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
