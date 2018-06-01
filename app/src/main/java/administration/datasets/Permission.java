package administration.datasets;

import java.io.Serializable;

public class Permission implements Serializable
{

private String captionID;
private Integer viewFlag;
private Integer editFlag;
private Integer createFlag;
private Integer removeFlag;
private Integer printFlag;
private Integer importFlag;
private Integer exportFlag;
private Integer isModule;
private final static long serialVersionUID = 3370045849769652462L;

public String getCaptionID() {
return captionID;
}

public void setCaptionID(String captionID) {
this.captionID = captionID;
}

public Integer getViewFlag() {
return viewFlag;
}

public void setViewFlag(Integer viewFlag) {
this.viewFlag = viewFlag;
}

public Integer getEditFlag() {
return editFlag;
}

public void setEditFlag(Integer editFlag) {
this.editFlag = editFlag;
}

public Integer getCreateFlag() {
return createFlag;
}

public void setCreateFlag(Integer createFlag) {
this.createFlag = createFlag;
}

public Integer getRemoveFlag() {
return removeFlag;
}

public void setRemoveFlag(Integer removeFlag) {
this.removeFlag = removeFlag;
}

public Integer getPrintFlag() {
return printFlag;
}

public void setPrintFlag(Integer printFlag) {
this.printFlag = printFlag;
}

public Integer getImportFlag() {
return importFlag;
}

public void setImportFlag(Integer importFlag) {
this.importFlag = importFlag;
}

public Integer getExportFlag() {
return exportFlag;
}

public void setExportFlag(Integer exportFlag) {
this.exportFlag = exportFlag;
}

public Integer getIsModule() {
return isModule;
}

public void setIsModule(Integer isModule) {
this.isModule = isModule;
}

}