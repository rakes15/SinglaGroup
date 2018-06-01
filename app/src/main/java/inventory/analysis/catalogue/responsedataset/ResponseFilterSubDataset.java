package inventory.analysis.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Rakesh on 23-Aug-16.
 */
public class ResponseFilterSubDataset {

    @SerializedName("Filter")
    private List<Map<String,String>> Filter;
    @SerializedName("FilterCaption")
    private List<Map<String,String>> FilterCaption;

    public List<Map<String,String>> getFilter() {
        return Filter;
    }
    public void setFilter(List<Map<String,String>> Filter) {
        List<Map<String,String>> dataset=new ArrayList<Map<String,String>>();
        this.Filter = (Filter==null)?dataset:Filter;
    }

    public List<Map<String,String>> getFilterCaption() {
        return FilterCaption;
    }
    public void setFilterCaption(List<Map<String,String>> FilterCaption) {
        List<Map<String,String>> dataset=new ArrayList<Map<String,String>>();
        this.FilterCaption = (FilterCaption==null)?dataset:FilterCaption;
    }

}
