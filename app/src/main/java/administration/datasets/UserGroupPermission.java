package administration.datasets;

import java.io.Serializable;
import java.util.List;

public class UserGroupPermission implements Serializable
{

private String iD;
private String name;
private List<Permission> permission = null;
private final static long serialVersionUID = 4453834140132526750L;

public String getID() {
return iD;
}

public void setID(String iD) {
this.iD = iD;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public List<Permission> getPermission() {
return permission;
}

public void setPermission(List<Permission> permission) {
this.permission = permission;
}

}