package orderbooking.customerlist.temp;

import android.support.annotation.NonNull;

import orderbooking.customerlist.datasets.GodownDataset;

public class HeaderItem extends ListItem {

    private String Godown;

    public HeaderItem(@NonNull String date) {
        this.Godown = date;
    }

    @NonNull
    public String getGodown() {
        return Godown;
    }

    // here getters and setters
    // for title and so on, built
    // using date

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}