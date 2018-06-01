package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Rakesh on 23-Aug-16.
 */
public class ResponseItemDetailsSubDataset {

    @SerializedName("ItemDetails")
    private List<Map<String,String>> ItemDetails;
    @SerializedName("AttributeCaption")
    private List<Map<String,String>> AttributeCaption;

    public List<Map<String,String>> getItemDetails() {
        return ItemDetails;
    }
    public void setItemDetails(List<Map<String,String>> ItemDetails) {
        List<Map<String,String>> dataset=new ArrayList<Map<String,String>>();
        this.ItemDetails = (ItemDetails==null)?dataset:ItemDetails;
    }
    public List<Map<String,String>> getAttributeCaption() {
        return AttributeCaption;
    }
    public void setAttributeCaption(List<Map<String,String>> AttributeCaption) {
        List<Map<String,String>> dataset=new ArrayList<Map<String,String>>();
        this.AttributeCaption = (AttributeCaption==null)?dataset:AttributeCaption;
    }

}
