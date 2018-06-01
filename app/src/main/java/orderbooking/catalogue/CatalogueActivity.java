package orderbooking.catalogue;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerAddToBox;
import DatabaseController.DatabaseSqlLiteHandlerAllGroups;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import DatabaseController.DatabaseSqliteRootHandler;
import orderbooking.StaticValues;
import orderbooking.catalogue.adapter.RecyclerGroupIconAdapter;
import orderbooking.catalogue.adapter.RecyclerPreFilterAdapter;
import orderbooking.catalogue.adapter.SuggestionAdapter;
import orderbooking.catalogue.addtobox.adapter.RecyclerBoxGroupAdapter;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxGroupDataset;
import orderbooking.catalogue.addtobox.dataset.ResponseBoxListDataset;
import orderbooking.catalogue.dataset.RecyclerGroupDataset;
import orderbooking.catalogue.responsedataset.ResponseAllGroupsDataset;
import orderbooking.catalogue.wishlist.GroupWishListActivity;
import orderbooking.catalogue.wishlist.adapter.RecyclerWishListGroupAdapter;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;
import orderbooking.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;
import stockcheck.StockCheckActivity;
import uploadimagesfiles.voucherdocupload.VoucherDocumentActivity;

/**
 * Created by rakes on 21-Feb-17.
 */

