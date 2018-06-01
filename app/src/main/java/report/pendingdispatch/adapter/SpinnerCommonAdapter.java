package report.pendingdispatch.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint({ "ViewHolder", "ResourceAsColor" })
public class SpinnerCommonAdapter extends BaseAdapter{
	

    private Context context;
    List<Map<String, String>> data=null;
    private static LayoutInflater inflater=null;
    public SpinnerCommonAdapter(Context context, List<Map<String, String>> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (data==null)?0:data.size();
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
		txt.setTextSize(16);
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
        inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        TextView TXT = (TextView) convertView.findViewById(android.R.id.text1);
        Map<String,String> map =new HashMap<String,String>();
        map=data.get(position);
        TXT.setTextSize(14);
        TXT.setPadding(10, 10, 10, 10);
        //TXT.setTextColor(R.color.txt_font);
        TXT.setText(map.get("Name"));
		return convertView;
	}
    
}
