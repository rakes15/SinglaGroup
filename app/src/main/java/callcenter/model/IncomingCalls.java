package callcenter.model;

import java.io.Serializable;

public class IncomingCalls implements Serializable
{

private String party;
private String subparty;
private String typeName;
private String cPName;
private String calledNumber;
private String callStatusMsg;
private Integer callStatus;
private String holdTime;
private String duration;
private Integer sGAgentCount;
private String autoNo;
private String recordFileName;
private String recordFileUrl;
private final static long serialVersionUID = 1842576520723318304L;

public String getParty() {
return party;
}

public void setParty(String party) {
this.party = party;
}

public String getSubparty() {
return subparty;
}

public void setSubparty(String subparty) {
this.subparty = subparty;
}

public String getTypeName() {
return typeName;
}

public void setTypeName(String typeName) {
this.typeName = typeName;
}

public String getCPName() {
return cPName;
}

public void setCPName(String cPName) {
this.cPName = cPName;
}

public String getCalledNumber() {
return calledNumber;
}

public void setCalledNumber(String calledNumber) {
this.calledNumber = calledNumber;
}

public String getCallStatusMsg() {
return callStatusMsg;
}

public void setCallStatusMsg(String callStatusMsg) {
this.callStatusMsg = callStatusMsg;
}

public Integer getCallStatus() {
return callStatus;
}

public void setCallStatus(Integer callStatus) {
this.callStatus = callStatus;
}

public String getHoldTime() {
return holdTime;
}

public void setHoldTime(String holdTime) {
this.holdTime = holdTime;
}

public String getDuration() {
return duration;
}

public void setDuration(String duration) {
this.duration = duration;
}

public Integer getSGAgentCount() {
return sGAgentCount;
}

public void setSGAgentCount(Integer sGAgentCount) {
this.sGAgentCount = sGAgentCount;
}

public String getAutoNo() {
return autoNo;
}

public void setAutoNo(String autoNo) {
this.autoNo = autoNo;
}

public String getRecordFileName() {
return recordFileName;
}

public void setRecordFileName(String recordFileName) {
this.recordFileName = recordFileName;
}

public String getRecordFileUrl() {
return recordFileUrl;
}

public void setRecordFileUrl(String recordFileUrl) {
this.recordFileUrl = recordFileUrl;
}

}