package report.stockreport.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class StockReportCommon implements Serializable {
    private static final long serialVersionUID = 1L;

    String ID,Name;
    int TotalItem,TotalStock,TotalSaleAmount,TotalPurchaseAmount;

    public StockReportCommon(String ID, String Name, int TotalItem, int TotalStock, int TotalSaleAmount, int TotalPurchaseAmount) {
        this.ID = ID;
        this.Name = Name;
        this.TotalItem = TotalItem;
        this.TotalStock = TotalStock;
        this.TotalSaleAmount = TotalSaleAmount;
        this.TotalPurchaseAmount = TotalPurchaseAmount;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public int getTotalItem() {
        return TotalItem;
    }
    public void setTotalItem(int TotalItem) {
        this.TotalItem = TotalItem;
    }

    public int getTotalStock() {
        return TotalStock;
    }
    public void setTotalStock(int TotalStock) {
        this.TotalStock = TotalStock;
    }

    public int getTotalSaleAmount() {
        return TotalSaleAmount;
    }
    public void setTotalSaleAmount(int TotalSaleAmount) {
        this.TotalSaleAmount = TotalSaleAmount;
    }

    public int getTotalPurchaseAmount() {
        return TotalPurchaseAmount;
    }
    public void setTotalPurchaseAmount(int TotalPurchaseAmount) {
        this.TotalPurchaseAmount = TotalPurchaseAmount;
    }


}
