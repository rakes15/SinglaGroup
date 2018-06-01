package orderbooking.view_order_details;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerFilter;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderViewDetails;
import orderbooking.StaticValues;
import orderbooking.customerlist.temp.EventItem;
import orderbooking.view_order_details.adapter.FilterCheckBoxListAdapter;
import orderbooking.view_order_details.adapter.FilterTabListAdapter;
import orderbooking.view_order_details.adapter.OrderViewItemByGroupAdapter;
import orderbooking.view_order_details.dataset.OrderViewGroupDataset;
import orderbooking.view_order_details.dataset.OrderViewItemByGroupDataset;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 05-Nov-16.
 */
public class OrderViewItemByGroupActivity extends AppCompatActivity{

    private ActionBar actionBar;
    private Context context;
    RecyclerView recyclerView;
    DatabaseSqlLiteHandlerFilter DBHandlerFilter;
    List<Map<String,String>> mapListCaption;
    ListView listViewTab,listViewCheckBox;
    String[] strFilter=new String[10];
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String OrderID = "";
    private int flag = 0, itemListFlag = 0, FilterFlag = 0;
    int MDApplicable = 0;
    int SubItemApplicable = 0;
    private OrderViewGroupDataset dataset;
    private DatabaseSqlLiteHandlerOrderViewDetails DBOrderView;
    private OrderViewItemByGroupAdapter adapter;
    private static String TAG = OrderViewItemByGroupActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Initialization();
        setActionBarHeader();
        CallApiMethod(dataset);
        //LoadRecyclerView(DBOrderView.getItemList(OrderID,dataset.getGroupID()),flag);
        //System.out.println("Data:"+DBOrderView.getItemList(OrderID,dataset.getGroupID()).get(0).getItemImage());
    }
    private void Initialization(){
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.context = OrderViewItemByGroupActivity.this;
        this.DBOrderView = new DatabaseSqlLiteHandlerOrderViewDetails(context);
        this.DBHandlerFilter = new DatabaseSqlLiteHandlerFilter(context);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallApiMethod(dataset);
                //LoadRecyclerView(DBOrderView.getItemList(OrderID,dataset.getGroupID()),flag);
            }
        });
    }
    private void setActionBarHeader(){
        try{
            dataset = (OrderViewGroupDataset) getIntent().getExtras().get("Key");
            actionBar.setTitle(dataset.getGroupName()+" ( "+dataset.getMainGroup()+" )");
            OrderID = dataset.getOrderID();
        }catch (Exception e){
            MessageDialog.MessageDialog(context, "Exception", "Intent:"+e.toString());
        }
    }
    private void CallApiMethod(final OrderViewGroupDataset dataset){
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyViewBookedOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID,dataset.getGroupID(),"");
                        //CallVolleyFilterCaptionWithSeq(str[3], str[4], str[0], str[5], str[14], str[15],dataset.getGroupID(),"");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyViewBookedOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID,dataset.getGroupID(),"");
                //CallVolleyFilterCaptionWithSeq(str[3], str[4], str[0], str[5], str[14], str[15],dataset.getGroupID(),"");
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(context,"","",status);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: Activity Intent to Parent Caption
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.Action_Search://TODO: Search
                break;
            case R.id.Save_Next://TODO: View like Single Or multi view
                DialogCustomViewSettings();
                break;
            case R.id.Report_View://TODO: Search Filter
                if (!mapListCaption.isEmpty()){
                    DialogFilter(mapListCaption);
                }else{
                    MessageDialog.MessageDialog(context,"","Swipe to refresh the filter");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TODO: Activity Finish
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        CallApiMethod(dataset);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_custom, menu);
        MenuItem View = menu.findItem(R.id.Save_Next);
        View.setIcon(getResources().getDrawable(R.drawable.ic_action_view_white));
        MenuItem Filter = menu.findItem(R.id.Report_View);
        Filter.setIcon(getResources().getDrawable(R.drawable.ic_action_filter_search));
        MenuItem fca = menu.findItem(R.id.Push_Fca);
        fca.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.Action_Search);
        searchItem.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
        return super.onCreateOptionsMenu(menu);
    }
    private void CallVolleyViewBookedOrder(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID,final String GroupID,final String SubGroupID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyTempOrderDetails", new Response.Listener<String>()
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
                        List<Map<String,String>> mapList = new ArrayList<>();
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int z = 0; z<jsonArrayResult.length(); z++) {
                            String OrderID = jsonArrayResult.getJSONObject(z).getString("OrderID");
                            JSONArray jsonArrayDetails = jsonArrayResult.getJSONObject(z).getJSONArray("Details");
                            for (int i = 0; i<jsonArrayDetails.length(); i++) {
                                Map<String,String> map = new HashMap<>();
                                MDApplicable = jsonArrayDetails.getJSONObject(i).getInt("MDApplicable");
                                SubItemApplicable = jsonArrayDetails.getJSONObject(i).getInt("SubItemApplicable");
                                map.put("OrderID", OrderID);
                                map.put("PartyID", jsonArrayDetails.getJSONObject(i).getString("PartyID"));
                                map.put("PartyName", jsonArrayDetails.getJSONObject(i).getString("PartyName"));
                                map.put("SubPartyID", (jsonArrayDetails.getJSONObject(i).getString("SubPartyID") == null || jsonArrayDetails.getJSONObject(i).getString("SubPartyID").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubPartyID"));
                                map.put("SubParty", (jsonArrayDetails.getJSONObject(i).getString("SubParty") == null || jsonArrayDetails.getJSONObject(i).getString("SubParty").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubParty"));
                                map.put("RefName", jsonArrayDetails.getJSONObject(i).getString("RefName"));
                                map.put("Remarks", jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                map.put("OrderDate", jsonArrayDetails.getJSONObject(i).getString("OrderDate"));
                                map.put("OrderNo", jsonArrayDetails.getJSONObject(i).getString("OrderNo"));
                                map.put("MainGroupID", jsonArrayDetails.getJSONObject(i).getString("MainGroupID"));
                                map.put("MainGroup", jsonArrayDetails.getJSONObject(i).getString("MainGroup"));
                                map.put("GroupID", jsonArrayDetails.getJSONObject(i).getString("GroupID"));
                                map.put("GroupName", jsonArrayDetails.getJSONObject(i).getString("GroupName"));
                                map.put("GroupImage", "");
                                map.put("SubGroupID", jsonArrayDetails.getJSONObject(i).getString("SubGroupID"));
                                map.put("SubGroup", jsonArrayDetails.getJSONObject(i).getString("SubGroup"));
                                map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                map.put("ItemName", jsonArrayDetails.getJSONObject(i).getString("ItemName"));
                                map.put("ItemCode", jsonArrayDetails.getJSONObject(i).getString("ItemCode"));
                                map.put("SubItemID", (jsonArrayDetails.getJSONObject(i).getString("SubItemID") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemID").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemID"));
                                map.put("SubItemName", (jsonArrayDetails.getJSONObject(i).getString("SubItemName") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemName").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemName"));
                                map.put("SubItemCode", (jsonArrayDetails.getJSONObject(i).getString("SubItemCode") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemCode").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemCode"));
                                map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                map.put("ColorFamilyID", jsonArrayDetails.getJSONObject(i).getString("ColorFamilyID"));
                                map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
                                map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
                                map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
                                map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
                                map.put("SizeName", jsonArrayDetails.getJSONObject(i).getString("SizeName"));
                                map.put("BookQty", jsonArrayDetails.getJSONObject(i).getString("bookQty"));
                                map.put("BookFrom", jsonArrayDetails.getJSONObject(i).getString("BookFrom"));
                                map.put("ExcepDelDt", jsonArrayDetails.getJSONObject(i).getString("ExcepDelDt"));
                                map.put("Rate", jsonArrayDetails.getJSONObject(i).getString("Rate"));
                                map.put("Mrp", jsonArrayDetails.getJSONObject(i).optString("MRP") == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"));
                                map.put("MDApplicable", jsonArrayDetails.getJSONObject(i).getString("MDApplicable"));
                                map.put("SubItemApplicable", jsonArrayDetails.getJSONObject(i).getString("SubItemApplicable"));
                                map.put("CreatedDate", jsonArrayDetails.getJSONObject(i).getString("CreatedDate"));
                                map.put("TBookedAmt", jsonArrayDetails.getJSONObject(i).getString("Amount"));

                                map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                map.put("Unit", jsonArrayDetails.getJSONObject(i).getString("Unit"));
                                map.put("UserName", jsonArrayDetails.getJSONObject(i).getString("UserName"));
                                map.put("UserFullName", jsonArrayDetails.getJSONObject(i).getString("UserFullName"));
                                map.put("EmpCVType", jsonArrayDetails.getJSONObject(i).getString("EmpCVType"));
                                map.put("EmpCVName", jsonArrayDetails.getJSONObject(i).getString("EmpCVName"));

                                map.put("AttribID1", (jsonArrayDetails.getJSONObject(i).getString("AttribID1") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID1").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID1"));
                                map.put("AttribID2", (jsonArrayDetails.getJSONObject(i).getString("AttribID2") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID2").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID2"));
                                map.put("AttribID3", (jsonArrayDetails.getJSONObject(i).getString("AttribID3") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID3").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID3"));
                                map.put("AttribID4", (jsonArrayDetails.getJSONObject(i).getString("AttribID4") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID4").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID4"));
                                map.put("AttribID5", (jsonArrayDetails.getJSONObject(i).getString("AttribID5") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID5").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID5"));
                                map.put("AttribID6", (jsonArrayDetails.getJSONObject(i).getString("AttribID6") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID6").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID6"));
                                map.put("AttribID7", (jsonArrayDetails.getJSONObject(i).getString("AttribID7") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID7").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID7"));
                                map.put("AttribID8", (jsonArrayDetails.getJSONObject(i).getString("AttribID8") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID8").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID8"));
                                map.put("AttribID9", (jsonArrayDetails.getJSONObject(i).getString("AttribID9") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID9").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID9"));
                                map.put("AttribID10", (jsonArrayDetails.getJSONObject(i).getString("AttribID10") == null || jsonArrayDetails.getJSONObject(i).getString("AttribID10").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribID10"));

                                map.put("AttribName1", (jsonArrayDetails.getJSONObject(i).getString("AttribName1") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName1").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName1"));
                                map.put("AttribName2", (jsonArrayDetails.getJSONObject(i).getString("AttribName2") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName2").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName2"));
                                map.put("AttribName3", (jsonArrayDetails.getJSONObject(i).getString("AttribName3") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName3").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName3"));
                                map.put("AttribName4", (jsonArrayDetails.getJSONObject(i).getString("AttribName4") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName4").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName4"));
                                map.put("AttribName5", (jsonArrayDetails.getJSONObject(i).getString("AttribName5") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName5").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName5"));
                                map.put("AttribName6", (jsonArrayDetails.getJSONObject(i).getString("AttribName6") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName6").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName6"));
                                map.put("AttribName7", (jsonArrayDetails.getJSONObject(i).getString("AttribName7") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName7").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName7"));
                                map.put("AttribName8", (jsonArrayDetails.getJSONObject(i).getString("AttribName8") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName8").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName8"));
                                map.put("AttribName9", (jsonArrayDetails.getJSONObject(i).getString("AttribName9") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName9").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName9"));
                                map.put("AttribName10", (jsonArrayDetails.getJSONObject(i).getString("AttribName10") == null || jsonArrayDetails.getJSONObject(i).getString("AttribName10").equals("null")) ? null : jsonArrayDetails.getJSONObject(i).getString("AttribName10"));
                                map.put("AttSequence", "0");//jsonArrayDetails.getJSONObject(i).getString("AttSequence"));
                                mapList.add(map);
                            }
                        }
                        context.deleteDatabase(DatabaseSqlLiteHandlerOrderViewDetails.DATABASE_NAME);
                        DatabaseSqlLiteHandlerOrderViewDetails DBOrderView = new DatabaseSqlLiteHandlerOrderViewDetails(context);
                        DBOrderView.deleteOrderDetails();
                        DBOrderView.insertOrderDetails(mapList);
                        if (itemListFlag == 0) {
                            LoadRecyclerView(DBOrderView.getItemList(OrderID, dataset.getGroupID(),strFilter), flag, itemListFlag);
                        }else if (itemListFlag == 1) {
                            LoadRecyclerView(DBOrderView.getItemListColorWise(OrderID,dataset.getGroupID(),strFilter,MDApplicable,SubItemApplicable),flag,itemListFlag);
                        }
                        //TODO: Filter Call
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null)
                            CallVolleyFilterCaptionWithSeq(str[3], str[4], str[0], str[5], str[14], str[15],dataset.getGroupID(),"");
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
                params.put("GroupID", GroupID);
                params.put("SubGroupID", SubGroupID);
                params.put("BookType", (StaticValues.BookType == 0? "0" : StaticValues.AdvanceOrBookOrder == 0 ? "1" : "2"));
                Log.d(TAG,"Order view details parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyFilterCaptionWithSeq(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String GroupID,final String SubGroupID){
        //showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"GetFilterCaption", new Response.Listener<String>()
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
                        List<Map<String,String>> mapList = new ArrayList<>();
                        mapListCaption = new ArrayList<>();
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int z = 0; z<jsonArrayResult.length(); z++) {
                            String Caption = jsonArrayResult.getJSONObject(z).getString("Cap");
                            if (!Caption.isEmpty()) {
                                int Seq = jsonArrayResult.getJSONObject(z).getInt("Seq");
                                if (Seq < 10) {
                                    List<Map<String, String>> mapListAttr = DBOrderView.getAttributesWithIDs(dataset.getGroupID(), Seq);
                                    //System.out.println("Attributes:"+mapListAttr.toString());
                                    if (!mapListAttr.isEmpty()) {
                                        for (int i = 0; i < mapListAttr.size(); i++) {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("Caption", Caption);
                                            map.put("Seq", String.valueOf(Seq));
                                            map.put("ID", mapListAttr.get(i).get("AttributeID"));
                                            map.put("Name", mapListAttr.get(i).get("Attribute"));
                                            map.put("TotalItem", "");
                                            mapList.add(map);
                                        }
                                    }
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Caption", Caption);
                                    map.put("Seq", String.valueOf(Seq));
                                    mapListCaption.add(map);
                                }
                            }
                        }
                        DBHandlerFilter.FilterTableDelete();
                        DBHandlerFilter.insertFilterTable(mapList);
                        //FilterFlag = 1;
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
                params.put("GroupID", GroupID);
                params.put("SubGroupID", SubGroupID);
                Log.d(TAG,"Filter caption parameters:"+params.toString());
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
    //TODO:AsycTask of Order Details
    private void LoadRecyclerView(List<OrderViewItemByGroupDataset> datasetList,int flag,int itemListFlag){
        if (itemListFlag == 0) {
            if (flag == 0) {
                adapter = new OrderViewItemByGroupAdapter(context, datasetList, flag);
                recyclerView.setAdapter(adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderViewItemByGroupActivity.this, 2);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else if (flag == 1) {
                adapter = new OrderViewItemByGroupAdapter(context, datasetList, flag);
                recyclerView.setAdapter(adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderViewItemByGroupActivity.this, 1);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }else if (itemListFlag == 1) {
            if (flag == 0) {
                adapter = new OrderViewItemByGroupAdapter(context, datasetList, flag);
                recyclerView.setAdapter(adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderViewItemByGroupActivity.this, 2);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else if (flag == 1) {
                adapter = new OrderViewItemByGroupAdapter(context, datasetList, flag);
                recyclerView.setAdapter(adapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderViewItemByGroupActivity.this, 1);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                OrderViewItemByGroupDataset dataset = (OrderViewItemByGroupDataset) adapter.getItem(position);
                DeletePopMenu(v, dataset);
                return true;
            }
        });
    }
    private void DialogCustomViewSettings(){
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_view_settings);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle("Item list view settings");
        dialog.show();
        RadioGroup radioGroupSingleOrMultiView = (RadioGroup) dialog.findViewById(R.id.Radio_Group_SingleOrMultiWise);
        RadioGroup radioGroupItemOrColorWise = (RadioGroup) dialog.findViewById(R.id.Radio_Group_ItemOrColorWise);
        RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.Radio_Button1);
        RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.Radio_Button2);
        RadioButton radioButton3 = (RadioButton) dialog.findViewById(R.id.Radio_Button3);
        RadioButton radioButton4 = (RadioButton) dialog.findViewById(R.id.Radio_Button4);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        if(flag == 0) {
            radioButton4.setChecked(true);
        }else if (flag == 1){
            radioButton3.setChecked(true);
        }
        if (itemListFlag == 0){
            radioButton1.setChecked(true);
        }else if (itemListFlag == 1){
            radioButton2.setChecked(true);
        }
        radioGroupItemOrColorWise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId==R.id.Radio_Button1){
                    itemListFlag = 0;
                }
                else if(checkedId==R.id.Radio_Button2){
                    itemListFlag = 1;
                }
            }
        });
        radioGroupSingleOrMultiView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId==R.id.Radio_Button3){
                    flag = 1;
                }
                else if(checkedId==R.id.Radio_Button4){
                    flag = 0;
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListFlag == 0){
                    LoadRecyclerView(DBOrderView.getItemList(OrderID,dataset.getGroupID(),strFilter),flag,itemListFlag);
                }else if (itemListFlag == 1){
                    LoadRecyclerView(DBOrderView.getItemListColorWise(OrderID,dataset.getGroupID(),strFilter,MDApplicable,SubItemApplicable),flag,itemListFlag);
                }
                dialog.dismiss();
            }
        });
    }
    private void DialogFilter(final List<Map<String,String>> mapListCaption){
        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        ImageView imageViewClose=(ImageView) dialog.findViewById(R.id.imageView_close);
        final TextView txtViewApply=(TextView) dialog.findViewById(R.id.textView_Apply);
        TextView txtViewclear=(TextView) dialog.findViewById(R.id.textView_Clear);
        listViewTab=(ListView) dialog.findViewById(R.id.listViewTab);
        listViewCheckBox=(ListView) dialog.findViewById(R.id.listViewCheckBox);
        txtViewApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFilter=new String[10];
                strFilter=DBHandlerFilter.ApplyFilter(10);
                //TODO: filter
                if (itemListFlag == 0) {
                    LoadRecyclerView(DBOrderView.getItemList(OrderID, dataset.getGroupID(),strFilter), flag, itemListFlag);
                }else if (itemListFlag == 1) {
                    LoadRecyclerView(DBOrderView.getItemListColorWise(OrderID,dataset.getGroupID(),strFilter,MDApplicable,SubItemApplicable),flag,itemListFlag);
                }
                dialog.dismiss();
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHandlerFilter.RestoreFilterFlag();
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    DBHandlerFilter.RestoreFilterFlag();
                    dialog.dismiss();
                }
                return true;
            }
        });
        txtViewclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHandlerFilter.UpdateFlagClear(0);
                filterMethod(mapListCaption);
                strFilter=null;
            }
        });
        filterMethod(mapListCaption);
    }
    //TODO: Filter Method
    private void filterMethod(final List<Map<String,String>> mapListCaption){
        FilterTabListAdapter filterTabListAdapter=new FilterTabListAdapter(context,mapListCaption);
        listViewTab.setAdapter(filterTabListAdapter);
        List<Map<String,String>> mapList=DBHandlerFilter.getColor(0);
        FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(context, mapList);
        listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
        listViewTab.setItemChecked(0, true);
        listViewTab.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewTab.setSelector(R.color.colorPrimary);
        listViewTab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<Map<String,String>> mapList=DBHandlerFilter.getColor(Integer.valueOf(mapListCaption.get(position).get("Seq")));
                FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(context, mapList);
                listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
            }
        });
    }
    private void DeletePopMenu(View v, final OrderViewItemByGroupDataset dataset){
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_pop_up_delete:
                        DeleteStyle(dataset);
                        return true;
                }
                return false;
            }
        });
    }
    private void DeleteStyle(OrderViewItemByGroupDataset dataset){
        if (StaticValues.removeFlag == 1) {
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj = new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str != null)
                    if (dataset.getMDApplicable() == 1) {
                        //MessageDialog.MessageDialog(context,"",""+positionTag);
                        CallVolleyStyleDelete(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getOrderID(), dataset.getGroupID(), dataset.getItemID(), dataset.getColorID(), "");
                    } else {
                        if (dataset.getSubItemApplicable() == 1) {
                            //MessageDialog.MessageDialog(context,"",""+positionTag);
                            CallVolleyStyleDelete(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getOrderID(), dataset.getGroupID(), dataset.getItemID(), "", dataset.getColorID());
                        } else {
                            //MessageDialog.MessageDialog(context,"",""+positionTag);
                            CallVolleyStyleDelete(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getOrderID(), dataset.getGroupID(), dataset.getItemID(), "", "");
                        }
                    }
            } else {
                MessageDialog.MessageDialog(context, "", status);
            }
        }else{
            MessageDialog.MessageDialog(context,"Alert","You don't have delete permission of this module");
        }
    }
    //TODO: CallVolley Style Delete
    private void CallVolleyStyleDelete(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID, final String ItemID, final String ColorID, final String SubItemID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderItemSubItemColorDel", new Response.Listener<String>()
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
                        CallApiMethod(dataset);
                        MessageDialog.MessageDialog(context,"",Msg);
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
                params.put("ItemID", ItemID);
                params.put("ColorID", ColorID);
                params.put("SubItemID", SubItemID);
                Log.d(TAG,"Delete style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}