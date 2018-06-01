package orderbooking.customerlist.datasets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MstBussinessPartnerCpDataset {

@SerializedName("ID")
@Expose
private String iD;
@SerializedName("PartyID")
@Expose
private String partyID;
@SerializedName("SubPartyID")
@Expose
private String subPartyID;
@SerializedName("Name")
@Expose
private String name;
@SerializedName("Designation")
@Expose
private String designation;
@SerializedName("Address1")
@Expose
private String address1;
@SerializedName("Address2")
@Expose
private String address2;
@SerializedName("Address3")
@Expose
private String address3;
@SerializedName("PartyName")
@Expose
private String partyName;
@SerializedName("SubParty")
@Expose
private String subParty;
@SerializedName("CountryID")
@Expose
private String countryID;
@SerializedName("Country")
@Expose
private String country;
@SerializedName("StateID")
@Expose
private String stateID;
@SerializedName("State")
@Expose
private String state;
@SerializedName("CityID")
@Expose
private String cityID;
@SerializedName("City")
@Expose
private String city;
@SerializedName("CellNo")
@Expose
private String cellNo;
@SerializedName("Email")
@Expose
private String email;
@SerializedName("Fax")
@Expose
private String fax;
@SerializedName("PhoneNo")
@Expose
private String phoneNo;
@SerializedName("PIN")
@Expose
private String pIN;
@SerializedName("SG_WhatsappFLG")
@Expose
private Integer sGWhatsappFLG;
@SerializedName("SG_Remarks")
@Expose
private String sG_Remarks;


public String getID() {
return iD;
}

public void setID(String iD) {
this.iD = iD;
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

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getDesignation() {
return designation;
}

public void setDesignation(String designation) {
this.designation = designation;
}

public String getAddress1() {
return address1;
}

public void setAddress1(String address1) {
this.address1 = address1;
}

public String getAddress2() {
return address2;
}

public void setAddress2(String address2) {
this.address2 = address2;
}

public String getAddress3() {
return address3;
}

public void setAddress3(String address3) {
this.address3 = address3;
}

public String getPartyName() {
return partyName;
}

public void setPartyName(String partyName) {
this.partyName = partyName;
}

public String getSubParty() {
return subParty;
}

public void setSubParty(String subParty) {
this.subParty = subParty;
}

public String getCountryID() {
return countryID;
}

public void setCountryID(String countryID) {
this.countryID = countryID;
}

public String getCountry() {
return country;
}

public void setCountry(String country) {
this.country = country;
}

public String getStateID() {
return stateID;
}

public void setStateID(String stateID) {
this.stateID = stateID;
}

public String getState() {
return state;
}

public void setState(String state) {
this.state = state;
}

public String getCityID() {
return cityID;
}

public void setCityID(String cityID) {
this.cityID = cityID;
}

public String getCity() {
return city;
}

public void setCity(String city) {
this.city = city;
}

public String getCellNo() {
return cellNo;
}

public void setCellNo(String cellNo) {
this.cellNo = cellNo;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public String getFax() {
return fax;
}

public void setFax(String fax) {
this.fax = fax;
}

public String getPhoneNo() {
return phoneNo;
}

public void setPhoneNo(String phoneNo) {
this.phoneNo = phoneNo;
}

public String getPIN() {
return pIN;
}

public void setPIN(String pIN) {
this.pIN = pIN;
}

public Integer getSGWhatsappFLG() {
return sGWhatsappFLG;
}

public void setSGWhatsappFLG(Integer sGWhatsappFLG) {
this.sGWhatsappFLG = sGWhatsappFLG;
}

public String getSG_Remarks() {
    return sG_Remarks;
}

public void setSG_Remarks(String sG_Remarks) {
    this.sG_Remarks = sG_Remarks;
}
}