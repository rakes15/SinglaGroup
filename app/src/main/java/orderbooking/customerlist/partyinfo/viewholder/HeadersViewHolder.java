package orderbooking.customerlist.partyinfo.viewholder;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.singlagroup.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class HeadersViewHolder extends GroupViewHolder {

    private TextView HeaderName;

    public HeadersViewHolder(View itemView) {
        super(itemView);

        HeaderName = (TextView) itemView.findViewById(R.id.textView_Header);
        HeaderName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_grey, 0);
        HeaderName.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        HeaderName.setTextColor(itemView.getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void expand() {
        HeaderName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_grey, 0);
        Log.i("Adapter", "expand");
    }

    @Override
    public void collapse() {
        Log.i("Adapter", "collapse");
        HeaderName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_grey, 0);
    }

    public void setGroupName(ExpandableGroup group) {
        HeaderName.setText(group.getTitle());
    }
}
