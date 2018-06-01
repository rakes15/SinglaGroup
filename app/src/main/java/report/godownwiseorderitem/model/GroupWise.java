package report.godownwiseorderitem.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class GroupWise implements Serializable {
    private static final long serialVersionUID = 1L;

    String GroupID,GroupName,MainGroupID,MainGroup,PartyID,PartyName,OrderID,OrderNo;
    int PendingItems,PendingQty;

    public GroupWise(String GroupID, String GroupName, String MainGroupID, String MainGroup, int PendingItems, int PendingQty,String PartyID,String PartyName,String OrderID,String OrderNo) {
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.MainGroupID = MainGroupID;
        this.MainGroup = MainGroup;
        this.PendingItems = PendingItems;
        this.PendingQty = PendingQty;
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.OrderID = OrderID;
        this.OrderNo = OrderNo;
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
    
    public String getMainGroupID() {
        return MainGroupID;
    }
    public void setMainGroupID(String MainGroupID) {
        this.MainGroupID = MainGroupID;
    }

    public String getMainGroup() {
        return MainGroup;
    }
    public void setMainGroup(String MainGroup) {
        this.MainGroup = MainGroup;
    }

    public int getPendingItems() {
        return PendingItems;
    }
    public void setPendingItems(int PendingItems) {
        this.PendingItems = PendingItems;
    }

    public int getPendingQty() {
        return PendingQty;
    }
    public void setPendingQty(int PendingQty) {
        this.PendingQty = PendingQty;
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
}
