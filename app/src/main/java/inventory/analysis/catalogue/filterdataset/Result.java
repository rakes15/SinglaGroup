package inventory.analysis.catalogue.filterdataset;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("Cap")
@Expose
private String cap;
@SerializedName("Seq")
@Expose
private Integer seq;
@SerializedName("Attribute")
@Expose
private List<Attribute> attribute = null;

public String getCap() {
return cap;
}

public void setCap(String cap) {
this.cap = cap;
}

public Integer getSeq() {
return seq;
}

public void setSeq(Integer seq) {
this.seq = seq;
}

public List<Attribute> getAttribute() {
return attribute;
}

public void setAttribute(List<Attribute> attribute) {
this.attribute = attribute;
}

}