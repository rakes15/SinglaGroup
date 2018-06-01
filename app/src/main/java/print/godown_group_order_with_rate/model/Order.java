package print.godown_group_order_with_rate.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    String OrderID,OrderNo,OrderDate,OrderStatus,PartyID,PartyName,SubPartyID,SubParty,RefName;
//    ,OrderTypeName,UrgencyLevel,Showroom,ExpectedDelDate,PartyID,PartyName,SubPartyID,SubParty,RefName,PendingSince;
//    int PendingItems,PendingQty,OrderQty,PendingPercentage;

    public Order(String OrderID, String OrderNo, String OrderDate, String OrderStatus, String PartyID, String PartyName, String SubPartyID, String SubParty, String RefName){//, String OrderTypeName, String UrgencyLevel, String Showroom, String ExpectedDelDate, String PendingSince, int PendingItems, int PendingQty, int OrderQty, int PendingPercentage) {
        this.OrderID = OrderID;
        this.OrderNo = OrderNo;
        this.OrderDate = OrderDate;
        this.OrderStatus = OrderStatus;
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.SubPartyID = SubPartyID;
        this.SubParty = SubParty;
        this.RefName = RefName;
//        this.OrderTypeName = OrderTypeName;
//        this.UrgencyLevel = UrgencyLevel;
//        this.Showroom = Showroom;
//        this.ExpectedDelDate = ExpectedDelDate;
//        this.PendingItems = PendingItems;
//        this.PendingQty = PendingQty;
//        this.OrderQty = OrderQty;
//        this.PendingSince = PendingSince;
//        this.PendingPercentage = PendingPercentage;
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

    public String getOrderStatus() {
        return OrderStatus;
    }
    public void setOrderStatus(String OrderStatus) {
        this.OrderStatus = OrderStatus;
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

    public String getSubPartyID() {
        return SubPartyID;
    }
    public void setSubPartyID(String SubPartyID) {
        this.SubPartyID = SubPartyID;
    }

    public String getSubParty() {
        return SubParty;
    }
    public void setSubParty(String SubParty) {
        this.SubParty = SubParty;
    }

    public String getRefName() {
        return RefName;
    }
    public void setRefName(String RefName) {
        this.RefName = RefName;
    }


//    public String getOrderTypeName() {
//        return OrderTypeName;
//    }
//    public void setOrderTypeName(String OrderTypeName) {
//        this.OrderTypeName = OrderTypeName;
//    }
//
//    public String getUrgencyLevel() {
//        return UrgencyLevel;
//    }
//    public void setUrgencyLevel(String UrgencyLevel) {
//        this.UrgencyLevel = UrgencyLevel;
//    }
//
//    public String getShowroom() {
//        return Showroom;
//    }
//    public void setShowroom(String Showroom) {
//        this.Showroom = Showroom;
//    }
//
//    public String getExpectedDelDate() {
//        return ExpectedDelDate;
//    }
//    public void setExpectedDelDate(String ExpectedDelDate) {
//        this.ExpectedDelDate = ExpectedDelDate;
//    }
//
//    public String getPendingSince() {
//        return PendingSince;
//    }
//    public void setPendingSince(String PendingSince) {
//        this.PendingSince = PendingSince;
//    }
//
//    public int getPendingItems() {
//        return PendingItems;
//    }
//    public void setPendingItems(int PendingItems) {
//        this.PendingItems = PendingItems;
//    }
//
//    public int getPendingQty() {
//        return PendingQty;
//    }
//    public void setPendingQty(int PendingQty) {
//        this.PendingQty = PendingQty;
//    }
//
//    public int getOrderQty() {
//        return OrderQty;
//    }
//    public void setOrderQty(int OrderQty) {
//        this.OrderQty = OrderQty;
//    }
//
//    public int getPendingPercentage() {
//        return PendingPercentage;
//    }
//    public void setPendingPercentage(int PendingPercentage) {
//        this.PendingPercentage = PendingPercentage;
//    }
}
