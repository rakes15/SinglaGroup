package orderbooking.customerlist.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class SelectSubCustomerForOrderDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String PartyID,SubPartyID,SubPartyName,SubPartyCode,Mobile,City,State,Address1,Address2,Email,AccountNo,AccountHolderName,IFSCCOde,IDName,GSTIN;
    String Label;
    public SelectSubCustomerForOrderDataset(String PartyID, String SubPartyID, String SubPartyName, String SubPartyCode, String Mobile, String City, String State, String Address1, String Address2,String Email,String AccountNo,String AccountHolderName,String IFSCCOde,String IDName,String GSTIN) {
        this.PartyID = PartyID;
        this.SubPartyID = SubPartyID;
        this.SubPartyName = SubPartyName;
        this.SubPartyCode = SubPartyCode;
        this.Mobile = Mobile;
        this.City = City;
        this.State = State;
        this.Address1 = Address1;
        this.Address2 = Address2;
        this.Email = Email;
        this.AccountNo = AccountNo;
        this.AccountHolderName = AccountHolderName;
        this.IFSCCOde = IFSCCOde;
        this.IDName = IDName;
        this.GSTIN = GSTIN;
        //this.Label = Label;
    }
    public String getPartyID() {
        return PartyID;
    }
    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    public String getSubPartyID() {
        return SubPartyID;
    }
    public void setSubPartyID(String SubPartyID) {
        this.SubPartyID = SubPartyID;
    }

    public String getSubPartyName() {
        return SubPartyName;
    }
    public void setSubPartyName(String SubPartyName) {
        this.SubPartyName = SubPartyName;
    }

    public String getSubPartyCode() {
        return SubPartyCode;
    }
    public void setSubPartyCode(String SubPartyCode) {
        this.SubPartyCode = SubPartyCode;
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

    public String getAddress1() {
        return Address1;
    }
    public void setAddress1(String Address1) {
        this.Address1 = Address1;
    }

    public String getAddress2() {
        return Address2;
    }
    public void setAddress2(String Address2) {
        this.Address2 = Address2;
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

    public String getLabel() {
        return Label;
    }
    public void setLabel(String Label) {
        this.Label = Label;
    }
}
