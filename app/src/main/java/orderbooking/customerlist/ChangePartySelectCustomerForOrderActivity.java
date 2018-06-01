package orderbooking.customerlist;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerChangeParty;
import orderbooking.StaticValues;
import orderbooking.customerlist.adapter.ChangePartySelectCustomerForOrderAdapter;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class ChangePartySelectCustomerForOrderActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ChangePartySelectCustomerForOrderAdapter adapter;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    public static String OrderID="";
    private static String TAG = ChangePartySelectCustomerForOrderActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        RecevieIntentByKey();
    }
    private void Initialization(){
        this.context = ChangePartySelectCustomerForOrderActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Select Customer For Order");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RecevieIntentByKey();
            }
        });
    }
    private void RecevieIntentByKey(){
        try {
            CloseOrBookDataset dataset = (CloseOrBookDataset)getIntent().getExtras().get("Key");
            OrderID = dataset.getOrderID();
            actionBar.setTitle("Party List");
            //String Title = bundle.getString("Title");
            //if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod();
//            }else {
//                MessageDialog.MessageDialog(ChangePartySelectCustomerForOrderActivity.this,"Alert","You don't have permission of this module");
//            }
        }catch (Exception e){
            MessageDialog.MessageDialog(ChangePartySelectCustomerForOrderActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(ChangePartySelectCustomerForOrderActivity.this);
                    if (str!=null) {
                        CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
                    }
                } else {
                    MessageDialog.MessageDialog(ChangePartySelectCustomerForOrderActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(ChangePartySelectCustomerForOrderActivity.this);
            if (str!=null) {
                CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(ChangePartySelectCustomerForOrderActivity.this,"","","No Internet Connection");
        }
    }
    private void CallVolleySelectCustomerForOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyList", new Response.Listener<String>()
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
                            map.put("PartyID",jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                            map.put("PartyName",jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                            map.put("AgentID",(jsonArrayScfo.getJSONObject(i).getString("AgentID") == null || jsonArrayScfo.getJSONObject(i).getString("AgentID").equals("null")) ? "" : jsonArrayScfo.getJSONObject(i).getString("AgentID"));
                            map.put("AgentName",(jsonArrayScfo.getJSONObject(i).getString("AgentName") == null || jsonArrayScfo.getJSONObject(i).getString("AgentName").equals("null")) ? "" : jsonArrayScfo.getJSONObject(i).getString("AgentName"));
                            map.put("City",jsonArrayScfo.getJSONObject(i).getString("City"));
                            map.put("State",jsonArrayScfo.getJSONObject(i).getString("State"));
                            map.put("Mobile",jsonArrayScfo.getJSONObject(i).getString("Mobile"));
                            map.put("Address",jsonArrayScfo.getJSONObject(i).getString("Address"));
                            map.put("Active",jsonArrayScfo.getJSONObject(i).getString("Active"));
                            map.put("MultiOrder",jsonArrayScfo.getJSONObject(i).getString("MultiOrder"));
                            map.put("SubPartyApplicable",jsonArrayScfo.getJSONObject(i).getString("SubPartyApplicable"));
                            map.put("Email",jsonArrayScfo.getJSONObject(i).getString("Email"));
                            map.put("AccountNo",jsonArrayScfo.getJSONObject(i).getString("AccountNo"));
                            map.put("AccountHolderName",jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"));
                            map.put("IFSCCOde",jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"));
                            map.put("IDName",jsonArrayScfo.getJSONObject(i).getString("IDName"));
                            map.put("GSTIN",jsonArrayScfo.getJSONObject(i).getString("GSTIN"));
                            map.put("Label",(jsonArrayScfo.getJSONObject(i).optString("Extin1")==null || jsonArrayScfo.getJSONObject(i).optString("Extin1").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("Extin1"));
                            mapList.add(map);
                        }
                        DatabaseSqlLiteHandlerChangeParty DBChangeParty = new DatabaseSqlLiteHandlerChangeParty(context);
                        DBChangeParty.deleteChangePartyNEW();
                        DBChangeParty.insertChangePartyNewData(mapList);
                        List<SelectCustomerForOrderDataset> PartyList = DBChangeParty.getPartyList();
                        adapter=new ChangePartySelectCustomerForOrderAdapter(context,PartyList);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);
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
    protected void onResume() {
        super.onResume();
        RecevieIntentByKey();
    }
    @Override
    protected void onPause() {
        super.onPause();
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
