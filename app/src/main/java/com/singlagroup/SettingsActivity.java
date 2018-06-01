package com.singlagroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.adapters.AutoLaunchModuleFilterableAdapter;
import com.singlagroup.adapters.BranchFilterableAdapter;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.adapters.CompanyFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.BranchDataset;
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
import orderbooking.customerlist.BookOrdersActivity;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 10-Oct-16.
 */

public class SettingsActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private CommonSearchableSpinner spnAutoLaunch;
    private TextView textView;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    ArrayList<Map<String,String>> AutoLaunchModuleList;
    String AutoLaunchModuleName="",AutoLaunchModuleType="";
    private ProgressDialog progressDialog;
    private Context context;
    private static String TAG = SettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_settings);
        this.context = SettingsActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Settings");
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
        textView = (TextView) findViewById(R.id.text_view);
        spnAutoLaunch = (CommonSearchableSpinner) findViewById(R.id.Spinner_Auto_launch_module);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void LoadSpinnerData(){
        AutoLaunchModuleList = new ArrayList<>();
        int c=0,position=0;
        Map<String,String> map=new HashMap<String,String>();
        map.put("ID","");
        map.put("Name","No Default Module");
        map.put("Type","0");
        AutoLaunchModuleList.add(c,map);
        for (int i=0;i<DBHandler.getModuleList().size();i++){
            c++;
            map=new HashMap<String,String>();
            map.put("ID",DBHandler.getModuleList().get(i).get("ID"));
            map.put("Name",DBHandler.getModuleList().get(i).get("Name"));
            map.put("Type",DBHandler.getModuleList().get(i).get("Vtype"));
            AutoLaunchModuleList.add(c,map);
            if(CommanStatic.AutoLaunchModule == Integer.valueOf(DBHandler.getModuleList().get(i).get("Vtype"))){
                position = i;
                textView.setText(DBHandler.getModuleList().get(i).get("Name")+" - "+DBHandler.getModuleList().get(i).get("Vtype"));
            }
        }
        if (AutoLaunchModuleList!=null) {
            spnAutoLaunch.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,AutoLaunchModuleList,AutoLaunchModuleList));
            spnAutoLaunch.setSelection(position);
            spnAutoLaunch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AutoLaunchModuleName = AutoLaunchModuleList.get(position).get("Name");
                    AutoLaunchModuleType = AutoLaunchModuleList.get(position).get("Type");
                    textView.setVisibility(View.GONE);
                    //if (position>0) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null) {
                            AlertDialogAutoLaunch(str[3], str[4], str[0], str[14],AutoLaunchModuleName,AutoLaunchModuleType);
                        }
                    //}
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
    public void AlertDialogAutoLaunch(final String DeviceID, final String UserID, final String SessionID,final String CompanyID,final String AutoModuleName,final String VType){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make auto launch module");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int arg1) {
                                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                                if (!status.contentEquals("No Internet Connection")) {
                                    CallVolleyAutoLaunchModule(DeviceID,UserID,SessionID,CompanyID,AutoModuleName,VType);
                                    dialogInterface.dismiss();
                                }else{
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
    private void CallVolleyAutoLaunchModule(final String DeviceID, final String UserID, final String SessionID, final String CompanyID,final String AutoModuleName,final String VType){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UserDefaultModuleUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    //int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    MessageDialog.MessageDialog(context,"",""+Msg);
                    CommanStatic.AutoLaunchModule = Integer.valueOf(VType);
                    CommanStatic.AutoLaunchModuleName = AutoModuleName;
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
                params.put("CompanyID", CompanyID);
                params.put("VType", VType);
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
