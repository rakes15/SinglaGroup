package orderbooking.customerlist.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class GodownDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String Godown,GodownID;

    public GodownDataset( String GodownID, String Godown) {
        this.Godown = Godown;
        this.GodownID = GodownID;
    }
    public GodownDataset(){

    }
    public String getGodown() {
        return Godown;
    }
    public void setGodown(String Godown) {
        this.Godown = Godown;
    }
    public String getGodownID() {
        return GodownID;
    }
    public void setGodownID(String GodownID) {
        this.GodownID = GodownID;
    }


}
