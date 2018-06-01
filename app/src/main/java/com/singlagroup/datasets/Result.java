package com.singlagroup.datasets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {
@SerializedName("DeviceRequestNo")
@Expose
private String deviceRequestNo;
@SerializedName("DefUserID")
@Expose
private String defUserID;
@SerializedName("DeviceType")
@Expose
private String deviceType;
@SerializedName("AssetNo")
@Expose
private String assetNo;
@SerializedName("Remarks")
@Expose
private String remarks;
@SerializedName("EmpCVID")
@Expose
private String empCVID;
@SerializedName("UserFullName")
@Expose
private String userFullName;
@SerializedName("CompanyName")
@Expose
private String companyName;
@SerializedName("DBName")
@Expose
private String dBName;
@SerializedName("RequestStatus")
@Expose
private Integer requestStatus;
@SerializedName("AllowUser")
@Expose
private Integer allowUser;
@SerializedName("AutoLogIN")
@Expose
private Integer autoLogIN;
@SerializedName("DeviceID")
@Expose
private String deviceID;
@SerializedName("DefaultUser")
@Expose
private String defaultUser;
@SerializedName("Password")
@Expose
private String password;
@SerializedName("AppDownloadLink")
@Expose
private String appDownloadLink;
@SerializedName("Msg")
@Expose
private String msg;
@SerializedName("DefDivision")
@Expose
private String defDivision;
@SerializedName("DefBranch")
@Expose
private String defBranch;
@SerializedName("DefGodown")
@Expose
private String defGodown;
@SerializedName("EmpCVType")
@Expose
private String empCVType;
@SerializedName("EmpCVName")
@Expose
private String empCVName;

public String getDeviceRequestNo() {
    return deviceRequestNo;
}

public void setDeviceRequestNo(String deviceRequestNo) {
    this.deviceRequestNo = deviceRequestNo;
}

public String getDefUserID() {
return defUserID;
}

public void setDefUserID(String defUserID) {
this.defUserID = defUserID;
}

public String getDeviceType() {
return deviceType;
}

public void setDeviceType(String deviceType) {
this.deviceType = deviceType;
}

public String getAssetNo() {
return assetNo;
}

public void setAssetNo(String assetNo) {
this.assetNo = assetNo;
}

public String getRemarks() {
return remarks;
}

public void setRemarks(String remarks) {
this.remarks = remarks;
}

public String getEmpCVID() {
return empCVID;
}

public void setEmpCVID(String empCVID) {
this.empCVID = empCVID;
}

public String getUserFullName() {
return userFullName;
}

public void setUserFullName(String userFullName) {
this.userFullName = userFullName;
}

public String getCompanyName() {
return companyName;
}

public void setCompanyName(String companyName) {
this.companyName = companyName;
}

public String getDBName() {
return dBName;
}

public void setDBName(String dBName) {
this.dBName = dBName;
}

public Integer getRequestStatus() {
return requestStatus;
}

public void setRequestStatus(Integer requestStatus) {
this.requestStatus = requestStatus;
}

public Integer getAllowUser() {
return allowUser;
}

public void setAllowUser(Integer allowUser) {
this.allowUser = allowUser;
}

public Integer getAutoLogIN() {
return autoLogIN;
}

public void setAutoLogIN(Integer autoLogIN) {
this.autoLogIN = autoLogIN;
}

public String getDeviceID() {
return deviceID;
}

public void setDeviceID(String deviceID) {
this.deviceID = deviceID;
}

public String getDefaultUser() {
return defaultUser;
}

public void setDefaultUser(String defaultUser) {
this.defaultUser = defaultUser;
}

public String getPassword() {
return password;
}

public void setPassword(String password) {
this.password = password;
}

public String getAppDownloadLink() {
return appDownloadLink;
}

public void setAppDownloadLink(String appDownloadLink) {
this.appDownloadLink = appDownloadLink;
}

public String getMsg() {
return msg;
}

public void setMsg(String msg) {
this.msg = msg;
}

public String getDefDivision() {
return defDivision;
}

public void setDefDivision(String defDivision) {
this.defDivision = defDivision;
}

public String getDefBranch() {
return defBranch;
}

public void setDefBranch(String defBranch) {
this.defBranch = defBranch;
}

public String getDefGodown() {
return defGodown;
}

public void setDefGodown(String defGodown) {
this.defGodown = defGodown;
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

}