package stockcheck.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class Godown implements Serializable {
    private static final long serialVersionUID = 1L;

    String GodownID,GodownName,ItemID;
    int Active;

    public String getGodownID() {
        return GodownID;
    }
    public void setGodownID(String GodownID) {
        this.GodownID = GodownID;
    }

    public String getGodownName() {
        return GodownName;
    }
    public void setGodownName(String GodownName) {
        this.GodownName = GodownName;
    }

    public int getActive() {
        return Active;
    }
    public void setActive(int Active) {
        this.Active = Active;
    }

    public String getItemID() {
        return ItemID;
    }
    public void setItemID(String ItemID) {
        this.ItemID = ItemID;
    }
}
