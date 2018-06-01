package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class DivisionEmpDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String DivisionID,Division;

    public DivisionEmpDataset(String DivisionID, String Division) {
        this.DivisionID = DivisionID;
        this.Division = Division;
    }
    public String getDivisionID() {
        return DivisionID;
    }
    public void setDivisionID(String DivisionID) {
        this.DivisionID = DivisionID;
    }

    public String getDivision() {
        return Division;
    }
    public void setDivision(String Division) {
        this.Division = Division;
    }

}
