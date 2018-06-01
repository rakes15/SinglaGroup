package print.godown_group_order_with_rate.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    String GroupID,GroupName,MainGroupID,MainGroup,OrderID,OrderNo,OrderDate;
    int Flag;

    public Group(String GroupID, String GroupName, String MainGroupID, String MainGroup, int Flag, String OrderID, String OrderNo, String OrderDate) {
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.MainGroupID = MainGroupID;
        this.MainGroup = MainGroup;
        this.Flag = Flag;
        this.OrderID = OrderID;
        this.OrderNo = OrderNo;
        this.OrderDate = OrderDate;
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

    public int getFlag() {
        return Flag;
    }
    public void setFlag(int Flag) {
        this.Flag = Flag;
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
}
