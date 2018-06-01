package orderbooking.view_order_details.dataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class OrderViewItemByGroupDataset {
    //@SerializedName("OrderID")
    private String OrderID;
    //@SerializedName("GroupID")
    private String GroupID;
    //@SerializedName("GroupName")
    private String GroupName;
    //@SerializedName("ItemName")
    private String ItemName;
    //@SerializedName("ItemCode")
    private String ItemCode;
    //@SerializedName("ItemID")
    private String ItemID;
    //@SerializedName("ItemImage")
    private String ItemImage;
    //@SerializedName("Stock")
    private String ItemStock;
    //@SerializedName("Unit")
    private String Unit;
    //@SerializedName("Rate")
    private String Rate;
    //@SerializedName("Mrp")
    private String Mrp;
    //@SerializedName("TotalColor")
    private int TotalColor;
    //@SerializedName("ColorID")
    private String ColorID;
    //@SerializedName("ColorName")
    private String ColorName;
    //@SerializedName("MDApplicable")
    private int MDApplicable;
    private int SubItemApplicable;
    private String Barcode;

    public OrderViewItemByGroupDataset(String OrderID,String GroupID, String GroupName, String ItemName, String ItemCode, String ItemID, int TotalColor, String ItemStock, String Unit, String Rate, String Mrp, String ItemImage, String ColorID, String ColorName, int MDApplicable,int SubItemApplicable,String Barcode) {

        this.OrderID = OrderID;
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.ItemID = ItemID;
        this.ColorID = ColorID;
        this.TotalColor = TotalColor;
        this.ItemImage = ItemImage;
        this.Unit = Unit;
        this.ItemStock = ItemStock;
        this.Rate = Rate;
        this.Mrp = Mrp;
        this.ColorName=ColorName;
        this.MDApplicable=MDApplicable;
        this.SubItemApplicable=SubItemApplicable;
        this.Barcode=Barcode;
    }
    public String getOrderID() {
        return OrderID;
    }
    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
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

    public String getItemCode() {
        return ItemCode;
    }
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
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

    public String getMrp() {
        return Mrp;
    }
    public void setMrp(String Mrp) {
        this.Mrp = Mrp;
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

    public int getMDApplicable() {
        return MDApplicable;
    }
    public void setMDApplicable(int MDApplicable) {
        this.MDApplicable = MDApplicable;
    }

    public int getSubItemApplicable() {
        return SubItemApplicable;
    }
    public void setSubItemApplicable(int SubItemApplicable) {
        this.SubItemApplicable = SubItemApplicable;
    }

    public String getBarcode() {
        return Barcode;
    }
    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
    }
}
