package inventory.analysis.catalogue.filterdataset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

@SerializedName("ID")
@Expose
private String iD;
@SerializedName("Name")
@Expose
private String name;
@SerializedName("Seq")
@Expose
private Integer seq;

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

public Integer getSeq() {
return seq;
}

public void setSeq(Integer seq) {
this.seq = seq;
}

}