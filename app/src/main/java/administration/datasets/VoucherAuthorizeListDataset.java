package administration.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class VoucherAuthorizeListDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String Heading,VType;

    public VoucherAuthorizeListDataset(String Heading, String VType) {
        this.Heading = Heading;
        this.VType = VType;
    }
    public String getHeading() {
        return Heading;
    }
    public void setHeading(String Heading) {
        this.Heading = Heading;
    }

    public String getVType() {
        return VType;
    }
    public void setVType(String VType) {
        this.VType = VType;
    }
}
