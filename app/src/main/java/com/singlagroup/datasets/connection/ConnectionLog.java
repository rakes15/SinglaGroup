package com.singlagroup.datasets.connection;

import java.io.Serializable;

public class ConnectionLog implements Serializable
{

private String iD;
private String userID;
private String deviceID;
private Integer type;
private String macId;
private String imeiNo;
private String modelNo;
private String updateDate;
private String connectionStatus;
private String userName;
private final static long serialVersionUID = 107874014085887911L;

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

public String getDeviceID() {
return deviceID;
}

public void setDeviceID(String deviceID) {
this.deviceID = deviceID;
}

public Integer getType() {
return type;
}

public void setType(Integer type) {
this.type = type;
}

public String getMacId() {
return macId;
}

public void setMacId(String macId) {
this.macId = macId;
}

public String getImeiNo() {
return imeiNo;
}

public void setImeiNo(String imeiNo) {
this.imeiNo = imeiNo;
}

public String getModelNo() {
return modelNo;
}

public void setModelNo(String modelNo) {
this.modelNo = modelNo;
}

public String getUpdateDate() {
return updateDate;
}

public void setUpdateDate(String updateDate) {
this.updateDate = updateDate;
}

public String getConnectionStatus() {
return connectionStatus;
}

public void setConnectionStatus(String connectionStatus) {
this.connectionStatus = connectionStatus;
}

public String getUserName() {
return userName;
}

public void setUserName(String userName) {
this.userName = userName;
}

}