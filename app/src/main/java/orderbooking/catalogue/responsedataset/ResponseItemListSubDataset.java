package orderbooking.catalogue.responsedataset;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import orderbooking.catalogue.adapter.RecyclerItemDataset;


/**
 * Created by Rakesh on 23-Aug-16.
 */
public class ResponseItemListSubDataset {

    @SerializedName("ItemList")
    private List<RecyclerItemDataset> ItemList;
    @SerializedName("TotalStyle")
    private int TotalStyle;
    private Integer totalStyle;
    @SerializedName("MinRate")
    private Integer minRate;
    @SerializedName("MaxRate")
    private Integer maxRate;
    @SerializedName("MinStock")
    private Integer minStock;
    @SerializedName("MaxStock")
    private Integer maxStock;

    public List<RecyclerItemDataset> getItemList() {
        return ItemList;
    }
    public void setItemList(List<RecyclerItemDataset> ItemList) {
        this.ItemList = ItemList;
    }

    public int getTotalStyle() {
        return TotalStyle;
    }
    public void setTotalStyle(int TotalStyle) {
        this.TotalStyle = TotalStyle;
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
}
