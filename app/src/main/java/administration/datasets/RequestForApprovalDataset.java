package administration.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class RequestForApprovalDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String PartyID,PartyName,AgentID,AgentName,Mobile,City,State,Address,Active;
    int SubPartyApplicable,MultiOrder;

    public RequestForApprovalDataset(String PartyID, String PartyName, String AgentID, String AgentName, String Mobile, String City, String State, String Address, String Active, int SubPartyApplicable, int MultiOrder) {
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.AgentID = AgentID;
        this.AgentName = AgentName;
        this.Mobile = Mobile;
        this.City = City;
        this.State = State;
        this.Address = Address;
        this.Active = Active;
        this.SubPartyApplicable = SubPartyApplicable;
        this.MultiOrder = MultiOrder;
    }
    public String getPartyID() {
        return PartyID;
    }
    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    public String getAgentID() {
        return AgentID;
    }
    public void setAgentID(String AgentID) {
        this.AgentID = AgentID;
    }

    public String getAgentName() {
        return AgentName;
    }
    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
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

    public String getAddress() {
        return Address;
    }
    public void setAddress(String Address) {
        this.AgentID = Address;
    }

    public String getActive() {
        return Active;
    }
    public void setActive(String Active) {
        this.Active = Active;
    }

    public int getSubPartyApplicable() {
        return SubPartyApplicable;
    }
    public void setSubPartyApplicable(int SubPartyApplicable) {
        this.SubPartyApplicable = SubPartyApplicable;
    }

    public int getMultiOrder() {
        return MultiOrder;
    }
    public void setMultiOrder(int MultiOrder) {
        this.MultiOrder = SubPartyApplicable;
    }

}
