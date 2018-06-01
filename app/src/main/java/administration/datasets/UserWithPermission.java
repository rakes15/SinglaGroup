package administration.datasets;

import java.io.Serializable;

public class UserWithPermission implements Serializable
{

private String userGroupID;
private String typeName;
private String taggedName;
private String userGroup;
private String userID;
private String userName;
private Integer sGScreenshot;
private String fullName;
private String phone;
private String emailID;
private Integer sGDeviceAccess;
private Integer sGMultiSession;
private Integer sGWebAccessFlag;
private Integer sGAutoLaunchModule;
private final static long serialVersionUID = -6552342324377212680L;

public String getUserGroupID() {
return userGroupID;
}

public void setUserGroupID(String userGroupID) {
this.userGroupID = userGroupID;
}

public String getTypeName() {
return typeName;
}

public void setTypeName(String typeName) {
this.typeName = typeName;
}

public String getTaggedName() {
return taggedName;
}

public void setTaggedName(String taggedName) {
this.taggedName = taggedName;
}

public String getUserGroup() {
return userGroup;
}

public void setUserGroup(String userGroup) {
this.userGroup = userGroup;
}

public String getUserID() {
return userID;
}

public void setUserID(String userID) {
this.userID = userID;
}

public String getUserName() {
return userName;
}

public void setUserName(String userName) {
this.userName = userName;
}

public Integer getSGScreenshot() {
return sGScreenshot;
}

public void setSGScreenshot(Integer sGScreenshot) {
this.sGScreenshot = sGScreenshot;
}

public String getFullName() {
return fullName;
}

public void setFullName(String fullName) {
this.fullName = fullName;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public String getEmailID() {
return emailID;
}

public void setEmailID(String emailID) {
this.emailID = emailID;
}

public Integer getSGDeviceAccess() {
return sGDeviceAccess;
}

public void setSGDeviceAccess(Integer sGDeviceAccess) {
this.sGDeviceAccess = sGDeviceAccess;
}

public Integer getSGMultiSession() {
return sGMultiSession;
}

public void setSGMultiSession(Integer sGMultiSession) {
this.sGMultiSession = sGMultiSession;
}

public Integer getSGWebAccessFlag() {
return sGWebAccessFlag;
}

public void setSGWebAccessFlag(Integer sGWebAccessFlag) {
this.sGWebAccessFlag = sGWebAccessFlag;
}

public Integer getSGAutoLaunchModule() {
return sGAutoLaunchModule;
}

public void setSGAutoLaunchModule(Integer sGAutoLaunchModule) {
this.sGAutoLaunchModule = sGAutoLaunchModule;
}

}
