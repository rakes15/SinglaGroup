package uploadimagesfiles.voucherdocupload.datasets;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class MasterDocumentDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String DocID,City,State,PartyName,GLName;
    int SubParty,VType;
    List<Map<String,String>> AttechDet;
    public MasterDocumentDataset(int VType,String DocID, String City, String State, String PartyName, int SubParty, String GLName, List<Map<String,String>> AttechDet) {
        this.VType = VType;
        this.DocID = DocID;
        this.City = City;
        this.State = State;
        this.PartyName = PartyName;
        this.SubParty = SubParty;
        this.GLName = GLName;
        this.AttechDet = AttechDet;
    }

    public int getVType() {
        return VType;
    }
    public void setVType(int VType) {
        this.VType = VType;
    }

    public String getDocID() {
        return DocID;
    }
    public void setDocID(String DocID) {
        this.DocID = DocID;
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

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    public int getSubParty() {
        return SubParty;
    }
    public void setSubParty(int SubParty) {
        this.SubParty = SubParty;
    }

    public String getGLName() {
        return GLName;
    }
    public void setGLName(String GLName) {
        this.GLName = GLName;
    }

    public List<Map<String,String>> getAttechDet() {
        return AttechDet;
    }
    public void setAttechDet(List<Map<String,String>> AttechDet) {
        this.AttechDet = AttechDet;
    }
}
