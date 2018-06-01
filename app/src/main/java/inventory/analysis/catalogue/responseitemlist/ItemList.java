package inventory.analysis.catalogue.responseitemlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemList {

    @SerializedName("TotalSale")
    @Expose
    private Double totalSale;
    @SerializedName("TotalProd")
    @Expose
    private Double totalProd;
    @SerializedName("ItemCreateDays")
    @Expose
    private Integer itemCreateDays;
    @SerializedName("LastInDays")
    @Expose
    private Integer lastInDays;
    @SerializedName("LastOutDays")
    @Expose
    private Integer lastOutDays;
    @SerializedName("AgeingDays")
    @Expose
    private Integer ageingDays;
    @SerializedName("WishlistStatus")
    @Expose
    private Integer wishlistStatus;
    @SerializedName("CartStatus")
    @Expose
    private Integer cartStatus;
    @SerializedName("RowNo")
    @Expose
    private String rowNo;
    @SerializedName("MDApplicable")
    @Expose
    private Integer mDApplicable;
    @SerializedName("SubItemApplicable")
    @Expose
    private Integer subItemApplicable;
    @SerializedName("GroupID")
    @Expose
    private String groupID;
    @SerializedName("GroupName")
    @Expose
    private String groupName;
    @SerializedName("ImageStatus")
    @Expose
    private Integer imageStatus;
    @SerializedName("SubGroup")
    @Expose
    private String subGroup;
    @SerializedName("SubGroupID")
    @Expose
    private String subGroupID;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("ItemName")
    @Expose
    private String itemName;
    @SerializedName("ItemCode")
    @Expose
    private String itemCode;
    @SerializedName("Rate")
    @Expose
    private String rate;
    @SerializedName("DiscountRate")
    @Expose
    private String discountRate;
    @SerializedName("UnitName")
    @Expose
    private String unitName;
    @SerializedName("Stock")
    @Expose
    private String stock;
    @SerializedName("SalebleStock")
    @Expose
    private String salebleStock;
    @SerializedName("ReserveStock")
    @Expose
    private String reserveStock;
    @SerializedName("RejetionStock")
    @Expose
    private String rejetionStock;
    @SerializedName("ProdPurchStock")
    @Expose
    private String prodPurchStock;
    @SerializedName("PendingDel")
    @Expose
    private String pendingDel;
    @SerializedName("ProductionStock")
    @Expose
    private String productionStock;
    @SerializedName("PurchaseStock")
    @Expose
    private String purchaseStock;
    @SerializedName("Disc1")
    @Expose
    private Double disc1;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("TotalColor")
    @Expose
    private Integer totalColor;

    public Double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(Double totalSale) {
        this.totalSale = totalSale;
    }

    public Double getTotalProd() {
        return totalProd;
    }

    public void setTotalProd(Double totalProd) {
        this.totalProd = totalProd;
    }

    public Integer getItemCreateDays() {
        return itemCreateDays;
    }

    public void setItemCreateDays(Integer itemCreateDays) {
        this.itemCreateDays = itemCreateDays;
    }

    public Integer getLastInDays() {
        return lastInDays;
    }

    public void setLastInDays(Integer lastInDays) {
        this.lastInDays = lastInDays;
    }

    public Integer getLastOutDays() {
        return lastOutDays;
    }

    public void setLastOutDays(Integer lastOutDays) {
        this.lastOutDays = lastOutDays;
    }

    public Integer getAgeingDays() {
        return ageingDays;
    }

    public void setAgeingDays(Integer ageingDays) {
        this.ageingDays = ageingDays;
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

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    public Integer getMDApplicable() {
        return mDApplicable;
    }

    public void setMDApplicable(Integer mDApplicable) {
        this.mDApplicable = mDApplicable;
    }

    public Integer getSubItemApplicable() {
        return subItemApplicable;
    }

    public void setSubItemApplicable(Integer subItemApplicable) {
        this.subItemApplicable = subItemApplicable;
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

    public Integer getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(Integer imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public String getSubGroupID() {
        return subGroupID;
    }

    public void setSubGroupID(String subGroupID) {
        this.subGroupID = subGroupID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getSalebleStock() {
        return salebleStock;
    }

    public void setSalebleStock(String salebleStock) {
        this.salebleStock = salebleStock;
    }

    public String getReserveStock() {
        return reserveStock;
    }

    public void setReserveStock(String reserveStock) {
        this.reserveStock = reserveStock;
    }

    public String getRejetionStock() {
        return rejetionStock;
    }

    public void setRejetionStock(String rejetionStock) {
        this.rejetionStock = rejetionStock;
    }

    public String getProdPurchStock() {
        return prodPurchStock;
    }

    public void setProdPurchStock(String prodPurchStock) {
        this.prodPurchStock = prodPurchStock;
    }

    public String getPendingDel() {
        return pendingDel;
    }

    public void setPendingDel(String pendingDel) {
        this.pendingDel = pendingDel;
    }

    public String getProductionStock() {
        return productionStock;
    }

    public void setProductionStock(String productionStock) {
        this.productionStock = productionStock;
    }

    public String getPurchaseStock() {
        return purchaseStock;
    }

    public void setPurchaseStock(String purchaseStock) {
        this.purchaseStock = purchaseStock;
    }

    public Double getDisc1() {
        return disc1;
    }

    public void setDisc1(Double disc1) {
        this.disc1 = disc1;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getTotalColor() {
        return totalColor;
    }

    public void setTotalColor(Integer totalColor) {
        this.totalColor = totalColor;
    }

}