package com.singlagroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.adapters.BranchFilterableAdapter;
import com.singlagroup.adapters.CompanyFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.BranchDataset;
import com.singlagroup.datasets.DeviceInfoDataset;
import com.singlagroup.datasets.DivisionDataset;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 10-Oct-16.
 */

public class SettingsCompanyActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Spinner spnCompany,spnDivision,spnBranch,spnGodown;
    private Button btnSave;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    List<Map<String,String>> CompanyList;
    List<DivisionDataset> DivisionList;
    List<BranchDataset> BranchList;
    List<GodownDataset> GodownList;
    String CompanyID="",DivisionID="",BranchID="",GodownID="";
    private ProgressDialog progressDialog;
    private Context context;
    private static String TAG = SettingsCompanyActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_company_settings);
        this.context = SettingsCompanyActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Company Change Settings");
        this.DBHandler=new DatabaseSqlLiteHandlerUserInfo(getApplicationContext());
        Initialization();
        LoadSpinnerData();
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoadSpinnerData();
                }else{
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
    }
    private void Initialization(){
        spnCompany = (Spinner) findViewById(R.id.Spinner_Company);
        spnDivision = (Spinner) findViewById(R.id.Spinner_Division);
        spnBranch = (Spinner) findViewById(R.id.Spinner_Branch);
        spnGodown = (Spinner) findViewById(R.id.Spinner_Godown);
        btnSave = (Button) findViewById(R.id.Button_Save);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void LoadSpinnerData(){
        CompanyList = DBHandler.getUserInfoCompanyList();
        BranchList = new ArrayList<>();
        GodownList = new ArrayList<>();
        if (CompanyList!=null) {
            spnCompany.setAdapter(new CompanyFilterableAdapter(context,CompanyList,CompanyList));
            spnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CompanyID=CompanyList.get(position).get("CompanyID");
                    DivisionList = DBHandler.getUserInfoDivision(CompanyID);

                    spnDivision.setAdapter(new DivisionFilterableAdapter(context, DivisionList, DivisionList));
                    spnDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            DivisionID = DivisionList.get(position).getDivisionID();
                            BranchList = DBHandler.getUserInfoBranch(CompanyID,DivisionID);

                            if (BranchList!=null) {
                                spnBranch.setAdapter(new BranchFilterableAdapter(context, BranchList, BranchList));
                                spnBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        BranchID = BranchList.get(position).getBranchID();
                                        GodownList = DBHandler.getUserInfoGodown(CompanyID,BranchID);

                                        if (GodownList!=null) {
                                            spnGodown.setAdapter(new GodownFilterableAdapter(context, GodownList, GodownList));
                                            spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    GodownID = GodownList.get(position).getGodownID();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CompanyID.isEmpty() && !DivisionID.isEmpty() && !BranchID.isEmpty() && !GodownID.isEmpty()){
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        MainActivity obj2 = new MainActivity();
                        DeviceInfoDataset dataset = obj2.GetSharePreferenceDeviceInfo(context);
                        if (str!=null)
                            if (!CompanyID.equals(str[14])) {
                                CallVolleyCompanyChange(str[4], str[3], StaticValues.Latitude, StaticValues.Longitude, dataset.getVersion(), CommanStatic.AppType, CompanyID, str[14],str[0],"01:01:01");
                            }else{
                                if (!DivisionID.equals(str[5])){
                                    DBHandler.UpdateDivision(CompanyID,DivisionID,1);
                                    DBHandler.UpdateBranch(CompanyID,BranchID,DivisionID,1);
                                    DBHandler.UpdateGodown(CompanyID,GodownID,BranchID,1);
                                    SharePreferenceSession(CompanyID,DivisionID,BranchID,GodownID,str[0]);
                                    MessageDialog.MessageDialog(context,"","Update successfully your change settings");
                                }else{
                                    if (!BranchID.equals(str[15])){
                                        DBHandler.UpdateBranch(CompanyID,BranchID,DivisionID,1);
                                        DBHandler.UpdateGodown(CompanyID,GodownID,BranchID,1);
                                        SharePreferenceSession(CompanyID,DivisionID,BranchID,GodownID,str[0]);
                                        MessageDialog.MessageDialog(context,"","Update successfully your change settings");
                                    }else{
                                        if (!GodownID.equals(str[6])){
                                            DBHandler.UpdateGodown(CompanyID,GodownID,BranchID,1);
                                            SharePreferenceSession(CompanyID,DivisionID,BranchID,GodownID,str[0]);
                                            MessageDialog.MessageDialog(context,"","Update successfully your change settings");
                                        }else{
                                            MessageDialog.MessageDialog(context,"","You are already in same company/division/branch/godown");
                                        }
                                    }
                                }
                            }
                    }else{
                        MessageDialog.MessageDialog(context,"",""+status);
                    }
                }
            }
        });
    }
    public void SharePreferenceSession(String CompanyID,String DivisionID,String BranchID,String GodownID,String SessionID){

        SharedPreferences prefs = getSharedPreferences("MyPrefDeviceSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CompanyID", CompanyID);
        editor.putString("DivisionID", DivisionID);
        editor.putString("BranchID", BranchID);
        editor.putString("GodownID", GodownID);
        editor.putString("SessionID", SessionID);
        editor.commit();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //do your stuff
            //Utils.setBadgeCount(NotificationActivity.this, PartyInfoActivity.iconNotification, CommanStatic.notificationCount);
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                //Utils.setBadgeCount(NotificationActivity.this, PartyInfoActivity.iconNotification, CommanStatic.notificationCount);
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
    private void CallVolleyCompanyChange(final String UserID,final String DeviceID, final String Latitude, final String Longitude,final String AppVersion, final String AppType, final String NewCompanyID, final String OldCompanyID,final String OldSessionID,final String ActiveTime){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CompanyChange", new Response.Listener<String>()
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
                        JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("Result"));
                        String SessionID = jsonObjectResult.getString("SessionID");
                        DBHandler.UpdateCompany(CompanyID,1);
                        DBHandler.UpdateDivision(CompanyID,DivisionID,1);
                        DBHandler.UpdateBranch(CompanyID,BranchID,DivisionID,1);
                        DBHandler.UpdateGodown(CompanyID,GodownID,BranchID,1);
                        SharePreferenceSession(CompanyID,DivisionID,BranchID,GodownID,SessionID);
                        MessageDialog.MessageDialog(context,"","Update successfully your change settings");
                    }else{
                        MessageDialog.MessageDialog(context,"",""+Msg);
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
                params.put("UserID", UserID);
                params.put("DeviceID", DeviceID);
                params.put("Latitude", Latitude);
                params.put("Longitude", Longitude);
                params.put("AppVersion", AppVersion);
                params.put("AppType", AppType);
                params.put("NewCompanyID", NewCompanyID);
                params.put("OldCompanyID", OldCompanyID);
                params.put("OldSessionID", OldSessionID);
                params.put("ActiveTime", ActiveTime);
                Log.d(TAG,"Parameters:"+params.toString());
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
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
