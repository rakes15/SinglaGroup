package orderbooking.customerlist.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class AgentDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String AgentName,AgentID,GodownID;

    public AgentDataset(String AgentID, String AgentName,String GodownID) {
        this.AgentName = AgentName;
        this.AgentID = AgentID;
        this.GodownID = GodownID;
    }
    public String getAgentName() {
        return AgentName;
    }
    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }
    public String getAgentID() {
        return AgentID;
    }
    public void setAgentID(String AgentID) {
        this.AgentID = AgentID;
    }

    public String getGodownID() {
        return GodownID;
    }
    public void setGodownID(String GodownID) {
        this.GodownID = GodownID;
    }

}
