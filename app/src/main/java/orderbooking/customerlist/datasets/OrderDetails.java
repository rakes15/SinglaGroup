package orderbooking.customerlist.datasets;

import java.util.HashMap;
import java.util.Map;
public class OrderDetails {

private String orderID;
private String fairName;
private Integer itemCount;
private Integer tBookQty;
private Integer totalAmount;
private String lastBookDate;
private String userName;
private String empCVName;
private String empCVType;
private String orderNo;
private String orderDate;
private String godown;
private String partyName;
private String partyID;
private Integer subPartyApplicable;
private String subParty;
private String subPartyID;
private String refName;
private String remarks;
private String agentName;
private String agentID;
private String address;
private String city;
private String state;
private String mobile;
private Integer creditDays;
private String creditLimit;
private String totalDueAmt;
private Integer totalOverDueAmt;
private Integer exceedAmt;

    public OrderDetails(String orderID
            ,String fairName
            ,Integer itemCount
            ,Integer tBookQty
            ,Integer totalAmount
            ,String lastBookDate
            ,String userName
            ,String empCVName
            ,String empCVType
            ,String orderNo
            ,String orderDate
            ,String godown
            ,String partyName
            ,String partyID
            ,Integer subPartyApplicable
            ,String subParty
            ,String subPartyID
            ,String refName
            ,String remarks
            ,String agentName
            ,String agentID
            ,String address
            ,String city
            ,String state
            ,String mobile
            ,Integer creditDays
            ,String creditLimit
            ,String totalDueAmt
            ,Integer totalOverDueAmt
            ,Integer exceedAmt){
            this.orderID=orderID;
            this.fairName=fairName;
            this.itemCount=itemCount;
            this.tBookQty=tBookQty;
            this.totalAmount=totalAmount;
            this.lastBookDate=lastBookDate;
            this.userName=userName;
            this.empCVName=empCVName;
            this.empCVType=empCVType;
            this.orderNo=orderNo;
            this.orderDate=orderDate;
            this.godown=godown;
            this.partyName=partyName;
            this.partyID=partyID;
            this. subPartyApplicable=subPartyApplicable;
            this.subParty=subParty;
            this.subPartyID=subPartyID;
            this.refName=refName;
            this.remarks=remarks;
            this.agentName=agentName;
            this.agentID=agentID;
            this.address=address;
            this.city=city;
            this.state=state;
            this.mobile=mobile;
            this. creditDays=creditDays;
            this.creditLimit=creditLimit;
            this.totalDueAmt=totalDueAmt;
            this. totalOverDueAmt=totalOverDueAmt;
            this. exceedAmt=exceedAmt;
        
    }

public String getOrderID() {
return orderID;
}

public void setOrderID(String orderID) {
this.orderID = orderID;
}

public String getFairName() {
return fairName;
}

public void setFairName(String fairName) {
this.fairName = fairName;
}

public Integer getItemCount() {
return itemCount;
}

public void setItemCount(Integer itemCount) {
this.itemCount = itemCount;
}

public Integer getTBookQty() {
return tBookQty;
}

public void setTBookQty(Integer tBookQty) {
this.tBookQty = tBookQty;
}

public Integer getTotalAmount() {
return totalAmount;
}

public void setTotalAmount(Integer totalAmount) {
this.totalAmount = totalAmount;
}

public String getLastBookDate() {
return lastBookDate;
}

public void setLastBookDate(String lastBookDate) {
this.lastBookDate = lastBookDate;
}

public String getUserName() {
return userName;
}

public void setUserName(String userName) {
this.userName = userName;
}

public String getEmpCVName() {
return empCVName;
}

public void setEmpCVName(String empCVName) {
this.empCVName = empCVName;
}

public String getEmpCVType() {
return empCVType;
}

public void setEmpCVType(String empCVType) {
this.empCVType = empCVType;
}

public String getOrderNo() {
return orderNo;
}

public void setOrderNo(String orderNo) {
this.orderNo = orderNo;
}

public String getOrderDate() {
return orderDate;
}

public void setOrderDate(String orderDate) {
this.orderDate = orderDate;
}

public String getGodown() {
return godown;
}

public void setGodown(String godown) {
this.godown = godown;
}

public String getPartyName() {
return partyName;
}

public void setPartyName(String partyName) {
this.partyName = partyName;
}

public String getPartyID() {
return partyID;
}

public void setPartyID(String partyID) {
this.partyID = partyID;
}

public Integer getSubPartyApplicable() {
return subPartyApplicable;
}

public void setSubPartyApplicable(Integer subPartyApplicable) {
this.subPartyApplicable = subPartyApplicable;
}

public String getSubParty() {
return subParty;
}

public void setSubParty(String subParty) {
this.subParty = subParty;
}

public String getSubPartyID() {
return subPartyID;
}

public void setSubPartyID(String subPartyID) {
this.subPartyID = subPartyID;
}

public String getRefName() {
return refName;
}

public void setRefName(String refName) {
this.refName = refName;
}

public String getRemarks() {
return remarks;
}

public void setRemarks(String remarks) {
this.remarks = remarks;
}

public String getAgentName() {
return agentName;
}

public void setAgentName(String agentName) {
this.agentName = agentName;
}

public String getAgentID() {
return agentID;
}

public void setAgentID(String agentID) {
this.agentID = agentID;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

public String getCity() {
return city;
}

public void setCity(String city) {
this.city = city;
}

public String getState() {
return state;
}

public void setState(String state) {
this.state = state;
}

public String getMobile() {
return mobile;
}

public void setMobile(String mobile) {
this.mobile = mobile;
}

public Integer getCreditDays() {
return creditDays;
}

public void setCreditDays(Integer creditDays) {
this.creditDays = creditDays;
}

public String getCreditLimit() {
return creditLimit;
}

public void setCreditLimit(String creditLimit) {
this.creditLimit = creditLimit;
}

public String getTotalDueAmt() {
return totalDueAmt;
}

public void setTotalDueAmt(String totalDueAmt) {
this.totalDueAmt = totalDueAmt;
}

public Integer getTotalOverDueAmt() {
return totalOverDueAmt;
}

public void setTotalOverDueAmt(Integer totalOverDueAmt) {
this.totalOverDueAmt = totalOverDueAmt;
}

public Integer getExceedAmt() {
return exceedAmt;
}

public void setExceedAmt(Integer exceedAmt) {
this.exceedAmt = exceedAmt;
}

}