package report.revertforceclose;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;
import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import report.DatabaseSqlite.DatabaseSqlLiteHandlerRevertForceClose;
import report.revertforceclose.adapter.RevertForceCloseAdapter;
import report.revertforceclose.model.RevertFlagTypeWithNameDataset;
import report.revertforceclose.model.RevertForceCloseDataset;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

public class RevertForceCloseViewPagerActivity extends AppCompatActivity{
	private ActionBar actionBar;
	private TabLayout tabLayout;
	private ViewPager mViewPager;
	private Context context;
	private ProgressDialog progressDialog;
	private Bundle bundle;
	private int Type=0;
	DatabaseSqlLiteHandlerRevertForceClose DBHandler;
	private static String TAG = RevertForceCloseViewPagerActivity.class.getSimpleName();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
		setContentView(R.layout.viewpager_design);
		Initialization();
		ModulePermission();
	}
	private void Initialization(){
		this.context = RevertForceCloseViewPagerActivity.this;
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCanceledOnTouchOutside(false);
		DBHandler = new DatabaseSqlLiteHandlerRevertForceClose(context);
	}
	private void ModulePermission(){
		try {
			bundle = getIntent().getBundleExtra("PermissionBundle");
			String Title = bundle.getString("Title");
			StaticValues.viewFlag = bundle.getInt("ViewFlag");
			StaticValues.editFlag = bundle.getInt("EditFlag");
			StaticValues.createFlag = bundle.getInt("CreateFlag");
			StaticValues.removeFlag = bundle.getInt("RemoveFlag");
			StaticValues.printFlag = bundle.getInt("PrintFlag");
			StaticValues.importFlag = bundle.getInt("ImportFlag");
			StaticValues.exportFlag = bundle.getInt("ExportFlag");
			StaticValues.Vtype = bundle.getInt("Vtype");
			actionBar.setTitle(Title);
			if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
				if (StaticValues.Vtype == 24){
					//TODO: Sales Order
					Type = 0;
				}else if (StaticValues.Vtype == 37){
					//TODO: Purchase Order
					Type = 1;
				}else if (StaticValues.Vtype == 25){
					//TODO: Producation Order
					Type = 2;
				}
				DialogSearchFilter(context);
			}else {
				MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
			}
		}catch (Exception e){
			MessageDialog.MessageDialog(context,"Exception",e.toString());
		}
	}
	//TODO: Call Api Method
	private void CallApiMethod(final String FromDate, final String ToDate,final String Type){
		//TODO: Book Groups Request
		NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
			@Override
			public void networkReceived(String status) {
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj=new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str!=null && !FromDate.isEmpty() && !ToDate.isEmpty() && !Type.isEmpty()) {
						CallVolleyAllRevertForceClosedList(str[3], str[4], str[0], str[5],str[14],str[15],FromDate,ToDate,Type);
					}
				} else {
					MessageDialog.MessageDialog(context,"",status);
				}
			}
		});
		String status = NetworkUtils.getConnectivityStatusString(context);
		if (!status.contentEquals("No Internet Connection")) {
			LoginActivity obj=new LoginActivity();
			String[] str = obj.GetSharePreferenceSession(context);
			if (str!=null && !FromDate.isEmpty() && !ToDate.isEmpty() && !Type.isEmpty()) {
				CallVolleyAllRevertForceClosedList(str[3], str[4], str[0], str[5],str[14],str[15],FromDate,ToDate,Type);
			}
		} else {
			MessageDialog.MessageDialog(context,"",status);
		}
	}
	private void LoadViewPager(){
		List<RevertFlagTypeWithNameDataset> dataList=new ArrayList<>();
		RevertFlagTypeWithNameDataset dataset = new RevertFlagTypeWithNameDataset("Item Wise",0);
		dataList.add(dataset);
		dataset = new RevertFlagTypeWithNameDataset("Item in Order Wise",1);
		dataList.add(dataset);
		dataset = new RevertFlagTypeWithNameDataset("Item in Party Wise",2);
		dataList.add(dataset);
		dataset = new RevertFlagTypeWithNameDataset("Party Wise",3);
		dataList.add(dataset);
		dataset = new RevertFlagTypeWithNameDataset("Order Wise",4);
		dataList.add(dataset);

		ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),dataList);
		mViewPager.setAdapter(mPagerAdapter);
		tabLayout.setupWithViewPager(mViewPager);
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
			@Override
			public void onPageSelected(int position) {
				mViewPager.setCurrentItem(position,true);
			}
			@Override
			public void onPageScrollStateChanged(int state) { }
		});
	}
	private void DialogSearchFilter(final Context context){
		final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
		dialog.setContentView(R.layout.dialog_search_filter_by_date);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.TOP;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.show();
		//TODO: Declarations
		final EditText edtFromDate = (EditText) dialog.findViewById(R.id.EditText_FromDate);
		final EditText edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
		LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.Linear_Type);
		Spinner spnType = (Spinner) dialog.findViewById(R.id.spinner_Type);
		linearLayout.setVisibility(View.GONE);
		Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
		//TODO: Get Current Date
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String CurrentDate = df.format(c.getTime());
		edtFromDate.setInputType(InputType.TYPE_NULL);
		//TODO: Get Yesterday Date
		c.add(Calendar.DATE, -1);
		String YesterdayDate = df.format(c.getTime());
		edtFromDate.setText(YesterdayDate);
		edtToDate.setInputType(InputType.TYPE_NULL);
		edtToDate.setText(CurrentDate);
		edtFromDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
				// Create the DatePickerDialog instance
				DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						DecimalFormat formatter = new DecimalFormat("00");
						String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
						edtFromDate.setText(Date);
					}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				datePicker.setTitle("Select From Date");
				datePicker.show();
			}
		});
		edtToDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
				// Create the DatePickerDialog instance
				DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						DecimalFormat formatter = new DecimalFormat("00");
						String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
						edtToDate.setText(Date);
					}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				datePicker.setTitle("Select To Date");
				datePicker.show();
			}
		});
		//TODO: Order Type Spinner
		String[] arrayAppType = getResources().getStringArray(R.array.ForceCloseType);
		for (int i=0; i<arrayAppType.length; i++) {
			if (Type == i){
				spnType.setSelection(i);
				break;
			}
		}
		spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				Type = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		btnApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Filter Apply
				String FromDate = edtFromDate.getText().toString();
				String ToDate = edtToDate.getText().toString();
				if (!FromDate.isEmpty() && !ToDate.isEmpty()) {
					CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate)+StaticValues.FromTime,DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate)+StaticValues.ToTime,String.valueOf(Type));
					dialog.dismiss();
				}else{
					MessageDialog.MessageDialog(context,"","From Date and To Date cann't be blank!!!");
				}
			}
		});
	}
	public void CallVolleyAllRevertForceClosedList(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String FromDate,final String ToDate,final String Type){
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderForceClosedList", new Response.Listener<String>()
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
						JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
						List<Map<String,String>> mapList = new ArrayList<>();
						for (int i=0; i< jsonArrayScfo.length(); i++){
							Map<String,String> map = new HashMap<>();
							map.put("CustID",(jsonArrayScfo.getJSONObject(i).optString("PartyID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyID")));
							map.put("CustName",(jsonArrayScfo.getJSONObject(i).optString("PartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyName")));
							map.put("OrderID",(jsonArrayScfo.getJSONObject(i).optString("OrderID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderID")));
							map.put("OrderNo",(jsonArrayScfo.getJSONObject(i).optString("OrderNo")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderNo")));
							map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
							map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
							map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
							map.put("FlagType",(jsonArrayScfo.getJSONObject(i).optString("OperationMode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OperationMode")));
							map.put("FlagTypeName",(jsonArrayScfo.getJSONObject(i).optString("FlagHeader")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("FlagHeader")));
							map.put("TransactionID",(jsonArrayScfo.getJSONObject(i).optString("ID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ID")));
							map.put("EntryDateTime",(jsonArrayScfo.getJSONObject(i).optString("EntryTime")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("EntryTime")));
							map.put("UserID",(jsonArrayScfo.getJSONObject(i).optString("UserID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("UserID")));
							map.put("UserName",(jsonArrayScfo.getJSONObject(i).optString("UserName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("UserName")));
							map.put("OrderAmt",(jsonArrayScfo.getJSONObject(i).optString("OrderAmount")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderAmount")));
							map.put("ItemBookQty",(jsonArrayScfo.getJSONObject(i).optString("ItemBookQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemBookQty")));
							map.put("ItemBookAmt",(jsonArrayScfo.getJSONObject(i).optString("ItemBookAmt")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemBookAmt")));
							//TODO: Add map into list
							mapList.add(map);
						}
						context.deleteDatabase(DatabaseSqlLiteHandlerRevertForceClose.DATABASE_NAME);
						DBHandler.deleteRevertForceClose();
						DBHandler.insertRevertForceClose(mapList);
						//TODO: Load View Pager
						LoadViewPager();
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
				params.put("FromDt", FromDate);
				params.put("ToDt", ToDate);
				params.put("Type", Type);
				Log.d(TAG,"Revert Force Close List parameters:"+params.toString());
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_HOME){
			// Stop your service here
			System.out.println("This app is close");
			finishAffinity();
		}else if(keyCode==KeyEvent.KEYCODE_BACK){
			//TODO: Activity finish
			context.deleteDatabase(DatabaseSqlLiteHandlerRevertForceClose.DATABASE_NAME);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()){
			case android.R.id.home:
				//TODO: Activity finish
				context.deleteDatabase(DatabaseSqlLiteHandlerRevertForceClose.DATABASE_NAME);
				finish();
				break;
			case R.id.action_filter_search:
				DialogSearchFilter(context);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setVisible(false);
		MenuItem searchFilterItem = menu.findItem(R.id.action_filter_search);
		searchFilterItem.setVisible(true);
		return super.onCreateOptionsMenu(menu);
	}
	//TODO: View Pager Adapter
	public class ViewPagerAdapter extends FragmentStatePagerAdapter {

		List<RevertFlagTypeWithNameDataset> data=null;
		public ViewPagerAdapter(FragmentManager fragmentManager, List<RevertFlagTypeWithNameDataset> data) {
			super(fragmentManager);
			this.data=data;
		}
		@Override
		public Fragment getItem(int position) {
			return RevertForceClose.newInstance(data.get(position).getFlagType(),data.get(position).getFlagName());
		}
		public CharSequence getPageTitle(int position) {
			return data.get(position).getFlagName()+"("+data.get(position).getFlagType()+")";
		}
		@Override
		public int getCount() {
			return data.size();
		}
		@Override
	    public int getItemPosition(Object object) {
	        // refresh all fragments when data set changed
	        return POSITION_NONE;
	    }
	}
	//TODO: Fragment
	public static class RevertForceClose extends Fragment {
		private static final String FLAG_TYPE = "FlagType";
		private static final String FLAG_TYPE_NAME = "FlagTypeName";
		DatabaseSqlLiteHandlerRevertForceClose DBHandler;
		RevertForceCloseAdapter adapter;
		private RecyclerView recyclerView;
		private SwipeRefreshLayout swipeRefreshLayout;
		private ProgressDialog progressDialog;
		private Context context;
		int Flag=0;
		private SearchView searchView;
		private static String TAG = RevertForceClose.class.getSimpleName();
		public static RevertForceClose newInstance(int FlagType,String FlagTypeName) {
			RevertForceClose fragment = new RevertForceClose();
			Bundle args = new Bundle();
			args.putInt(FLAG_TYPE, FlagType);
			args.putString(FLAG_TYPE_NAME, FlagTypeName);
			fragment.setArguments(args);
			return fragment;
		}
		public RevertForceClose() { }
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			getActivity().supportInvalidateOptionsMenu();
			setHasOptionsMenu(true);
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
			View rootView = inflater.inflate(R.layout.activity_recyclerview_swipe_refresh_layout, null);
			Flag = getArguments().getInt(FLAG_TYPE);
			Initialization(rootView);
			LoadRecyclerView(Flag);
			SwipeRefresh();
			return rootView;	    
		}
		private void Initialization(View view){
			this.context = getActivity();
			swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
			recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCanceledOnTouchOutside(false);
			DBHandler=new DatabaseSqlLiteHandlerRevertForceClose(getActivity());
			//TODO: Module Permission
			//ModulePermission();
		}
		private void SwipeRefresh(){
			//TODO: Swipe Refresh Layout
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					LoadRecyclerView(Flag);
				}
			});
		}
		private void LoadRecyclerView(int FlagType){
			adapter = new RevertForceCloseAdapter(context,DBHandler.getItemDetailsByFlag(FlagType));
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setAdapter(adapter);
			recyclerView.setLayoutManager(linearLayoutManager);
			ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View v) {
					RevertForceCloseDataset dataset = (RevertForceCloseDataset) adapter.getItem(position);
					String TransactionID = dataset.getTransactionID();
					int FlagType = dataset.getFlagType();
					AlertDialogRevertForceClosed(TransactionID,FlagType);
				}
			});
			hidepDialog();
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
			  switch(item.getItemId()){
			  case R.id.action_search:
				   break;
			  case android.R.id.home:
				  getActivity().finish();
				  getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
				   break;
			  }
		   return super.onOptionsItemSelected(item);	      
		}
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 inflater.inflate(R.menu.menu_main, menu);
		 MenuItem filterSearchItem = menu.findItem(R.id.action_filter_search);
		 filterSearchItem.setVisible(true);
		 MenuItem searchItem = menu.findItem(R.id.action_search);
		 searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		 SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
		 searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		 searchView.setIconifiedByDefault(false);
		 searchView.setFocusable(true);
		 searchView.setFocusableInTouchMode(true);
		 searchView.requestFocus();
		 searchView.requestFocusFromTouch();
		 searchView.setQueryHint("Search...");
		 SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
		 {
			 @Override
			 public boolean onQueryTextChange(String newText)
			 {
				 // this is your adapter that will be filtered
				 if(adapter!=null) {
					 adapter.filter(newText);
				 }
//                System.out.println("on text chnge text: "+newText);
				 return true;
			 }
			 @Override
			 public boolean onQueryTextSubmit(String query)
			 {
				 // this is your adapter that will be filtered
				 if(adapter!=null)
				 {
					 adapter.filter(query);
				 }
//                System.out.println("on query submit: "+query);
				 return true;
			 }
		 };
		 searchView.setOnQueryTextListener(textChangeListener);
		 super.onCreateOptionsMenu(menu,inflater);
	}
		@Override
		public void onDestroyOptionsMenu() {
			super.onDestroyOptionsMenu();
			if (searchView != null && !searchView.getQuery().toString().isEmpty()) {
				searchView.setIconified(true);
				searchView.setIconified(true);
			}
		}
		private void AlertDialogRevertForceClosed(final String TransactionID, final int Type){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setMessage("Are you sure? You want to revert force closed.");
			alertDialogBuilder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			alertDialogBuilder.setNegativeButton("YES",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String status = NetworkUtils.getConnectivityStatusString(context);
					if (!status.contentEquals("No Internet Connection")) {
						LoginActivity obj=new LoginActivity();
						String[] str = obj.GetSharePreferenceSession(context);
						if (str!=null) {
							CallVolleyRevertForceClosed(str[3], str[4], str[0], str[5],str[14],str[15],TransactionID,String.valueOf(Type));
							dialog.dismiss();
						}
					} else {
						MessageDialog.MessageDialog(context,"",status);
					}
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
			alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						dialog.dismiss();
					}
					return true;
				}
			});
		}
		public void CallVolleyRevertForceClosed(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String TransID,final String Type){
			showpDialog();
			StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderForceClosedCancle", new Response.Listener<String>()
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
							MessageDialog.MessageDialog(context,"",Msg);
							DBHandler.deleteByTransaction(TransID, Integer.parseInt(Type));
							LoadRecyclerView(Integer.parseInt(Type));
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
					params.put("TransID", TransID);
					params.put("Type", Type);
					Log.d(TAG,"Cancel Force Closed parameters:"+params.toString());
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
			if(progressDialog!=null || swipeRefreshLayout!=null) {
				progressDialog.dismiss();
				swipeRefreshLayout.setRefreshing(false);
			}
		}
	}
}
