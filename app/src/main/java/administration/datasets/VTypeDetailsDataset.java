package administration.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class VTypeDetailsDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String DocID,DocDate,Branch,AmendNo,DocNo,PartyName,Narration,Remark,Requested,Amount,Remarks;
    int Status,VType;
    public VTypeDetailsDataset(String DocID, String DocDate, String Branch, String AmendNo, String DocNo,String PartyName, String Narration, String Remark, String Requested, String Amount, int Status, int VType,String Remarks) {
        this.DocID = DocID;
        this.DocDate = DocDate;
        this.Branch = Branch;
        this.AmendNo = AmendNo;
        this.DocNo = DocNo;
        this.PartyName = PartyName;
        this.Narration = Narration;
        this.Remark = Remark;
        this.Requested = Requested;
        this.Amount = Amount;
        this.Status = Status;
        this.VType = VType;
        this.Remarks = Remarks;
    }
    public String getDocID() {
        return DocID;
    }
    public void setDocID(String DocID) {
        this.DocID = DocID;
    }

    public String getDocDate() {
        return DocDate;
    }
    public void setDocDate(String DocDate) {
        this.DocDate = DocDate;
    }

    public String getBranch() {
        return Branch;
    }
    public void setBranch(String Branch) {
        this.Branch = Branch;
    }

    public String getAmendNo() {
        return AmendNo;
    }
    public void setAmendNo(String AmendNo) {
        this.AmendNo = AmendNo;
    }

    public String getDocNo() {
        return DocNo;
    }
    public void setDocNo(String DocNo) {
        this.DocNo = DocNo;
    }

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    public String getNarration() {
        return Narration;
    }
    public void setNarration(String Narration) {
        this.Narration = Narration;
    }

    public String getRemark() {
        return Remark;
    }
    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getRequested() {
        return Requested;
    }
    public void setRequested(String Requested) {
        this.Branch = Requested;
    }

    public String getAmount() {
        return Amount;
    }
    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public int getStatus() {
        return Status;
    }
    public void setStatus(int Status) {
        this.Status = Status;
    }

    public int getVType() {
        return VType;
    }
    public void setVType(int VType) {
        this.VType = Status;
    }

    public String getRemarks() {
        return Remarks;
    }
    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
}
