package callcenter.model;

import java.io.Serializable;

public class CallSummary implements Serializable
{

private Integer totalCountg;
private String callStatusMsg;
private Integer callStatus;
private final static long serialVersionUID = -4500800114226800838L;

public Integer getTotalCountg() {
return totalCountg;
}

public void setTotalCountg(Integer totalCountg) {
this.totalCountg = totalCountg;
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

}
