package report.godownwiseorderitem;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerWithSubCustomerForOrderDataset;
import report.DatabaseSqlite.DBSqlLiteGodownWiseOrderItem;
import report.DatabaseSqlite.DBSqlLiteSalesReportWithOrderType;
import report.godownwiseorderitem.adapter.ItemDetailsAdapter;
import report.godownwiseorderitem.model.GroupWise;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class ItemDetailsListByAllWiseActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ItemDetailsAdapter adapter;
    private ProgressDialog progressDialog;
    private int sort=0;
    private String GroupID="",PartyID="",OrderID="";
    private DBSqlLiteGodownWiseOrderItem DBHandler;
    private static String TAG = ItemDetailsListByAllWiseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        this.context = ItemDetailsListByAllWiseActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteGodownWiseOrderItem(context);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetIntentMethod();
            }
        });
    }
    private void GetIntentMethod(){
        try {
            GroupWise groupWise = (GroupWise) getIntent().getExtras().get("Key");
            if(groupWise!=null) {
                GroupID = groupWise.getGroupID();
                PartyID = groupWise.getPartyID();
                OrderID = groupWise.getOrderID();
                actionBar.setTitle("" + groupWise.getGroupName() + " ( "+groupWise.getMainGroup() + " ) ");
                if (!groupWise.getPartyName().isEmpty()) {
                    LoadRecyclerView(groupWise.getGroupID(), groupWise.getPartyID(), "",0);
                } else if (!groupWise.getOrderNo().isEmpty()) {
                    LoadRecyclerView(groupWise.getGroupID(), "" ,groupWise.getOrderID(),0);
                } else {
                    LoadRecyclerView(groupWise.getGroupID(), "" , "",0);
                }
            }else {
                MessageDialog.MessageDialog(ItemDetailsListByAllWiseActivity.this,"Intent","Dataset is null");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(ItemDetailsListByAllWiseActivity.this,"Exception",e.toString());
        }
    }
    private void LoadRecyclerView(String GroupID,String PartyID,String OrderID,int flag){
        //TODO: Load Recycler View
        if (!PartyID.isEmpty() || PartyID!=null) {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByPartyWise(GroupID,PartyID));
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (!OrderID.isEmpty() || OrderID!=null) {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByDocumentWise(GroupID,OrderID));
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByGroupWise(GroupID,flag));
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        hidepDialog();
    }
    private void CallVolleyForceClose(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyWithSubPartyList", new Response.Listener<String>()
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
                        final List<SelectCustomerWithSubCustomerForOrderDataset> list = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            String SubPartyID = (jsonArrayScfo.getJSONObject(i).getString("SubPartyID")==null || jsonArrayScfo.getJSONObject(i).getString("SubPartyID").equals("null") ? "" : jsonArrayScfo.getJSONObject(i).getString("SubPartyID"));
                            list.add(new SelectCustomerWithSubCustomerForOrderDataset(jsonArrayScfo.getJSONObject(i).getString("PartyID"),jsonArrayScfo.getJSONObject(i).getString("PartyName"),jsonArrayScfo.getJSONObject(i).getString("AgentID"),jsonArrayScfo.getJSONObject(i).getString("AgentName"),jsonArrayScfo.getJSONObject(i).getString("Mobile"),jsonArrayScfo.getJSONObject(i).getString("City"),jsonArrayScfo.getJSONObject(i).getString("State"),jsonArrayScfo.getJSONObject(i).getString("Address"),jsonArrayScfo.getJSONObject(i).getString("Active"),jsonArrayScfo.getJSONObject(i).getInt("SubPartyApplicable"),jsonArrayScfo.getJSONObject(i).getInt("MultiOrder"),jsonArrayScfo.getJSONObject(i).getString("Email"),jsonArrayScfo.getJSONObject(i).getString("AccountNo"),jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"),jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"),jsonArrayScfo.getJSONObject(i).getString("IDName"),jsonArrayScfo.getJSONObject(i).getString("GSTIN"),SubPartyID,"","",jsonArrayScfo.getJSONObject(i).getString("Extin1")));
                        }
                        //adapter=new SelectCustomerWithSubCustomerForOrderAdapter(context,list);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);

                            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                    SelectCustomerWithSubCustomerForOrderDataset dataset = (SelectCustomerWithSubCustomerForOrderDataset) list.get(position);
                                    Intent intent = new Intent(getApplicationContext(), CatalogueActivity.class);
                                    CloseOrBookDataset closeOrBookDataset=null;
                                    intent.putExtra("ResultNewParty",dataset);
                                    intent.putExtra("ResultRunningParty",closeOrBookDataset);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        //}
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
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                Log.d(TAG,"Select customer for order parameters:"+params.toString());
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
    @Override
    public void onResume(){
        super.onResume();
        GetIntentMethod();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                finish();
                break;
            case R.id.action_search:
                break;
            case R.id.action_logout://TODO: Sort (PendingQty > StockQty and PendingQty < StockQty)
                if (sort == 0){ //TODO: Sort (PendingQty > StockQty)
                    sort = 1;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
                }else if (sort == 1){ //TODO: Sort (PendingQty < StockQty)
                    sort = 2;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
                }else if (sort == 2){ //TODO: Sort (ItemCode ASC)
                    sort = 0;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem sortItem = menu.findItem(R.id.action_logout);//TODO: Sort Group Wise
        if ((PartyID.isEmpty() || PartyID==null) && (OrderID.isEmpty() || OrderID==null)) {
            sortItem.setVisible(true);
        }
        sortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_sort));
        MenuItem searchItem = menu.findItem(R.id.action_search);
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
}
