package orderbooking.customerlist.partyinfo.viewholder;

import android.view.View;
import android.widget.TableLayout;
import com.singlagroup.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class BasicInfoViewHolder extends ChildViewHolder {

    private TableLayout tableLayout;

    public BasicInfoViewHolder(View itemView) {
        super(itemView);
        tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
    }

    public void onBind(String contents, ExpandableGroup group) {
//        phoneName.setText(contents.getName());
//        if (group.getTitle().equals("Android")) {
//            //phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nexus, 0, 0, 0);
//        } else if (group.getTitle().equals("iOS")) {
//            //phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.iphone, 0, 0, 0);
//        } else {
//            //phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.window_phone, 0, 0, 0);
//        }
    }
}
