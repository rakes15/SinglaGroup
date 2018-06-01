package uploadimagesfiles.voucherdocupload.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uploadimagesfiles.datasets.ItemInfoDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseVoucherDocumentDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<VoucherDocumentDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<VoucherDocumentDataset> getResult() {
        return result;
    }
    public void setResult(List<VoucherDocumentDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
