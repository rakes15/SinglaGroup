package print.godown_group_order_with_rate.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class GroupOrGodown implements Serializable {
    private static final long serialVersionUID = 1L;

    String GroupOrGodownID,GroupOrGodownName,MainGroupID,MainGroup,OrderID,OrderNo,OrderDate;
    int Flag;

    public GroupOrGodown(String GroupOrGodownID, String GroupOrGodownName, String MainGroupID, String MainGroup, int Flag, String OrderID, String OrderNo, String OrderDate) {
        this.GroupOrGodownID = GroupOrGodownID;
        this.GroupOrGodownName = GroupOrGodownName;
        this.MainGroupID = MainGroupID;
        this.MainGroup = MainGroup;
        this.Flag = Flag;
        this.OrderID = OrderID;
        this.OrderNo = OrderNo;
        this.OrderDate = OrderDate;
    }
    public String getGroupOrGodownID() {
        return GroupOrGodownID;
    }
    public void setGroupOrGodownID(String GroupOrGodownID) {
        this.GroupOrGodownID = GroupOrGodownID;
    }

    public String getGroupOrGodownName() {
        return GroupOrGodownName;
    }
    public void setGroupOrGodownName(String GroupOrGodownName) {
        this.GroupOrGodownName = GroupOrGodownName;
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
