package report.showroomitemcheck.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class ItemDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    String ItemID,ItemName,ItemCode,SubItemID,SubItemName,SubItemCode,SubGroupID,SubGroupName,GroupID,GroupName,MainGroupID,MainGroup,Stock,ItemSubItemStock;
    int MDApplicable,SubItemApplicable;

    public ItemDetails(String ItemID, String ItemName, String ItemCode, String SubItemID, String SubItemName, String SubItemCode,String SubGroupID,String SubGroupName, String GroupID, String GroupName,String MainGroupID, String MainGroup, int MDApplicable, int SubItemApplicable,String Stock,String ItemSubItemStock) {
        this.ItemID = ItemID;
        this.ItemName = ItemName;
        this.ItemCode = ItemCode;
        this.SubItemID = SubItemID;
        this.SubItemName = SubItemName;
        this.SubItemCode = SubItemCode;
        this.SubGroupID = SubGroupID;
        this.SubGroupName = SubGroupName;
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.MainGroupID = MainGroupID;
        this.MainGroup = MainGroup;
        this.MDApplicable = MDApplicable;
        this.SubItemApplicable = SubItemApplicable;
        this.Stock = Stock;
        this.ItemSubItemStock = ItemSubItemStock;
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

    public String getSubGroupID() {
        return SubGroupID;
    }
    public void setSubGroupID(String SubGroupID) {
        this.SubGroupID = SubGroupID;
    }

    public String getSubGroupName() {
        return SubGroupName;
    }
    public void setSubGroupName(String SubGroupName) {
        this.SubGroupName = SubGroupName;
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

    public String getStock() {
        return Stock;
    }
    public void setStock(String Stock) {
        this.Stock = Stock;
    }

    public String getItemSubItemStock() {
        return ItemSubItemStock;
    }
    public void setItemSubItemStock(String ItemSubItemStock) {
        this.ItemSubItemStock = ItemSubItemStock;
    }

}
