package callcenter;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ProgressBar;
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
import java.util.Timer;
import java.util.TimerTask;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import callcenter.adapter.AgentLSAdapter;
import callcenter.adapter.CallSummarydapter;
import callcenter.model.AgentLS;
import callcenter.model.CallSummary;
import orderbooking.StaticValues;
import orderbooking.catalogue.CatalogueActivity;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.FileImageUplodingAcitvity;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class AgentLSActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private RecyclerView recyclerView,recyclerView2;
    private TextView txtCallSummary;
    private AgentLSAdapter adapter;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private Thread thread;
    private boolean thFlag = true;
    private List<AgentLS> agentLSList;
    private static String TAG = AgentLSActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_multi);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = AgentLSActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_2);
        txtCallSummary = (TextView) findViewById(R.id.text_call_summary);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title",String.valueOf(R.string.app_name));
            StaticValues.viewFlag = bundle.getInt("ViewFlag",0);
            StaticValues.editFlag = bundle.getInt("EditFlag",0);
            StaticValues.createFlag = bundle.getInt("CreateFlag",0);
            StaticValues.removeFlag = bundle.getInt("RemoveFlag",0);
            StaticValues.printFlag = bundle.getInt("PrintFlag",0);
            StaticValues.importFlag = bundle.getInt("ImportFlag",0);
            StaticValues.exportFlag = bundle.getInt("ExportFlag",0);
            StaticValues.Vtype = bundle.getInt("Vtype",0);
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod();
                // TODO: Timer
                thread = new Thread() {
                    @Override
                    public void run() {
                        while (!isInterrupted() && thFlag) {
                            try {
                                Thread.sleep(3000);  //1000ms = 1 sec
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CallApiMethod();
                                    }
                                });

                            } catch (InterruptedException e) {
                                Log.e(TAG,"InterruptedException: "+e.toString());
                            }
                        }
                    }
                };
                thread.start();
            }else {
                MessageDialog.MessageDialog(AgentLSActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(AgentLSActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(AgentLSActivity.this);
                    if (str!=null) {
                        CallVolleyagentLS(str[3], str[4], str[0], str[5],str[14]);
                    }
                } else {
                    MessageDialog.MessageDialog(AgentLSActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(AgentLSActivity.this);
            if (str!=null) {
                CallVolleyagentLS(str[3], str[4], str[0], str[5],str[14]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(AgentLSActivity.this,"","","No Internet Connection");
        }
    }
    private void LoadRcyclerView(List<AgentLS> agentLSList,List<CallSummary> callSummaryList){
        //TODO: Call Summary
        if (!callSummaryList.isEmpty()) {
            txtCallSummary.setVisibility(View.VISIBLE);
            int total = 0;
            for (int i = 0; i < callSummaryList.size(); i++) {
                total += callSummaryList.get(i).getTotalCountg();
            }
            CallSummary callSummary = new CallSummary();
            callSummary.setCallStatus(9);
            callSummary.setTotalCountg(total);
            callSummary.setCallStatusMsg("" + 9);
            callSummaryList.add(callSummary);
        }else{
            txtCallSummary.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new CallSummarydapter(context,callSummaryList));
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //TODO: Agent List
        this.agentLSList = agentLSList;
        adapter=new AgentLSAdapter(context,agentLSList);
        recyclerView2.setAdapter(adapter);
        linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager);
        recyclerView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }
    private void CallVolleyagentLS(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID){
        showProgressBar();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CallCentreAgentLS", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<AgentLS> list = new ArrayList<>();
                List<CallSummary> callSummaryList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONObject jsonObjResult = new JSONObject(jsonObject.getString("Result"));
                        JSONArray jsonArrAgent = jsonObjResult.optJSONArray("AgentList")==null ? new JSONArray() : jsonObjResult.optJSONArray("AgentList");
                        JSONArray jsonArrSummary = jsonObjResult.optJSONArray("CallSummary") == null ? new JSONArray() : jsonObjResult.optJSONArray("CallSummary");
                        if (jsonArrAgent.length() > 0) {
                            for (int i = 0; i < jsonArrAgent.length(); i++) {
                                AgentLS agentLS = new AgentLS();
                                agentLS.setPBXLogin(jsonArrAgent.getJSONObject(i).optInt("PBX_Login"));
                                agentLS.setExtentionNo(jsonArrAgent.getJSONObject(i).optString("Extention_No") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Extention_No"));
                                agentLS.setState(jsonArrAgent.getJSONObject(i).optString("state") == null ? "" : jsonArrAgent.getJSONObject(i).optString("state"));
                                agentLS.setCallConnected(jsonArrAgent.getJSONObject(i).optInt("Call_Connected"));
                                agentLS.setCallNumber(jsonArrAgent.getJSONObject(i).optString("CallNumber") == null ? "" : jsonArrAgent.getJSONObject(i).optString("CallNumber"));
                                agentLS.setSkillLevel(jsonArrAgent.getJSONObject(i).optString("Skill_Level") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Skill_Level"));
                                agentLS.setAgentName(jsonArrAgent.getJSONObject(i).optString("Agent_Name") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Agent_Name"));
                                agentLS.setAgFullName(jsonArrAgent.getJSONObject(i).optString("AgFullName") == null ? "" : jsonArrAgent.getJSONObject(i).optString("AgFullName"));
                                agentLS.setCRMState(jsonArrAgent.getJSONObject(i).optString("CRM_State") == null ? "" : jsonArrAgent.getJSONObject(i).optString("CRM_State"));
                                agentLS.setCallDurration(jsonArrAgent.getJSONObject(i).optString("CallDurration") == null ? "" : jsonArrAgent.getJSONObject(i).optString("CallDurration"));
                                agentLS.setCPName(jsonArrAgent.getJSONObject(i).optString("CPName") == null ? "" : jsonArrAgent.getJSONObject(i).optString("CPName"));
                                agentLS.setParty(jsonArrAgent.getJSONObject(i).optString("Party") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Party"));
                                agentLS.setSubParty(jsonArrAgent.getJSONObject(i).optString("SubParty") == null ? "" : jsonArrAgent.getJSONObject(i).optString("SubParty"));
                                agentLS.setTypeName(jsonArrAgent.getJSONObject(i).optString("TypeName") == null ? "" : jsonArrAgent.getJSONObject(i).optString("TypeName"));

                                list.add(agentLS);
                            }

                        }
                        callSummaryList = new ArrayList<>();
                        //TODO: Call Summary
                        if (jsonArrSummary.length() > 0) {
                            for (int i = 0; i < jsonArrSummary.length(); i++) {
                                CallSummary callSummary = new CallSummary();
                                callSummary.setTotalCountg(jsonArrSummary.getJSONObject(i).optInt("TotalCountg"));
                                callSummary.setCallStatusMsg(jsonArrSummary.getJSONObject(i).optString("CallStatusMsg") == null ? "" : jsonArrSummary.getJSONObject(i).optString("CallStatusMsg"));
                                callSummary.setCallStatus(jsonArrSummary.getJSONObject(i).optInt("CallStatus"));
                                callSummaryList.add(callSummary);
                            }
                        }
                        //TODO: Load Recycler View
                        LoadRcyclerView(list,callSummaryList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        LoadRcyclerView(list,callSummaryList);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideProgressBar();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideProgressBar();
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
                Log.d(TAG,"Agent Ls parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyAgentsLogout(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String AgName,final String Ext,final int size,final int index){
        if (index==0){ showDialog(); }
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CallCenterAgLogOut", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<AgentLS> list = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        if (size==(index+1)) { MessageDialog.MessageDialog(context, "", Msg); }
                    } else {
                        if (size==(index+1)) { MessageDialog.MessageDialog(context, "", Msg); }
                    }
                    if (size==(index+1)) { hideDialog(); }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    if (size==(index+1)) { hideDialog(); }
                }
                //hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                if (size==(index+1)) { hideDialog(); }
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
                params.put("AgName", AgName);
                params.put("Ext", Ext);
                Log.d(TAG,"Agents Logout parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showProgressBar() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgressBar() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.GONE);
        }
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        thFlag = true;
        ModulePermission();

    }
    @Override
    protected void onPause() {
        super.onPause();
        thFlag = false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                thFlag = false;
                //TODO: Activity finish
                finish();
                break;
            case R.id.action_search:
                break;
            case R.id.action_logout:
                if (!agentLSList.isEmpty()){
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(AgentLSActivity.this);
                        if (str != null) {
                            for (int i = 0; i < agentLSList.size(); i++) {
                                CallVolleyAgentsLogout(str[3], str[4], str[0], str[5],str[14],agentLSList.get(i).getAgentName(),agentLSList.get(i).getExtentionNo(),agentLSList.size(),i);
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(AgentLSActivity.this,"","No Internet Connection");
                    }
                }else{
                    MessageDialog.MessageDialog(context,"","No agents login");
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
            thFlag = false;
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
