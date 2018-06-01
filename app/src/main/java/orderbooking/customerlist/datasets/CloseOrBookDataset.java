package orderbooking.customerlist.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class CloseOrBookDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String PartyID,PartyName,SubPartyID,SubParty,AgentID,AgentName,Mobile,City,State,Address,OrderNo,OrderDate,Godown,UserName;
    int SubPartyApplicable,itemCount,creditDays;
    String orderID,fairName,lastBookDate,empCVName,empCVType,refName,remarks,creditLimit,EntryDate;
    String Email,AccountNo,AccountHolderName,IFSCCOde,IDName,GSTIN,AvgOverDueDays,AvgDueDays;
    String PricelistID,Label,UnderName;
    double tBookQty,totalAmount,totalDueAmt,totalOverDueAmt,exceedAmt;
    public CloseOrBookDataset(String PartyID, String PartyName,String SubPartyID,String SubParty, String AgentID, String AgentName, String Mobile, String City, String State, String Address, String OrderNo,String OrderDate, int SubPartyApplicable,String Godown,String UserName,String orderID,String fairName,String lastBookDate,String empCVName,String empCVType,String refName,String remarks,String creditLimit,int itemCount,double  tBookQty,double  totalAmount,int  creditDays,double  totalDueAmt,double  totalOverDueAmt,double  exceedAmt,String EntryDate,String Email,String AccountNo,String AccountHolderName,String IFSCCOde,String IDName,String GSTIN,String AvgOverDueDays,String AvgDueDays,String PricelistID,String Label,String UnderName) {

        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.SubPartyID = SubPartyID;
        this.SubParty = SubParty;
        this.AgentID = AgentID;
        this.AgentName = AgentName;
        this.Mobile = Mobile;
        this.City = City;
        this.State = State;
        this.Address = Address;
        this.OrderNo = OrderNo;
        this.OrderDate = OrderDate;
        this.SubPartyApplicable = SubPartyApplicable;
        this.OrderDate = OrderDate;
        this.Godown = Godown;
        this.UserName = UserName;
        this.orderID = orderID;
        this.fairName = fairName;
        this.lastBookDate = lastBookDate;
        this.empCVName = empCVName;
        this.empCVType = empCVType;
        this.refName = refName;
        this.remarks = remarks;
        this.creditLimit = creditLimit;
        this.itemCount = itemCount;
        this.tBookQty = tBookQty;
        this.totalAmount = totalAmount;
        this.creditDays = creditDays;
        this.totalDueAmt = totalDueAmt;
        this.totalOverDueAmt = totalOverDueAmt;
        this.exceedAmt = exceedAmt;
        this.EntryDate = EntryDate;
        this.Email = Email;
        this.AccountNo = AccountNo;
        this.AccountHolderName = AccountHolderName;
        this.IFSCCOde = IFSCCOde;
        this.IDName = IDName;
        this.GSTIN = GSTIN;
        this.AvgOverDueDays = AvgOverDueDays;
        this.AvgDueDays = AvgDueDays;
        this.PricelistID = PricelistID;
        this.Label = Label;
        this.UnderName = UnderName;
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

    public String getAgentID() {
        return AgentID;
    }
    public void setAgentID(String AgentID) {
        this.AgentID = AgentID;
    }

    public String getAgentName() {
        return AgentName;
    }
    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }

    public String getMobile() {
        return Mobile;
    }
    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getCity() {
        return City;
    }
    public void setCity(String City) {
        this.City = City;
    }

    public String getState() {
        return State;
    }
    public void setState(String State) {
        this.State = State;
    }

    public String getAddress() {
        return Address;
    }
    public void setAddress(String Address) {
        this.AgentID = Address;
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
    public int getSubPartyApplicable() {
        return SubPartyApplicable;
    }
    public void setSubPartyApplicable(int SubPartyApplicable) {
        this.SubPartyApplicable = SubPartyApplicable;
    }

    public String getGodown() {
        return Godown;
    }
    public void setGodown(String Godown) {
        this.Godown = Godown;
    }
    public String getUserName() {
        return UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
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

    public double getTBookQty() {
        return tBookQty;
    }

    public void setTBookQty(double tBookQty) {
        this.tBookQty = tBookQty;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLastBookDate() {
        return lastBookDate;
    }

    public void setLastBookDate(String lastBookDate) {
        this.lastBookDate = lastBookDate;
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

    public double getTotalDueAmt() {
        return totalDueAmt;
    }

    public void setTotalDueAmt(double totalDueAmt) {
        this.totalDueAmt = totalDueAmt;
    }

    public double getTotalOverDueAmt() {
        return totalOverDueAmt;
    }

    public void setTotalOverDueAmt(double totalOverDueAmt) {
        this.totalOverDueAmt = totalOverDueAmt;
    }

    public double getExceedAmt() {
        return exceedAmt;
    }

    public void setExceedAmt(double exceedAmt) {
        this.exceedAmt = exceedAmt;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String EntryDate) {
        this.EntryDate = EntryDate;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getAccountNo() {
        return AccountNo;
    }
    public void setAccountNo(String AccountNo) {
        this.AccountNo = AccountNo;
    }

    public String getAccountHolderName() {
        return AccountHolderName;
    }
    public void setAccountHolderName(String AccountHolderName) {
        this.AccountHolderName = AccountHolderName;
    }

    public String getIFSCCOde() {
        return IFSCCOde;
    }
    public void setIFSCCOde(String IFSCCOde) {
        this.IFSCCOde = IFSCCOde;
    }

    public String getIDName() {
        return IDName;
    }
    public void setIDName(String IDName) {
        this.IDName = IDName;
    }

    public String getGSTIN() {
        return GSTIN;
    }
    public void setGSTIN(String GSTIN) {
        this.GSTIN = GSTIN;
    }

    public String getAvgOverDueDays() {
        return AvgOverDueDays;
    }
    public void setAvgOverDueDays(String AvgOverDueDays) {
        this.AvgOverDueDays = AvgOverDueDays;
    }

    public String getAvgDueDays() {
        return AvgDueDays;
    }
    public void setAvgDueDays(String AvgDueDays) {
        this.AvgDueDays = AvgDueDays;
    }

    public String getPricelistID() {
        return PricelistID;
    }
    public void setPricelistID(String PricelistID) {
        this.PricelistID = PricelistID;
    }

    public String getLabel() {
        return Label;
    }
    public void setLabel(String Label) {
        this.Label = Label;
    }

    public String getUnderName() {
        return UnderName;
    }
    public void setUnderName(String UnderName) {
        this.UnderName = UnderName;
    }
}
