package report.godownwiseorderitem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.singlagroup.datasets.GodownDataset;

import java.util.List;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class ShowroomAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater=null;
    List<GodownDataset> data;
    public ShowroomAdapter(Context context, List<GodownDataset> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        // This a new view we inflate the new layout
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        GodownDataset dataset = data.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                if (position == 0) {
                    customerNameLabel.setTag(dataset.getGodownID());
                    customerNameLabel.setText("ALL Showrooms");
                }else{
                    customerNameLabel.setTag(dataset.getGodownID());
                    customerNameLabel.setText(dataset.getGodownName());
                }
            }
        }
        return convertView;
    }
}
