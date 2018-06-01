package callcenter.model;

import java.io.Serializable;

public class AgentLS implements Serializable
{

private Integer pBXLogin;
private String extentionNo;
private String state;
private Integer callConnected;
private String callNumber;
private String skillLevel;
private String agentName;
private String agFullName;
private String cRMState;
private String callDurration;
private String cPName;
private String party;
private String subParty;
private String typeName;
private final static long serialVersionUID = -8076749955965191419L;

public Integer getPBXLogin() {
return pBXLogin;
}

public void setPBXLogin(Integer pBXLogin) {
this.pBXLogin = pBXLogin;
}

public String getExtentionNo() {
return extentionNo;
}

public void setExtentionNo(String extentionNo) {
this.extentionNo = extentionNo;
}

public String getState() {
return state;
}

public void setState(String state) {
this.state = state;
}

public Integer getCallConnected() {
return callConnected;
}

public void setCallConnected(Integer callConnected) {
this.callConnected = callConnected;
}

public String getCallNumber() {
return callNumber;
}

public void setCallNumber(String callNumber) {
this.callNumber = callNumber;
}

public String getSkillLevel() {
return skillLevel;
}

public void setSkillLevel(String skillLevel) {
this.skillLevel = skillLevel;
}

public String getAgentName() {
return agentName;
}

public void setAgentName(String agentName) {
this.agentName = agentName;
}

public String getAgFullName() {
    return agFullName;
}

public void setAgFullName(String agFullName) {
    this.agFullName = agFullName;
}

public String getCRMState() {
return cRMState;
}

public void setCRMState(String cRMState) {
this.cRMState = cRMState;
}

public String getCallDurration() {
return callDurration;
}

public void setCallDurration(String callDurration) {
this.callDurration = callDurration;
}

public String getCPName() {
return cPName;
}

public void setCPName(String cPName) {
this.cPName = cPName;
}

public String getParty() {
return party;
}

public void setParty(String party) {
this.party = party;
}

public String getSubParty() {
return subParty;
}

public void setSubParty(String subParty) {
this.subParty = subParty;
}

public String getTypeName() {
return typeName;
}

public void setTypeName(String typeName) {
this.typeName = typeName;
}

}