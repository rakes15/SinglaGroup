package inventory.analysis.catalogue.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerItemDataset{
    @SerializedName("GroupID")
    private String GroupID;
    @SerializedName("GroupName")
    private String GroupName;
    @SerializedName("ItemName")
    private String ItemName;
    @SerializedName("ItemCode")
    private String Itemcode;
    @SerializedName("ItemID")
    private String ItemID;
    @SerializedName("ItemImage")
    private String ItemImage;
    @SerializedName("Stock")
    private String ItemStock;
    @SerializedName("Unit")
    private String Unit;
    @SerializedName("Rate")
    private String Rate;
    @SerializedName("TotalColor")
    private int TotalColor;
    @SerializedName("RowNo")
    private String RowNo;
    @SerializedName("WishlistStatus")
    private int WishlistStatus;
    @SerializedName("CartStatus")
    private int CartStatus;

    public RecyclerItemDataset(String GroupID, String GroupName, String ItemName, String Itemcode, String ItemID, String RowNo, int TotalColor, String ItemStock,String Unit, String Rate, String ItemImage, int WishlistStatus,int CartStatus) {

        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.ItemName = ItemName;
        this.Itemcode = Itemcode;
        this.ItemID = ItemID;
        this.RowNo = RowNo;
        this.TotalColor = TotalColor;
        this.ItemImage = ItemImage;
        this.Unit = Unit;
        this.ItemStock = ItemStock;
        this.Rate = Rate;
        this.WishlistStatus=WishlistStatus;
        this.CartStatus=CartStatus;
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

    public String getRowNo() {
        return RowNo;
    }
    public void setRowNo(String RowNo) {
        this.RowNo = RowNo;
    }

    public int getTotalColor() {
        return TotalColor;
    }
    public void setTotalColor(int TotalColor) {
        this.TotalColor = TotalColor;
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

    public String getUnit() {
        return Unit;
    }
    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public String getRate() {
        return Rate;
    }
    public void setRate(String Rate) {
        this.Rate = Rate;
    }

    public int isSelectedWishlist() {
        return WishlistStatus;
    }
    public void setSelectedWishlist(int WishlistStatus) {
        this.WishlistStatus = WishlistStatus;
    }

    public int getCartStatus() {
        return CartStatus;
    }
    public void setCartStatus(int CartStatus) {
        this.CartStatus = CartStatus;
    }
}
