package orderbooking.catalogue.responseitemlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemList {

    @SerializedName("RowNo")
    @Expose
    private String rowNo;
    @SerializedName("TotalColor")
    @Expose
    private Integer totalColor;
    @SerializedName("ImageStatus")
    @Expose
    private Integer imageStatus;
    @SerializedName("GroupID")
    @Expose
    private String groupID;
    @SerializedName("GroupName")
    @Expose
    private String groupName;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("WishlistStatus")
    @Expose
    private Integer wishlistStatus;
    @SerializedName("CartStatus")
    @Expose
    private Integer cartStatus;
    @SerializedName("ItemCode")
    @Expose
    private String itemCode;
    @SerializedName("ItemName")
    @Expose
    private String itemName;
    @SerializedName("ItemImage")
    @Expose
    private String itemImage;
    @SerializedName("Stock")
    @Expose
    private Integer stock;
    @SerializedName("Unit")
    @Expose
    private String unit;
    @SerializedName("Rate")
    @Expose
    private Integer rate;

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    public Integer getTotalColor() {
        return totalColor;
    }

    public void setTotalColor(Integer totalColor) {
        this.totalColor = totalColor;
    }

    public Integer getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(Integer imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public Integer getWishlistStatus() {
        return wishlistStatus;
    }

    public void setWishlistStatus(Integer wishlistStatus) {
        this.wishlistStatus = wishlistStatus;
    }

    public Integer getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(Integer cartStatus) {
        this.cartStatus = cartStatus;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

}