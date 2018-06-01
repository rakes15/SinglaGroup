package uploadimagesfiles.voucherdocupload.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentSpinnerDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseVoucherDocumentSpinnerDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<VoucherDocumentSpinnerDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<VoucherDocumentSpinnerDataset> getResult() {
        return result;
    }
    public void setResult(List<VoucherDocumentSpinnerDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
