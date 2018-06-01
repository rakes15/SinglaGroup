package print.godown_group_order_with_rate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

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
import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import print.Database_Sqlite.DatabaseSqlLiteHandlerGodownOrGroupPrintReport;
import print.godown_group_order_with_rate.adapter.GroupAdapter;
import print.godown_group_order_with_rate.model.Group;
import print.godown_group_order_with_rate.model.GroupOrGodown;
import print.godown_group_order_with_rate.model.Order;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 28-Aug-17.
 */
public class GodownOrGroupListActivity1 extends AppCompatActivity{

    private ActionBar actionBar;
    private Context context;
    RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GroupAdapter adapter;
    private List<Group> groupList;
    private Order order;
    private GroupOrGodown groupOrGodown;
    private DatabaseSqlLiteHandlerGodownOrGroupPrintReport DBHandler;
    private static String TAG = GodownOrGroupListActivity1.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        GetIntent();
    }
    private void Initialization(){
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.context = GodownOrGroupListActivity1.this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetIntent();
            }
        });
        GroupAdapter.listGroup = new ArrayList<>();
        DBHandler = new DatabaseSqlLiteHandlerGodownOrGroupPrintReport(context);
    }
    private void GetIntent(){
        try {
            order = (Order) getIntent().getExtras().get("Order");
            groupOrGodown = (GroupOrGodown) getIntent().getExtras().get("Godown");
            GroupAdapter.listGroup = new ArrayList<>();
            if (order!=null && groupOrGodown!=null) {
                String Title = order.getOrderNo();
                actionBar.setTitle(Title);
                LoadRecyclerView(order,groupOrGodown);
                //CallApiMethod(order);
            }

        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void CallApiMethod(final Order order){
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyGodownOrGodownWisePrint(str[3], str[4], str[0], str[5], str[14], str[15],order.getOrderID());
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
                CallVolleyGodownOrGodownWisePrint(str[3], str[4], str[0], str[5], str[14], str[15],order.getOrderID());
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
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
            case R.id.action_filter_search: //TODO: Print
                if (GroupAdapter.listGroup!=null && !GroupAdapter.listGroup.isEmpty()){
                    Intent intent = new Intent(context,PrintActivity.class);
                    intent.putExtra("Group",GroupAdapter.listGroup.get(0));
                    intent.putExtra("Godown",groupOrGodown);
                    intent.putExtra("Order",order);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context,PrintActivity.class);
                    intent.putExtra("Group",groupList.get(0));
                    intent.putExtra("Godown",groupOrGodown);
                    intent.putExtra("Order",order);
                    startActivity(intent);
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
        GetIntent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem actionSearch = menu.findItem(R.id.action_search);
        actionSearch.setVisible(false);
        MenuItem actionFilterSearch = menu.findItem(R.id.action_filter_search);
        actionFilterSearch.setVisible(true);
        actionFilterSearch.setIcon(getResources().getDrawable(R.drawable.ic_action_print_white));
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(actionSearch);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false);
//        searchView.setFocusable(true);
//        searchView.setFocusableInTouchMode(true);
//        searchView.requestFocus();
//        searchView.requestFocusFromTouch();
//        searchView.setQueryHint("Search...");
//        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
//        {
//            @Override
//            public boolean onQueryTextChange(String newText)
//            {
//                // this is your adapter that will be filtered
//                if(adapter!=null) {
//                    adapter.filter(newText);
//                }
////                System.out.println("on text chnge text: "+newText);
//                return true;
//            }
//            @Override
//            public boolean onQueryTextSubmit(String query)
//            {
//                // this is your adapter that will be filtered
//                if(adapter!=null)
//                {
//                    adapter.filter(query);
//                }
////                System.out.println("on query submit: "+query);
//                return true;
//            }
//        };
//        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
    private void CallVolleyGodownOrGodownWisePrint(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderItemAvailablity", new Response.Listener<String>()
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
                        for (int i = 0; i < jsonArrayResult.length(); i++) {
                            Map<String,String> map = new HashMap<>();

                            map.put("OrderID", jsonArrayResult.getJSONObject(i).optString("OrderID") == null ? order.getOrderID() : jsonArrayResult.getJSONObject(i).optString("OrderID"));
                            map.put("OrderDate", order.getOrderDate());//jsonArrayResult.getJSONObject(i).optString("OrderDate") == null ? order.getOrderDate() : jsonArrayResult.getJSONObject(i).optString("OrderDate"));
                            map.put("OrderNo", order.getOrderNo());//jsonArrayResult.getJSONObject(i).optString("OrderNo") == null ? order.getOrderNo() : jsonArrayResult.getJSONObject(i).optString("OrderNo"));
                            map.put("MainGroupID", jsonArrayResult.getJSONObject(i).optString("MainGroupID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MainGroupID"));
                            map.put("MainGroup", jsonArrayResult.getJSONObject(i).optString("MainGroup") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MainGroup"));
                            map.put("GroupID", jsonArrayResult.getJSONObject(i).optString("GroupID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GroupID"));
                            map.put("GroupName", jsonArrayResult.getJSONObject(i).optString("GroupName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GroupName"));
                            map.put("GroupImage", jsonArrayResult.getJSONObject(i).optString("GroupImage") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GroupImage"));
                            map.put("SubGroupID", jsonArrayResult.getJSONObject(i).optString("SubGroupID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroupID"));
                            map.put("SubGroup", jsonArrayResult.getJSONObject(i).optString("SubGroup") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroup"));
                            map.put("ItemID", jsonArrayResult.getJSONObject(i).optString("ItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemID"));
                            map.put("ItemName", jsonArrayResult.getJSONObject(i).optString("ItemName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemName"));
                            map.put("ItemCode", jsonArrayResult.getJSONObject(i).optString("ItemCode") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemCode"));
                            map.put("SubItemID", jsonArrayResult.getJSONObject(i).optString("SubItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemID"));
                            map.put("SubItemName", jsonArrayResult.getJSONObject(i).optString("SubItemName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemName"));
                            map.put("SubItemCode", jsonArrayResult.getJSONObject(i).optString("SubItemCode") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemCode"));
                            map.put("ColorID", jsonArrayResult.getJSONObject(i).getString("ColorID"));
                            map.put("Color", jsonArrayResult.getJSONObject(i).getString("Color"));
                            map.put("SizeID", jsonArrayResult.getJSONObject(i).getString("SizeID"));
                            map.put("SizeName", jsonArrayResult.getJSONObject(i).getString("Size"));
                            map.put("AttribName2", jsonArrayResult.getJSONObject(i).getString("Attrbute2"));
                            map.put("AttribName6", jsonArrayResult.getJSONObject(i).getString("Attrbute6"));
                            map.put("AttribName7", jsonArrayResult.getJSONObject(i).getString("Attrbute7"));
                            map.put("AttribName8", jsonArrayResult.getJSONObject(i).getString("Attrbute8"));
                            map.put("MDApplicable", jsonArrayResult.getJSONObject(i).optString("MDApplicable") == null ? "1" : jsonArrayResult.getJSONObject(i).optString("MDApplicable"));
                            map.put("SubItemApplicable", jsonArrayResult.getJSONObject(i).optString("SubItemApplicable") == null ? "0" : jsonArrayResult.getJSONObject(i).optString("SubItemApplicable"));

                            map.put("OrderQty", jsonArrayResult.getJSONObject(i).getString("OrderQty"));
                            map.put("OrderItemPendingQty", jsonArrayResult.getJSONObject(i).getString("OrderItemPendingQty"));
                            map.put("ItemStockGodown", jsonArrayResult.getJSONObject(i).getString("ItemStockGodown"));
                            map.put("MDOrderQty", jsonArrayResult.getJSONObject(i).getString("MDOrderQty"));
                            map.put("MDPendingQty", jsonArrayResult.getJSONObject(i).getString("MDPendingQty"));
                            map.put("MDStock", jsonArrayResult.getJSONObject(i).getString("MDStock"));
                            map.put("GodownID", jsonArrayResult.getJSONObject(i).getString("GodownID"));
                            map.put("GodownName", jsonArrayResult.getJSONObject(i).getString("GodownName"));
                            map.put("GodownType", jsonArrayResult.getJSONObject(i).getString("GodownType"));
                            map.put("GodownTypeName", jsonArrayResult.getJSONObject(i).getString("GodownTypeName"));
                            map.put("Rate", jsonArrayResult.getJSONObject(i).optString("Rate") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Rate"));
                            mapList.add(map);
                        }
                        context.deleteDatabase(DatabaseSqlLiteHandlerGodownOrGroupPrintReport.DATABASE_NAME);
                        DatabaseSqlLiteHandlerGodownOrGroupPrintReport DBGodownOrGroup = new DatabaseSqlLiteHandlerGodownOrGroupPrintReport(context);
                        DBGodownOrGroup.deleteOrderDetails();
                        DBGodownOrGroup.insertOrderDetails(mapList);

                        //LoadRecyclerView(DBGodownOrGroup.getGodownList(OrderID));
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
                Log.d(TAG," Godown || Group parameters:"+params.toString());
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
    //TODO:Load Recycler View
    private void LoadRecyclerView(final Order order, final GroupOrGodown godown){
        this.groupList = DBHandler.getGroupMainGroupList(order.getOrderID(),groupOrGodown.getGroupOrGodownID());
        adapter = new GroupAdapter(context,groupList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Group group = (Group) adapter.getItem(position);
                Intent intent = new Intent(context, PrintActivity.class);
                intent.putExtra("Group",group);
                intent.putExtra("Godown",godown);
                intent.putExtra("Order",order);
                startActivity(intent);
            }
        });
    }
}