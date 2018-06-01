package uploadimagesfiles.voucherdocupload.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class FileImageDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String Path,Description;
    boolean DescFlag;

    public FileImageDataset(String Path, String Description, boolean DescFlag) {
        this.Path = Path;
        this.Description = Description;
        this.DescFlag = DescFlag;
    }

    public String getPath() {
        return Path;
    }
    public void setPath(String Path) {
        this.Path = Path;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String Description) {
        this.Description = Description;
    }

    public boolean getDescFlag() {
        return DescFlag;
    }
    public void setDescFlag(boolean DescFlag) {
        this.DescFlag = DescFlag;
    }
}
