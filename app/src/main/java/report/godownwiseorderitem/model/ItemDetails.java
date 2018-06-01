package report.godownwiseorderitem.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class ItemDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    String ItemID,ItemName,ItemCode,SubItemID,SubItemName,SubItemCode,GroupID,GroupName,MainGroup,PartyID,PartyName,OrderID,OrderDate,OrderNo;
    int MDApplicable,SubItemApplicable;

    public ItemDetails(String ItemID, String ItemName, String ItemCode, String SubItemID, String SubItemName,String SubItemCode, String GroupID, String GroupName, String MainGroup, String PartyID, String PartyName, String OrderID, String OrderDate, String OrderNo, int MDApplicable, int SubItemApplicable) {
        this.ItemID = ItemID;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.SubItemID = SubItemID;
        this.SubItemName = SubItemName;
        this.SubItemCode = SubItemCode;
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.MainGroup = MainGroup;
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.OrderID = OrderID;
        this.OrderDate = OrderDate;
        this.OrderNo = OrderNo;
        this.MDApplicable = MDApplicable;
        this.SubItemApplicable = SubItemApplicable;
    }
    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
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

    public String getSubItemID() {
        return SubItemID;
    }
    public void setSubItemID(String SubItemID) {
        this.SubItemID = SubItemID;
    }

    public String getSubItemName() {
        return SubItemName;
    }
    public void setSubItemName(String SubItemName) {
        this.SubItemName = SubItemName;
    }

    public String getSubItemCode() {
        return SubItemCode;
    }
    public void setSubItemCode(String SubItemCode) {
        this.SubItemCode = SubItemCode;
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

    public String getMainGroup() {
        return MainGroup;
    }
    public void setMainGroup(String MainGroup) {
        this.MainGroup = MainGroup;
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

    public String getOrderDate() {
        return OrderDate;
    }
    public void setOrderDate(String OrderDate) {
        this.OrderDate = OrderDate;
    }

    public String getOrderNo() {
        return OrderNo;
    }
    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
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
