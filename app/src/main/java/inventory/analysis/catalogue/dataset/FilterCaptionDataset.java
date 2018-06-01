package inventory.analysis.catalogue.dataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class FilterCaptionDataset {
    @SerializedName("GroupID")
    private String GroupID;
    @SerializedName("GroupName")
    private String GroupName;
    @SerializedName("CaptionID")
    private String CaptionID;
    @SerializedName("CaptionName")
    private String CaptionName;

    public FilterCaptionDataset(String GroupID, String GroupName, String CaptionID, String CaptionName) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.CaptionID = CaptionID;
        this.CaptionName = CaptionName;
    }
    public String getGroupID() {
        return GroupID;
    }
    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }

    public String getGroupName() {
        return GroupName;
    }
    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
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
