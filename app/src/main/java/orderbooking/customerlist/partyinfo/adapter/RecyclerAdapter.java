package orderbooking.customerlist.partyinfo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.singlagroup.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

import orderbooking.customerlist.partyinfo.viewholder.HeadersViewHolder;
import orderbooking.customerlist.partyinfo.viewholder.BasicInfoViewHolder;

public class RecyclerAdapter extends ExpandableRecyclerViewAdapter<HeadersViewHolder, BasicInfoViewHolder> {

    private Activity activity;

    public RecyclerAdapter(Activity activity, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.activity = activity;
    }

    @Override
    public HeadersViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cardview_header, parent, false);

        return new HeadersViewHolder(view);
    }

    @Override
    public BasicInfoViewHolder onCreateChildViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.child_view_holder, parent, false);

        return new BasicInfoViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(BasicInfoViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        //final String contents = ((Headers) group).getItems().get(childIndex);
        //holder.onBind(contents,group);
    }

    @Override
    public void onBindGroupViewHolder(HeadersViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupName(group);
    }
}
