package com.singlagroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.adapters.ConnectionLogAdapter;
import com.singlagroup.customwidgets.AeSimpleSHA1;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.connection.ConnectionLog;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import administration.VoucherAuthorizeListActivity;
import administration.datasets.VTypeDetailsDataset;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.customerlist.CloseOrdersActivity;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;

/**
 * Created by Rakesh on 10-Oct-16.
 */

public class ConnectionActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Button btnConStop;
    private ConnectionLogAdapter adapter;
    private ProgressDialog progressDialog;
    private static String TAG = ConnectionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = ConnectionActivity.this;
        this.actionBar=getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnConStop = (Button) findViewById(R.id.button_book_order); //TODO: Stop Connection Button
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ModulePermission();
            }
        });
    }
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            StaticValues.Vtype = bundle.getInt("Vtype");
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                if (!CommanStatic.LogIN_UserPassword.isEmpty()) {
                    DialogChangePassword(CommanStatic.LogIN_UserPassword);
                }else {
                    MessageDialog.MessageDialog(context,"","Encripted Password is blank");
                }
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Button Visible
        btnConStop.setVisibility(View.VISIBLE);
        btnConStop.setText("Stop Connection");
        btnConStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null)
                    DilaogConfirmationForStopConnection(str[3], str[4]);
            }
        });
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyConnectionLog(str[3], str[4]);
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
                CallVolleyConnectionLog(str[3], str[4]);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Re-authentication password
    private void DialogChangePassword(final String EncrytedPassword){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_authenticate_password);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
        final TextInputLayout PasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_Password);
        CursorColor.CursorColor(PasswordWrapper.getEditText());
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Password Authenticate
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    PasswordWrapper.setError("");
                    String Pass = PasswordWrapper.getEditText().getText().toString();
                    if(!Pass.isEmpty()) {
                        try {
                            String Encripted = AeSimpleSHA1.SHA1(Pass);
                            if (Encripted.equals(EncrytedPassword)){
                                dialog.dismiss();
                                CallApiMethod();
                            }else {
                                MessageDialog.MessageDialog(context,"Password Authentication","Your password may be wrong!!!");
                            }
                        }catch (NoSuchAlgorithmException e){
                            MessageDialog.MessageDialog(context,"NoSuchAlgorithmException",""+e.toString());
                        }catch (UnsupportedEncodingException e){
                            MessageDialog.MessageDialog(context,"UnsupportedEncodingException",""+e.toString());
                        }
                    }else {
                        PasswordWrapper.setError("Password cann't be blank");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
        hidepDialog();
    }
    private void DilaogConfirmationForStopConnection(final String DeviceID, final String UserID){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to replace it?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    CallVolleyConnectionStop(DeviceID, UserID);
                    dialog.dismiss();
                }else{
                    MessageDialog.MessageDialog(context,"",status);
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
    private void LoadRecyclerView(List<ConnectionLog> connectionLogList){
        adapter = new ConnectionLogAdapter(context,connectionLogList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void CallVolleyConnectionLog(final String DeviceID, final String UserID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"DBCloseLog", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<ConnectionLog> connectionLogList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        if (jsonArrayResult.length()>0){
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                ConnectionLog cLog = new ConnectionLog();
                                cLog.setID(jsonArrayResult.getJSONObject(i).optString("ID")==null ? "" : jsonArrayResult.getJSONObject(i).optString("ID"));
                                cLog.setDeviceID(jsonArrayResult.getJSONObject(i).optString("DeviceID")==null ? "" : jsonArrayResult.getJSONObject(i).optString("DeviceID"));
                                cLog.setUserID(jsonArrayResult.getJSONObject(i).optString("UserID")==null ? "" : jsonArrayResult.getJSONObject(i).optString("UserID"));
                                cLog.setUserName(jsonArrayResult.getJSONObject(i).optString("UserName")==null ? "" : jsonArrayResult.getJSONObject(i).optString("UserName"));
                                cLog.setImeiNo(jsonArrayResult.getJSONObject(i).optString("imeiNo")==null ? "" : jsonArrayResult.getJSONObject(i).optString("imeiNo"));
                                cLog.setMacId(jsonArrayResult.getJSONObject(i).optString("macId")==null ? "" : jsonArrayResult.getJSONObject(i).optString("macId"));
                                cLog.setModelNo(jsonArrayResult.getJSONObject(i).optString("modelNo")==null ? "" : jsonArrayResult.getJSONObject(i).optString("modelNo"));
                                cLog.setUpdateDate(jsonArrayResult.getJSONObject(i).optString("UpdateDate")==null ? "" : jsonArrayResult.getJSONObject(i).optString("UpdateDate"));
                                cLog.setType(jsonArrayResult.getJSONObject(i).optInt("Type"));
                                cLog.setConnectionStatus(jsonArrayResult.getJSONObject(i).optString("ConnectionStatus")==null ? "" : jsonArrayResult.getJSONObject(i).optString("ConnectionStatus"));
                                connectionLogList.add(cLog);
                            }
                        }
                        LoadRecyclerView(connectionLogList);
                    }else {
                        MessageDialog.MessageDialog(context,"",""+Msg);
                        LoadRecyclerView(connectionLogList);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    LoadRecyclerView(connectionLogList);
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(ConnectionActivity.this,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                Log.d(TAG,"Connection Log Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyConnectionStop(final String DeviceID, final String UserID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StopDB", new Response.Listener<String>()
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
                        MessageDialogByIntent(context,"",""+Msg);
                    }else {
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
                MessageDialog.MessageDialog(ConnectionActivity.this,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                Log.d(TAG,"Connection Log Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
        try {
            final Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.cardview_message_box);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
            TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
            Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
            txtViewMessageTitle.setText(Title);
            txtViewMessage.setText(Mesaage);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    //TODO: Activity Intent to Parent Caption
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null || refreshLayout!=null) {
            progressDialog.dismiss();
            refreshLayout.setRefreshing(false);
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
}
