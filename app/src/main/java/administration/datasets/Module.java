package administration.datasets;

import java.io.Serializable;

public class Module implements Serializable
{

private Integer vType;
private String iD;
private String caption;
private Permission permission;
private final static long serialVersionUID = 6693912655169192653L;

public Integer getVType() {
return vType;
}

public void setVType(Integer vType) {
this.vType = vType;
}

public String getID() {
    return iD;
}

public void setID(String iD) {
    this.iD = iD;
}

public String getCaption() {
return caption;
}

public void setCaption(String caption) {
this.caption = caption;
}

public Permission getPermission() {
    return permission;
}

public void setPermission(Permission permission) {
    this.permission = permission;
}


}