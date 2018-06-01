package uploadimagesfiles.datasets;

import java.io.Serializable;
import java.sql.Blob;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ItemInfoDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String MainGroup,GroupName,SubGroup,ItemID,ItemName,ItemCode,ItemImage,SmallImage,MasterID;
    int ImageStatus,MRP,Rate;
    
    public ItemInfoDataset(String MainGroup,String GroupName,String SubGroup,String ItemID,String ItemName,String ItemCode,String ItemImage,String SmallImage,String MasterID,int ImageStatus,int MRP,int Rate) {
        this.MainGroup = MainGroup;
        this.GroupName = GroupName;
        this.SubGroup = SubGroup;
        this.ItemID = ItemID;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.ItemImage = ItemImage;
        this.SmallImage = SmallImage;
        this.MasterID = MasterID;
        this.ImageStatus = ImageStatus;
        this.MRP = MRP;
        this.Rate = Rate;
    }

    public String getMainGroup() {
        return MainGroup;
    }
    public void setMainGroup(String MainGroup) {
        this.MainGroup = MainGroup;
    }

    public String getGroupName() {
        return GroupName;
    }
    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    public String getSubGroup() {
        return SubGroup;
    }
    public void setSubGroup(String SubGroup) {
        this.SubGroup = SubGroup;
    }

    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
    }

    public String getItemName() {
        return ItemName;
    }
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getItemCode() {
        return ItemCode;
    }
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    public String getItemImage() {
        return ItemImage;
    }
    public void setItemImage(String ItemImage) {
        this.ItemImage = ItemImage;
    }

    public String getSmallImage() {
        return SmallImage;
    }
    public void setSmallImage(String SmallImage) {
        this.SmallImage = SmallImage;
    }

    public String getMasterID() {
        return MasterID;
    }
    public void setMasterID(String MasterID) {
        this.MasterID = MasterID;
    }

    public int getImageStatus() {
        return ImageStatus;
    }
    public void setImageStatus(int ImageStatus) {
        this.ImageStatus = ImageStatus;
    }

    public int getMRP() {
        return MRP;
    }
    public void setMRP(int MRP) {
        this.MRP = MRP;
    }

    public int getRate() {
        return Rate;
    }
    public void setRate(int Rate) {
        this.Rate = Rate;
    }
}
