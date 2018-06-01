package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseFilterDataset {

    String TAG= ResponseFilterDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private ResponseFilterSubDataset result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseFilterSubDataset getResult() {
        return result;
    }
    public void setResult(ResponseFilterSubDataset result) {
        ResponseFilterSubDataset dataset=new ResponseFilterSubDataset();
        this.result = (result==null)?dataset:result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
