package orderbooking.catalogue.addtobox.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseMoveToWishlistDataset {

    String TAG=ResponseMoveToWishlistDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private Map<String,String> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String,String> getResult() {
        return result;
    }
    public void setResult(Map<String,String> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
