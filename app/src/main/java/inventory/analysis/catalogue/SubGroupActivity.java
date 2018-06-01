package inventory.analysis.catalogue;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerAllGroups;
import inventory.analysis.catalogue.adapter.RecyclerGroupIconAdapter;
import inventory.analysis.catalogue.adapter.SuggestionAdapter;
import inventory.analysis.catalogue.dataset.RecyclerGroupDataset;
import inventory.analysis.catalogue.wishlist.GroupWishListActivity;
import orderbooking.StaticValues;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import stockcheck.StockCheckActivity;

/**
 * Created by rakes on 08-Sep-17.
 */

public class SubGroupActivity extends AppCompatActivity {
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button btnAllSubGroup;
    ActionBar actionBar;
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
    private static String GroupID = "",Group = "",PartyID="",SubPartyID="",RefName="",PriceListID="",StockCheckFlag = "",ItemCheckFlag = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        loginFlag=0;
        DBHandler = new DatabaseSqlLiteHandlerAllGroups(getApplicationContext());
        Initialization();
        GetIntent();
    }
    private void Initialization(){
        this.context = SubGroupActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        btnAllSubGroup = (Button) findViewById(R.id.button_book_order);
        btnAllSubGroup.setVisibility(View.VISIBLE);
        btnAllSubGroup.setText("All Sub Group");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void GetIntent(){
        try{
            GroupID = getIntent().getExtras().getString("GroupID","");
            Group = getIntent().getExtras().getString("Group","");
            CloseOrBookDataset closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("PartyDetails");
            actionBar.setTitle(""+Group);
            if (closeOrBookDataset!=null) {
                if (!closeOrBookDataset.getSubParty().isEmpty() && closeOrBookDataset.getSubParty() != null) {
                    actionBar.setSubtitle(closeOrBookDataset.getSubParty() + "(" + closeOrBookDataset.getPartyName() + ")");
                } else if (!closeOrBookDataset.getRefName().isEmpty() && closeOrBookDataset.getRefName() != null) {
                    actionBar.setSubtitle(closeOrBookDataset.getRefName() + "(" + closeOrBookDataset.getPartyName() + ")");
                } else {
                    actionBar.setSubtitle(closeOrBookDataset.getPartyName());
                }
            }
            LoadRecyclerView(DBHandler.getSubGroupList(GroupID));
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Intent Exception",""+e.toString());
        }
    }
    private void CallApiMethod(final String PartyID, final String SubPartyID, final String RefName,final String PriceListID){
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //TODO: CallRetrofit Item List
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(SubGroupActivity.this);
            if (str!=null) {
                CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,PartyID,SubPartyID,RefName,StockCheckFlag,ItemCheckFlag);
            }
        }else{
            MessageDialog.MessageDialog(SubGroupActivity.this,"",status);
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //TODO: CallRetrofit Item List
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(SubGroupActivity.this);
                    if (str!=null) {
                        CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,PartyID,SubPartyID,RefName,StockCheckFlag,ItemCheckFlag);
                    }
                }else{
                    MessageDialog.MessageDialog(SubGroupActivity.this,"",status);
                }
            }
        });
    }
    private void LoadRecyclerView(final List<RecyclerGroupDataset> datasetList){
        ItemListActivity.start=1;
        ItemListActivity.end=30;
        RecyclerGroupIconAdapter adapter=new RecyclerGroupIconAdapter(context,datasetList);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                RecyclerGroupDataset dataset = (RecyclerGroupDataset) datasetList.get(position);
                ItemListActivity.start=1;
                ItemListActivity.end=30;
                if (closeOrBookDataset!=null || dataset!=null) {
                    Intent intent = new Intent(context, ItemListActivity.class);
                    intent.putExtra("GroupID", GroupID);
                    intent.putExtra("Group", Group);
                    intent.putExtra("SubGroupDataset", dataset);
                    intent.putExtra("PartyDetails", closeOrBookDataset);
                    startActivity(intent);
                }else {
                    MessageDialog.MessageDialog(context,"","Please add Party, Sub Party and Reference Name");
                }
            }
        });
        btnAllSubGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemListActivity.start=1;
                ItemListActivity.end=30;
                if (closeOrBookDataset != null) {
                    Intent intent = new Intent(context, ItemListActivity.class);
                    intent.putExtra("GroupID", GroupID);
                    intent.putExtra("Group", Group);
                    intent.putExtra("PartyDetails", closeOrBookDataset);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ItemListActivity.class);
                    intent.putExtra("GroupID", GroupID);
                    intent.putExtra("Group", Group);
                    intent.putExtra("PartyDetails", closeOrBookDataset);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        actionBar.invalidateOptionsMenu();
    }
    @Override
    public void onPause(){
        super.onPause();
        actionBar.invalidateOptionsMenu();
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null && swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    //TODO: CallRetrofitAllGroupList
    public void CallVolleyAllGroup(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID, final String BranchID,final String PriceListID,final String PartyID,final String SubPartyID,final String RefName,final String StockCheckFlag,final String ItemCheckFlag){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"AllCategory", new Response.Listener<String>()
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
                        DatabaseSqlLiteHandlerAllGroups DBHandler = new DatabaseSqlLiteHandlerAllGroups(SubGroupActivity.this);
                        DBHandler.GroupsTableDelete();
                        DBHandler.SizeSetTableDelete();
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        //Log.d(TAG,"Result:"+jsonArrayResult.toString());
                        List<Map<String,String>> datasetList = new ArrayList<>();
                        for (int i=0; i<jsonArrayResult.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("ParentID",jsonArrayResult.getJSONObject(i).getString("ParentID"));
                            map.put("Parent",jsonArrayResult.getJSONObject(i).getString("Parent"));
                            map.put("ParentIcon",jsonArrayResult.getJSONObject(i).getString("ParentIcon"));
                            map.put("MainGroupID",jsonArrayResult.getJSONObject(i).getString("MainGroupID"));
                            map.put("MainGroup",jsonArrayResult.getJSONObject(i).getString("MainGroup"));
                            map.put("Sequence",(jsonArrayResult.getJSONObject(i).optString("Sequence")==null ? "0": jsonArrayResult.getJSONObject(i).optString("Sequence")));
                            map.put("GroupImage",jsonArrayResult.getJSONObject(i).getString("GroupImage"));
                            map.put("GroupName",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                            map.put("GroupID",jsonArrayResult.getJSONObject(i).getString("GroupID"));
                            map.put("SubGroupID",jsonArrayResult.getJSONObject(i).getString("SubGroupID"));
                            map.put("SubGroup",jsonArrayResult.getJSONObject(i).getString("SubGroup"));
                            map.put("SubGroupIcon",jsonArrayResult.getJSONObject(i).getString("SubGroupIcon"));
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
                        LoadRecyclerView(DBHandler.getParentList());
                        hidepDialog();
                    }else{
                        MessageDialog.MessageDialog(SubGroupActivity.this,"",""+Msg);
                    }

                }catch (Exception e){
                    MessageDialog.MessageDialog(SubGroupActivity.this,"Exception re",""+e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(SubGroupActivity.this,"Error",""+error.toString());
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
                params.put("StockCheckFlag", StockCheckFlag);
                params.put("ItemCheckFlag", ItemCheckFlag);
                Log.d(TAG,"All Category Parameters:"+params.toString());
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
                Intent intent = new Intent(context, GroupWishListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_box:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    intent = new Intent(context, OrderViewOrPushActivity.class);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select Running Customer first");
                }
                //CustomDialogBox(1,"My Box", 1);
                break;
            case R.id.action_account:
                DialogLoginCatalogue(SubGroupActivity.this,closeOrBookDataset);
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
        MenuItem itemAccount = menu.findItem(R.id.action_account);
        itemAccount.setVisible(false);
        MenuItem box = menu.findItem(R.id.action_box);
        iconBox = (LayerDrawable) box.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconBox, StaticValues.totalBoxCount);

        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        iconWishlist = (LayerDrawable) wishlist.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconWishlist, StaticValues.totalWishlistCount);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
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
                Intent intent=new Intent(SubGroupActivity.this,SearchedItemListActivity.class);
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
                Intent intent=new Intent(SubGroupActivity.this,SearchedItemListActivity.class);
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
            mSearchSuggestionAdapter = new SuggestionAdapter(SubGroupActivity.this, cursor);

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
                    SubGroupActivity.closeOrBookDataset = null;
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
                SubGroupActivity.closeOrBookDataset = closeOrBookDataset;
                if (closeOrBookDataset!=null){
                    //TODO: Running Customer
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        //TODO: CallRetrofit Item List
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(SubGroupActivity.this);
                        if (str!=null && !CommanStatic.PriceListID.isEmpty() && !closeOrBookDataset.getPartyID().isEmpty()) {
                            actionBar.setTitle(closeOrBookDataset.getPartyName());
                            PartyID = closeOrBookDataset.getPartyID();
                            PriceListID = closeOrBookDataset.getPricelistID();
                            //SubPartyID = dataset.getPartyID();
                            //RefName = dataset.getPartyID();
                            CallVolleyAllGroup(str[3], str[0], str[14], str[4], str[5], str[15],PriceListID,closeOrBookDataset.getPartyID(),"","",StockCheckFlag,ItemCheckFlag);
                        }else{
                            MessageDialog.MessageDialog(SubGroupActivity.this,"",""+closeOrBookDataset.getPartyName());
                        }
                    }else{
                        MessageDialog.MessageDialog(SubGroupActivity.this,"",status);
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

