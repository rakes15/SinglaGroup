package uploadimagesfiles.voucherdocupload.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class VoucherDocumentSpinnerDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String ID,VoucherHeading,Vtype;

    public VoucherDocumentSpinnerDataset(String ID, String VoucherHeading, String Vtype) {
        this.ID = ID;
        this.VoucherHeading = VoucherHeading;
        this.Vtype = Vtype;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVoucherHeading() {
        return VoucherHeading;
    }
    public void setVoucherHeading(String VoucherHeading) {
        this.VoucherHeading = VoucherHeading;
    }

    public String getVtype() {
        return Vtype;
    }
    public void setVtype(String Vtype) {
        this.Vtype = Vtype;
    }
}
