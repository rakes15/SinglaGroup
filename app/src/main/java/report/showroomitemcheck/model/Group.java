package report.showroomitemcheck.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    String GroupID,GroupName,MainGroupID,MainGroup;//,GodownID,GodownName;//,OrderID,OrderNo;
    int Flag;

    public Group(String GroupID, String GroupName, String MainGroupID, String MainGroup, int Flag){//, int PendingQty, String GodownID, String GodownName, String OrderID, String OrderNo) {
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.MainGroupID = MainGroupID;
        this.MainGroup = MainGroup;
        this.Flag = Flag;
//        this.GodownID = GodownID;
//        this.GodownName = GodownName;
//        this.PendingQty = PendingQty;
//        this.OrderID = OrderID;
//        this.OrderNo = OrderNo;
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

//    public String getGodownID() {
//        return GodownID;
//    }
//    public void setGodownID(String GodownID) {
//        this.GodownID = GodownID;
//    }
//
//    public String getGodownName() {
//        return GodownName;
//    }
//    public void setGodownName(String GodownName) {
//        this.GodownName = GodownName;
//    }

//    public int getPendingQty() {
//        return PendingQty;
//    }
//    public void setPendingQty(int PendingQty) {
//        this.PendingQty = PendingQty;
//    }
//
//    public String getOrderID() {
//        return OrderID;
//    }
//    public void setOrderID(String OrderID) {
//        this.OrderID = OrderID;
//    }
//
//    public String getOrderNo() {
//        return OrderNo;
//    }
//    public void setOrderNo(String OrderNo) {
//        this.OrderNo = OrderNo;
//    }
}
