package report.itemsrndetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.BarcodeScanner;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import report.itemsrndetails.adapter.ItemSRNDetailsAdapter;
import report.itemsrndetails.adapter.ItemSRNDetailsViceVersaAdapter;
import services.NetworkUtils;
import stockcheck.StockCheckActivity;

public class ItemSRNDetailsViewPagerActivity extends AppCompatActivity{
	private List<String> ListHeader=null;
	private ActionBar actionBar;
	private static ViewPager mViewPager;
	private Context context;
	private TabLayout tabLayout;
	private static String tempItemCode="";
	private static EditText editTxtBarCode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
		setContentView(R.layout.viewpager_design);
		Initialization();
		LoadViewPager();
	}
	private void Initialization(){
		this.context = ItemSRNDetailsViewPagerActivity.this;
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
    }
    private void LoadViewPager(){
	   ListHeader=new ArrayList<>();
	   ListHeader.add("Item SRN Details");
	   ListHeader.add("Item SRN Details(Vice-Versa)");

	   ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),ListHeader);
	   mViewPager.setAdapter(mPagerAdapter);
	   tabLayout.setupWithViewPager(mViewPager);
	   mViewPager.setCurrentItem(0,true);
	   mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		   @Override
		   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		   }

		   @Override
		   public void onPageSelected(int position) {
			   //pagerPosition = position;
			   mViewPager.setCurrentItem(position,true);
		   }

		   @Override
		   public void onPageScrollStateChanged(int state) {
		   }
	   });
   }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_HOME){
			// Stop your service here
			System.out.println("This app is close");
			finishAffinity();
		}else if(keyCode==KeyEvent.KEYCODE_BACK){
			//TODO: Activity finish
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public class ViewPagerAdapter extends FragmentStatePagerAdapter {

		List<String> data=null;
		public ViewPagerAdapter(FragmentManager fragmentManager, List<String> maingrpList) {
			super(fragmentManager);
			this.data=maingrpList;
		}
		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
					return FragmentItemSRNDetails.newInstance("SRN");
				case 1:
					return FragmentItemSRNDetailsViceVersa.newInstance("SRNReverse");
			}
			return null;
		}
		public CharSequence getPageTitle(int position) {
			return data.get(position);
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
	public static class FragmentItemSRNDetails extends DialogFragment {
		private static final String SRN = "SRN";
		private Context context;
		private Button btnScan;
		private ProgressDialog progressDialog;
		private RecyclerView recyclerView,recyclerView1;
		private SwipeRefreshLayout swipeRefreshLayout;
		String Barcode="";
		private ItemSRNDetailsAdapter srnAdapter;
		private static String TAG = FragmentItemSRNDetails.class.getSimpleName();
		public static FragmentItemSRNDetails newInstance(String Srn) {
			FragmentItemSRNDetails fragment = new FragmentItemSRNDetails();
			Bundle args = new Bundle();
			args.putString(SRN, Srn);
			fragment.setArguments(args);
			return fragment;
		}
		public FragmentItemSRNDetails() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle){
			View rootView = inflater.inflate(R.layout.barcode_search_barcodewise, null);
			setHasOptionsMenu(true);
			Initialization(rootView);
			EventHandler();
			return rootView;	    
		}
		private void Initialization(View rootView) {
			this.context = getActivity();
			swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_Refresh_Layout);
			swipeRefreshLayout.setRefreshing(false);
			recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
			editTxtBarCode =(EditText)rootView.findViewById(R.id.editText_barcode);
			btnScan = (Button) rootView.findViewById(R.id.scan_button);
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		private void EventHandler(){
			btnScan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, BarcodeScanner.class);
					startActivityForResult(intent,100);
				}
			});
			editTxtBarCode.setHint("Style/Barcode");
			editTxtBarCode.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						// Perform action on Enter key press
						Barcode=editTxtBarCode.getText().toString().toUpperCase().trim();
						editTxtBarCode.setText("");
						if(!Barcode.isEmpty())	{
							String status = NetworkUtils.getConnectivityStatusString(context);
							if (!status.contentEquals("No Internet Connection")) {
								LoginActivity obj = new LoginActivity();
								String[] str = obj.GetSharePreferenceSession(context);
								if (str != null)
									CallVolleyItemSRNDetails(str[3], str[4], str[0], str[5], str[14], Barcode, "","","" ,"","","0");
									editTxtBarCode.requestFocus();
							} else {
								MessageDialog.MessageDialog(context,"",""+status);
							}
						} else {
							MessageDialog.MessageDialog(getActivity(),"", "You must be enter Style Code & Barcode");
						}
						return true;
					}
					return false;
				}
			});
			editTxtBarCode.requestFocus();
			if(!ItemSRNDetailsViewPagerActivity.tempItemCode.isEmpty()) {
				Barcode = ItemSRNDetailsViewPagerActivity.tempItemCode;
			}
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
			  switch(item.getItemId()){
			  case android.R.id.home:
					getActivity().onBackPressed();
				    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
				   break;
			  case R.id.action_attachment: //TODO: Stock Check
				  if (!Barcode.isEmpty()){
					  //TODO: Stock check Link
					  DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
					  Map<String,String> map = DBInfo.getModulePermissionByVtype(11);
					  if (map != null && !map.isEmpty()) {
						  Bundle bundle = new Bundle();
						  bundle.putString("ItemCode",Barcode);
						  bundle.putString("Title",map.get("Name"));
						  bundle.putInt("ViewFlag",Integer.valueOf(map.get("ViewFlag")));
						  bundle.putInt("EditFlag",Integer.valueOf(map.get("EditFlag")));
						  bundle.putInt("CreateFlag",Integer.valueOf(map.get("CreateFlag")));
						  bundle.putInt("RemoveFlag",Integer.valueOf(map.get("RemoveFlag")));
						  bundle.putInt("PrintFlag",Integer.valueOf(map.get("PrintFlag")));
						  bundle.putInt("ImportFlag",Integer.valueOf(map.get("ImportFlag")));
						  bundle.putInt("ExportFlag",Integer.valueOf(map.get("ExportFlag")));
						  bundle.putInt("Vtype",Integer.valueOf(map.get("Vtype")));
						  //System.out.println("bundle print:"+bundle.toString());
						  Intent in = new Intent(context, StockCheckActivity.class);
						  in.putExtra("PermissionBundle", bundle);
						  startActivity(in);
					  }
				  }else{
					  MessageDialog.MessageDialog(context,"","Please search Barcode or Style code then stock check");
				  }
				break;
			  }

		   return super.onOptionsItemSelected(item);	      
		}
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 inflater.inflate(R.menu.main, menu);
			MenuItem stockCheckItem = menu.findItem(R.id.action_attachment); //TODO: Stock Check
			stockCheckItem.setIcon(getResources().getDrawable(R.drawable.ic_action_stock_check_white));
		 super.onCreateOptionsMenu(menu,inflater);
	 }
	 	//Call Api Method
		public void CallApiMethod(String ItemCode){
			String status = NetworkUtils.getConnectivityStatusString(context);
			if (!status.contentEquals("No Internet Connection")) {
				LoginActivity obj = new LoginActivity();
				String[] str = obj.GetSharePreferenceSession(context);
				if (str != null)
					CallVolleyItemSRNDetails(str[3], str[4], str[0], str[5], str[14], ItemCode, "","","" ,"","","0");
					editTxtBarCode.requestFocus();
			} else {
				MessageDialog.MessageDialog(context,"",""+status);
			}
		}
		//TODO: Call Volley Item SRN Details
		public void CallVolleyItemSRNDetails(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String ItemCode,final String ItemID,final String Barcode,final String SubItemID,final String DocID,final String DocDetID,final String Flag){
			showpDialog();
			StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemSrnDetails", new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response) {
					// response
					Log.d("Response", response);
					try{
						List<Map<String,String>> mapList = new ArrayList<>();
						JSONObject jsonObject = new JSONObject(response);
						int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
						String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
						if (Status == 1) {
							JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
							for (int i=0; i< jsonArrayScfo.length(); i++){
								Map<String,String> map = new HashMap<>();
								//Barcode = (jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode"));
								map.put("VID",(jsonArrayScfo.getJSONObject(i).optString("VID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VID")));
								map.put("VDetailID",(jsonArrayScfo.getJSONObject(i).optString("VDetailID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VDetailID")));
								map.put("VDate",(jsonArrayScfo.getJSONObject(i).optString("VDate")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VDate")));
								map.put("VNo",(jsonArrayScfo.getJSONObject(i).optString("VNo")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VNo")));
								map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
								map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
								map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
								map.put("PartyName",(jsonArrayScfo.getJSONObject(i).optString("PartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyName")));
								map.put("PartyCode",(jsonArrayScfo.getJSONObject(i).optString("PartyCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyCode")));
								map.put("StockQty",(jsonArrayScfo.getJSONObject(i).optString("StockQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("StockQty")));
								map.put("ItemUnitRate", (jsonArrayScfo.getJSONObject(i).optString("ItemUnitRate")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemUnitRate")));
								map.put("MakerCharge", (jsonArrayScfo.getJSONObject(i).optString("MakerCharge")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MakerCharge")));
								map.put("VType",(jsonArrayScfo.getJSONObject(i).optString("VType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VType")));
								map.put("Header",(jsonArrayScfo.getJSONObject(i).optString("Header")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Header")));
								//TODO: Add map into list
								mapList.add(map); //
							}
							//TODO: Load Recycler View
							LoadRecyclerView(mapList,Integer.valueOf(Flag));
						} else {
							LoadRecyclerView(mapList,Integer.valueOf(Flag));
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
					params.put("ItemCode", ItemCode);
					params.put("ItemID", ItemID);
					params.put("Barcode", Barcode);
					params.put("SubItemID", SubItemID);
					params.put("DocID", DocID);
					params.put("DocDetID", DocDetID);
					params.put("Flag", Flag);
					Log.d(TAG,"Item Srn Details parameters:"+params.toString());
					return params;
				}
			};
			postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			AppController.getInstance().addToRequestQueue(postRequest);
		}
		private void LoadRecyclerView(List<Map<String,String>> mapList, final int Flag){
			if (Flag == 0) {
				srnAdapter = new ItemSRNDetailsAdapter(context, mapList, Flag);
				recyclerView.setAdapter(srnAdapter);
				LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
				linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
				recyclerView.setLayoutManager(linearLayoutManager);
				ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
					@Override
					public void onItemClicked(RecyclerView recyclerView, int position, View v) {
						Map<String, String> map = (Map<String, String>) srnAdapter.getItem(position);
						if (map!=null && map.get("VType").equals("76")) {
							DialogMoreDetails(context,map,1);
						}
					}
				});
			}else if (Flag == 1) {
				if (mapList.size() == 1){
					DialogMoreDetails(context,mapList.get(0),2);
				}else if (mapList.size() > 1){
					if (recyclerView1!=null) {
						final ItemSRNDetailsAdapter itemSRNDetailsAdapter = new ItemSRNDetailsAdapter(context, mapList, Flag);
						recyclerView1.setAdapter(itemSRNDetailsAdapter);
						LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
						linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
						recyclerView1.setLayoutManager(linearLayoutManager);
						ItemClickSupport.addTo(recyclerView1).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
							@Override
							public void onItemClicked(RecyclerView recyclerView, int position, View v) {
								Map<String, String> map = (Map<String, String>) itemSRNDetailsAdapter.getItem(position);
								if (map != null && map.get("ItemID").isEmpty()) {
									DialogMoreDetails(context,map,2);
								}
							}
						});
					}
				}
			}else if (Flag == 2) {
				if (recyclerView1!=null)
					recyclerView1.setAdapter(new ItemSRNDetailsAdapter(context, mapList, Flag));
					LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
					linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
					recyclerView1.setLayoutManager(linearLayoutManager);
			}
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
		private void DialogMoreDetails(final Context context,Map<String, String> map,int Flag) {
			final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.FullHeightDialog));
			dialog.setContentView(R.layout.dialog_recycler_view);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(dialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			dialog.show();
			//TODO: Declarations
			recyclerView1 = (RecyclerView) dialog.findViewById(R.id.recycler_view);
			Button btnOk = (Button) dialog.findViewById(R.id.Button_OK);
			String status = NetworkUtils.getConnectivityStatusString(context);
			if (!status.contentEquals("No Internet Connection")) {
				LoginActivity obj = new LoginActivity();
				String[] str = obj.GetSharePreferenceSession(context);
				if (str != null)
					if (Flag == 1)
						CallVolleyItemSRNDetails(str[3], str[4], str[0], str[5], str[14], Barcode, "", "", "", "" + map.get("VID"), "" + map.get("VDetailID"), "1");
					else if (Flag == 2)
						CallVolleyItemSRNDetails(str[3], str[4], str[0], str[5], str[14], "", map.get("ItemID"), "", "", "" + map.get("VID"), "" + map.get("VDetailID"), "2");
			} else {
				MessageDialog.MessageDialog(context,"",""+status);
			}
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
		}
	}
	public static class FragmentItemSRNDetailsViceVersa extends Fragment{
		public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
		private static final String ARG_SECTION_NUMBER = "SRNReverse";
		private Context context;
		private EditText editTxtFabricCode;
		private RecyclerView recyclerView;
		private ProgressDialog progressDialog;
		private SwipeRefreshLayout swipeRefreshLayout;
		private String FabricCode="";
		private ItemSRNDetailsViceVersaAdapter SRNViceVersaAdapter;
		private static String TAG = FragmentItemSRNDetailsViceVersa.class.getSimpleName();
		public static FragmentItemSRNDetailsViceVersa newInstance(String sectionNumber) {
			FragmentItemSRNDetailsViceVersa fragment = new FragmentItemSRNDetailsViceVersa();
			Bundle args = new Bundle();
			args.putString(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}
		public FragmentItemSRNDetailsViceVersa() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle){
			View rootView = inflater.inflate(R.layout.barcode_search_barcodewise, null);
			//ItemSRNDetailsViewPagerActivity.context=getActivity();
			Initialization(rootView);
			return rootView;	    
		}
		private void Initialization(View rootView){
			this.context = getActivity();
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCanceledOnTouchOutside(false);
			swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_Refresh_Layout);
			swipeRefreshLayout.setRefreshing(false);
			recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
			editTxtFabricCode =(EditText)rootView.findViewById(R.id.editText_barcode);
			Button btnScan = (Button) rootView.findViewById(R.id.scan_button);
			btnScan.setVisibility(View.GONE);
			editTxtFabricCode.setHint("Fabric Code");
			editTxtFabricCode.setOnKeyListener(new OnKeyListener() {
		        public boolean onKey(View v, int keyCode, KeyEvent event) {
		            // If the event is a key-down event on the "enter" button
		            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
		                // Perform action on Enter key press
		            	FabricCode=editTxtFabricCode.getText().toString().toUpperCase().trim();
		            	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	                    imm.hideSoftInputFromWindow(editTxtFabricCode.getWindowToken(), 0);
	                    editTxtFabricCode.setText("");
	                    if(!FabricCode.isEmpty()) {
							String status = NetworkUtils.getConnectivityStatusString(context);
							if (!status.contentEquals("No Internet Connection")) {
								LoginActivity obj = new LoginActivity();
								String[] str = obj.GetSharePreferenceSession(context);
								if (str != null)
									CallVolleyItemSRNProductionDetails(str[3], str[4], str[0], str[5], str[14], ""+FabricCode);
									editTxtFabricCode.requestFocus();
							} else {
								MessageDialog.MessageDialog(context,"",""+status);
							}
						}else{
	                    	MessageDialog.MessageDialog(getActivity(), "","You must be enter style code");
			            }
		            	return true;
		            }
		            return false;
		        }
		    });
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
			  switch(item.getItemId()){
			  case android.R.id.home:
					getActivity().onBackPressed();
				    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
				   break;
			  }
		   return super.onOptionsItemSelected(item);	      
		}
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 inflater.inflate(R.menu.main, menu);
	     super.onCreateOptionsMenu(menu,inflater);
	     MenuItem searchItem = menu.findItem(R.id.action_search);
		    searchItem.setVisible(false);
	 }
		//TODO: Call Volley Item SRN Details
		public void CallVolleyItemSRNProductionDetails(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String FabricCode){
			showpDialog();
			StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemSRNProductionDet", new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response) {
					// response
					Log.d("Response", response);
					try{
						List<Map<String,String>> mapList = new ArrayList<>();
						JSONObject jsonObject = new JSONObject(response);
						int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
						String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
						if (Status == 1) {
							JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
							for (int i=0; i< jsonArrayScfo.length(); i++){
								Map<String,String> map = new HashMap<>();
								map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
								map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
								map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
								//TODO: Add map into lis
								mapList.add(map);
							}
							//TODO: Load Recycler View
							LoadRecyclerView(mapList);
						} else {
							LoadRecyclerView(mapList);
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
					params.put("FabricCode", FabricCode);
					Log.d(TAG,"Item Srn Production Details parameters:"+params.toString());
					return params;
				}
			};
			postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			AppController.getInstance().addToRequestQueue(postRequest);
		}
		private void LoadRecyclerView(List<Map<String,String>> mapList){
			SRNViceVersaAdapter = new ItemSRNDetailsViceVersaAdapter(context, mapList);
			recyclerView.setAdapter(SRNViceVersaAdapter);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View v) {
					Map<String, String> map = (Map<String, String>) SRNViceVersaAdapter.getItem(position);
					if (map!=null) {
						tempItemCode = map.get("ItemCode");
						if (!tempItemCode.isEmpty()) {
							mViewPager.setCurrentItem(0, true);
							editTxtBarCode.setText(tempItemCode);
							FragmentItemSRNDetails frObj = new FragmentItemSRNDetails();
							frObj.CallApiMethod(tempItemCode);
						}
					}
				}
			});
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