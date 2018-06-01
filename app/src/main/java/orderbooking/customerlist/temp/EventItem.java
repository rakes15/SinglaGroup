package orderbooking.customerlist.temp;

import android.support.annotation.NonNull;

import orderbooking.customerlist.datasets.CloseOrBookDataset;

public class EventItem extends ListItem {

    private CloseOrBookDataset event;

    public EventItem(@NonNull CloseOrBookDataset event) {
        this.event = event;
    }

    @NonNull
    public CloseOrBookDataset getEvent() {
        return event;
    }
    // here getters and setters 
    // for title and so on, built 
    // using event

    @Override
    public int getType() {
        return TYPE_EVENT;
    }

}