package orderbooking.customerlist.temp;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class GodownDS implements Serializable {
    private static final long serialVersionUID = 1L;

    String Godown;
    public GodownDS(String Godown) {
        this.Godown = Godown;
    }
    public GodownDS() {
    }
    public String getGodown() {
        return Godown;
    }
    public void setGodown(String Godown) {
        this.Godown = Godown;
    }

}
