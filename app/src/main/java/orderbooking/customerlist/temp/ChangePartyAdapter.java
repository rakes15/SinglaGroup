package orderbooking.customerlist.temp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerChangeParty;
import orderbooking.StaticValues;
import orderbooking.customerlist.ChangePartySelectCustomerForOrderActivity;
import orderbooking.customerlist.adapter.RunningFairAdapter;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.partyinfo.PartyInfoActivity;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import services.NetworkUtils;

public class ChangePartyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
	private ProgressDialog progressDialog;
	private String GodownID="",Godown="",FairID="";
	private Spinner spnRunningFair;
	private LinearLayout linearLayoutRunningFair;
	private DatabaseSqlLiteHandlerUserInfo DBHandler;
	private static String TAG = ChangePartyAdapter.class.getSimpleName();
	public ChangePartyAdapter(@NonNull List<ListItem> items, Context context) {
		this.items = items;
		this.context = context;
		this.filterItems = new ArrayList<ListItem>();
		this.filterItems.addAll(this.items);
		this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this.context);
		this.progressDialog = new ProgressDialog(this.context);
		this.progressDialog.setMessage("Please wait...");
		this.progressDialog.setCanceledOnTouchOutside(false);
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
//				holder.cardView.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						CloseOrBookDataset Dataset = (CloseOrBookDataset) view.getTag();
//						DialogChangePartyOrShowroomOption(Dataset);
//					}
//				});
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
		double TotalOverDueAmt = dataset.getTotalOverDueAmt();
		double ExceedAmt = dataset.getExceedAmt();
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
		double CreditLimitExceed = (dataset.getTotalAmount()+dataset.getTotalDueAmt())-Integer.valueOf(dataset.getCreditLimit());
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
	private void DialogChangePartyOrShowroomOption(final CloseOrBookDataset Dataset){
		final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
		dialog.setContentView(R.layout.dialog_change_password);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.show();
		final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
		final TextView txtViewMsg = (TextView) dialog.findViewById(R.id.text_Msg);
		RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_NewForgot);
		TextInputLayout OldPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_OldPassword);
		TextInputLayout NewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_NewPassword);
		TextInputLayout ConfirmPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_confirmPassword);
		Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
		RadioButton radioButtonChangeParty = (RadioButton) dialog.findViewById(R.id.RadioButton_NewUser);
		radioButtonChangeParty.setText("Party");
		RadioButton radioButtonChangeShowroom = (RadioButton) dialog.findViewById(R.id.RadioButton_ForgotPassword);
		radioButtonChangeShowroom.setText("Showroom");
		CursorColor.CursorColor(OldPasswordWrapper.getEditText());
		CursorColor.CursorColor(NewPasswordWrapper.getEditText());
		CursorColor.CursorColor(ConfirmPasswordWrapper.getEditText());
		txtViewTitle.setText("Change Party Or Showroom");
		radioGroupChangeOrShowroom.setVisibility(View.VISIBLE);
		OldPasswordWrapper.setVisibility(View.GONE);
		NewPasswordWrapper.setVisibility(View.GONE);
		ConfirmPasswordWrapper.setVisibility(View.GONE);
		btnSubmit.setVisibility(View.GONE);
		radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_NewUser){
					dialog.dismiss();
					//TODO: Change Party
					DatabaseSqlLiteHandlerChangeParty DBChangeParty = new DatabaseSqlLiteHandlerChangeParty(context);
					DBChangeParty.deleteChangePartyOLD();
					DBChangeParty.insertChangePartyOldData(Dataset.getPartyID(),Dataset.getSubPartyID(),Dataset.getRefName());
					Intent intent = new Intent(context, ChangePartySelectCustomerForOrderActivity.class);
					intent.putExtra("Key",Dataset);
					context.startActivity(intent);
				}else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_ForgotPassword){
					dialog.dismiss();
					//TODO: Change Showroom
					MsgDialogFunction(context, Dataset);
				}
			}
		});

	}
	private void MsgDialogFunction(final Context context, final CloseOrBookDataset dataset){
		final Dialog dialog = new Dialog(context);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_fair_normal);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		dialog.setTitle("Are you sure to Approved ? ");
		linearLayoutRunningFair=(LinearLayout)dialog.findViewById(R.id.Linear_RunningFair);
		spnRunningFair=(Spinner)dialog.findViewById(R.id.spinner_running_fair);
		Spinner spnGodown=(Spinner)dialog.findViewById(R.id.spinner_Godown);
		LoginActivity obj=new LoginActivity();
		final String[] str=obj.GetSharePreferenceSession(context);
		if (str!=null) {
			final List<GodownDataset> godownDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
			GodownFilterableAdapter adapter = new GodownFilterableAdapter(context, godownDatasetList, godownDatasetList);
			spnGodown.setAdapter(adapter);
			spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if (position>0) {
						Godown = godownDatasetList.get(position).getGodownName();
						if (Godown.equals("Fair")) {
							GodownID = godownDatasetList.get(position).getGodownID();
							Godown = godownDatasetList.get(position).getGodownName();
							linearLayoutRunningFair.setVisibility(View.VISIBLE);
							CallVolleyRunningFair(str[3], str[4], str[0], str[14], str[5], str[15]);
						} else {
							GodownID = godownDatasetList.get(position).getGodownID();
							Godown = godownDatasetList.get(position).getGodownName();
							linearLayoutRunningFair.setVisibility(View.GONE);
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			spnRunningFair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if (position>0) {
						Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
						FairID = map.get("ID");
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		}
		Button ok=(Button)dialog.findViewById(R.id.btn_ok);
		Button cancel=(Button)dialog.findViewById(R.id.btn_cancel);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = NetworkUtils.getConnectivityStatusString(context);
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj = new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str != null && !GodownID.isEmpty() && !Godown.isEmpty()) {
						if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
							if (Godown.equals("Fair")) {
								if (FairID.length()>0 && !FairID.isEmpty()) {
									CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], dataset.getOrderID(), GodownID, Godown,FairID,"1");
									dialog.dismiss();
								}else{
									MessageDialog.MessageDialog(context,"No fair available","Please select another showroom");
								}
							}else{
								CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], dataset.getOrderID(), GodownID, Godown,FairID,"1");
								dialog.dismiss();
							}
						}else {
							MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the party");
						}
					}
				}else {
					MessageDialog.MessageDialog(context,"",""+status);
				}

			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void AlertDialogMethod(final String OrderID,final String GodownID,final String Godown,final String FairID){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setMessage("Are you sure You want to change this Party");
		alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String status = NetworkUtils.getConnectivityStatusString(context);
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj = new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str != null)
						CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], OrderID, GodownID, Godown,FairID,"1");
				}else{
					MessageDialog.MessageDialog(context,"",""+status);
				}
			}
		});
		alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void CallVolleyRunningFair(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID){
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"RunningFair", new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response) {
				// response
				Log.d("Response", response);
				try{
					JSONObject jsonObject = new JSONObject(response);
					int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
					String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
					if (Status == 1) {
						JSONArray jsonArrayRunningFair = jsonObject.getJSONArray("Result");
						int c=0;
						List<Map<String,String>> list = new ArrayList<>();
						Map<String,String> map = new HashMap<>();
						map.put("ID","");
						map.put("Name","Select running fair");
						list.add(c,map);
						for (int i=0; i< jsonArrayRunningFair.length(); i++){
							c++;
							map = new HashMap<>();
							map.put("ID",jsonArrayRunningFair.getJSONObject(i).getString("ID"));
							map.put("Name",jsonArrayRunningFair.getJSONObject(i).getString("Name"));
							list.add(c,map);
						}
						spnRunningFair.setAdapter(new RunningFairAdapter(context,list,list));
					} else {
						MessageDialog.MessageDialog(context,"",Msg);
						linearLayoutRunningFair.setVisibility(View.GONE);
					}
				}catch (Exception e){
					MessageDialog.MessageDialog(context,"Exception",""+e.toString());
				}
				hidepDialog();
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error) {
				// error
				Log.d("Error.Response", ""+error);
				MessageDialog.MessageDialog(context,"Error",""+error.toString());
				hidepDialog();
			}
		} ) {
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String>  params = new HashMap<String, String>();
				params.put("DeviceID", DeviceID);
				params.put("UserID", UserID);
				params.put("SessionID", SessionID);
				params.put("DivisionID", DivisionID);
				params.put("CompanyID", CompanyID);
				params.put("BranchID", BranchID);
				Log.d(TAG,"Running fair parameters:"+params.toString());
				return params;
			}
		};
		postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppController.getInstance().addToRequestQueue(postRequest);
	}
	private void CallVolleyChangeShowroom(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String OrderID,final String GodownID,final String GodownName,final String FairID,final String InfoTypeFlag){
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderInfoUpdate", new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response) {
				// response
				Log.d("Response", response);
				try{
					JSONObject jsonObject = new JSONObject(response);
					int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
					String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
					if (Status == 1) {
						MessageDialog.MessageDialog(context,"",""+Msg);
					} else {
						MessageDialog.MessageDialog(context,"",Msg);
					}
				}catch (Exception e){
					MessageDialog.MessageDialog(context,"Exception",""+e.toString());
				}
				hidepDialog();
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error) {
				// error
				Log.d("Error.Response", ""+error);
				MessageDialog.MessageDialog(context,"Error",""+error.toString());
				hidepDialog();
			}
		} ) {
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String>  params = new HashMap<String, String>();
				params.put("DeviceID", DeviceID);
				params.put("UserID", UserID);
				params.put("SessionID", SessionID);
				params.put("DivisionID", DivisionID);
				params.put("CompanyID", CompanyID);
				params.put("BranchID", BranchID);
				params.put("OrderID", OrderID);
				params.put("GodownID", GodownID);
				params.put("GodownName", GodownName);
				params.put("FairID", FairID);
				params.put("InfoTypeFlag",InfoTypeFlag);
				Log.d(TAG,"Change Showroom parameters:"+params.toString());
				return params;
			}
		};
		postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppController.getInstance().addToRequestQueue(postRequest);
	}
	private void showpDialog() {
		if(progressDialog!=null) {
			progressDialog.show();
		}
	}
	private void hidepDialog() {
		if(progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
}