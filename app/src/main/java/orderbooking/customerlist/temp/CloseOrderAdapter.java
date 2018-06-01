package orderbooking.customerlist.temp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerChangeParty;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.customerlist.ChangePartySelectCustomerForOrderActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.partyinfo.PartyInfoActivity;
import orderbooking.view_order_details.OrderViewOrPushActivity;

public class CloseOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static class HeaderViewHolder extends RecyclerView.ViewHolder {

		TextView txt_header;

		HeaderViewHolder(View itemView) {
            super(itemView);
			txt_header = (TextView) itemView.findViewById(R.id.tv_prefix);
		}

	}
	private static class EventViewHolder extends RecyclerView.ViewHolder {

		CardView cardView;
		TableLayout tableLayout,tableLayout2,tableLayout3;
		CheckBox checkBox;
		ImageView imageViewInfo;
		WebView webView;
		EventViewHolder(View itemView) {
			super(itemView);
			cardView = (CardView) itemView.findViewById(R.id.card_view);
			tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
			tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
			tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
			imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
			checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_multi_customer);
			webView = (WebView) itemView.findViewById(R.id.web_view);
		}

	}
	@NonNull
	private List<ListItem> items = Collections.emptyList();
	@NonNull
	private List<ListItem> filterItems = Collections.emptyList();
	private Context context;
	public CloseOrderAdapter(@NonNull List<ListItem> items, Context context) {
		this.items = items;
		this.context = context;
		this.filterItems = new ArrayList<ListItem>();
		this.filterItems.addAll(this.items);
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case ListItem.TYPE_HEADER: {
				View itemView = LayoutInflater.from(context).inflate(R.layout.header, parent, false);
				return new HeaderViewHolder(itemView);
			}
			case ListItem.TYPE_EVENT: {
				View itemView = LayoutInflater.from(context).inflate(R.layout.cardview_all_customer_list, parent, false);
				return new EventViewHolder(itemView);
			}
			default:
				throw new IllegalStateException("unsupported item type");
		}
	}
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
		int viewType = getItemViewType(position);
		switch (viewType) {
			case ListItem.TYPE_HEADER: {
				HeaderItem header = (HeaderItem) filterItems.get(position);
				HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
				// your logic here
				holder.txt_header.setText(""+header.getGodown());
				break;
			}
			case ListItem.TYPE_EVENT: {
				final EventItem event = (EventItem) filterItems.get(position);
				final EventViewHolder holder = (EventViewHolder) viewHolder;
				// your logic here
				setTableLayout(event.getEvent(),holder.tableLayout,holder.tableLayout2,holder.tableLayout3,holder.webView);
				holder.imageViewInfo.setTag(event.getEvent());
				holder.imageViewInfo.setVisibility(View.VISIBLE);
				holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CloseOrBookDataset Dataset = (CloseOrBookDataset) v.getTag();
						if (Dataset!=null) {
							//DialogPartyInfoCheck(context,event.getEvent());
							Intent intent = new Intent(context, PartyInfoActivity.class);
							intent.putExtra("PartyID", Dataset.getPartyID());
							intent.putExtra("SubPartyID", Dataset.getSubPartyID());
							context.startActivity(intent);
						}
					}
				});
				//TODO: Set Days Compare
				String LastBookedTime = event.getEvent().getLastBookDate();
				int flag = DaysCompare(LastBookedTime);
				if(flag == 1){
					holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
				}else if(flag == 2){
					holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_10));
				}else if(flag == 3){
					holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_25));
				}else if(flag == 4){
					holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_50));
				}else if(flag == 5) {
					holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_75));
				}
				holder.cardView.setTag(event.getEvent());
				holder.cardView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						CloseOrBookDataset Dataset = (CloseOrBookDataset) view.getTag();
						//TODO: Close Order
						BookOrderAdapter.listMultiCustomer = new ArrayList<CloseOrBookDataset>();
						if (Dataset!=null) {
							StaticValues.CatalogueFlag = 0;
							BookOrderAdapter.listMultiCustomer.add(Dataset);
							StaticValues.MultiOrderSize = BookOrderAdapter.listMultiCustomer.size();
							Intent intent = new Intent(context, OrderViewOrPushActivity.class);
							context.startActivity(intent);
						}else {
							MessageDialog.MessageDialog(context, "Error", "Something went wrong!!!");
						}
					}
				});
				break;
			}
			default:
				throw new IllegalStateException("unsupported item type");
		}
	}
	@Override
	public int getItemCount() {
		return (null != filterItems ? filterItems.size() : 0);
	}
	@Override
	public int getItemViewType(int position) {
		return filterItems.get(position).getType();
	}
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		int viewType = getItemViewType(position);
		switch (viewType) {
			case ListItem.TYPE_HEADER:
				HeaderItem header = (HeaderItem) filterItems.get(position);
				return header;
			case ListItem.TYPE_EVENT:
				final EventItem event = (EventItem) filterItems.get(position);
				return event;
		}
		return filterItems.get(position);
	}
	//TODO: Filter Search
	public void filter(final String text) {

		// Searching could be complex..so we will dispatch it to a different thread...
		new Thread(new Runnable() {
			@Override
			public void run() {

				// Clear the filter list
				filterItems.clear();

				// If there is no search value, then add all original list items to filter list
				if (TextUtils.isEmpty(text)) {

					filterItems.addAll(items);

				} else {
					System.out.println("Search:"+text);
					// Iterate in the original List and add it to filter list...
					for (ListItem item : items) {
						if(item.getType()==0){
							HeaderItem headerItem = (HeaderItem)item;
							if (headerItem.getGodown().toLowerCase().contains(text.toLowerCase())){
								filterItems.add(item);
								//System.out.println("item Header:"+headerItem.getGodown());
							}
						}else if(item.getType()==1){
							EventItem eventItem = (EventItem)item;
							if (eventItem.getEvent().getPartyName().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getAgentName().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getCity().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getState().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getFairName().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getMobile().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getSubParty().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getRefName().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getGodown().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getEmpCVName().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getEmpCVType().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getUserName().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getLastBookDate().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getOrderNo().toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getOrderDate().toLowerCase().contains(text.toLowerCase())||
									String.valueOf(eventItem.getEvent().getTBookQty()).toLowerCase().contains(text.toLowerCase())||
									eventItem.getEvent().getEmail().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getAccountNo().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getAccountHolderName().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getIFSCCOde().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getIDName().toLowerCase().contains(text.toLowerCase()) ||
									eventItem.getEvent().getGSTIN().toLowerCase().contains(text.toLowerCase())) {
								// Adding Matched items
								filterItems.add(item);
								//System.out.println("item event:"+eventItem.getEvent().getPartyName()+" "+eventItem.getEvent().getGodown());
							}
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
	private void setTableLayout(CloseOrBookDataset dataset, TableLayout tableLayout, TableLayout tableLayout2, TableLayout tableLayout3, final WebView webView){

		//TODO: TableLayout set
		tableLayout.removeAllViewsInLayout();
		tableLayout.removeAllViews();

		String SubPartyHeader = (dataset.getSubParty().isEmpty() || dataset.getSubParty().equals("null") ?"": "Sub Party Name:");
		String SubPartyResult = (dataset.getSubParty().isEmpty() || dataset.getSubParty().equals("null") ?"": dataset.getSubParty());
		String FairHeader = (dataset.getFairName().isEmpty() || dataset.getFairName().equals("null") ?"": "Fair Name:");
		String FairResult = (dataset.getFairName().isEmpty() || dataset.getFairName().equals("null") ?"": dataset.getFairName());

		//TODO: Party Name
		View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Party Name",dataset.getPartyName()));
		//TODO: Mobile Number
		String Mobile = (dataset.getMobile()==null || dataset.getMobile().equals("null") ?"":dataset.getMobile());
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mobile",""+Mobile));

		//TODO: Call and WhatsApp
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		TextView txtHeader1= (TextView) v.findViewById(R.id.header);
		txtHeader1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
		txtHeader1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_call_grey, 0, 0, 0);
		//txtHeader1.setBackgroundColor(context.getResources().getColor(R.color.red_10));
		txtHeader1.setGravity(Gravity.CENTER_VERTICAL);
		txtHeader1.setText("Click to Call");
		txtHeader1.setTag(Mobile);

		TextView txt1= (TextView) v.findViewById(R.id.content);
		txt1.setTag(Mobile);
		txt1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
		txt1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_whats_app, 0, 0, 0);
		txt1.setGravity(Gravity.CENTER_VERTICAL);
		//txt1.setBackgroundColor(context.getResources().getColor(R.color.TransparentGreen));
		txt1.setText("Click to Whats App");
		tableLayout.addView(v);
		txtHeader1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String Mobile = v.getTag().toString();
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Mobile));
				context.startActivity(intent);
			}
		});
		txt1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String Mobile = v.getTag().toString();
				String Message = "";
				webView.setWebViewClient(new MyWebViewClient(context));
				webView.getSettings().setJavaScriptEnabled(true);
				webView.loadUrl("https://api.whatsapp.com/send?phone=91"+Mobile+"&text="+Message);
			}
		});
		//TODO: City and State
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"City / State",dataset.getCity()+" / "+dataset.getState()));

		if (!SubPartyHeader.isEmpty()){
			//TODO: Sub Party
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
			tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,SubPartyHeader,SubPartyResult));
		}else if (!dataset.getRefName().isEmpty()){
			//TODO: Reference Name
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
			tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Reference Name",dataset.getRefName()));
			//TODO: Under Name
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
			tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Under Name",dataset.getUnderName()));
		}
		if (!FairHeader.isEmpty()) {
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
			tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Fair Name",dataset.getFairName()));
		}
		//TODO: Agent Name
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Agent Name",dataset.getAgentName()));
		//TODO: Label
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Label",dataset.getLabel()));
		//TODO: Order No
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Order No",dataset.getOrderNo()));
		//TODO: Order Date
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Order Date",dataset.getOrderDate()));
		//TODO: Party Entered
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Party Entered",DateFormatsMethods.DaysHoursMinutesCount(dataset.getEntryDate())));
		//TODO: Last Booked
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Last Booked",DateFormatsMethods.DaysHoursMinutesCount(dataset.getLastBookDate())));
		//TODO: Created By, Name,  type and Username
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Created By \n(Name - Type\n -Username)",dataset.getEmpCVName()+" - "+dataset.getEmpCVType()+" - "+dataset.getUserName()));
		//TODO: Email
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Email",dataset.getEmail()));
		//TODO: ID Name
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"ID Name",dataset.getIDName()));
		//TODO: GSTIN
		v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
		tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"GSTIN",dataset.getGSTIN()));
		//TODO: TableLayout2 set
		tableLayout2.removeAllViewsInLayout();
		tableLayout2.removeAllViews();
		//TODO: 1th Row
		View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayout, false);

		TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
		txtContent1.setText("Total Style: "+dataset.getItemCount());

		TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
		txtContent2.setText("Total Qty: "+dataset.getTBookQty());

		TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
		txtContent3.setText("Total Amt: ₹"+dataset.getTotalAmount());
		tableLayout2.addView(vt1);
		//TODO: Conditions
		//TODO: TableLayout3 set
		tableLayout3.removeAllViewsInLayout();
		tableLayout3.removeAllViews();
		int TotalOverDueAmt = dataset.getTotalOverDueAmt();
		int ExceedAmt = Integer.valueOf(dataset.getExceedAmt());
		if (TotalOverDueAmt > 0 && ExceedAmt <=0){
			//TODO: Over Due Amount
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Over Due Amt","₹"+TotalOverDueAmt+ (dataset.getAvgOverDueDays()==null ? "" : " ("+dataset.getAvgOverDueDays()+" Days)")));
			//TODO: Total Due Amount
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Total Due Amt"," ₹"+dataset.getTotalDueAmt()+ (dataset.getAvgDueDays()==null ? "" : " ("+dataset.getAvgDueDays()+" Days)")));
		}else if(ExceedAmt > 0 && TotalOverDueAmt <= 0){
			//TODO: Credit limit Exceed By
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Credit limit Exceed By"," ₹"+dataset.getExceedAmt()));
			//TODO: Credit Days
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Credit Days"," ₹"+dataset.getCreditDays()));
		}else if (TotalOverDueAmt > 0 && ExceedAmt > 0){
			//TODO: Over Due Amount
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Over Due Amt","₹"+TotalOverDueAmt+ (dataset.getAvgOverDueDays()==null ? "" : " ("+dataset.getAvgOverDueDays()+" Days)")));
			//TODO: Total Due Amount
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Total Due Amt"," ₹"+dataset.getTotalDueAmt()+ (dataset.getAvgDueDays()==null ? "" : " ("+dataset.getAvgDueDays()+" Days)")));
			//TODO: Credit limit Exceed By
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Credit limit Exceed By"," ₹"+dataset.getExceedAmt()));
		}
		int CreditLimitExceed = (dataset.getTotalAmount()+dataset.getTotalDueAmt())-Integer.valueOf(dataset.getCreditLimit());
		if (CreditLimitExceed>0 && dataset.getTotalAmount()>0){
			//TODO: 10th Row
			v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout3, false);
			v.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
			tableLayout3.addView(CustomTextView.setTableRow2Columns(context,v,"Alert","Credit limit will exceed by ₹"+CreditLimitExceed+" amount"));
		}
	}
	private int DaysCompare(String EntryDateTime){
		int flag=0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = sdf.parse(EntryDateTime);
			Date date2 = new Date();
			String CurrentDate =  sdf.format(date2).substring(0,10);

			long diff = date2.getTime() - date1.getTime();
			//int diffInDays = (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
			long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

			//System.out.println("Hour:"+diffInHours);
			if(diffInHours < 1){
				flag=1;
				//Toast.makeText(context, "Hour: " + diffInHours, Toast.LENGTH_LONG).show();
			}else if(diffInHours >= 1 && diffInHours < 2) {
				flag=2;
				//Toast.makeText(context, "Hour: " + diffInHours, Toast.LENGTH_LONG).show();
			}else if(diffInHours >= 2 && diffInHours < 4){
				flag=3;
				//Toast.makeText(context, "Hour: " + diffInHours, Toast.LENGTH_LONG).show();
			}else if(diffInHours >= 4 && diffInHours < 6){
				flag=4;
				//Toast.makeText(context, "Hour: " + diffInHours, Toast.LENGTH_LONG).show();
			}else if(!EntryDateTime.substring(0, 10).equals(CurrentDate) || diffInHours>=6){
				flag=5;
				//Toast.makeText(context, "Hour: " + diffInHours, Toast.LENGTH_LONG).show();
			}
		}catch (Exception e) {
			// TODO: handle exception
			MessageDialog.MessageDialog(context,"Error", "Exception:"+e.toString());
		}
		return flag;
	}
}