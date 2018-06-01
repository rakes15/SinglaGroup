package report.revertforceclose.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class RevertFlagTypeWithNameDataset implements Serializable {
    private static final long serialVersionUID = 1L;
    String FlagName;
    int FlagType;

    public RevertFlagTypeWithNameDataset(String FlagName, int FlagType) {
        this.FlagName = FlagName;
        this.FlagType = FlagType;
    }
    public RevertFlagTypeWithNameDataset() {
    }
    public String getFlagName() {
        return FlagName;
    }
    public void setFlagName(String FlagName) {
        this.FlagName = FlagName;
    }

    public int getFlagType() {
        return FlagType;
    }
    public void setFlagType(int FlagType) {
        this.FlagType = FlagType;
    }
}
