package com.singlagroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.singlagroup.adapters.RecyclerViewPrinterServicesAdapter;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.RecommandedAppsDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import orderbooking.customerlist.BookOrdersActivity;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 10-March-17.
 */

public class RecommandedAppsActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Context context;
    private static String TAG = RecommandedAppsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        CallApiMethod();
    }
    private void Initialization(){
        this.context = RecommandedAppsActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Recommanded Apps");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallApiMethod();
            }
        });
    }
    private void CallApiMethod(){
        //TODO: Book Orders Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    CallVolleyRecommandedApps(CommanStatic.AppType);
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallVolleyRecommandedApps(CommanStatic.AppType);
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    private void LoadRecyclerView(List<RecommandedAppsDataset> datasetList){
        RecyclerViewPrinterServicesAdapter adapter = new RecyclerViewPrinterServicesAdapter(RecommandedAppsActivity.this,datasetList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecommandedAppsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    public void CallVolleyRecommandedApps(final String AppType){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.STATIC_BASE_URL+"RecommendedApps", new Response.Listener<String>()
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
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        Log.d(TAG,"Result:"+jsonArrayResult.toString());
                        List<RecommandedAppsDataset> datasetList = new ArrayList<>();
                        for (int i=0; i<jsonArrayResult.length(); i++){
                            RecommandedAppsDataset dataset = new RecommandedAppsDataset(jsonArrayResult.getJSONObject(i).getString("ID"),jsonArrayResult.getJSONObject(i).getString("Name"),jsonArrayResult.getJSONObject(i).getString("URL"),jsonArrayResult.getJSONObject(i).getString("PackageName"),jsonArrayResult.getJSONObject(i).getInt("IsMandatory"),jsonArrayResult.getJSONObject(i).getInt("AppType"),jsonArrayResult.getJSONObject(i).getString("Icon"));
                            datasetList.add(dataset);
                        }
                        LoadRecyclerView(datasetList);
                    }else{
                        MessageDialog.MessageDialog(context,"",""+Msg);
                    }

                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception re",""+e.toString());
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
                params.put("AppType", AppType);
                Log.d(TAG,"Recommanded Apps parameters:"+params.toString());
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_attachment);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onPause() {
        super.onPause();
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(getApplicationContext());
        //SessionManage.CallRetrofitSessionLogout(str[3],str[0],str[4],str[14]);
    }
}
