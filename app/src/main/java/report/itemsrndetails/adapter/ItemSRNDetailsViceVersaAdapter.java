package report.itemsrndetails.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
public class ItemSRNDetailsViceVersaAdapter extends RecyclerView.Adapter<ItemSRNDetailsViceVersaAdapter.RecyclerViewHolder>{

	private Context context;
	private ProgressDialog progressDialog;
	private LayoutInflater inflater;
	private List<Map<String,String>> datasetList,filterDatasetList;
	public ItemSRNDetailsViceVersaAdapter(Context context, List<Map<String,String>> datasetList) {
		this.context=context;
		this.datasetList=datasetList;
		this.filterDatasetList = new ArrayList<>();
		this.filterDatasetList.addAll(this.datasetList);
		this.inflater = LayoutInflater.from(context);
		this.progressDialog = new ProgressDialog(this.context);
		this.progressDialog.setMessage("Please wait...");
		this.progressDialog.setCanceledOnTouchOutside(false);
	}
	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
		RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
		return viewHolder;
	}
	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

		final Map<String,String> dataset = filterDatasetList.get(position);
		//TODO: call TableLayout method
		setTableLayout(dataset, position, holder.tableLayout, holder.tableLayout2);
	}
	@Override
	public int getItemCount() {
		return (null != filterDatasetList ? filterDatasetList.size() : 0);
	}
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return filterDatasetList.get(position);
	}
	//TODO: Filter Search
	public void filter(final String text) {

		// Searching could be complex..so we will dispatch it to a different thread...
		new Thread(new Runnable() {
			@Override
			public void run() {

				// Clear the filter list
				filterDatasetList.clear();

				// If there is no search value, then add all original list items to filter list
				if (TextUtils.isEmpty(text)) {

					filterDatasetList.addAll(datasetList);

				} else {
					// Iterate in the original List and add it to filter list...
					for (Map<String,String> item : datasetList) {
//                        if(item.getGroupName()!=null && item.getMainGroup()!=null)
//                            if (item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
//                                    item.getMainGroup().toLowerCase().contains(text.toLowerCase()) ) {
//                                // Adding Matched items
//                                filterDatasetList.add(item);
//                            }
					}
				}

				// Set on UI Thread
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// Notify the List that the DataSet has changed...
						notifyDataSetChanged();
					}
				});

			}
		}).start();

	}
	//TODO RecyclerViewHolder
	public class RecyclerViewHolder extends RecyclerView.ViewHolder {

		CardView cardView;
		TableLayout tableLayout,tableLayout2;
		public RecyclerViewHolder(View itemView) {
			super(itemView);

			cardView = (CardView) itemView.findViewById(R.id.card_view);
			tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
			tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
		}
	}
	//TODO: Display TableLayout
	private void setTableLayout(Map<String,String> map,int position, TableLayout tableLayout, TableLayout tableLayout2) {
		//TODO: TableLayout set
		tableLayout.removeAllViewsInLayout();
		tableLayout.removeAllViews();
		//TODO: Item Code
		View v = inflater.inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Item Code", ""+map.get("ItemCode")));
		//TODO: Item Name
		v = inflater.inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Item Name", ""+map.get("ItemName")));
	}
}
