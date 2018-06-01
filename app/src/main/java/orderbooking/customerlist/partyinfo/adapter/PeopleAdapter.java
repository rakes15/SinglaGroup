package orderbooking.customerlist.partyinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.ExpandableRecyclerAdapter;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends ExpandableRecyclerAdapter<PeopleAdapter.PeopleListItem> {
public static final int TYPE_PERSON = 1001;
private List<PeopleListItem> items;
public PeopleAdapter(Context context,List<PeopleListItem> items) {
    super(context);
    setItems(items);
    this.items = items;
}

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
    public String Text;

    public PeopleListItem(String group) {
        super(TYPE_HEADER);

        Text = group;
    }

    public PeopleListItem(String Title, String Content) {
        super(TYPE_PERSON);
        if (!Content.isEmpty()) {
            Text = Title + "/" + Content;
        }else{
            Text = Title;
        }
    }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
    TextView name;

    public HeaderViewHolder(View view) {
        super(view, (ImageView) view.findViewById(R.id.img_arrow));

        name = (TextView) view.findViewById(R.id.txt_header_name);
    }

    public void bind(int position) {
        super.bind(position);

        name.setText(visibleItems.get(position).Text);
    }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
    TableLayout tableLayout;

    public PersonViewHolder(View view) {
        super(view);
        tableLayout = (TableLayout) view.findViewById(R.id.table_Layout);
    }

    public void bind(int position) {
        //String Header = visibleItems.get(position).Text;
        setTableLayout(tableLayout,items.get(position));
        //MessageDialog.MessageDialog(mContext,"","position: "+position);
    }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
        case TYPE_HEADER:
            return new HeaderViewHolder(inflate(R.layout.item_header, parent));
        case TYPE_PERSON:
        default:
            return new PersonViewHolder(inflate(R.layout.child_view_holder, parent));
    }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
    switch (getItemViewType(position)) {
        case TYPE_HEADER:
            ((HeaderViewHolder) holder).bind(position);
            break;
        case TYPE_PERSON:
        default:
            ((PersonViewHolder) holder).bind(position);
            break;
    }
    }
    private void setTableLayout(TableLayout tableLayout,PeopleListItem peopleListItem){
        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();
        if (peopleListItem.Text.contains("/")) {
            String[] str = peopleListItem.Text.split("/");
            //TODO: 1nd Row
            View v1 = LayoutInflater.from(mContext).inflate(R.layout.table_row, tableLayout, false);
            TextView txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("" + str[0]);

            TextView txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + str[1]);
            tableLayout.addView(v1);
        }else{
            ///TODO: 1nd Row
            View v1 = LayoutInflater.from(mContext).inflate(R.layout.table_row, tableLayout, false);
            TextView txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("" + peopleListItem.Text);

            TextView txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("");
            tableLayout.addView(v1);
        }
    }
}