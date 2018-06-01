package orderbooking.catalogue.adapter;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RecyclerColorOptionDataSet implements Serializable{
    private static final long serialVersionUID = 1L;

    String ItemName, ItemCode,ColorID,Color, ItemID,ItemImage,Rate,ColorStock,Unit;
    public RecyclerColorOptionDataSet() {

    }
    public RecyclerColorOptionDataSet(String ColorID, String Color, String ItemName, String ItemCode, String ItemID, String Rate,String ItemImage,String ColorStoc,String Unitk) {

        this.ColorID = ColorID;
        this.Color = Color;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.ItemID = ItemID;
        this.ItemImage = ItemImage;
        this.Rate = Rate;
        this.ColorStock=ColorStock;
        this.Unit=Unit;
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
}
