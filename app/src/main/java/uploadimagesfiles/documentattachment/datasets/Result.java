package uploadimagesfiles.documentattachment.datasets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class Result {
    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("CombinedVNo")
    @Expose
    private String combinedVNo;
    @SerializedName("BillNetAmount")
    @Expose
    private String billNetAmount;
    @SerializedName("VDate")
    @Expose
    private String vDate;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getCombinedVNo() {
        return combinedVNo;
    }

    public void setCombinedVNo(String combinedVNo) {
        this.combinedVNo = combinedVNo;
    }

    public String getBillNetAmount() {
        return billNetAmount;
    }

    public void setBillNetAmount(String billNetAmount) {
        this.billNetAmount = billNetAmount;
    }

    public String getVDate() {
        return vDate;
    }

    public void setVDate(String vDate) {
        this.vDate = vDate;
    }

}
