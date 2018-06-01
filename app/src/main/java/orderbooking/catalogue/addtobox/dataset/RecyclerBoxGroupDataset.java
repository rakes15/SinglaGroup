package orderbooking.catalogue.addtobox.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerBoxGroupDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String GroupID, GroupName,GroupImage,MainGroup;

    public RecyclerBoxGroupDataset() {

    }
    public RecyclerBoxGroupDataset(String GroupID, String GroupName, String GroupImage, String MainGroup) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.GroupImage = GroupImage;
        this.MainGroup = MainGroup;
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

    public String getMainGroup() {
        return MainGroup;
    }

    public void setMainGroup(String MainGroup) {
        this.MainGroup = MainGroup;
    }}
