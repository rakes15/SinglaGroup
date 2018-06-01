package orderbooking.customerlist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerCloseOrderList;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.temp.CloseOrderAdapter;
import orderbooking.customerlist.temp.EventItem;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.customerlist.temp.HeaderItem;
import orderbooking.customerlist.temp.ListItem;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class BookOrdersActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> Header;
    private Context context;
    static int activityFlag = 0;
    private static String TAG = BookOrdersActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.viewpager_design);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        Header=new ArrayList<String>();
        Header.add("Single Customer");
        Header.add("Multi Customer");
        this.context = BookOrdersActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),Header);
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
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            StaticValues.Vtype = bundle.getInt("Vtype");
            activityFlag = bundle.getInt("ActivityFlag",0);
            actionBar.setTitle(Title);
            if (StaticValues.Vtype == 2) {
                StaticValues.AdvanceOrBookOrder = 0;
                StaticValues.BookType = 1;
            }else {
                StaticValues.AdvanceOrBookOrder = 1;
                StaticValues.BookType = 2;
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
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
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

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
                    return SingleCustomerFragment.newInstance("SingleCustomer");
                }
                case 1:
                {
                    return MultiCustomerFragment.newInstance("MultiCustomer");
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
    //TODO: Single Customer fragment
    public static class SingleCustomerFragment extends Fragment{
        private static final String SINGLE_CUSTOMER = "SingleCustomer";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private BookOrderAdapter adapter;
        private ProgressDialog progressDialog;
        @NonNull
        private List<ListItem> items = new ArrayList<>();
        public static SingleCustomerFragment newInstance(String SingleCustomer) {
            SingleCustomerFragment fragment = new SingleCustomerFragment();
            Bundle args = new Bundle();
            args.putString(SINGLE_CUSTOMER, SingleCustomer);
            fragment.setArguments(args);
            return fragment;
        }
        public SingleCustomerFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_swipe_refresh_layout, null);
            setHasOptionsMenu(true);
            StaticValues.PushOrderFlag = 0;
            this.context = getActivity();
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            BookOrderAdapter.listMultiCustomer = new ArrayList<>();
            StaticValues.MultiOrderSize = BookOrderAdapter.listMultiCustomer.size();
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod(1);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO:
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        CallApiMethod(1);
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void CallApiMethod(final int flag){
            //TODO: Book Orders Request
            NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
                @Override
                public void networkReceived(String status) {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            CallVolleyBookOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15],flag);
                        }
                    } else {
                        MessageDialog messageDialog=new MessageDialog();
                        messageDialog.MessageDialog(context,"","",status);
                    }
                }
            });
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    CallVolleyBookOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15],flag);
                }
            } else {
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(context,"","",status);
            }
        }
        private void CallVolleyBookOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID,final String BranchID,final int flag){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ApprovedPartyList", new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("Response", response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                        String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                        items = new ArrayList<>();
                        if (Status == 1) {
                            JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                            List<Map<String,String>> mapList = new ArrayList<>();
                            for (int i=0; i< jsonArrayScfo.length(); i++){
                                Map<String,String> map = new HashMap<>();
                                map.put("OrderID",jsonArrayScfo.getJSONObject(i).getString("OrderID"));
                                map.put("OrderNo",jsonArrayScfo.getJSONObject(i).getString("OrderNo"));
                                map.put("OrderDate",jsonArrayScfo.getJSONObject(i).getString("OrderDate"));
                                map.put("Godown",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                                map.put("GodownID",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                                map.put("PartyID",jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                                map.put("PartyName",jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                                map.put("SubPartyID",(jsonArrayScfo.getJSONObject(i).getString("SubPartyID")==null || jsonArrayScfo.getJSONObject(i).getString("SubPartyID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubPartyID"));
                                map.put("SubParty",(jsonArrayScfo.getJSONObject(i).getString("SubParty")==null || jsonArrayScfo.getJSONObject(i).getString("SubParty").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubParty"));
                                map.put("SubPartyApplicable",jsonArrayScfo.getJSONObject(i).getString("SubPartyApplicable"));
                                map.put("RefName",jsonArrayScfo.getJSONObject(i).getString("RefName"));
                                map.put("Remarks",jsonArrayScfo.getJSONObject(i).getString("Remarks"));
                                map.put("UserName",jsonArrayScfo.getJSONObject(i).getString("UserName"));
                                map.put("AgentID",(jsonArrayScfo.getJSONObject(i).getString("AgentID")==null || jsonArrayScfo.getJSONObject(i).getString("AgentID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentID"));
                                map.put("AgentName",(jsonArrayScfo.getJSONObject(i).getString("AgentName")==null || jsonArrayScfo.getJSONObject(i).getString("AgentName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentName"));
                                map.put("City",jsonArrayScfo.getJSONObject(i).getString("City"));
                                map.put("State",jsonArrayScfo.getJSONObject(i).getString("State"));
                                map.put("Mobile",jsonArrayScfo.getJSONObject(i).getString("Mobile"));
                                map.put("FairName",(jsonArrayScfo.getJSONObject(i).getString("FairName")==null || jsonArrayScfo.getJSONObject(i).getString("FairName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("FairName"));
                                map.put("ItemCount",jsonArrayScfo.getJSONObject(i).getString("ItemCount"));
                                map.put("TotalBookQty",jsonArrayScfo.getJSONObject(i).getString("TBookQty"));
                                map.put("TotalAmount",jsonArrayScfo.getJSONObject(i).getString("TotalAmount"));
                                map.put("LastBookDate",(jsonArrayScfo.getJSONObject(i).getString("LastBookDate")==null || jsonArrayScfo.getJSONObject(i).getString("LastBookDate").equals("null"))? jsonArrayScfo.getJSONObject(i).getString("EntryDate") : jsonArrayScfo.getJSONObject(i).getString("LastBookDate"));
                                map.put("EmpCVName",jsonArrayScfo.getJSONObject(i).getString("EmpCVName"));
                                map.put("EmpCVType",jsonArrayScfo.getJSONObject(i).getString("EmpCVType"));
                                map.put("CreditDays",jsonArrayScfo.getJSONObject(i).getString("CreditDays"));
                                map.put("CreditLimit",jsonArrayScfo.getJSONObject(i).getString("CreditLimit"));
                                map.put("TotalDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalDueAmt"));
                                map.put("TotalOverDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalOverDueAmt"));
                                map.put("ExceedAmt",jsonArrayScfo.getJSONObject(i).getString("ExceedAmt"));
                                map.put("EntryDate",jsonArrayScfo.getJSONObject(i).getString("EntryDate"));
                                map.put("Email",jsonArrayScfo.getJSONObject(i).getString("Email"));
                                map.put("AccountNo",jsonArrayScfo.getJSONObject(i).getString("AccountNo"));
                                map.put("AccountHolderName",jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"));
                                map.put("IFSCCOde",jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"));
                                map.put("IDName",jsonArrayScfo.getJSONObject(i).getString("IDName"));
                                map.put("GSTIN",(jsonArrayScfo.getJSONObject(i).getString("GSTIN")==null || jsonArrayScfo.getJSONObject(i).getString("GSTIN").equals("null"))? "" : jsonArrayScfo.getJSONObject(i).getString("GSTIN"));
                                map.put("AvgOverDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgOverDueDays"));
                                map.put("AvgDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgDueDays"));
                                map.put("PricelistID",jsonArrayScfo.getJSONObject(i).getString("PricelistID"));
                                map.put("Label",(jsonArrayScfo.getJSONObject(i).optString("Extin1")==null || jsonArrayScfo.getJSONObject(i).optString("Extin1").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("Extin1"));
                                map.put("UnderName",(jsonArrayScfo.getJSONObject(i).optString("UnderName")==null || jsonArrayScfo.getJSONObject(i).optString("UnderName").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("UnderName"));
                                mapList.add(map);
                            }
                            DatabaseSqlLiteHandlerCloseOrderList DB= new DatabaseSqlLiteHandlerCloseOrderList(context);
                            DB.CloseOrderTableDelete();
                            DB.insertCloseOrderTable(mapList);
                            //adapter=new BookOrdersAdapter(context,list,0);
                            Map<String, List<CloseOrBookDataset>> events = toMap(loadEvents());

                            for (String date : events.keySet()) {
                                HeaderItem header = new HeaderItem(date);
                                items.add(header);
                                for (CloseOrBookDataset event : events.get(date)) {
                                    EventItem item = new EventItem(event);
                                    items.add(item);
                                }
                            }
                            adapter = new BookOrderAdapter(items,context,0);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                                    if (recyclerView.getAdapter().getItemViewType(position) == 1){
                                        EventItem eventItem = (EventItem)adapter.getItem(position);
                                        if (StaticValues.createFlag == 1) {
                                            AlertDialogMethod(eventItem.getEvent().getOrderID());
                                        }else{
                                            MessageDialog.MessageDialog(context,"Alert","You don't have update permission of this module");
                                        }
                                    }
                                    return true;
                                }
                            });
                            if (activityFlag == 1){
                                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                    @Override
                                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                        if (recyclerView.getAdapter().getItemViewType(position) == 1){
                                            EventItem eventItem = (EventItem)adapter.getItem(position);

                                            BookOrderAdapter.listMultiCustomer = new ArrayList<CloseOrBookDataset>();
                                            BookOrderAdapter.listMultiCustomer.add(eventItem.getEvent());
                                            StaticValues.MultiOrderSize = BookOrderAdapter.listMultiCustomer.size();

                                            Intent intent = new Intent(context, CatalogueActivity.class);
                                            SelectCustomerForOrderDataset dataset=null;
                                            intent.putExtra("ResultNewParty",dataset);
                                            intent.putExtra("ResultRunningParty",eventItem.getEvent());
                                            getActivity().setResult(RESULT_OK,intent);
                                            getActivity().finish();
                                        }

                                    }
                                });
                            }
                        } else {
                            if (flag == 1)
                            MessageDialog.MessageDialog(context,"",Msg);
                            adapter = new BookOrderAdapter(items,context,0);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
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
                    params.put("MasterType", MasterType);
                    params.put("UserID", UserID);
                    params.put("SessionID", SessionID);
                    params.put("MasterID", MasterID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("BranchID", BranchID);
                    params.put("CurDateFlag", "1");
                    Log.d(TAG,"Book order parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        private void CallVolleyDeleteOrder(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderDel", new Response.Listener<String>()
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
                            MessageDialogByIntent(context, "", Msg);
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
                    Log.d(TAG,"Order Delete parameters:"+params.toString());
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
        private void AlertDialogMethod(final String OrderID){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Are you sure. You want to delete this party Order");
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.removeFlag == 1) {
                                if (!OrderID.isEmpty())
                                    CallVolleyDeleteOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID);
                            }else {
                                MessageDialog.MessageDialog(context,"Permission denied","You don't have delete permission to delete this module.");
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
        @NonNull
        private List<CloseOrBookDataset> loadEvents() {

            List<CloseOrBookDataset> events = new ArrayList<>();
            DatabaseSqlLiteHandlerCloseOrderList db=new DatabaseSqlLiteHandlerCloseOrderList(context);
            events = db.getOrderList("","");
            return events;
        }
        @NonNull
        private Map<String, List<CloseOrBookDataset>> toMap(@NonNull List<CloseOrBookDataset> events) {
            Map<String, List<CloseOrBookDataset>> map = new TreeMap<>();
            for (CloseOrBookDataset event : events) {
                List<CloseOrBookDataset> value = map.get(event.getGodown());
                if (value == null) {
                    value = new ArrayList<>();
                    map.put(event.getGodown(), value);
                }
                value.add(event);
            }
            return map;
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
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
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
            CallApiMethod(0);
        }
        public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
            try {
                final Dialog dialog=new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cardview_message_box);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
                TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
                Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
                txtViewMessageTitle.setText(Title);
                txtViewMessage.setText(Mesaage);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //TODO: Activity finish
                        onResume();
                    }
                });
                dialog.show();
            }catch (Exception e){
                Log.e("TAG","MessageDialogException2"+e.toString());
            }
        }
    }
    //TODO: Single Customer fragment
    public static class MultiCustomerFragment extends Fragment{
        private static final String MULTI_CUSTOMER = "MultiCustomer";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private BookOrderAdapter adapter;
        private ProgressDialog progressDialog;
        @NonNull
        private List<ListItem> items = new ArrayList<>();
        private Button btnBookOrder;
        public static MultiCustomerFragment newInstance(String MultiCustomer) {
            MultiCustomerFragment fragment = new MultiCustomerFragment();
            Bundle args = new Bundle();
            args.putString(MULTI_CUSTOMER, MultiCustomer);
            fragment.setArguments(args);
            return fragment;
        }
        public MultiCustomerFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_swipe_refresh_layout, null);
            setHasOptionsMenu(true);
            StaticValues.PushOrderFlag = 0;
            this.context = getActivity();
            Initialization(rootView);
            btnBookOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StaticValues.CatalogueFlag = 0;
                if (BookOrderAdapter.listMultiCustomer.isEmpty()){
                    MessageDialog.MessageDialog(context,"","Please select atleast 1 Customer");
                    StaticValues.MultiOrderSize = BookOrderAdapter.listMultiCustomer.size();
                }else{
                    StaticValues.MultiOrderSize = BookOrderAdapter.listMultiCustomer.size();
                    Intent intent = new Intent(context, BarcodeSearchViewPagerActivity.class);
                    context.startActivity(intent);
                }
                }
            });
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            btnBookOrder = (Button) view .findViewById(R.id.button_book_order);
            btnBookOrder.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod(0);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        CallApiMethod(0);
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void CallApiMethod(final int flag){
            //TODO: Book Orders Request
            NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
                @Override
                public void networkReceived(String status) {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            CallVolleyBookOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15],flag);
                        }
                    } else {
                        MessageDialog messageDialog=new MessageDialog();
                        messageDialog.MessageDialog(context,"","",status);
                    }
                }
            });
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    CallVolleyBookOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15],flag);
                }
            } else {
                MessageDialog.MessageDialog(context,"",status);
            }
        }
        private void CallVolleyBookOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID,final String BranchID,final int flag){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ApprovedPartyList", new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("Response", response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                        String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                        items = new ArrayList<>();
                        if (Status == 1) {
                            JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                            List<Map<String,String>> mapList = new ArrayList<>();
                            for (int i=0; i< jsonArrayScfo.length(); i++){
                                //list.add(new CloseOrBookDataset(jsonArrayScfo.getJSONObject(i).getString("PartyID"),jsonArrayScfo.getJSONObject(i).getString("PartyName"),jsonArrayScfo.getJSONObject(i).getString("SubPartyID"),jsonArrayScfo.getJSONObject(i).getString("SubParty"),jsonArrayScfo.getJSONObject(i).getString("AgentID"),jsonArrayScfo.getJSONObject(i).getString("AgentName"),jsonArrayScfo.getJSONObject(i).getString("Mobile"),jsonArrayScfo.getJSONObject(i).getString("City"),jsonArrayScfo.getJSONObject(i).getString("State"),jsonArrayScfo.getJSONObject(i).getString("Address"),jsonArrayScfo.getJSONObject(i).getString("OrderNo"),jsonArrayScfo.getJSONObject(i).getString("OrderDate"),jsonArrayScfo.getJSONObject(i).getInt("SubPartyApplicable"),jsonArrayScfo.getJSONObject(i).getString("Godown"),jsonArrayScfo.getJSONObject(i).getString("UserName")));
                                Map<String,String> map = new HashMap<>();
                                map.put("OrderID",jsonArrayScfo.getJSONObject(i).getString("OrderID"));
                                map.put("OrderNo",jsonArrayScfo.getJSONObject(i).getString("OrderNo"));
                                map.put("OrderDate",jsonArrayScfo.getJSONObject(i).getString("OrderDate"));
                                map.put("Godown",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                                map.put("GodownID",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                                map.put("PartyID",jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                                map.put("PartyName",jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                                map.put("SubPartyID",(jsonArrayScfo.getJSONObject(i).getString("SubPartyID")==null || jsonArrayScfo.getJSONObject(i).getString("SubPartyID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubPartyID"));
                                map.put("SubParty",(jsonArrayScfo.getJSONObject(i).getString("SubParty")==null || jsonArrayScfo.getJSONObject(i).getString("SubParty").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubParty"));
                                map.put("SubPartyApplicable",jsonArrayScfo.getJSONObject(i).getString("SubPartyApplicable"));
                                map.put("RefName",jsonArrayScfo.getJSONObject(i).getString("RefName"));
                                map.put("Remarks",jsonArrayScfo.getJSONObject(i).getString("Remarks"));
                                map.put("UserName",jsonArrayScfo.getJSONObject(i).getString("UserName"));
                                map.put("AgentID",(jsonArrayScfo.getJSONObject(i).getString("AgentID")==null || jsonArrayScfo.getJSONObject(i).getString("AgentID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentID"));
                                map.put("AgentName",(jsonArrayScfo.getJSONObject(i).getString("AgentName")==null || jsonArrayScfo.getJSONObject(i).getString("AgentName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentName"));
                                map.put("City",jsonArrayScfo.getJSONObject(i).getString("City"));
                                map.put("State",jsonArrayScfo.getJSONObject(i).getString("State"));
                                map.put("Mobile",jsonArrayScfo.getJSONObject(i).getString("Mobile"));
                                map.put("FairName",(jsonArrayScfo.getJSONObject(i).getString("FairName")==null || jsonArrayScfo.getJSONObject(i).getString("FairName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("FairName"));
                                map.put("ItemCount",jsonArrayScfo.getJSONObject(i).getString("ItemCount"));
                                map.put("TotalBookQty",jsonArrayScfo.getJSONObject(i).getString("TBookQty"));
                                map.put("TotalAmount",jsonArrayScfo.getJSONObject(i).getString("TotalAmount"));
                                map.put("LastBookDate",(jsonArrayScfo.getJSONObject(i).getString("LastBookDate")==null || jsonArrayScfo.getJSONObject(i).getString("LastBookDate").equals("null"))? jsonArrayScfo.getJSONObject(i).getString("EntryDate") : jsonArrayScfo.getJSONObject(i).getString("LastBookDate"));
                                map.put("EmpCVName",jsonArrayScfo.getJSONObject(i).getString("EmpCVName"));
                                map.put("EmpCVType",jsonArrayScfo.getJSONObject(i).getString("EmpCVType"));
                                map.put("CreditDays",jsonArrayScfo.getJSONObject(i).getString("CreditDays"));
                                map.put("CreditLimit",jsonArrayScfo.getJSONObject(i).getString("CreditLimit"));
                                map.put("TotalDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalDueAmt"));
                                map.put("TotalOverDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalOverDueAmt"));
                                map.put("ExceedAmt",jsonArrayScfo.getJSONObject(i).getString("ExceedAmt"));
                                map.put("EntryDate",jsonArrayScfo.getJSONObject(i).getString("EntryDate"));
                                map.put("Email",jsonArrayScfo.getJSONObject(i).getString("Email"));
                                map.put("AccountNo",jsonArrayScfo.getJSONObject(i).getString("AccountNo"));
                                map.put("AccountHolderName",jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"));
                                map.put("IFSCCOde",jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"));
                                map.put("IDName",jsonArrayScfo.getJSONObject(i).getString("IDName"));
                                map.put("GSTIN",jsonArrayScfo.getJSONObject(i).getString("GSTIN"));
                                map.put("AvgOverDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgOverDueDays"));
                                map.put("AvgDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgDueDays"));
                                map.put("PricelistID",jsonArrayScfo.getJSONObject(i).getString("PricelistID"));
                                map.put("Label",(jsonArrayScfo.getJSONObject(i).optString("Extin1")==null || jsonArrayScfo.getJSONObject(i).optString("Extin1").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("Extin1"));
                                map.put("UnderName",(jsonArrayScfo.getJSONObject(i).optString("UnderName")==null || jsonArrayScfo.getJSONObject(i).optString("UnderName").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("UnderName"));
                                mapList.add(map);
                            }
                            DatabaseSqlLiteHandlerCloseOrderList DB= new DatabaseSqlLiteHandlerCloseOrderList(context);
                            DB.CloseOrderTableDelete();
                            DB.insertCloseOrderTable(mapList);
                            //adapter=new BookOrdersAdapter(context,list,0);
                            Map<String, List<CloseOrBookDataset>> events = toMap(loadEvents());

                            for (String date : events.keySet()) {
                                HeaderItem header = new HeaderItem(date);
                                items.add(header);
                                for (CloseOrBookDataset event : events.get(date)) {
                                    EventItem item = new EventItem(event);
                                    items.add(item);
                                }
                            }
                            adapter = new BookOrderAdapter(items,context,1);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                                    if (recyclerView.getAdapter().getItemViewType(position) == 1){
                                        EventItem eventItem = (EventItem)adapter.getItem(position);
                                        AlertDialogMethod(eventItem.getEvent().getOrderID());
                                    }
                                    return true;
                                }
                            });
                        } else {
                            if (flag == 1)
                            MessageDialog.MessageDialog(context,"",Msg);
                            adapter = new BookOrderAdapter(items,context,1);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
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
                    params.put("MasterType", MasterType);
                    params.put("UserID", UserID);
                    params.put("SessionID", SessionID);
                    params.put("MasterID", MasterID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("BranchID", BranchID);
                    params.put("CurDateFlag", "1");
                    Log.d(TAG,"Book order parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        private void CallVolleyDeleteOrder(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderDel", new Response.Listener<String>()
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
                            MessageDialogByIntent(context, "", Msg);
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
                    Log.d(TAG,"Order Delete parameters:"+params.toString());
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
        private void AlertDialogMethod(final String OrderID){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Are you sure. You want to delete this party Order");
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.removeFlag == 1) {
                                if (!OrderID.isEmpty())
                                    CallVolleyDeleteOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID);
                            }else {
                                MessageDialog.MessageDialog(context,"Permission denied","You don't have delete permission to delete this module.");
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
        @NonNull
        private List<CloseOrBookDataset> loadEvents() {

            List<CloseOrBookDataset> events = new ArrayList<>();
            DatabaseSqlLiteHandlerCloseOrderList db=new DatabaseSqlLiteHandlerCloseOrderList(context);
            events = db.getOrderList("","");
            return events;
        }
        @NonNull
        private Map<String, List<CloseOrBookDataset>> toMap(@NonNull List<CloseOrBookDataset> events) {
            Map<String, List<CloseOrBookDataset>> map = new TreeMap<>();
            for (CloseOrBookDataset event : events) {
                List<CloseOrBookDataset> value = map.get(event.getGodown());
                if (value == null) {
                    value = new ArrayList<>();
                    map.put(event.getGodown(), value);
                }
                value.add(event);
            }
            return map;
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
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
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
            CallApiMethod(0);
        }
        public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
            try {
                final Dialog dialog=new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cardview_message_box);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
                TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
                Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
                txtViewMessageTitle.setText(Title);
                txtViewMessage.setText(Mesaage);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //TODO: Activity finish
                        onResume();
                    }
                });
                dialog.show();
            }catch (Exception e){
                Log.e("TAG","MessageDialogException2"+e.toString());
            }
        }
    }
}
