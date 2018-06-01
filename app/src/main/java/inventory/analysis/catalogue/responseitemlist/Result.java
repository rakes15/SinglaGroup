package inventory.analysis.catalogue.responseitemlist;

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
    private Double minRate;
    @SerializedName("MaxRate")
    @Expose
    private Double maxRate;
    @SerializedName("MinStock")
    @Expose
    private Double minStock;
    @SerializedName("MaxStock")
    @Expose
    private Double maxStock;
    @SerializedName("TotalStock")
    @Expose
    private Double totalStock;
    @SerializedName("TotalItemValue")
    @Expose
    private Double totalItemValue;

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

    public Double getMinRate() {
        return minRate;
    }

    public void setMinRate(Double minRate) {
        this.minRate = minRate;
    }

    public Double getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(Double maxRate) {
        this.maxRate = maxRate;
    }

    public Double getMinStock() {
        return minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public Double getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Double maxStock) {
        this.maxStock = maxStock;
    }

    public Double getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Double totalStock) {
        this.totalStock = totalStock;
    }

    public Double getTotalItemValue() {
        return totalItemValue;
    }

    public void setTotalItemValue(Double totalItemValue) {
        this.totalItemValue = totalItemValue;
    }

}