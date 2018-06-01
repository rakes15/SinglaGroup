package inventory.analysis.catalogue.addtobox.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseBoxListDataset {

    String TAG=ResponseBoxListDataset.class.getSimpleName();
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<Map<String,String>> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<Map<String,String>> getResult() {
        return result;
    }
    public void setResult(List<Map<String,String>> result) {
        List<Map<String,String>> dataset=new ArrayList<>();
        this.result = (result==null)?dataset:result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
