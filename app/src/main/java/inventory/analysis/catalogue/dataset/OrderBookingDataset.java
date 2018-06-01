package inventory.analysis.catalogue.dataset;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class OrderBookingDataset implements Serializable{
    private static final long serialVersionUID = 1L;

    String SizeID,SizeName,Qty, Price;
    int listSize;
    public OrderBookingDataset() {
    }
    public OrderBookingDataset(String SizeID, String SizeName, String Qty, String Price,int listsize) {

        this.SizeID = SizeID;
        this.SizeName = SizeName;
        this.Qty = Qty;
        this.Price = Price;
        this.listSize = listsize;
    }
    public String getSizeID() {
        return SizeID;
    }
    public void setSizeID(String SizeID) {
        this.SizeID = SizeID;
    }
    public String getSizeName() {
        return SizeName;
    }
    public void setSizeName(String SizeName) {
        this.SizeName = SizeName;
    }
    public String getQty() {
        return Qty;
    }
    public void setQty(String Qty) {
        this.Qty = Qty;
    }
    public String getPrice() {
        return Price;
    }
    public void setPrice(String Price) {
        this.Price = Price;
    }
    public int getListSize() {
        return listSize;
    }
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
