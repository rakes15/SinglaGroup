package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseAddOrRemoveDataset {

    String TAG= ResponseAddOrRemoveDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
