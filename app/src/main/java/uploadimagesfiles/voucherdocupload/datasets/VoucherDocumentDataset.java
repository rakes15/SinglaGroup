package uploadimagesfiles.voucherdocupload.datasets;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class VoucherDocumentDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String RefDate,RefDocNo,DocID,DocNo,DocDate,DocAmount,DetAmount,PartyName,SubPartyName,GLName;
    int VType;
    List<Map<String,String>> AttechDet;
    public VoucherDocumentDataset(String RefDate,String RefDocNo,int VType,String DocID, String DocNo, String DocDate,String DocAmount,String DetAmount,String PartyName,String SubPartyName,String GLName,List<Map<String,String>> AttechDet) {
        this.RefDate = RefDate;
        this.RefDocNo = RefDocNo;
        this.VType = VType;
        this.DocID = DocID;
        this.DocNo = DocNo;
        this.DocDate = DocDate;
        this.DocAmount = DocAmount;
        this.DetAmount = DetAmount;
        this.PartyName = PartyName;
        this.SubPartyName = SubPartyName;
        this.GLName = GLName;
        this.AttechDet = AttechDet;
    }
    public String getRefDate() {
        return RefDate;
    }
    public void setRefDate(String RefDate) {
        this.RefDate = RefDate;
    }

    public String getRefDocNo() {
        return RefDocNo;
    }
    public void setRefDocNo(String RefDocNo) {
        this.RefDocNo = RefDocNo;
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

    public String getDocNo() {
        return DocNo;
    }
    public void setDocNo(String DocNo) {
        this.DocNo = DocNo;
    }

    public String getDocDate() {
        return DocDate;
    }
    public void setDocDate(String DocDate) {
        this.DocDate = DocDate;
    }

    public String getDocAmount() {
        return DocAmount;
    }
    public void setDocAmount(String DocAmount) {
        this.DocAmount = DocAmount;
    }

    public String getDetAmount() {
        return DetAmount;
    }
    public void setDetAmount(String DetAmount) {
        this.DetAmount = DetAmount;
    }

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    public String getSubPartyName() {
        return SubPartyName;
    }
    public void setSubPartyName(String SubPartyName) {
        this.SubPartyName = SubPartyName;
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
