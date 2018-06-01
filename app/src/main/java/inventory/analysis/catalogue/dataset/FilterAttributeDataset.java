package inventory.analysis.catalogue.dataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class FilterAttributeDataset {
    @SerializedName("CaptionID")
    private String CaptionID;
    @SerializedName("CaptionName")
    private String CaptionName;

    public FilterAttributeDataset(String CaptionID, String CaptionName) {

        this.CaptionID = CaptionID;
        this.CaptionName = CaptionName;
    }
    public String getCaptionID() {
        return CaptionID;
    }
    public void setCaptionID(String CaptionID) {
        this.CaptionID = CaptionID;
    }

    public String getCaptionName() {
        return CaptionName;
    }
    public void setCaptionName(String CaptionName) {
        this.CaptionName = CaptionName;
    }
}
