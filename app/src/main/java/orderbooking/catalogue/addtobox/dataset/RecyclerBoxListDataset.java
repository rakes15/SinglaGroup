package orderbooking.catalogue.addtobox.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerBoxListDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String GroupID, GroupName, ItemName, Itemcode, ItemID, ColorID,ColorName,ItemImage;

    public RecyclerBoxListDataset() {

    }
    public RecyclerBoxListDataset(String GroupID, String GroupName, String ItemName, String Itemcode, String ItemID,String ColorID,String ColorName, String ItemImage) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.ItemName = ItemName;
        this.Itemcode = Itemcode;
        this.ItemID = ItemID;
        this.ColorID = ColorID;
        this.ColorName = ColorName;
        this.ItemImage = ItemImage;
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
    public String getColorID() {
        return ColorID;
    }

    public void setColorID(String ColorID) {
        this.ColorID = ColorID;
    }
    public String getColorName() {
        return ColorName;
    }

    public void setColorName(String ColorName) {
        this.ColorName = ColorName;
    }
    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String ItemImage) {
        this.ItemImage = ItemImage;
    }

}
