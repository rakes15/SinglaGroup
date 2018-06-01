package inventory.analysis.catalogue.wishlist.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerWishlistDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String GroupID, GroupName, ItemName, Itemcode, ItemID, ItemImage, ItemStock,Rate,Totalcolor,Unit;

    public RecyclerWishlistDataset() {

    }
    public RecyclerWishlistDataset(String GroupID, String GroupName, String ItemName, String Itemcode, String ItemID, String ItemStock, String Rate, String ItemImage,String Totalcolor,String Unit) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.ItemName = ItemName;
        this.Itemcode = Itemcode;
        this.ItemID = ItemID;
        this.ItemImage = ItemImage;
        this.ItemStock = ItemStock;
        this.Rate = Rate;
        this.Totalcolor=Totalcolor;
        this.Unit=Unit;
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
    public String getItemImage() {
        return ItemImage;
    }
    public void setItemImage(String ItemImage) {
        this.ItemImage = ItemImage;
    }
    public String getItemStock() {
        return ItemStock;
    }

    public void setItemStock(String ItemStock) {
        this.ItemStock = ItemStock;
    }
    public String getRate() {
        return Rate;
    }
    public void setRate(String Rate) {
        this.Rate = Rate;
    }
    public String getTotalcolor() {
        return Totalcolor;
    }
    public void setTotalcolor(String Totalcolor) {
        this.Totalcolor = Totalcolor;
    }
    public String getUnit() {
        return Unit;
    }
    public void setUnit(String Unit) {
        this.Unit = Unit;
    }
}
