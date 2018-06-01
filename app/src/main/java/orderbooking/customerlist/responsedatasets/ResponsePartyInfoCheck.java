package orderbooking.customerlist.responsedatasets;

public class ResponsePartyInfoCheck {
private String orderID;
private String orderNo;
private String orderDate;
private String partyID;
private String subPartyID;
private String refName;
private String remarks;
private String entryDatetime;
private String godownID;
private String godown;
private String divisionID;
private String division;
private String fairID;
private String fair;
private String branchID;
private String branch;
private Integer oldApprovedFlag;
private Integer creditDays;
private String creditLimit;
private String totalDueAmt;
private Integer totalOverDueAmt;
private Integer exceedAmt;

public ResponsePartyInfoCheck(String orderID, String orderNo, String orderDate, String partyID, String subPartyID, String refName,String remarks, String entryDatetime, String godownID, String godown, String divisionID, String division,String fairID, String fair, String branchID, String branch, Integer oldApprovedFlag,Integer creditDays, String creditLimit,String totalDueAmt,Integer totalOverDueAmt, Integer exceedAmt){
    this.orderID=orderID;
    this.orderNo=orderNo;
    this.orderDate=orderDate;
    this.partyID=partyID;
    this.subPartyID=subPartyID;
    this.refName=refName;
    this.remarks=remarks;
    this.entryDatetime=entryDatetime;
    this.godownID=godownID;
    this.godown=godown;
    this.divisionID=divisionID;
    this.division=division;
    this.fairID=fairID;
    this.fair=fair;
    this.branchID=branchID;
    this.branch=branch;
    this.oldApprovedFlag=oldApprovedFlag;
    this.creditDays=creditDays;
    this.creditLimit=creditLimit;
    this.totalDueAmt=totalDueAmt;
    this.totalOverDueAmt=totalOverDueAmt;
    this.exceedAmt=exceedAmt;
}
public String getOrderID() {
    return orderID;
}

public void setOrderID(String orderID) {
    this.orderID = orderID;
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

public String getPartyID() {
    return partyID;
}

public void setPartyID(String partyID) {
    this.partyID = partyID;
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

public String getEntryDatetime() {
    return entryDatetime;
}

public void setEntryDatetime(String entryDatetime) {
    this.entryDatetime = entryDatetime;
}

public String getGodownID() {
    return godownID;
}

public void setGodownID(String godownID) {
    this.godownID = godownID;
}

public String getGodown() {
    return godown;
}

public void setGodown(String godown) {
    this.godown = godown;
}

public String getDivisionID() {
    return divisionID;
}

public void setDivisionID(String divisionID) {
    this.divisionID = divisionID;
}

public String getDivision() {
    return division;
}

public void setDivision(String division) {
    this.division = division;
}

public String getFairID() {
    return fairID;
}

public void setFairID(String fairID) {
    this.fairID = fairID;
}

public String getFair() {
    return fair;
}

public void setFair(String fair) {
    this.fair = fair;
}

public String getBranchID() {
    return branchID;
}

public void setBranchID(String branchID) {
    this.branchID = branchID;
}

public String getBranch() {
    return branch;
}

public void setBranch(String branch) {
    this.branch = branch;
}

public Integer getOldApprovedFlag() {
    return oldApprovedFlag;
}

public void setOldApprovedFlag(Integer oldApprovedFlag) {
    this.oldApprovedFlag = oldApprovedFlag;
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

