package administration.datasets;

import java.io.Serializable;

public class Device implements Serializable
{

private String iD;
private String userID;
private String fullName;
private String userName;
private String uKey;
private String iMEINo;
private String dModel;
private String mobileNo;
private String reqDt;
private String refName;
private String refRemarks;
private String macID;
private String serialNo;
private Integer allowedUser;
private Integer userType;
private Integer autoLogIN;
private Integer ownership;
private Integer reqID;
private final static long serialVersionUID = -1646816838270860730L;

public String getID() {
return iD;
}

public void setID(String iD) {
this.iD = iD;
}

public String getUserID() {
return userID;
}

public void setUserID(String userID) {
this.userID = userID;
}

public String getFullName() {
return fullName;
}

public void setFullName(String fullName) {
this.fullName = fullName;
}

public String getUserName() {
return userName;
}

public void setUserName(String userName) {
this.userName = userName;
}

public String getUKey() {
return uKey;
}

public void setUKey(String uKey) {
this.uKey = uKey;
}

public String getIMEINo() {
return iMEINo;
}

public void setIMEINo(String iMEINo) {
this.iMEINo = iMEINo;
}

public String getDModel() {
return dModel;
}

public void setDModel(String dModel) {
this.dModel = dModel;
}

public String getMobileNo() {
return mobileNo;
}

public void setMobileNo(String mobileNo) {
this.mobileNo = mobileNo;
}

public String getReqDt() {
return reqDt;
}

public void setReqDt(String reqDt) {
this.reqDt = reqDt;
}

public String getRefName() {
return refName;
}

public void setRefName(String refName) {
this.refName = refName;
}

public String getRefRemarks() {
return refRemarks;
}

public void setRefRemarks(String refRemarks) {
this.refRemarks = refRemarks;
}

public String getMacID() {
return macID;
}

public void setMacID(String macID) {
this.macID = macID;
}

public String getSerialNo() {
return serialNo;
}

public void setSerialNo(String serialNo) {
this.serialNo = serialNo;
}

public Integer getAllowedUser() {
return allowedUser;
}

public void setAllowedUser(Integer allowedUser) {
this.allowedUser = allowedUser;
}

public Integer getUserType() {
return userType;
}

public void setUserType(Integer userType) {
this.userType = userType;
}

public Integer getAutoLogIN() {
return autoLogIN;
}

public void setAutoLogIN(Integer autoLogIN) {
this.autoLogIN = autoLogIN;
}

public Integer getOwnership() {
return ownership;
}

public void setOwnership(Integer ownership) {
this.ownership = ownership;
}

public Integer getReqID() {
return reqID;
}

public void setReqID(Integer reqID) {
this.reqID = reqID;
}

}