public class CatalogueActivity extends AppCompatActivity {
    private Context context;
    ActionBar actionBar;
    TabLayout tabLayout;
    ViewPager mViewPager;
    ProgressDialog progressDialog;
    static String MainGroupID,MainGroup;
    public static LayerDrawable iconWishlist,iconBox;
    private Dialog dialog;
    private final String[] SEARCH_SUGGESTION_CURSOR_COLUMNS = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_QUERY, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2};
    private SearchView mSearchView;
    private SuggestionAdapter mSearchSuggestionAdapter;
    DatabaseSqlLiteHandlerAllGroups DBHandler;
    public static int loginFlag=0;
    private static CloseOrBookDataset closeOrBookDataset;
    private static String TAG = StockCheckActivity.class.getSimpleName();
    private static String PartyID="",SubPartyID="",RefName="",PriceListID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.viewpager_design);
        loginFlag=0;
        DBHandler = new DatabaseSqlLiteHandlerAllGroups(getApplicationContext());
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = CatalogueActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        //TODO: Call Screen Size method
        ScreenSize();
        //TODO:
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
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                // Call Api by Party Id
                if (PartyID.isEmpty()){
                    CallApiMethod(StaticValues.ConstantValue, SubPartyID, RefName,PriceListID);
                }else {
                    CallApiMethod(PartyID, SubPartyID, RefName,PriceListID);
                }
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",""+e.toString());
        }
    }
    private void CallApiMethod(final String PartyID, final String SubPartyID, final String RefName,final String PriceListID){
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //TODO: CallRetrofit Item List
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(CatalogueActivity.this);
            if (str!=null && !CommanStatic.PriceListID.isEmpty() && !PartyID.isEmpty()) {
                CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,PartyID,SubPartyID,RefName);
            }
        }else{
            MessageDialog.MessageDialog(CatalogueActivity.this,"",status);
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //TODO: CallRetrofit Item List
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(CatalogueActivity.this);
                    if (str!=null && !CommanStatic.PriceListID.isEmpty() && !PartyID.isEmpty()) {
                        CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,PartyID,SubPartyID,RefName);
                    }
                }else{
                    MessageDialog.MessageDialog(CatalogueActivity.this,"",status);
                }
            }
        });
    }
    private void LoadViewPager(List<Map<String, String>> mapList){

        MainGroupPagerAdapter mPagerAdapter=new MainGroupPagerAdapter(getSupportFragmentManager(),mapList);
        mViewPager.setAdapter(mPagerAdapter);
        ItemListActivity.start=1;
        ItemListActivity.end=30;
        StaticValues.SearchQuery="";
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(StaticValues.CurrentPosition, true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                StaticValues.CurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        CallApiMethod(PartyID,SubPartyID,RefName,PriceListID);
        if (CatalogueActivity.iconBox!=null && CatalogueActivity.iconWishlist!=null) {
            actionBar.invalidateOptionsMenu();
            Utils.setBadgeCount(CatalogueActivity.this, CatalogueActivity.iconBox, StaticValues.totalBoxCount);
            Utils.setBadgeCount(CatalogueActivity.this, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
            Log.d(TAG, "OnResume:" + StaticValues.totalBoxCount);
        }
    }
    public static class FragmentMainGroup extends Fragment {
        private static final String MAIN_GROUP_ID = "MainGroupID";
        private static final String MAIN_GROUP_NAME = "MainGroupName";
        DatabaseSqlLiteHandlerAllGroups DBHandler;
        RecyclerView recyclerView;
        ProgressDialog progressDialog;
        public FragmentMainGroup() {}
        public static FragmentMainGroup newInstance(String  MainGroupID,String MainGroupName) {
            FragmentMainGroup fragment = new FragmentMainGroup();
            Bundle args = new Bundle();
            args.putString(MAIN_GROUP_ID, MainGroupID);
            args.putString(MAIN_GROUP_NAME, MainGroupName);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview, container, false);
            recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view);
            MainGroupID=getArguments().getString(MAIN_GROUP_ID);
            MainGroup=getArguments().getString(MAIN_GROUP_NAME);
            String MainGroupID=(getArguments().getString(MAIN_GROUP_ID)==null)?"null":"'"+getArguments().getString(MAIN_GROUP_ID)+"'";
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            DBHandler=new DatabaseSqlLiteHandlerAllGroups(getActivity());
            final List<RecyclerGroupDataset> datasetList=DBHandler.getGroup(MainGroupID);
            RecyclerGroupIconAdapter adapter=new RecyclerGroupIconAdapter(getActivity(),datasetList);
            recyclerView.setAdapter(adapter);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),3);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    RecyclerGroupDataset dataset = (RecyclerGroupDataset) datasetList.get(position);
                    if (closeOrBookDataset!=null && dataset!=null) {
                        if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            CallVolleyPreFilter(CommanStatic.DeviceID, CommanStatic.DevicePassword, dataset.getGroupID());
                        }
                    }else {
                            MessageDialog.MessageDialog(getActivity(), "", "Please add Party, Sub Party and Reference Name");
                    }
                }
            });
            return rootView;
        }
        private void CallVolleyPreFilter(final String AKey, final String pwd, final String GroupID){
            showDialog();
            //String Link = "http://singlaapparels.com/Test_B2BApi/";  //CommanStatic.URL
            StringRequest postRequest = new StringRequest(Request.Method.POST, CommanStatic.BaseUrl+"GroupCatalogue", new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response);
                    List<Map<String,String>> mapList = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                        String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                        if(Status==1){
                            Map<String,String> map = new HashMap<>();
                            map.put("Name","ALL");
                            map.put("GroupID",""+GroupID);
                            map.put("ID","");
                            map.put("Sequence","");
                            map.put("Attr1","");
                            map.put("Attr2","");
                            map.put("Attr3","");
                            map.put("Attr4","");
                            map.put("Attr5","");
                            map.put("Attr6","");
                            map.put("Attr7","");
                            map.put("Attr8","");
                            map.put("Attr9","");
                            map.put("Attr10","");
                            map.put("Color","");
                            map.put("Size","");
                            map.put("Description","");
                            mapList.add(map);
                            JSONArray jsonResult = new JSONArray(jsonObject.getString("Result"));
                            if (jsonResult.length() > 0) {
                                for (int i = 0; i < jsonResult.length(); i++) {
                                    map = new HashMap<>();
                                    map.put("ID",jsonResult.getJSONObject(i).optString("ID")==null ? "" : jsonResult.getJSONObject(i).optString("ID"));
                                    map.put("GroupID",jsonResult.getJSONObject(i).optString("GroupID")==null ? "" : jsonResult.getJSONObject(i).optString("GroupID"));
                                    map.put("Sequence",jsonResult.getJSONObject(i).optInt("Sequence")+"");
                                    map.put("Attr1",jsonResult.getJSONObject(i).optString("Attr1")==null ? "" : jsonResult.getJSONObject(i).optString("Attr1"));
                                    map.put("Attr2",jsonResult.getJSONObject(i).optString("Attr2")==null ? "" : jsonResult.getJSONObject(i).optString("Attr2"));
                                    map.put("Attr3",jsonResult.getJSONObject(i).optString("Attr3")==null ? "" : jsonResult.getJSONObject(i).optString("Attr3"));
                                    map.put("Attr4",jsonResult.getJSONObject(i).optString("Attr4")==null ? "" : jsonResult.getJSONObject(i).optString("Attr4"));
                                    map.put("Attr5",jsonResult.getJSONObject(i).optString("Attr5")==null ? "" : jsonResult.getJSONObject(i).optString("Attr5"));
                                    map.put("Attr6",jsonResult.getJSONObject(i).optString("Attr6")==null ? "" : jsonResult.getJSONObject(i).optString("Attr6"));
                                    map.put("Attr7",jsonResult.getJSONObject(i).optString("Attr7")==null ? "" : jsonResult.getJSONObject(i).optString("Attr7"));
                                    map.put("Attr8",jsonResult.getJSONObject(i).optString("Attr8")==null ? "" : jsonResult.getJSONObject(i).optString("Attr8"));
                                    map.put("Attr9",jsonResult.getJSONObject(i).optString("Attr9")==null ? "" : jsonResult.getJSONObject(i).optString("Attr9"));
                                    map.put("Attr10",jsonResult.getJSONObject(i).optString("Attr10")==null ? "" : jsonResult.getJSONObject(i).optString("Attr10"));
                                    map.put("Color",jsonResult.getJSONObject(i).optString("Color")==null ? "" : jsonResult.getJSONObject(i).optString("Color"));
                                    map.put("Size",jsonResult.getJSONObject(i).optString("Size")==null ? "" : jsonResult.getJSONObject(i).optString("Size"));
                                    map.put("Name",jsonResult.getJSONObject(i).optString("Name")==null ? "" : jsonResult.getJSONObject(i).optString("Name"));
                                    map.put("Description",jsonResult.getJSONObject(i).optString("Description")==null ? "" : jsonResult.getJSONObject(i).optString("Description"));
                                    mapList.add(map);
                                }
                                //TODO :Load Filter
                                DialogFilter(mapList);
                            }
                        }else{
                            //TODO : Intent
                            Intent intent = new Intent(getActivity(), ItemListActivity.class);
                            intent.putExtra("GroupID",GroupID);
                            intent.putExtra("PartyDetails", closeOrBookDataset);
                            getActivity().startActivity(intent);
                            //getActivity().finish();
                        }
                    }catch (Exception e){
                        MessageDialog.MessageDialog(getActivity(),"","Exception:"+e.toString());
                    }
                    hideDialog();
                }
            }, new Response.ErrorListener()	{
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.e("Error.Response", "VolleyError: "+error);
                    //TODO : Intent
                    Intent intent = new Intent(getActivity(), ItemListActivity.class);
                    intent.putExtra("GroupID",GroupID);
                    intent.putExtra("PartyDetails", closeOrBookDataset);
                    getActivity().startActivity(intent);
                    //getActivity().finish();
                    hideDialog();
                }
            } ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("AKey", AKey);
                    params.put("pwd", pwd);
                    params.put("GroupID", GroupID);
                    Log.d(TAG,"Group Cataglogue Parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        private void DialogFilter(List<Map<String,String>> preFilterList){
            final Dialog dialog=new Dialog(new ContextThemeWrapper(getActivity(), R.style.FullHeightDialog));
            dialog.setContentView(R.layout.dialog_recycler_view);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            RecyclerView recyclerView1 = (RecyclerView) dialog.findViewById(R.id.recycler_view);
            final RecyclerPreFilterAdapter mAdapter = new RecyclerPreFilterAdapter(getActivity(),preFilterList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView1.setAdapter(mAdapter);
            recyclerView1.setLayoutManager(layoutManager);
            ItemClickSupport.addTo(recyclerView1).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Map<String,String> dataset = (Map<String,String>) mAdapter.getItem(position);
                    if (dataset!=null){
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), ItemListActivity.class);
                        intent.putExtra("GroupID",dataset.get("GroupID"));
                        intent.putExtra("PreFilter",(Serializable) dataset);
                        intent.putExtra("PartyDetails", closeOrBookDataset);
                        getActivity().startActivity(intent);
                        //getActivity().finish();
                    }
                }
            });
        }
        private void showDialog() {
            if (progressDialog!=null)
                progressDialog.show();
        }
        private void hideDialog() {
            if (progressDialog!=null)
                progressDialog.dismiss();
        }
    }
    public class MainGroupPagerAdapter extends FragmentPagerAdapter {

        List<Map<String,String>> mapList;
        public MainGroupPagerAdapter(FragmentManager fm,List<Map<String,String>> mapList) {
            super(fm);
            this.mapList=mapList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return FragmentMainGroup.newInstance(mapList.get(position).get("MainGroupID"),mapList.get(position).get("MainGroupName"));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mapList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mapList.get(position).get("MainGroupName");
        }
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
    //TODO: CallRetrofitAllGroupList
    public void CallVolleyAllGroup(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID, final String BranchID,final String PriceListID,final String PartyID,final String SubPartyID,final String RefName){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemCategory", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    if (Status == 1){
                        DatabaseSqlLiteHandlerAllGroups DBHandler = new DatabaseSqlLiteHandlerAllGroups(CatalogueActivity.this);
                        DBHandler.GroupsTableDelete();
                        DBHandler.SizeSetTableDelete();
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        //Log.d(TAG,"Result:"+jsonArrayResult.toString());
                        List<Map<String,String>> datasetList = new ArrayList<>();
                        for (int i=0; i<jsonArrayResult.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("MainGroupID",jsonArrayResult.getJSONObject(i).getString("MainGroupID"));
                            map.put("MainGroup",jsonArrayResult.getJSONObject(i).getString("MainGroup"));
                            map.put("Sequence",jsonArrayResult.getJSONObject(i).getString("Sequence"));
                            map.put("GroupImage",jsonArrayResult.getJSONObject(i).getString("GroupImage"));
                            map.put("GroupName",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                            map.put("GroupID",jsonArrayResult.getJSONObject(i).getString("GroupID"));
                            datasetList.add(map);

                            if (jsonArrayResult.getJSONObject(i).getString("SizeSet")!=null) {
                                List<Map<String, String>> mapListSizeSet = new ArrayList<>();
                                JSONArray jsonArraySizeSet = new JSONArray(jsonArrayResult.getJSONObject(i).getString("SizeSet"));
                                for (int j = 0; j < jsonArraySizeSet.length(); j++) {
                                    map = new HashMap<>();
                                    map.put("MainGroupID", jsonArrayResult.getJSONObject(i).getString("MainGroupID"));
                                    map.put("GroupID", jsonArrayResult.getJSONObject(i).getString("GroupID"));
                                    map.put("SizeCount", jsonArraySizeSet.getJSONObject(j).getString("SizeCount"));
                                    map.put("Required", jsonArraySizeSet.getJSONObject(j).getString("Required"));
                                    mapListSizeSet.add(map);
                                }
                                DBHandler.insertSizeSetTables(mapListSizeSet);
                            }
                        }
                        DBHandler.insertGroupsTables(datasetList);
                        LoadViewPager(DBHandler.getMainGroup());
                        hidepDialog();
                    }else{
                        MessageDialog.MessageDialog(CatalogueActivity.this,"",""+Msg);
                    }

                }catch (Exception e){
                    MessageDialog.MessageDialog(CatalogueActivity.this,"Exception re",""+e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(CatalogueActivity.this,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                params.put("BranchID", BranchID);
                params.put("PriceListID", PriceListID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                Log.d(TAG,"Item Category Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                if (CommanStatic.RelaunchAutoModule == 0) {
                    finish();
                    closeOrBookDataset = null;
                    BookOrderAdapter.listMultiCustomer = new ArrayList<>();
                }else if(CommanStatic.RelaunchAutoModule == 1){
                    Intent intent = getIntent();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                    closeOrBookDataset = null;
                    BookOrderAdapter.listMultiCustomer = new ArrayList<>();
                }
                break;
            case R.id.action_search:
                break;
            case R.id.action_wishlist:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    //StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, GroupWishListActivity.class);
                    intent.putExtra("Key",closeOrBookDataset);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select Running Customer first");
                }
                break;
            case R.id.action_box:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, OrderViewOrPushActivity.class);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select Running Customer first");
                }
                //CustomDialogBox(1,"My Box", 1);
                break;
            case R.id.action_account:
                DialogLoginCatalogue(CatalogueActivity.this,closeOrBookDataset);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (CommanStatic.RelaunchAutoModule == 0) {
                closeOrBookDataset = null;
                BookOrderAdapter.listMultiCustomer = new ArrayList<>();
                finish();
            }else if(CommanStatic.RelaunchAutoModule == 1){
                Intent intent = getIntent();
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                closeOrBookDataset = null;
                BookOrderAdapter.listMultiCustomer = new ArrayList<>();
            }
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            closeOrBookDataset = null;
            BookOrderAdapter.listMultiCustomer = new ArrayList<>();
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem itemGrid = menu.findItem(R.id.action_single_view);
        itemGrid.setVisible(false);

        MenuItem box = menu.findItem(R.id.action_box);
        iconBox = (LayerDrawable) box.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconBox, StaticValues.totalBoxCount);

        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        iconWishlist = (LayerDrawable) wishlist.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconWishlist, StaticValues.totalWishlistCount);
        final MenuItem searchItem = menu.findItem(R.id.action_search);//searchItem.setVisible(false);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        MenuItemCompat.setActionView(searchItem, searchView);
        // search view
        //mSearchView = new SearchView(getSupportActionBar().getThemedContext());
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        setupSearchView(mSearchView);

        // search menu item
        //MenuItem searchMenuItem = menu.add(Menu.NONE, Menu.NONE, 1, R.string.menu_activity_example_search_hint);
        //searchMenuItem.setIcon(android.R.drawable.ic_menu_search);
        MenuItemCompat.setActionView(searchItem, mSearchView);
        //MenuItemCompat.setShowAsAction(searchItem, MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return true;
    }
    private void setupSearchView(SearchView searchView) {
        // search hint
        searchView.setQueryHint(getString(R.string.action_search));

        // text color
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(this, R.color.White));

        // suggestion listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // TODO: Suggestion click to search Item List Activity page
                Intent intent=new Intent(CatalogueActivity.this,SearchedItemListActivity.class);
                intent.putExtra("MainGroupID",MainGroupID);
                intent.putExtra("GroupID","");
                intent.putExtra("SearchQuery",query);
                intent.putExtra("PartyDetails",closeOrBookDataset);
                startActivity(intent);
                //Toast.makeText(PartyInfoActivity.this, "search: " + query+" / "+MainGroupID, Toast.LENGTH_SHORT).show();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.length() > 2){
                    updateSearchSuggestion(query);
                }
                return true;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
        {
            @Override
            public boolean onSuggestionSelect(int position)
            {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position)
            {
                Cursor cursor = (Cursor) mSearchSuggestionAdapter.getItem(position);
                String query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY));
                String Group = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                String subtitle = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));

                String GroupID=DBHandler.getGroupID(Group);
                // TODO: Suggestion click to search Item List Activity page
                Intent intent=new Intent(CatalogueActivity.this,SearchedItemListActivity.class);
                intent.putExtra("MainGroupID",MainGroupID);
                intent.putExtra("GroupID",GroupID);
                intent.putExtra("SearchQuery",query);
                intent.putExtra("PartyDetails",closeOrBookDataset);
                startActivity(intent);
                //Toast.makeText(PartyInfoActivity.this, "suggestion: "+query+" / " + Group + " / " + subtitle, Toast.LENGTH_SHORT).show();


                return true;
            }
        });
    }
    private void updateSearchSuggestion(String query) {

        //TODO: Get data of Main group and group
        // TODO : cursor
        MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGESTION_CURSOR_COLUMNS);
        List<RecyclerGroupDataset> group = DBHandler.getGroup("'"+MainGroupID+"'");
        for(int j=0;j<group.size();j++) {
            cursor.addRow(new String[]{"" + j+"", query ," " + group.get(j).getGroupName(), "in " + MainGroup});
        }
        // searchview content
        if(mSearchSuggestionAdapter == null)
        {
            // create adapter
            mSearchSuggestionAdapter = new SuggestionAdapter(CatalogueActivity.this, cursor);

            // set adapter
            mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
        }
        else
        {
            // refill adapter
            mSearchSuggestionAdapter.refill(this, cursor);

            // set adapter
            mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
        }
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    public void ScreenSize() {
        double screenSize = 0;
        try {
            // Compute screen size
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidth  = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            screenSize = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
            Log.e(TAG,"Devicee Screen Size Width:"+screenWidth+"\tHeight:"+screenHeight);
            if(screenSize>=7) {
                //TODO: Tablet
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
            else if(screenSize<7 && screenSize>=5.5){
                //TODO: Contents
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
            else {
                //TODO: Contents
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
        } catch(Throwable t) {
            Log.e(TAG,"Exception:"+t.toString());
        }
    }
    private void DialogLoginCatalogue(final Context context,CloseOrBookDataset closeOrBookDataset){
        dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_catalogue_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final LinearLayout linearLayoutSignIn = (LinearLayout)  dialog.findViewById(R.id.Linear_SignIn);
        final LinearLayout linearLayoutSignOut = (LinearLayout)  dialog.findViewById(R.id.Linear_SignOut);
        TextView textViewUserName = (TextView) dialog.findViewById(R.id.text_UserName);
        TextView textViewNewCustomer = (TextView) dialog.findViewById(R.id.text_new_customer);
        TextView textViewRunningCustomer = (TextView) dialog.findViewById(R.id.text_running_customer);
//        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
//        CommonSearchableSpinner spnRunningCustomer = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_running_customer);
        final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        if (closeOrBookDataset!=null) {
            textViewUserName.setText(""+closeOrBookDataset.getPartyName());
            linearLayoutSignOut.setVisibility(View.VISIBLE);
            linearLayoutSignIn.setVisibility(View.GONE);
            btnApply.setText("Sign out");
            btnApply.setVisibility(View.VISIBLE);
        }else{
            linearLayoutSignOut.setVisibility(View.GONE);
            linearLayoutSignIn.setVisibility(View.VISIBLE);
            btnApply.setText("Sign in");
            btnApply.setVisibility(View.GONE);
        }
//        if(loginFlag == 0){
//            linearLayoutSignOut.setVisibility(View.GONE);
//            linearLayoutSignIn.setVisibility(View.VISIBLE);
//            btnApply.setText("Sign in");
//            btnApply.setVisibility(View.GONE);
//        }else if(loginFlag == 1){
//            linearLayoutSignOut.setVisibility(View.VISIBLE);
//            linearLayoutSignIn.setVisibility(View.GONE);
//            btnApply.setText("Sign out");
//        }
        textViewNewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
                Map<String,String> map = DBHandler.getModulePermissionByVtype(1);
                if (!map.isEmpty() || map!=null) {
                    try {
                        //TODO: Set Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("Title", (map.get("Name") == null ? getResources().getString(R.string.app_name) : map.get("Name")));
                        bundle.putInt("ViewFlag", (map.get("ViewFlag") == null ? 0 : Integer.valueOf(map.get("ViewFlag"))));
                        bundle.putInt("EditFlag", (map.get("EditFlag") == null ? 0 : Integer.valueOf(map.get("EditFlag"))));
                        bundle.putInt("CreateFlag", (map.get("CreateFlag") == null ? 0 : Integer.valueOf(map.get("CreateFlag"))));
                        bundle.putInt("RemoveFlag", (map.get("RemoveFlag") == null ? 0 : Integer.valueOf(map.get("RemoveFlag"))));
                        bundle.putInt("PrintFlag", (map.get("PrintFlag") == null ? 0 : Integer.valueOf(map.get("PrintFlag"))));
                        bundle.putInt("ImportFlag", (map.get("ImportFlag") == null ? 0 : Integer.valueOf(map.get("ImportFlag"))));
                        bundle.putInt("ExportFlag", (map.get("ExportFlag") == null ? 0 : Integer.valueOf(map.get("ExportFlag"))));
                        bundle.putInt("Vtype", (map.get("Vtype") == null ? 0 : Integer.valueOf(map.get("Vtype"))));
                        //bundle.putInt("ActivityFlag",1);
                        String ClassName = (map.get("ContentClass")==null ? "" : map.get("ContentClass"));
                        if (!ClassName.isEmpty() && ClassName != null) {
                            //TODO: Intent the Activities by Class
                            Intent intent = new Intent(context, Class.forName(ClassName));
                            intent.putExtra("PermissionBundle", bundle);
                            startActivityForResult(intent, 100);
//                        linearLayoutSignOut.setVisibility(View.VISIBLE);
//                        linearLayoutSignIn.setVisibility(View.GONE);
                            dialog.dismiss();
                        } else {
                            MessageDialog.MessageDialog(context, "Permission", "You don't have permission of this module");
                        }
                    } catch (Exception e) {
                        MessageDialog.MessageDialog(context, "Permission Error", ""+e.toString());
                    }
                }else{
                    MessageDialog.MessageDialog(context, "Permission", "You don't have permission of this module");
                }
            }
        });
        textViewRunningCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
                Map<String,String> map = DBHandler.getModulePermissionByVtype(2);
                if (!map.isEmpty() || map!=null) {
                    try {
                        //TODO: Set Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("Title", (map.get("Name") == null ? getResources().getString(R.string.app_name) : map.get("Name")));
                        bundle.putInt("ViewFlag", (map.get("ViewFlag") == null ? 0 : Integer.valueOf(map.get("ViewFlag"))));
                        bundle.putInt("EditFlag", (map.get("EditFlag") == null ? 0 : Integer.valueOf(map.get("EditFlag"))));
                        bundle.putInt("CreateFlag", (map.get("CreateFlag") == null ? 0 : Integer.valueOf(map.get("CreateFlag"))));
                        bundle.putInt("RemoveFlag", (map.get("RemoveFlag") == null ? 0 : Integer.valueOf(map.get("RemoveFlag"))));
                        bundle.putInt("PrintFlag", (map.get("PrintFlag") == null ? 0 : Integer.valueOf(map.get("PrintFlag"))));
                        bundle.putInt("ImportFlag", (map.get("ImportFlag") == null ? 0 : Integer.valueOf(map.get("ImportFlag"))));
                        bundle.putInt("ExportFlag", (map.get("ExportFlag") == null ? 0 : Integer.valueOf(map.get("ExportFlag"))));
                        bundle.putInt("Vtype", (map.get("Vtype") == null ? 0 : Integer.valueOf(map.get("Vtype"))));
                        bundle.putInt("ActivityFlag", 1);
                        String ClassName = (map.get("ContentClass")==null ? "" : map.get("ContentClass"));
                        if (!ClassName.isEmpty() && ClassName != null) {
                            //TODO: Intent the Activities by Class
                            Intent intent = new Intent(context, Class.forName(ClassName));
                            intent.putExtra("PermissionBundle", bundle);
                            startActivityForResult(intent, 100);
//                        linearLayoutSignOut.setVisibility(View.VISIBLE);
//                        linearLayoutSignIn.setVisibility(View.GONE);
                            dialog.dismiss();
                        } else {
                            MessageDialog.MessageDialog(context, "Permission", "You don't have permission of this module");
                        }
                    } catch (Exception e) {
                        MessageDialog.MessageDialog(context, "Error", e.toString());
                    }
                }else{
                    MessageDialog.MessageDialog(context, "Permission", "You don't have permission of this module");
                }
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnApply.getText().equals("Sign out")){
                    linearLayoutSignOut.setVisibility(View.GONE);
                    linearLayoutSignIn.setVisibility(View.VISIBLE);
                    btnApply.setText("Sign in");
                    btnApply.setVisibility(View.GONE);
                    PartyID = "";
                    SubPartyID = "";
                    RefName = "";
                    BookOrderAdapter.listMultiCustomer = new ArrayList<CloseOrBookDataset>();
                    CatalogueActivity.closeOrBookDataset = null;
                    CallApiMethod(PartyID,SubPartyID,RefName,PriceListID);
                    actionBar.setTitle(getResources().getString(R.string.app_name));
                    dialog.dismiss();
                    //MessageDialog.MessageDialog(context,"","Logout successfully");
                    startActivity(getIntent());
                    finish();
                }
