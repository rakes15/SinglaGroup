package uploadimagesfiles.documentattachment.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import uploadimagesfiles.documentattachment.datasets.Result;
/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseDocumentDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<Result> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<Result> getResult() {
        return result;
    }
    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
