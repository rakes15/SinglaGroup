package orderbooking.view_order_details.dataset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class OrderViewGroupDataset implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderID;
    private String mainGroupID;
    private String mainGroup;
    private String groupID;
    private String userName;
    private String fullName;
    private String empCVType;
    private String empCVName;
    private String groupImage;
    private String groupName;
    private String subGroupID;
    private String subGroup;
    private Integer totalQty;
    private Integer totalStyle;
    private Integer totalAmount;
    private String lastBookDateTime;

    public OrderViewGroupDataset(String orderID,String mainGroupID,String mainGroup,String groupID,String userName,String fullName,String empCVType, String empCVName, String groupImage, String groupName, String subGroupID, String subGroup, int totalQty, int totalStyle, int totalAmount, String lastBookDateTime) {
        this.orderID = orderID;
        this.mainGroupID = mainGroupID;
        this.mainGroup = mainGroup;
        this.groupID = groupID;
        this.userName = userName;
        this.fullName = fullName;
        this.empCVType = empCVType;
        this.empCVName = empCVName;
        this.groupImage = groupImage;
        this.groupName = groupName;
        this.subGroupID = subGroupID;
        this.subGroup = subGroup;
        this.totalQty = totalQty;
        this.totalStyle = totalStyle;
        this.totalAmount = totalAmount;
        this.lastBookDateTime = lastBookDateTime;
    }
    public String getOrderID() {
        return orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getMainGroupID() {
        return mainGroupID;
    }

    public void setMainGroupID(String mainGroupID) {
        this.mainGroupID = mainGroupID;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(String mainGroup) {
        this.mainGroup = mainGroup;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmpCVType() {
        return empCVType;
    }

    public void setEmpCVType(String empCVType) {
        this.empCVType = empCVType;
    }

    public String getEmpCVName() {
        return empCVName;
    }

    public void setEmpCVName(String empCVName) {
        this.empCVName = empCVName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSubGroupID() {
        return subGroupID;
    }

    public void setSubGroupID(String subGroupID) {
        this.subGroupID = subGroupID;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getTotalStyle() {
        return totalStyle;
    }

    public void setTotalStyle(Integer totalStyle) {
        this.totalStyle = totalStyle;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLastBookDateTime() {
        return lastBookDateTime;
    }

    public void setLastBookDateTime(String lastBookDateTime) {
        this.lastBookDateTime = lastBookDateTime;
    }

}


