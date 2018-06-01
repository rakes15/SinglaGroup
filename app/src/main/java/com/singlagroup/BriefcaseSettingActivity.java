package com.singlagroup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.adapters.BranchFilterableAdapter;
import com.singlagroup.adapters.CompanyFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.adapters.RecyclerViewBriefcaseAdapter;
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
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 10-Oct-16.
 */

public class BriefcaseSettingActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Button btnAdd,btnRemove;
    private ProgressDialog progressDialog;
    public static List<Integer> BriefcaseChecked = new ArrayList<>();
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private static String TAG = BriefcaseSettingActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_briefcase_setting);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Briefcase Setting");
        this.DBHandler=new DatabaseSqlLiteHandlerUserInfo(getApplicationContext());
        Initialization();
        LoadViewData();
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoadViewData();
                }else{
                    MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"",status);
                }
            }
        });
    }
    private void Initialization(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnAdd = (Button) findViewById(R.id.Button_Add);
        btnRemove = (Button) findViewById(R.id.Button_Remove);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        //TODO: Add module into briefcase
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBriefcase(BriefcaseSettingActivity.this,0);
            }
        });
        //TODO: Remove module from briefcase
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBriefcase(BriefcaseSettingActivity.this,1);
            }
        });
    }
    private void LoadViewData(){
        List<Map<String,String>> mapList = new ArrayList<>();
        for (int i = 0; i < DBHandler.getModuleListByBriefcase(1).size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("Name", DBHandler.getModuleListByBriefcase(1).get(i).get("Name"));
            map.put("Vtype", DBHandler.getModuleListByBriefcase(1).get(i).get("Vtype"));
            map.put("CheckBox", "0");
            mapList.add(map);
        }
        Log.i(TAG,"List Briefcase:"+mapList.toString());
        recyclerView.setAdapter(new RecyclerViewBriefcaseAdapter(BriefcaseSettingActivity.this,mapList));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
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
    private void DialogBriefcase(final Context context, final int flagAddRemove) {
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_recycler_view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        BriefcaseChecked = new ArrayList<>();
        List<Map<String,String>> mapList = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        Button btnAddOrRemove = (Button) dialog.findViewById(R.id.Button_OK);
        if (flagAddRemove==0){
            btnAddOrRemove.setText("Add");
            for (int i = 0; i < DBHandler.getModuleListByBriefcase(0).size(); i++) {
                //if (!Brief.equals(DBHandler.getModuleList().get(i).get("Vtype"))) {
                Map<String, String> map = new HashMap<>();
                map.put("Name", DBHandler.getModuleListByBriefcase(0).get(i).get("Name"));
                map.put("Vtype", DBHandler.getModuleListByBriefcase(0).get(i).get("Vtype"));
                map.put("CheckBox", "1");
                mapList.add(map);
                //}
            }

        }else if (flagAddRemove==1){
            btnAddOrRemove.setText("Remove");
            for (int i = 0; i < DBHandler.getModuleListByBriefcase(1).size(); i++) {
                //if (Brief.equals(DBHandler.getModuleListByBriefcase(1).get(i).get("Vtype"))) {
                    Map<String, String> map = new HashMap<>();
                    map.put("Name", DBHandler.getModuleListByBriefcase(1).get(i).get("Name"));
                    map.put("Vtype", DBHandler.getModuleListByBriefcase(1).get(i).get("Vtype"));
                    map.put("CheckBox", "1");
                    mapList.add(map);
                //}
            }
        }
        btnAddOrRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagAddRemove == 0) {
                    String str = CommanStatic.Briefcase;
                    for (int i = 0; i < BriefcaseChecked.size(); i++) {
                        str = str + "|" + BriefcaseChecked.get(i);
                    }
                    //MessageDialog.MessageDialog(context,"",""+str);
                    Log.i(TAG,"Str:"+str);
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] LoginData = obj.GetSharePreferenceSession(BriefcaseSettingActivity.this);
                        if (LoginData!=null)
                            CallVolleyBriefcase(LoginData[3], LoginData[4], LoginData[0],LoginData[14],str);
                            dialog.dismiss();
                    }else {
                        MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"",""+status);
                    }
                }else if (flagAddRemove == 1){
                    String str = CommanStatic.Briefcase;
                    for (int i = 0; i < BriefcaseChecked.size(); i++) {
                        if (str.contains(String.valueOf(BriefcaseChecked.get(i)))) {
                            String s="|"+BriefcaseChecked.get(i);
                            str = str.replace(s,"");
                            Log.i(TAG,"Str:"+s+"\t"+str);
                        }
                    }
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] LoginData = obj.GetSharePreferenceSession(BriefcaseSettingActivity.this);
                        if (LoginData!=null)
                            CallVolleyBriefcase(LoginData[3], LoginData[4], LoginData[0],LoginData[14],str);
                            dialog.dismiss();
                    }else {
                        MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"",""+status);
                    }
                    //MessageDialog.MessageDialog(context,"",""+str+" \t Brief:"+BriefcaseChecked.toString());
                }
            }
        });
        recyclerView.setAdapter(new RecyclerViewBriefcaseAdapter(BriefcaseSettingActivity.this,mapList));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    private void CallVolleyBriefcase(final String DeviceID, final String UserID, final String SessionID,final String CompanyID,final String Briefcase){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UserBriefcaseUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"",""+Msg);
                    CommanStatic.Briefcase = Briefcase;
                    LoadViewData();
                }catch (Exception e){
                    MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"Exception",""+e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(BriefcaseSettingActivity.this,"Error",""+error.toString());
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
                params.put("Briefcase", Briefcase);
                Log.i(TAG,"Parameters:"+params.toString());
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
