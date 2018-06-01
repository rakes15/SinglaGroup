package orderbooking.customerlist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class RunningFairAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private static LayoutInflater inflater=null;
    List<Map<String,String>> data=null;
    List<Map<String,String>> data2=null;
    public RunningFairAdapter(Context context, List<Map<String,String>> data, List<Map<String,String>> data2) {
        this.context = context;
        this.data = data;
        this.data2 = data2;
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
        txt.setText(data.get(position).get("Name"));
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
        Map<String,String> dataset = data.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(dataset.get("ID"));
                customerNameLabel.setText(dataset.get("Name"));
            }
        }
        return convertView;
    }
    public android.widget.Filter getFilter() {

        return new android.widget.Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults Result = new FilterResults();
                // if constraint is empty return the original names
                data=data2;
                if(constraint.length() == 0 ){
                    Result.values = data;
                    Result.count = data.size();
                    Log.e("Results", Result.values.toString() + String.valueOf(Result.count));
                    return Result;
                }

                List<Map<String,String>> Filtered_Names = new ArrayList<Map<String,String>>();
                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for(int i = 0; i<data.size(); i++){
                    Map<String,String> searchdata = data.get(i);
                    String DocNo = searchdata.get("Name");
                    filterableString = DocNo;
                    if(filterableString.toLowerCase().contains(filterString)){
                        Filtered_Names.add(data.get(i));
                        Log.e("Added", String.valueOf(Filtered_Names.size()));
                    }
                }
                Result.values = Filtered_Names;
                Result.count = Filtered_Names.size();
                Log.e("Results", Result.values.toString() + String.valueOf(Result.count));
                return Result;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null) {
                    if (results.count > 0) {
                        data = new ArrayList<Map<String,String>>((List<Map<String,String>>) results.values) ;
                    }
                    else
                    {
                        data = new ArrayList<Map<String,String>>() ;
                    }
                }
                notifyDataSetChanged();
            }

        };
    }
}
