package orderbooking.view_order_details.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.singlagroup.R;

import java.util.List;
import java.util.Map;

public class FilterTabListAdapter extends BaseAdapter{

	private Context context;
	List<Map<String, String>> mapList=null;
	private static LayoutInflater inflater=null;
	String[] FilterCaptionSequence;
	//Constructor to initialize values
	public FilterTabListAdapter(Context context, List<Map<String, String>> mapList) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapList=mapList;
		//this.FilterCaptionSequence = FilterCaptionSequence;
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
	
    // Number of times getView method call depends upon gridValues.length
	
	public View getView(int position, View convertView, ViewGroup parent) {

		 //LayoutInflator to call external grid_item.xml file
		 inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View rowView = inflater.inflate(R.layout.cardview_tab_design, null);
		 TextView tv=(TextView) rowView.findViewById(R.id.textView_title);
		 ImageView imageView=(ImageView) rowView.findViewById(R.id.imageView_icon);
		 tv.setText(mapList.get(position).get("Caption"));
		 return rowView;
	}
}
