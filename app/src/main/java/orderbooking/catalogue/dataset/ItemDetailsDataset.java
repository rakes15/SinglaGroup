package orderbooking.catalogue.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ItemDetailsDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String ItemName, Itemcode, ItemID,ItemImage,GroupID;
    public ItemDetailsDataset(String ItemName, String Itemcode, String ItemID, String ItemImage, String GroupID) {

        this.ItemName = ItemName;
        this.Itemcode = Itemcode;
        this.ItemID = ItemID;
        this.ItemImage = ItemImage;
        this.GroupID = GroupID;
    }
    public String getItemName() {
        return ItemName;
    }
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getItemcode() {
        return Itemcode;
    }
    public void setItemcode(String Itemcode) {
        this.Itemcode = Itemcode;
    }

    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
    }

    public String getItemImage() {
        return ItemImage;
    }
    public void setItemImage(String ItemImage) {
        this.ItemImage = ItemImage;
    }

    public String getGroupID() {
        return GroupID;
    }
    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }
}
