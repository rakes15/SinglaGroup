package orderbooking.catalogue.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerSimilarItemsDataSet implements Serializable{
    private static final long serialVersionUID = 1L;

    String ItemName, ItemCode,ItemID,ItemImage,Rate,ItemStock,Unit;
    int TotalColor, Wishlist;
    public RecyclerSimilarItemsDataSet() {

    }
    public RecyclerSimilarItemsDataSet(String ItemName, String ItemCode, String ItemID, String Rate, String ItemImage, String ItemStock,String Unit,int TotalColor,int Wishlist) {

        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.ItemID = ItemID;
        this.ItemImage = ItemImage;
        this.Rate = Rate;
        this.ItemStock=ItemStock;
        this.Unit=Unit;
        this.TotalColor=TotalColor;
        this.Wishlist=Wishlist;
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
    public void setItemCode(String Itemcode) {
        this.ItemCode = ItemCode;
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

    public String getRate() {
        return Rate;
    }
    public void setRate(String Rate) {
        this.Rate = Rate;
    }

    public String getItemStock() {
        return ItemStock;
    }
    public void setItemStock(String ItemStock) {
        this.ItemStock = ItemStock;
    }

    public String getUnit() {
        return Unit;
    }
    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public int getTotalColor() {
        return TotalColor;
    }
    public void setTotalColor(int TotalColor) {
        this.TotalColor = TotalColor;
    }

    public int isSelectedWishlist() {
        return Wishlist;
    }
    public void setSelectedWishlist(int Wishlist) {
        this.Wishlist = Wishlist;
    }
}
