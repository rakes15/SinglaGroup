package report.revertforceclose.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class RevertForceCloseDataset implements Serializable {
    private static final long serialVersionUID = 1L;
    String ItemID,ItemCode,ItemName,OrderID,OrderNo,OrderDate,TransactionID,EntryDateTime,PartyID,PartyName;
    int FlagType,TotalQty,TotalAmt;

    public RevertForceCloseDataset(String ItemID, String ItemCode, String ItemName,String OrderID, String OrderNo, String OrderDate,  String TransactionID, String EntryDateTime, String PartyID, String PartyName, int FlagType,int TotalQty,int TotalAmt) {
        this.OrderID = OrderID;
        this.OrderNo = OrderNo;
        this.OrderDate = OrderDate;
        this.TransactionID = TransactionID;
        this.EntryDateTime = EntryDateTime;
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.ItemID = ItemID;
        this.ItemCode = ItemCode;
        this.ItemName = ItemName;
        this.FlagType = FlagType;
        this.TotalQty = TotalQty;
        this.TotalAmt = TotalAmt;
    }
    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
    }

    public String getItemCode() {
        return ItemCode;
    }
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    public String getItemName() {
        return ItemName;
    }
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getOrderID() {
        return OrderID;
    }
    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getOrderNo() {
        return OrderNo;
    }
    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }
    public void setOrderDate(String OrderDate) {
        this.OrderDate = OrderDate;
    }

    public String getTransactionID() {
        return TransactionID;
    }
    public void setTransactionID(String TransactionID) {
        this.TransactionID = TransactionID;
    }

    public String getEntryDateTime() {
        return EntryDateTime;
    }
    public void setEntryDateTime(String EntryDateTime) {
        this.EntryDateTime = EntryDateTime;
    }

    public String getPartyID() {
        return PartyID;
    }
    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    public int getFlagType() {
        return FlagType;
    }
    public void setFlagType(int FlagType) {
        this.FlagType = FlagType;
    }

    public int getTotalQty() {
        return TotalQty;
    }
    public void setTotalQty(int TotalQty) {
        this.TotalQty = TotalQty;
    }

    public int getTotalAmt() {
        return TotalAmt;
    }
    public void setTotalAmt(int TotalAmt) {
        this.TotalAmt = TotalAmt;
    }
}
