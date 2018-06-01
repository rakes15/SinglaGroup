package orderbooking.view_order_details.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class OrderViewColorOptionDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String OrderID,ItemName, ItemCode,ColorID,Color, ItemID,ItemImage,Rate,ColorStock,Unit,Barcode;
    private int MDApplicable;
    private int SubItemApplicable;
    public OrderViewColorOptionDataset(String OrderID,String ColorID, String Color, String ItemName, String ItemCode, String ItemID, String Rate, String ItemImage, String ColorStock, String Unit,String Barcode, int MDApplicable,int SubItemApplicable) {

        this.OrderID = OrderID;
        this.ColorID = ColorID;
        this.Color = Color;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.ItemID = ItemID;
        this.ItemImage = ItemImage;
        this.Rate = Rate;
        this.ColorStock=ColorStock;
        this.Unit=Unit;
        this.Barcode=Barcode;
        this.MDApplicable=MDApplicable;
        this.SubItemApplicable=SubItemApplicable;
    }
    public String getOrderID() {
        return OrderID;
    }
    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getColorID() {
        return ColorID;
    }
    public void setColorID(String GroupID) {
        this.ColorID = GroupID;
    }

    public String getColorName() {
        return Color;
    }
    public void setColorName(String Color) {
        this.Color = Color;
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
        this.ItemCode = Itemcode;
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

    public String getColorStock() {
        return ColorStock;
    }
    public void setColorStock(String ColorStock) {
        this.ColorStock = ColorStock;
    }

    public String getUnit() {
        return Unit;
    }
    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public String getBarcode() {
        return Barcode;
    }
    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
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
}