//               if (loginFlag==0) {
//                   loginFlag = 1;
//                   linearLayoutSignOut.setVisibility(View.VISIBLE);
//                   linearLayoutSignIn.setVisibility(View.GONE);
//                   btnApply.setText("Sign out");
//               }else if (loginFlag==1){
//                   loginFlag = 0;
//                   linearLayoutSignOut.setVisibility(View.GONE);
//                   linearLayoutSignIn.setVisibility(View.VISIBLE);
//                   btnApply.setText("Sign in");
//               }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                //SelectCustomerForOrderDataset dataset = (SelectCustomerForOrderDataset)data.getExtras().get("ResultNewParty");
                //MainGroupOrGroupActivity.selectCustomerForOrderDataset = dataset;
                CloseOrBookDataset closeOrBookDataset = (CloseOrBookDataset)data.getExtras().get("ResultRunningParty");
                CatalogueActivity.closeOrBookDataset = closeOrBookDataset;
                if (closeOrBookDataset!=null){
                    //TODO: Running Customer
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        //TODO: CallRetrofit Item List
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(CatalogueActivity.this);
                        if (str!=null && !CommanStatic.PriceListID.isEmpty() && !closeOrBookDataset.getPartyID().isEmpty()) {
                            actionBar.setTitle(closeOrBookDataset.getPartyName());
                            PartyID = closeOrBookDataset.getPartyID();
                            PriceListID = closeOrBookDataset.getPricelistID();
                            //SubPartyID = dataset.getPartyID();
                            //RefName = dataset.getPartyID();
                            CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,closeOrBookDataset.getPartyID(),"","");
                        }else{
                            MessageDialog.MessageDialog(CatalogueActivity.this,"",""+closeOrBookDataset.getPartyName());
                        }
                    }else{
                        MessageDialog.MessageDialog(CatalogueActivity.this,"",status);
                    }
                }else {

                    MessageDialog.MessageDialog(context,"Activity result","dataset is null");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}

