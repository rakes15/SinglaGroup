package orderbooking.view_order_details.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.singlagroup.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerFilter;

public class FilterCheckBoxListAdapter extends BaseAdapter{

	private Context context;
	List<Map<String, String>> mapList=null;
	private static LayoutInflater inflater=null;
	DatabaseSqlLiteHandlerFilter DBHandler;
	//Constructor to initialize values
	public FilterCheckBoxListAdapter(Context context, List<Map<String, String>> mapList) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapList = mapList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		
		// Number of times getView method call depends upon gridValues.length
		return mapList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		 //LayoutInflator to call external grid_item.xml file
		 inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View rowView = inflater.inflate(R.layout.cardview_checkbox_design, null);
		 DBHandler=new DatabaseSqlLiteHandlerFilter(context);
		 //LinearLayout linearLayoutColorBox=(LinearLayout) rowView.findViewById(R.id.linearLayout_ColorBox);
		 CheckBox checkBox=(CheckBox) rowView.findViewById(R.id.checkbox_title);
		 Map<String, String> map = new HashMap<String, String>();
		 map = mapList.get(position);
		 if(map.get("Flag").equals("1")) {
			 checkBox.setChecked(true);
		 }else{
			 checkBox.setChecked(false);
		 }
		 checkBox.setText(map.get("Attr"));
		 checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 @Override
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 if (isChecked==true) {
					 DBHandler.UpdateFlag(1,Integer.valueOf(mapList.get(position).get("Seq")),mapList.get(position).get("AttrID"));
				 }
				 else{
					 DBHandler.UpdateFlag(0,Integer.valueOf(mapList.get(position).get("Seq")),mapList.get(position).get("AttrID"));
				 }
			 }
		 });
		 return rowView;
	}
}
