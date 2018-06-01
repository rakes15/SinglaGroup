package report.showroomitemcheck.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.singlagroup.datasets.ShowroomDataset;
import java.util.List;

public class ShowroomAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater=null;
    List<ShowroomDataset> data;
    public ShowroomAdapter(Context context, List<ShowroomDataset> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public boolean isEnabled(int position){
        if(position == 0)
        {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        }
        else
        {
            return true;
        }
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(14, 14, 14, 14);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(data.get(position).getShowroomName());
        if(position == 0){
            // Set the hint text color gray
            txt.setTextColor(Color.GRAY);
        }
        else {
            txt.setTextColor(Color.BLACK);
        }
        return  txt;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // This a new view we inflate the new layout
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        ShowroomDataset dataset = data.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(dataset.getShowroomID());
                customerNameLabel.setText(dataset.getShowroomName());
            }
        }
        return convertView;
    }
}
