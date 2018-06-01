package report.revertforceclose.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import java.util.ArrayList;
import java.util.List;
import report.DatabaseSqlite.DatabaseSqlLiteHandlerRevertForceClose;
import report.revertforceclose.model.RevertForceCloseDataset;
/**
 * Created by Rakesh on 16-Aug-17.
 */
public class RevertForceCloseAdapter extends RecyclerView.Adapter<RevertForceCloseAdapter.RecyclerViewHolder>{

	private Context context;
	private ProgressDialog progressDialog;
	private LayoutInflater inflater;
	private List<RevertForceCloseDataset> datasetList,filterDatasetList;
	private DatabaseSqlLiteHandlerRevertForceClose DBHandler;
	public RevertForceCloseAdapter(Context context, List<RevertForceCloseDataset> datasetList) {
		this.context=context;
		this.datasetList=datasetList;
		this.filterDatasetList = new ArrayList<RevertForceCloseDataset>();
		this.filterDatasetList.addAll(this.datasetList);
		this.inflater = LayoutInflater.from(context);
		this.DBHandler = new DatabaseSqlLiteHandlerRevertForceClose(this.context);
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

		final RevertForceCloseDataset dataset = filterDatasetList.get(position);
		if (dataset.getFlagType() == 0){
			//TODO: Item Wise
			setTableLayout(dataset,holder.tableLayout,dataset.getFlagType());
		}else if (dataset.getFlagType() == 1){
			//TODO: Item With Order Wise
			setTableLayout(dataset,holder.tableLayout,dataset.getFlagType());
		}else if (dataset.getFlagType() == 2){
			//TODO: Item With Party Wise
			setTableLayout(dataset,holder.tableLayout,dataset.getFlagType());
		}else if (dataset.getFlagType() == 3){
			//TODO: Party Wise
			setTableLayout(dataset,holder.tableLayout,dataset.getFlagType());
		}else if (dataset.getFlagType() == 4){
			//TODO: Order Wise
			setTableLayout(dataset,holder.tableLayout,dataset.getFlagType());
		}
		//TODO: call TableLayout2 method
		setTableLayoutTotalQtyAndAmt(dataset,holder.tableLayout2);
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
					for (RevertForceCloseDataset item : datasetList) {
						if(item.getPartyName()!=null && item.getOrderNo()!=null && item.getOrderDate()!=null && item.getItemName()!=null && item.getItemCode()!=null && item.getEntryDateTime()!=null)
							if (item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
									item.getOrderNo().toLowerCase().contains(text.toLowerCase()) ||
									item.getOrderDate().toLowerCase().contains(text.toLowerCase()) ||
									item.getItemName().toLowerCase().contains(text.toLowerCase()) ||
									item.getItemCode().toLowerCase().contains(text.toLowerCase()) ||
									item.getEntryDateTime().toLowerCase().contains(text.toLowerCase()) ) {
								// Adding Matched items
								filterDatasetList.add(item);
							}
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
		ImageView imageViewInfo;
		public RecyclerViewHolder(View itemView) {
			super(itemView);

			cardView = (CardView) itemView.findViewById(R.id.card_view);
			tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
			tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
			imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
		}
	}
	//TODO: Display TableLayout
	private void setTableLayout(RevertForceCloseDataset dataset, TableLayout tableLayout,int FlagType){

		//TODO: TableLayout set
		tableLayout.removeAllViewsInLayout();
		tableLayout.removeAllViews();

		if (dataset.getFlagType() == 0){
			//TODO: Item Wise
			//TODO: 1st Row
			View v = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader= (TextView) v.findViewById(R.id.header);
			txtHeader.setText("Item Code:");

			TextView txt= (TextView) v.findViewById(R.id.content);
			txt.setText(""+(dataset.getItemCode()==null || dataset.getItemCode().equals("null") ?"":dataset.getItemCode()));
			tableLayout.addView(v);
			//TODO: 2nd Row
			View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
			txtHeader1.setText("Item Name:");

			TextView txt1= (TextView) v1.findViewById(R.id.content);
			txt1.setText(""+(dataset.getItemName()==null || dataset.getItemName().equals("null") ?"":dataset.getItemName()));
			tableLayout.addView(v1);
		}else if (dataset.getFlagType() == 1){
			//TODO: Item With Order Wise
			//TODO: 1st Row
			View v = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader= (TextView) v.findViewById(R.id.header);
			txtHeader.setText("Item Code:");

			TextView txt= (TextView) v.findViewById(R.id.content);
			txt.setText(""+(dataset.getItemCode()==null || dataset.getItemCode().equals("null") ?"":dataset.getItemCode()));
			tableLayout.addView(v);
			//TODO: 2nd Row
			View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
			txtHeader1.setText("Order No:");

			TextView txt1= (TextView) v1.findViewById(R.id.content);
			txt1.setText(""+(dataset.getOrderNo()==null || dataset.getOrderNo().equals("null") ?"":dataset.getOrderNo()));
			tableLayout.addView(v1);
		}else if (dataset.getFlagType() == 2){
			//TODO: Item With Party Wise
			//TODO: 1st Row
			View v = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader= (TextView) v.findViewById(R.id.header);
			txtHeader.setText("Item Code:");

			TextView txt= (TextView) v.findViewById(R.id.content);
			txt.setText(""+(dataset.getItemCode()==null || dataset.getItemCode().equals("null") ?"":dataset.getItemCode()));
			tableLayout.addView(v);
			//TODO: 2nd Row
			View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
			txtHeader1.setText("Party Name:");

			TextView txt1= (TextView) v1.findViewById(R.id.content);
			txt1.setText(""+(dataset.getPartyName()==null || dataset.getPartyName().equals("null") ?"":dataset.getPartyName()));
			tableLayout.addView(v1);
		}else if (dataset.getFlagType() == 3){
			//TODO: Party Wise
			//TODO: 1nd Row
			View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
			txtHeader1.setText("Party Name:");

			TextView txt1= (TextView) v1.findViewById(R.id.content);
			txt1.setText(""+(dataset.getPartyName()==null || dataset.getPartyName().equals("null") ?"":dataset.getPartyName()));
			tableLayout.addView(v1);
		}else if (dataset.getFlagType() == 4){
			//TODO: Order Wise
			//TODO: 1nd Row
			View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
			TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
			txtHeader1.setText("Order No:");

			TextView txt1= (TextView) v1.findViewById(R.id.content);
			txt1.setText(""+(dataset.getOrderNo()==null || dataset.getOrderNo().equals("null") ?"":dataset.getOrderNo()));
			tableLayout.addView(v1);
		}

		//TODO: 3rd Row
		View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
		TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
		txtHeader2.setText("Date:");

		TextView txt2= (TextView) v2.findViewById(R.id.content);
		txt2.setText(""+(dataset.getEntryDateTime()==null || dataset.getEntryDateTime().equals("null") ?"":DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getEntryDateTime().substring(0,10))));
		tableLayout.addView(v2);
	}
	//TODO: Display TableLayout
	private void setTableLayoutTotalQtyAndAmt(RevertForceCloseDataset dataset, TableLayout tableLayout){

		//TODO: TableLayout set
		tableLayout.removeAllViewsInLayout();
		tableLayout.removeAllViews();
		//TODO: 1st Row
		View v = inflater.inflate(R.layout.table_row_2_column, tableLayout, false);
		TextView txt0= (TextView) v.findViewById(R.id.content_column_1);
		txt0.setText("Total Booked Qty : "+dataset.getTotalQty());

		TextView txt= (TextView) v.findViewById(R.id.content_column_2);
		txt.setText("Total Booked Amt : ₹"+dataset.getTotalAmt());
		tableLayout.addView(v);
	}
}
