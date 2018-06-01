package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import inventory.analysis.catalogue.dataset.RecyclerSimilarItemsDataSet;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseSimilarColorDataset {

    String TAG= ResponseSimilarColorDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<RecyclerSimilarItemsDataSet> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<RecyclerSimilarItemsDataSet> getResult() {
        return result;
    }
    public void setResult(List<RecyclerSimilarItemsDataSet> result) {
        List<RecyclerSimilarItemsDataSet> dataset=new ArrayList<>();
        this.result = (result==null)?dataset:result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
