package orderbooking.catalogue.responseitemlistcolor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

@SerializedName("ItemList")
@Expose
private List<ItemList> itemList = null;
@SerializedName("TotalStyle")
@Expose
private Integer totalStyle;
@SerializedName("MinRate")
@Expose
private Integer minRate;
@SerializedName("MaxRate")
@Expose
private Integer maxRate;
@SerializedName("MinStock")
@Expose
private Integer minStock;
@SerializedName("MaxStock")
@Expose
private Integer maxStock;
@SerializedName("Filter")
@Expose
private List<Object> filter = null;
@SerializedName("FilterCaption")
@Expose
private List<Object> filterCaption = null;

public List<ItemList> getItemList() {
return itemList;
}

public void setItemList(List<ItemList> itemList) {
this.itemList = itemList;
}

public Integer getTotalStyle() {
return totalStyle;
}

public void setTotalStyle(Integer totalStyle) {
this.totalStyle = totalStyle;
}

public Integer getMinRate() {
return minRate;
}

public void setMinRate(Integer minRate) {
this.minRate = minRate;
}

public Integer getMaxRate() {
return maxRate;
}

public void setMaxRate(Integer maxRate) {
this.maxRate = maxRate;
}

public Integer getMinStock() {
return minStock;
}

public void setMinStock(Integer minStock) {
this.minStock = minStock;
}

public Integer getMaxStock() {
return maxStock;
}

public void setMaxStock(Integer maxStock) {
this.maxStock = maxStock;
}

public List<Object> getFilter() {
return filter;
}

public void setFilter(List<Object> filter) {
this.filter = filter;
}

public List<Object> getFilterCaption() {
return filterCaption;
}

public void setFilterCaption(List<Object> filterCaption) {
this.filterCaption = filterCaption;
}

}