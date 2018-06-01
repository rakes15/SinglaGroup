package orderbooking.catalogue.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerGroupDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String GroupID, GroupName, GroupImage;

    public RecyclerGroupDataset() {

    }

    public RecyclerGroupDataset(String GroupID, String GroupName, String GroupImage) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.GroupImage = GroupImage;
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

    public String getGroupImage() {
        return GroupImage;
    }
    public void setGroupImage(String GroupImage) {
        this.GroupImage = GroupImage;
    }
}