package orderbooking.barcode_search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.BarcodeScanner;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.barcode_search.adapter.BarcodeWiseBookingAdapter;
import orderbooking.barcode_search.adapter.ColorWiseBarcodeAdapter;
import orderbooking.customerlist.BookOrdersActivity;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import services.NetworkUtils;

/**
 * Created by Rakesh on 02-Nov-16.
 */
public class BarcodeSearchViewPagerActivity extends AppCompatActivity {

	ViewPager mViewPager;
	ActionBar actionBar;
	ArrayList<String> Header;
	TabLayout tabLayout;
	private Context context;
	public static String Barcode = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.viewpager_design);
		StaticValues.OrderViewIntentFlag = 0;
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Barcode Search");
		Initialization();
		Header=new ArrayList<String>();
		Header.add("Item Wise");
		Header.add("Color Wise");
		if(StaticValues.MultiOrderSize == 1) { Header.add("Barcode Wise"); }
		ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),Header);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setAdapter(mPagerAdapter);
		tabLayout.setupWithViewPager(mViewPager);
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				mViewPager.setCurrentItem(position,true);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
	private void Initialization(){
		this.context = BarcodeSearchViewPagerActivity.this;
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		setActionBarHeader();
	}
	private void setActionBarHeader(){
		try{
			if (!BookOrderAdapter.listMultiCustomer.isEmpty()){
				String Header="";
				for (int i=0;i<BookOrderAdapter.listMultiCustomer.size();i++) {
					String Party = BookOrderAdapter.listMultiCustomer.get(i).getPartyName();
					String SubParty = BookOrderAdapter.listMultiCustomer.get(i).getSubParty();
					String RefName = BookOrderAdapter.listMultiCustomer.get(i).getRefName();
					if (!SubParty.isEmpty()) {
						Header += SubParty +" ("+Party+"), ";
					}else if (!RefName.isEmpty()) {
						Header += RefName +" ("+Party+"), ";
					}else if (!RefName.isEmpty() && !SubParty.isEmpty()) {
						Header += RefName +" ("+SubParty+"-"+Party+"), ";
					}else{
						Header += Party+" , ";
					}
				}
				LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflator.inflate(R.layout.action_bar_header, null);
				ImageView imageView = (ImageView) v.findViewById(R.id.imageView_Back);
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
					}
				});
				TextView textView = (TextView) v.findViewById(R.id.text_view_header);
				textView.setSelected(true);
				textView.setWidth(StaticValues.sViewWidth-300);
				textView.setText("" + Header.substring(0,Header.length()-2));
				actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
				actionBar.setCustomView(v);
			}else{
				MessageDialog.MessageDialog(context,"Book order adapter","Something went wrong");
			}
		}catch (Exception e){
			MessageDialog.MessageDialog(context, "Exception", "Intent:"+e.toString());
		}
	}
	public class ViewPagerAdapter extends FragmentPagerAdapter {
		ArrayList<String> Header;
		public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> Header) {
			super(fragmentManager);
			this.Header=Header;
		}
		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
				{
					return ItemWiseTabFragment.newInstance("ItemWise");
				}
				case 1:
				{
					return ColorWiseTabFragment.newInstance("ColorWise");
				}
				case 2:
				{
					return BarcodeWiseTabFragment.newInstance("BarcodeWise");
				}
			}
			return null;
		}
		public CharSequence getPageTitle(int position) {
			return Header.get(position);
		}
		@Override
		public int getCount() {
			return Header.size();
		}
	}
	public static class ItemWiseTabFragment extends Fragment{
		private static final String ITEM_WISE = "ItemWise";
		DatabaseSqlLiteHandlerOrderBooking DBHandler;
		EditText editTxtStyleOrBarcode;
		String StyleOBarcode="";
		private Button btnScan;
		private Context context;
		private ProgressDialog progressDialog;
		private static String TAG = ItemWiseTabFragment.class.getSimpleName();
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static ItemWiseTabFragment newInstance(String ItemWise) {
			ItemWiseTabFragment fragment = new ItemWiseTabFragment();
			Bundle args = new Bundle();
			args.putString(ITEM_WISE, ItemWise);
			fragment.setArguments(args);
			return fragment;
		}
		public ItemWiseTabFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
			View rootView = inflater.inflate(R.layout.barcode_search_itemwise, null);
			setHasOptionsMenu(true);
			Initialization(rootView);
			return rootView;
		}
		private void Initialization(View view){
			BarcodeSearchViewPagerActivity.Barcode = "";
			StaticValues.OrderViewIntentFlag = 0;
			this.context=getActivity();
			DBHandler=new DatabaseSqlLiteHandlerOrderBooking(getActivity());
			this.progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCanceledOnTouchOutside(false);
			btnScan = (Button) view.findViewById(R.id.scan_button);
			btnScan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, BarcodeScanner.class);
					startActivityForResult(intent,100);
				}
			});
			editTxtStyleOrBarcode =(EditText) view.findViewById(R.id.editText_style_or_barcode);
			editTxtStyleOrBarcode.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
						// Perform action on Enter key press
						StyleOBarcode=editTxtStyleOrBarcode.getText().toString().toUpperCase().trim();
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editTxtStyleOrBarcode.getWindowToken(), 0);
						editTxtStyleOrBarcode.setText("");
						if(!StyleOBarcode.isEmpty() || StyleOBarcode!=null){
							CallApiMethod();
						}
						else{
							MessageDialog.MessageDialog(getActivity(), "","You must be enter style code or barcode");
						}
						return true;
					}
					return false;
				}
			});
		}
		private void CallApiMethod(){
			String status = NetworkUtils.getConnectivityStatusString(context);
			if (!status.contentEquals("No Internet Connection")) {
				LoginActivity obj=new LoginActivity();
				String[] str = obj.GetSharePreferenceSession(context);
				if (str!=null) {
					if (!BookOrderAdapter.listMultiCustomer.isEmpty()){
						String OrderID="";
						for (int i=0;i<BookOrderAdapter.listMultiCustomer.size();i++) {
							OrderID += BookOrderAdapter.listMultiCustomer.get(i).getOrderID()+"|";
						}
						CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14],str[15],StyleOBarcode,OrderID.substring(0,OrderID.length()-1));
						BarcodeSearchViewPagerActivity.Barcode = StyleOBarcode;
					}else{
						MessageDialog.MessageDialog(context,"Search Style or Barcode","Something went wrong");
					}
				}else {
					MessageDialog.MessageDialog(context,"","Somthing went wrong");
				}
			} else {
				MessageDialog.MessageDialog(context,"",status);
			}
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			if (requestCode == 100) {
				if (resultCode == Activity.RESULT_OK) {
					StyleOBarcode = data.getExtras().getString("Barcode");
					if (!StyleOBarcode.isEmpty()) {
						CallApiMethod();
					} else {
						MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
					}
				}
				if (resultCode == Activity.RESULT_CANCELED) {
					//Write your code if there's no result
				}
			}
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle presses on the action bar items
			switch(item.getItemId()){
				case android.R.id.home:
					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
					break;
				case R.id.Report_View:
					Intent intent = new Intent(context, OrderViewOrPushActivity.class);
					startActivity(intent);
					break;
			}
			return super.onOptionsItemSelected(item);
		}
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.menu_custom, menu);
			super.onCreateOptionsMenu(menu,inflater);
			MenuItem searchItem = menu.findItem(R.id.Action_Search);
			searchItem.setVisible(false);
			MenuItem saveNext = menu.findItem(R.id.Save_Next);
			saveNext.setVisible(false);
			MenuItem push = menu.findItem(R.id.Push_Fca);
			push.setVisible(false);
		}
		private void CallVolleyStyleOrBarcodeSearch(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String Barcode,final String OrderID){
			showpDialog();
			StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemDetailsForBooking", new Response.Listener<String>()
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
							StaticValues.ColorWise = 0;
							List<Map<String,String>> mapList = new ArrayList<>();
							List<Map<String,String>> mapListSizeSet = new ArrayList<>();
							JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
							String MainGroup = jsonObjectResult.getString("MainGroup");
							String MainGroupID = jsonObjectResult.getString("MainGroupID");
							String GroupName = jsonObjectResult.getString("GroupName");
							String GroupID = jsonObjectResult.getString("GroupID");
							String SubGroup = jsonObjectResult.getString("SubGroup");
							String SubGroupID = jsonObjectResult.getString("SubGroupID");
							String ItemID = jsonObjectResult.getString("ItemID");
							String ItemName = jsonObjectResult.getString("ItemName");
							String ItemCode = jsonObjectResult.getString("ItemCode");
							int SubItemApplicable = jsonObjectResult.getInt("SubItemApplicable");
							int MdApplicable = jsonObjectResult.getInt("MdApplicable");
							StaticValues.MDApplicable = MdApplicable;
							StaticValues.SubItemApplicable = SubItemApplicable;
							StaticValues.StockCheck = jsonObjectResult.getInt("StockCheck");
							if (MdApplicable == 1){
								//TODO: Multi Details only
								//TODO SizeSet Array
								JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
								//System.out.println("SizeSet:"+jsonArraySizeSet);
								//TODO Details Array
								JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
								//System.out.println("Details:"+jsonArrayDetails);
								for (int i=0; i< jsonArrayDetails.length(); i++) {
									Map<String, String> map = new HashMap<>();
									map.put("MainGroup", MainGroup);
									map.put("MainGroupID", MainGroupID);
									map.put("GroupName", GroupName);
									map.put("GroupID", GroupID);
									map.put("SubGroup", SubGroup);
									map.put("SubGroupID", SubGroupID);
									map.put("ItemID", ItemID);
									map.put("ItemName", ItemName);
									map.put("ItemCode", ItemCode);
									map.put("SubItemApplicable", String.valueOf(SubItemApplicable));
									map.put("MdApplicable", String.valueOf(MdApplicable));
									map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
									map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
									map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
									map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
									map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
									map.put("ColorCode", jsonArrayDetails.getJSONObject(i).getString("ColorCode"));
									map.put("Size", jsonArrayDetails.getJSONObject(i).getString("Size"));
									map.put("SizeSequence", jsonArrayDetails.getJSONObject(i).getString("SizeSequence"));
									map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
									map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
									map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
									map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
									map.put("Remarks", (jsonArrayDetails.getJSONObject(i).getString("Remarks0") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks0").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks0"));
									map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
									map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
									for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
										map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
										map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
										map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
										map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
										map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
										map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
										map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
									}
									mapList.add(map);
								}
								//TODO: Size set
								for (int i=0; i<jsonArraySizeSet.length(); i++){
									Map<String,String> map = new HashMap<>();
									map.put("MainGroupID", MainGroupID);
									map.put("GroupID", GroupID);
									map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
									map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
									mapListSizeSet.add(map);
								}
								context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
								DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
								DBOrder.deleteTablesData();
								DBOrder.deleteOutOfStockTablesData();
								DBOrder.insertOrderBookingTable(mapList);
								DBOrder.insertSizeSetTable(mapListSizeSet);
								hidepDialog();

								Intent intent = new Intent(context,OrderBookingActivity.class);
								startActivity(intent);
								getActivity().finish();
							}else {
								//TODO: SubItem Only
								if (SubItemApplicable == 1){
									//TODO SizeSet Array
									JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
									//System.out.println("SizeSet:"+jsonArraySizeSet);
									//TODO Details Array
									JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
									//System.out.println("Details:"+jsonArrayDetails);
									for (int i=0; i< jsonArrayDetails.length(); i++) {
										Map<String, String> map = new HashMap<>();
										map.put("MainGroup", MainGroup);
										map.put("MainGroupID", MainGroupID);
										map.put("GroupName", GroupName);
										map.put("GroupID", GroupID);
										map.put("SubGroup", SubGroup);
										map.put("SubGroupID", SubGroupID);
										map.put("ItemID", ItemID);
										map.put("ItemName", ItemName);
										map.put("ItemCode", ItemCode);
										map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
										map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
										map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
										map.put("SubItemID", jsonArrayDetails.getJSONObject(i).getString("SubItemID"));
										map.put("SubItemName", jsonArrayDetails.getJSONObject(i).getString("SubItemName"));
										map.put("SubItemCode", jsonArrayDetails.getJSONObject(i).getString("SubItemCode"));
										map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
										map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
										map.put("Remarks", (jsonArrayDetails.getJSONObject(i).getString("Remarks0") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks0").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks0"));
										map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
										map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
										for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
											map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
											map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
											map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
											map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
											map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
											map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
											map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
										}
										mapList.add(map);
									}
									//TODO: Size set
									for (int i=0; i<jsonArraySizeSet.length(); i++){
										Map<String,String> map = new HashMap<>();
										map.put("MainGroupID", MainGroupID);
										map.put("GroupID", GroupID);
										map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
										map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
										mapListSizeSet.add(map);
									}
									context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
									DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
									DBOrder.deleteTablesData();
									DBOrder.insertOrderBookingSubItemTable(mapList);
									DBOrder.insertSizeSetTable(mapListSizeSet);
									hidepDialog();

									Intent intent = new Intent(context,SubItemActivity.class);
									startActivity(intent);
									getActivity().finish();
								}else{
									//TODO: Without SubItem Or Item only
									//TODO SizeSet Array
									JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
									//System.out.println("SizeSet:"+jsonArraySizeSet);
									//TODO Details Array
									JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
									//System.out.println("Details:"+jsonArrayDetails);
									for (int i=0; i< jsonArrayDetails.length(); i++) {
										Map<String, String> map = new HashMap<>();
										map.put("MainGroup", MainGroup);
										map.put("MainGroupID", MainGroupID);
										map.put("GroupName", GroupName);
										map.put("GroupID", GroupID);
										map.put("SubGroup", SubGroup);
										map.put("SubGroupID", SubGroupID);
										map.put("ItemID", ItemID);
										map.put("ItemName", ItemName);
										map.put("ItemCode", ItemCode);
										map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
										map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
										map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
										map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
										map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
										map.put("Remarks", (jsonArrayDetails.getJSONObject(i).getString("Remarks0") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks0").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks0"));
										map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
										map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
										for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
											map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
											map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
											map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
											map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
											map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
											map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
											map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
										}
										mapList.add(map);
									}
									//TODO: Size set
									for (int i=0; i<jsonArraySizeSet.length(); i++){
										Map<String,String> map = new HashMap<>();
										map.put("MainGroupID", MainGroupID);
										map.put("GroupID", GroupID);
										map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
										map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
										mapListSizeSet.add(map);
									}
									context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
									DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
									DBOrder.deleteTablesData();
									DBOrder.insertOrderBookingWithoutSubItemTable(mapList);
									DBOrder.insertSizeSetTable(mapListSizeSet);
									hidepDialog();

									Intent intent = new Intent(context,WithoutSubItemActivity.class);
									startActivity(intent);
									getActivity().finish();
								}
							}
							//System.out.println("List:"+mapList.toString());
							//System.out.println("ListSizeSet:"+mapListSizeSet.toString());
						} else {
							MessageDialog.MessageDialog(context,"",Msg);
							hidepDialog();
						}
					}catch (Exception e){
						MessageDialog.MessageDialog(context,"Exception",""+e.toString());
						hidepDialog();
					}

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
					params.put("Barcode", Barcode);
					params.put("OrderID", OrderID);
					Log.d(TAG,"Search barcode or style parameters:"+params.toString());
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
	public static class ColorWiseTabFragment extends Fragment{
		private static final String COLOR_WISE = "ColorWise";
		RecyclerView recyclerView;
		Button btnBookOrder;
		EditText editTxtBarCode;
		String Barcode="";
		private Button btnScan;
		ColorWiseBarcodeAdapter adapter;
		public static ArrayList<String> dataListBarcode=new ArrayList<String>();
        private Context context;
        private ProgressDialog progressDialog;
        private static String TAG = ColorWiseTabFragment.class.getSimpleName();
		public static ColorWiseTabFragment newInstance(String ColorWise) {
			ColorWiseTabFragment fragment = new ColorWiseTabFragment();
			Bundle args = new Bundle();
			args.putString(COLOR_WISE, ColorWise);
			fragment.setArguments(args);
			return fragment;
		}
		public ColorWiseTabFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
			View rootView = inflater.inflate(R.layout.barcode_search_colorwise, null);
			setHasOptionsMenu(true);
			StaticValues.OrderViewIntentFlag = 0;
            this.context = getActivity();
			dataListBarcode=new ArrayList<String>();
            this.progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
			btnScan = (Button) rootView.findViewById(R.id.scan_button);
			btnScan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, BarcodeScanner.class);
					startActivityForResult(intent,100);
				}
			});
			recyclerView=(RecyclerView) rootView.findViewById(R.id.listView_BarcodeList);
			btnBookOrder=(Button)rootView.findViewById(R.id.button_ok);
			editTxtBarCode =(EditText)rootView.findViewById(R.id.editText_barcode);
			editTxtBarCode.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						// Perform action on Enter key press
						Barcode=editTxtBarCode.getText().toString().toUpperCase().trim();
						editTxtBarCode.setText("");
						if(!Barcode.equals(""))	{
							//ColorViewPager.CheckForUpdate="";
							if(dataListBarcode.isEmpty()){
								dataListBarcode.add(Barcode);
							}else{
								if(!dataListBarcode.contains(Barcode)){
									dataListBarcode.add(Barcode);
								}
							}
							LoadRecyclerView(dataListBarcode);

						} else {
							Toast.makeText(getActivity(), "You must be enter Barcode!!!", Toast.LENGTH_SHORT).show();
						}
						v.requestFocus();
						return true;
					}
					return false;
				}
			});
			btnBookOrder.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					//ColorViewPager.CheckForUpdate="";
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            if (!BookOrderAdapter.listMultiCustomer.isEmpty() && !dataListBarcode.isEmpty()){
                                String OrderID="",Barcode = "";
                                for (int i=0;i<BookOrderAdapter.listMultiCustomer.size();i++) {
                                    OrderID += BookOrderAdapter.listMultiCustomer.get(i).getOrderID()+"|";
                                }
                                for (int i=0;i<dataListBarcode.size();i++) {
                                    Barcode += dataListBarcode.get(i)+"|";
                                }
                                CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14],str[15],Barcode.substring(0,Barcode.length()-1),OrderID.substring(0,OrderID.length()-1));
                                BarcodeSearchViewPagerActivity.Barcode = Barcode;
                            }else{
								if(dataListBarcode.isEmpty()){
									MessageDialog.MessageDialog(context,"","Add atleast 1 barcode");
								}
								if (BookOrderAdapter.listMultiCustomer.isEmpty()){
									MessageDialog.MessageDialog(context,"","Something went wrong on barcode");
								}
                            }
                        }else {
                            MessageDialog.MessageDialog(context,"","Somthing went wrong");
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
				}
			});
			return rootView;
		}
		private void LoadRecyclerView(List<String> BarcodeList){
			adapter=new ColorWiseBarcodeAdapter(context, BarcodeList);
			recyclerView.setAdapter(adapter);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			editTxtBarCode.post(new Runnable() {
				public void run() {
					editTxtBarCode.requestFocus();
					editTxtBarCode.setFocusable(true);
					editTxtBarCode.setFocusableInTouchMode(true);
				}
			});
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			if (requestCode == 100) {
				if (resultCode == Activity.RESULT_OK) {
					Barcode = data.getExtras().getString("Barcode");
					if (!Barcode.isEmpty()) {
						if(dataListBarcode.isEmpty()){
							dataListBarcode.add(Barcode);
						}else{
							if(!dataListBarcode.contains(Barcode)){
								dataListBarcode.add(Barcode);
							}
						}
						LoadRecyclerView(dataListBarcode);
					} else {
						MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
					}
				}
				if (resultCode == Activity.RESULT_CANCELED) {
					//Write your code if there's no result
				}
			}
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle presses on the action bar items
			switch(item.getItemId()){
				case android.R.id.home:
					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
					break;
				case R.id.Report_View:
					Intent intent = new Intent(context, OrderViewOrPushActivity.class);
					startActivity(intent);
					break;
			}
			return super.onOptionsItemSelected(item);
		}
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.menu_custom, menu);
			super.onCreateOptionsMenu(menu,inflater);
			MenuItem searchItem = menu.findItem(R.id.Action_Search);
			//searchItem.setVisible(false);
			MenuItem saveNext = menu.findItem(R.id.Save_Next);
			saveNext.setVisible(false);
			MenuItem push = menu.findItem(R.id.Push_Fca);
			push.setVisible(false);
		}
        private void CallVolleyStyleOrBarcodeSearch(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String Barcode,final String OrderID){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemColorWiseDetailsForBooking", new Response.Listener<String>()
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
							StaticValues.ColorWise = 1;
                            List<Map<String,String>> mapList = new ArrayList<>();
                            List<Map<String,String>> mapListSizeSet = new ArrayList<>();
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							for (int z = 0; z<jsonArrayResult.length(); z++) {
								String MainGroup = jsonArrayResult.getJSONObject(z).getString("MainGroup");
								String MainGroupID = jsonArrayResult.getJSONObject(z).getString("MainGroupID");
								String GroupName = jsonArrayResult.getJSONObject(z).getString("GroupName");
								String GroupID = jsonArrayResult.getJSONObject(z).getString("GroupID");
								String SubGroup = jsonArrayResult.getJSONObject(z).getString("SubGroup");
								String SubGroupID = jsonArrayResult.getJSONObject(z).getString("SubGroupID");
								String ItemID = jsonArrayResult.getJSONObject(z).getString("ItemID");
								String ItemName = jsonArrayResult.getJSONObject(z).getString("ItemName");
								String ItemCode = jsonArrayResult.getJSONObject(z).getString("ItemCode");
								String UPCCode = jsonArrayResult.getJSONObject(z).getString("UPCCode");
								StaticValues.MDApplicable = 1;
								StaticValues.StockCheck = jsonArrayResult.getJSONObject(z).getInt("StockCheck");
								//TODO SizeSet Array
								JSONArray jsonArraySizeSet = jsonArrayResult.getJSONObject(z).getJSONArray("SizeSet");
								//System.out.println("SizeSet:"+jsonArraySizeSet);
								//TODO Details Array
								JSONArray jsonArrayDetails = jsonArrayResult.getJSONObject(z).getJSONArray("Details");
								//System.out.println("Details:"+jsonArrayDetails);
								for (int i = 0; i < jsonArrayDetails.length(); i++) {
									Map<String, String> map = new HashMap<>();
									map.put("MainGroup", MainGroup);
									map.put("MainGroupID", MainGroupID);
									map.put("GroupName", GroupName);
									map.put("GroupID", GroupID);
									map.put("SubGroup", SubGroup);
									map.put("SubGroupID", SubGroupID);
									map.put("ItemID", ItemID);
									map.put("ItemName", ItemName);
									map.put("ItemCode", ItemCode);
									map.put("UPCCode", UPCCode);
									map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
									map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
									map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
									map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
									map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
									map.put("ColorCode", jsonArrayDetails.getJSONObject(i).getString("ColorCode"));
									map.put("Size", jsonArrayDetails.getJSONObject(i).getString("Size"));
									map.put("SizeSequence", jsonArrayDetails.getJSONObject(i).getString("SizeSequence"));
									map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
									map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
									map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
									map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
									map.put("Remarks", (jsonArrayDetails.getJSONObject(i).getString("Remarks0") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks0").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks0"));
									map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
									map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
									for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
										map.put("ExpectedDate" + x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate" + x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate" + x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate" + x));
										map.put("Rate" + x, (jsonArrayDetails.getJSONObject(i).getString("Rate" + x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate" + x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate" + x));
										map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
										map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
										map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
										map.put("Ord" + x, (jsonArrayDetails.getJSONObject(i).getString("Ord" + x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord" + x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord" + x));
										map.put("OrderID" + x, (jsonArrayDetails.getJSONObject(i).getString("OrderID" + x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID" + x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID" + x));
									}
									mapList.add(map);
								}
								//TODO: Size set
								for (int i = 0; i < jsonArraySizeSet.length(); i++) {
									Map<String, String> map = new HashMap<>();
									map.put("MainGroupID", MainGroupID);
									map.put("GroupID", GroupID);
									map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
									map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
									mapListSizeSet.add(map);
								}
								context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
								DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
								DBOrder.deleteTablesData();
								DBOrder.insertOrderBookingTable(mapList);
								DBOrder.insertSizeSetTable(mapListSizeSet);
								hidepDialog();

								Intent intent = new Intent(context, OrderBookingActivity.class);
								startActivity(intent);
								getActivity().finish();
							}
                            //System.out.println("List:"+mapList.toString());
                            //System.out.println("ListSizeSet:"+mapListSizeSet.toString());
                        } else {
                            MessageDialog.MessageDialog(context,"",Msg);
                            hidepDialog();
                        }
                    }catch (Exception e){
                        MessageDialog.MessageDialog(context,"Exception","Color "+e.toString());
                        hidepDialog();
                    }

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
                    params.put("Barcode", Barcode);
                    params.put("OrderID", OrderID);
                    Log.d(TAG,"Search barcode parameters:"+params.toString());
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
	public static class BarcodeWiseTabFragment extends Fragment{
		private static final String BARCODE_WISE = "BarcodeWise";
		RecyclerView recyclerView;
		EditText editTxtBarCode;
		private Context context;
		private ProgressDialog progressDialog;
		String Barcode="";
		private Button btnScan;
		BarcodeWiseBookingAdapter adapter;
		private SwipeRefreshLayout swipeRefreshLayout;
		private static String TAG = BarcodeWiseTabFragment.class.getSimpleName();
		public static BarcodeWiseTabFragment newInstance(String BarcodeWise) {
			BarcodeWiseTabFragment fragment = new BarcodeWiseTabFragment();
			Bundle args = new Bundle();
			args.putString(BARCODE_WISE, BarcodeWise);
			fragment.setArguments(args);
			return fragment;
		}
		public BarcodeWiseTabFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle){
			View rootView = inflater.inflate(R.layout.barcode_search_barcodewise, null);
			setHasOptionsMenu(true);
			StaticValues.OrderViewIntentFlag = 0;
			this.context = getActivity();
			this.progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCanceledOnTouchOutside(false);
			swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_Refresh_Layout);
			recyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view);
			editTxtBarCode =(EditText)rootView.findViewById(R.id.editText_barcode);
			btnScan = (Button) rootView.findViewById(R.id.scan_button);
			btnScan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, BarcodeScanner.class);
					startActivityForResult(intent,100);
				}
			});
			editTxtBarCode.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						// Perform action on Enter key press
						Barcode=editTxtBarCode.getText().toString().toUpperCase().trim();
						editTxtBarCode.setText("");
						if(!Barcode.equals(""))	{
							String status = NetworkUtils.getConnectivityStatusString(context);
							if (!status.contentEquals("No Internet Connection")) {
								LoginActivity obj = new LoginActivity();
								String[] str = obj.GetSharePreferenceSession(context);
								if (str != null)
									if (!Barcode.isEmpty())
									CallVolleyBarcodeWise(str[3], str[4], str[0], str[5], str[14], str[15],BookOrderAdapter.listMultiCustomer.get(0).getOrderID(),"1",Barcode ,"0",1);
									editTxtBarCode.requestFocus();
							} else {
								MessageDialog.MessageDialog(context,"",""+status);
							}
						} else {
							Toast.makeText(getActivity(), "You must be enter Barcode!!!", Toast.LENGTH_SHORT).show();
						}
						return true;
					}
					return false;
				}
			});
			editTxtBarCode.requestFocus();
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
				String status = NetworkUtils.getConnectivityStatusString(context);
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj = new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str != null)
						Barcode = "temp";
						if (!Barcode.isEmpty())
							CallVolleyBarcodeWise(str[3], str[4], str[0], str[5], str[14], str[15],BookOrderAdapter.listMultiCustomer.get(0).getOrderID(),"1",Barcode ,"0",0);
				} else {
					MessageDialog.MessageDialog(context,"",""+status);
				}
				}
			});
			return rootView;
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			if (requestCode == 100) {
				if (resultCode == Activity.RESULT_OK) {
					Barcode = data.getExtras().getString("Barcode");
					if (!Barcode.isEmpty()) {
						String status = NetworkUtils.getConnectivityStatusString(context);
						if (!status.contentEquals("No Internet Connection")) {
							LoginActivity obj = new LoginActivity();
							String[] str = obj.GetSharePreferenceSession(context);
							if (str != null)
								if (!Barcode.isEmpty())
									CallVolleyBarcodeWise(str[3], str[4], str[0], str[5], str[14], str[15],BookOrderAdapter.listMultiCustomer.get(0).getOrderID(),"1",Barcode ,"0",1);
							editTxtBarCode.requestFocus();
						} else {
							MessageDialog.MessageDialog(context,"",""+status);
						}
					} else {
						MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
					}
				}
				if (resultCode == Activity.RESULT_CANCELED) {
					//Write your code if there's no result
				}
			}
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
				case R.id.Report_View:
					Intent intent = new Intent(context, OrderViewOrPushActivity.class);
					startActivity(intent);
					break;
			}
			return super.onOptionsItemSelected(item);
		}
		@Override
		public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
			// Inflate the menu items for use in the action bar
			inflater.inflate(R.menu.menu_custom, menu);
			super.onCreateOptionsMenu(menu,inflater);
			MenuItem saveNext = menu.findItem(R.id.Save_Next);
			saveNext.setVisible(false);
			MenuItem push = menu.findItem(R.id.Push_Fca);
			push.setVisible(false);
			MenuItem searchItem = menu.findItem(R.id.Action_Search);
			searchItem.setVisible(true);
			SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
		public void onResume(){
			super.onResume();
			String status = NetworkUtils.getConnectivityStatusString(context);
			if (!status.contentEquals("No Internet Connection")) {
				LoginActivity obj = new LoginActivity();
				String[] str = obj.GetSharePreferenceSession(context);
				if (str != null)
					Barcode = "temp";
					if (!Barcode.isEmpty()) {
						CallVolleyBarcodeWise(str[3], str[4], str[0], str[5], str[14], str[15], BookOrderAdapter.listMultiCustomer.get(0).getOrderID(), "1", Barcode, "0",0);
					}
			} else {
				MessageDialog.MessageDialog(context,"",""+status);
			}
		}
		private void CallVolleyBarcodeWise(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID, final String BookQty,final String Barcode,final String BarcodeDelFlag,final int msgFlag){
			showpDialog();
			String PartialUrl = "";
			if (StaticValues.AdvanceOrBookOrder == 0){
				PartialUrl = "TempOrderItemsBarcodeWise";//"UpdateTempOrderItems";
			}else if (StaticValues.AdvanceOrBookOrder == 1){
				PartialUrl = "TempOrderItemsAdvanceBarcodeWise";//"UpdateTempOrderItemsAdvance";
			}
			//String PartialUrl = (StaticValues.AdvanceOrBookOrder == 1) ?  "UpdateTempOrderItemsAdvance" : "UpdateTempOrderItems";
			StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + PartialUrl, new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response) {
					// response
					Log.d("Response", response);
					try{
						JSONObject jsonObject = new JSONObject(response);
						int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
						String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
						StaticValues.ColorWise = 0;
						if (Status == 1) {
							List<Map<String,String>> mapList = new ArrayList<>();
							JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							for (int z = 0; z<jsonArrayResult.length(); z++) {
								Map<String,String> map = new HashMap<>();
								map.put("Barcode", jsonArrayResult.getJSONObject(z).getString("Barcode"));
								map.put("ItemID", jsonArrayResult.getJSONObject(z).getString("ItemID"));
								map.put("ItemCode", jsonArrayResult.getJSONObject(z).getString("ItemCode"));
								map.put("ColorName", jsonArrayResult.getJSONObject(z).getString("Color"));
								map.put("SizeName", jsonArrayResult.getJSONObject(z).getString("Size"));
								map.put("SubItem", (jsonArrayResult.getJSONObject(z).getString("SubItem") == null || jsonArrayResult.getJSONObject(z).getString("SubItem").equals("null")) ? "" : jsonArrayResult.getJSONObject(z).getString("SubItem"));
								map.put("BookQty", jsonArrayResult.getJSONObject(z).getString("BookQty"));
								map.put("Stock", jsonArrayResult.getJSONObject(z).getString("Stock"));
								map.put("Rate", jsonArrayResult.getJSONObject(z).getString("Rate"));
								map.put("Mrp", jsonArrayResult.getJSONObject(z).optString("MRP") == null ? "0" : jsonArrayResult.getJSONObject(z).optString("MRP"));
								map.put("DiscountRate", (jsonArrayResult.getJSONObject(z).optString("DiscountRate") == null || jsonArrayResult.getJSONObject(z).optString("DiscountRate").isEmpty()) ? jsonArrayResult.getJSONObject(z).getString("Rate") : jsonArrayResult.getJSONObject(z).optString("DiscountRate"));
								map.put("DiscountPercentage", (jsonArrayResult.getJSONObject(z).optString("DiscountPercentage") == null || jsonArrayResult.getJSONObject(z).optString("DiscountPercentage").isEmpty()) ? "0" : jsonArrayResult.getJSONObject(z).optString("DiscountPercentage"));
								map.put("Status", jsonArrayResult.getJSONObject(z).getString("Status"));
								map.put("MDApplicable", jsonArrayResult.getJSONObject(z).getString("MDApplicable"));
								map.put("SubItemApplicable", jsonArrayResult.getJSONObject(z).getString("SubItemApplicable"));
								map.put("OrderNo", jsonArrayResult.getJSONObject(z).getString("OrderNo"));
								map.put("OrderID", jsonArrayResult.getJSONObject(z).getString("OrderID"));
								mapList.add(map);
							}
							LoadBarcodeData(mapList);
						} else if (Status == 0) {
							List<Map<String,String>> mapList = new ArrayList<>();
							JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							String Message = "";
							for (int z = 0; z<jsonArrayResult.length(); z++) {
								Map<String,String> map = new HashMap<>();
								map.put("Barcode", jsonArrayResult.getJSONObject(z).getString("Barcode"));
								map.put("ItemID", jsonArrayResult.getJSONObject(z).getString("ItemID"));
								map.put("ItemCode", jsonArrayResult.getJSONObject(z).getString("ItemCode"));
								map.put("ColorName", jsonArrayResult.getJSONObject(z).getString("Color"));
								map.put("SizeName", jsonArrayResult.getJSONObject(z).getString("Size"));
								map.put("SubItem", (jsonArrayResult.getJSONObject(z).getString("SubItem") == null || jsonArrayResult.getJSONObject(z).getString("SubItem").equals("null")) ? "" : jsonArrayResult.getJSONObject(z).getString("SubItem"));
								map.put("BookQty", jsonArrayResult.getJSONObject(z).getString("BookQty"));
								map.put("Stock", jsonArrayResult.getJSONObject(z).getString("Stock"));
								map.put("Rate", jsonArrayResult.getJSONObject(z).getString("Rate"));
								map.put("Mrp", jsonArrayResult.getJSONObject(z).optString("MRP") == null ? "0" : jsonArrayResult.getJSONObject(z).optString("MRP"));
								map.put("DiscountRate", (jsonArrayResult.getJSONObject(z).optString("DiscountRate") == null || jsonArrayResult.getJSONObject(z).optString("DiscountRate").isEmpty()) ? jsonArrayResult.getJSONObject(z).getString("Rate") : jsonArrayResult.getJSONObject(z).optString("DiscountRate"));
								map.put("DiscountPercentage", (jsonArrayResult.getJSONObject(z).optString("DiscountPercentage") == null || jsonArrayResult.getJSONObject(z).optString("DiscountPercentage").isEmpty()) ? "0" : jsonArrayResult.getJSONObject(z).optString("DiscountPercentage"));
								map.put("Status", jsonArrayResult.getJSONObject(z).getString("Status"));
								map.put("MDApplicable", jsonArrayResult.getJSONObject(z).getString("MDApplicable"));
								map.put("SubItemApplicable", jsonArrayResult.getJSONObject(z).getString("SubItemApplicable"));
								map.put("OrderNo", jsonArrayResult.getJSONObject(z).getString("OrderNo"));
								map.put("OrderID", jsonArrayResult.getJSONObject(z).getString("OrderID"));
								mapList.add(map);
								Message = jsonArrayResult.getJSONObject(z).getString("msg");
							}
							LoadBarcodeData(mapList);
							if (msgFlag ==1) { MessageDialog.MessageDialog(context,"",(Message.isEmpty()? Msg : Message)); }
						}else if (Status == 3) {
							JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							String Message = "";
							for (int z = 0; z<jsonArrayResult.length(); z++) {
								Message = jsonArrayResult.getJSONObject(z).getString("msg");
							}
							if (msgFlag ==1) { MessageDialog.MessageDialog(context,"",(Message.isEmpty()? Msg : Message)); }
							List<Map<String,String>> mapList = new ArrayList<>();
							LoadBarcodeData(mapList);
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
					params.put("BookQty", BookQty);
					params.put("Barcode", Barcode);
					params.put("BarcodeDelFlag", BarcodeDelFlag);
					Log.d(TAG,"Barcode Wise parameters:"+params.toString());
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
		private void LoadBarcodeData(final List<Map<String,String>> mapList){
			adapter = new BarcodeWiseBookingAdapter(context,mapList);
			recyclerView.setAdapter(adapter);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
					AlertDialogMethod(mapList.get(position).get("Barcode"));
					return true;
				}
			});
			editTxtBarCode.requestFocus();
		}
		private void AlertDialogMethod(final String Barcode){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setMessage("Are you sure. You want to delete this items");
			alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String status = NetworkUtils.getConnectivityStatusString(context);
					if (!status.contentEquals("No Internet Connection")) {
						LoginActivity obj = new LoginActivity();
						String[] str = obj.GetSharePreferenceSession(context);
						if (str != null)
							if (StaticValues.removeFlag == 1) {
								if (!Barcode.isEmpty())
								CallVolleyBarcodeWise(str[3], str[4], str[0], str[5], str[14], str[15],BookOrderAdapter.listMultiCustomer.get(0).getOrderID(),"1",Barcode ,"1",1);
							}else {
								MessageDialog.MessageDialog(context,"Permission denied","You don't have delete permission to delete this item.");
							}
					} else {
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
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_HOME){
			// Stop your service here
			System.out.println("This app is close");
			finishAffinity();
		}else if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
		}
		return super.onKeyDown(keyCode, event);
	}
}