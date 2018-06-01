package com.singlagroup;

import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.BranchDataset;
import com.singlagroup.datasets.DivisionDataset;
import com.singlagroup.datasets.GodownDataset;
import com.singlagroup.responsedatasets.ResponseChangePasswordDataset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerActiveSessionManage;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 30-Sept-16.
 */
public class LoginActivity extends AppCompatActivity {
    TextInputLayout usernameWrapper;
    TextInputLayout passwordWrapper;
    Button btnLogin,btnAutoLogin;
    ProgressDialog progressDialog;
    TextView txtViewChangePassword,txtViewForgotPassword,txtViewDeviceInfo;
    private static String TAG = LoginActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_login);
        Initialization();
        LoginConditions();
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginConditions();
                }else{
                    MessageDialog.MessageDialog(LoginActivity.this,"",status);
                }
            }
        });

        //Log.e(TAG,"Number to word converter:"+NumberWordConverter.convert(428823));
    }
    private void Initialization() {
        HomeAcitvity.timeWhenStopped = 0;
        usernameWrapper = (TextInputLayout) findViewById(R.id.editText_UserID);
        passwordWrapper = (TextInputLayout) findViewById(R.id.editText_Password);
        btnLogin = (Button) findViewById(R.id.button_Login);
        btnAutoLogin = (Button) findViewById(R.id.button_Auto_Login);
        txtViewChangePassword = (TextView) findViewById(R.id.textView_ChangePassword);
        txtViewForgotPassword = (TextView) findViewById(R.id.textView_ForgotPassword);
        txtViewDeviceInfo = (TextView) findViewById(R.id.textView_Device_info);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        //TODO: Change Password Onclick
        txtViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = usernameWrapper.getEditText().getText().toString().trim();
                if (!UserName.isEmpty()) {
                    DialogChangePassword(0,UserName);
                }else{
                    MessageDialog.MessageDialog(LoginActivity.this,"","Username cannot be blank");
                }
            }
        });
        txtViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = usernameWrapper.getEditText().getText().toString().trim();
                if (!UserName.isEmpty()) {
                    DialogChangePassword(1,UserName);
                }else{
                    MessageDialog.MessageDialog(LoginActivity.this,"","Username cannot be blank");
                }
            }
        });
        txtViewDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DeviceInfoActivity.class));
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }
    private void LoginConditions(){
        final String DeviceID = getIntent().getExtras().getString("DeviceID");
        final String DefaultUser = getIntent().getExtras().getString("DefultUserID");
        final String DefaultUserPassword = getIntent().getExtras().getString("DefultUserPassword");
        final String Latitude = StaticValues.Latitude;//getIntent().getExtras().getString("Latitude");
        final String Longitude = StaticValues.Longitude;//getIntent().getExtras().getString("Longitude");
        int AllowUser = getIntent().getExtras().getInt("AllowUser");
        final int AutoLogIN = getIntent().getExtras().getInt("AutoLogIN");
        MainActivity obj=new MainActivity();
        final String AppVersion = obj.GetSharePreferenceDeviceInfo(LoginActivity.this).getVersion();
        if(AllowUser==0 && AutoLogIN==0){
            usernameWrapper.getEditText().setText(DefaultUser);
            passwordWrapper.getEditText().requestFocus();
            btnAutoLogin.setVisibility(View.GONE);
        }else if(AllowUser==0 && AutoLogIN==1){
            usernameWrapper.getEditText().setText(DefaultUser);
            btnAutoLogin.setVisibility(View.VISIBLE);
        }else if(AllowUser==1 && AutoLogIN==0) {
            usernameWrapper.getEditText().setText(DefaultUser);
            passwordWrapper.getEditText().requestFocus();
            btnAutoLogin.setVisibility(View.GONE);
        }else if(AllowUser==1 && AutoLogIN==1) {
            usernameWrapper.getEditText().setText(DefaultUser);
            btnAutoLogin.setVisibility(View.VISIBLE);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                if (username.isEmpty()) {
                    usernameWrapper.setError("Username cannot be blank");
                } else if (password.isEmpty()) {
                    passwordWrapper.setError("Password cannot be blank");
                } else {
                    //TODO: Login Request
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        //MakePostRequestLogin("LogInUser",username, password, DeviceID);
                        //CallRetrofitLogin(username, password, DeviceID,Latitude,Longitude,AppVersion);
                        CallVolleyLogin(username, password, DeviceID,Latitude,Longitude,AppVersion,"0");
                    } else {
                        usernameWrapper.getEditText().setError("No Internet Connection");
                    }
                }
            }
        });
        passwordWrapper.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //TODO Login By Enter Key
                    String username = usernameWrapper.getEditText().getText().toString();
                    String password = passwordWrapper.getEditText().getText().toString();
                    if (username.isEmpty()) {
                        usernameWrapper.setError("Username cannot be blank");
                    } else if (password.isEmpty()) {
                        passwordWrapper.setError("Password cannot be blank");
                    } else {
                        //TODO: Login Request
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            //MakePostRequestLogin("LogInUser",username, password ,DeviceID);
                            //CallRetrofitLogin(username, password, DeviceID,Latitude,Longitude,AppVersion);
                            CallVolleyLogin(username, password, DeviceID,Latitude,Longitude,AppVersion,"0");
                        } else {
                            usernameWrapper.getEditText().setError("No Internet Connection");
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        EditText edtusername=(EditText)findViewById(R.id.editText_UserName);
        edtusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(DefaultUser)){
                    btnAutoLogin.setVisibility(View.VISIBLE);
                }else{
                    btnAutoLogin.setVisibility(View.GONE);
                }
            }
        });
        btnAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(AutoLogIN == 1) {
                    String username = DefaultUser;
                    String password = DefaultUserPassword;
                    if(!username.isEmpty() && !password.isEmpty()) {
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            //MakePostRequestLogin("LogInUser",username, password, DeviceID);
                            //CallRetrofitLogin(username, password, DeviceID,Latitude,Longitude,AppVersion);
                            CallVolleyLogin(username, password, DeviceID,Latitude,Longitude,AppVersion,"1");
                        } else {
                            usernameWrapper.getEditText().setError("No Internet Connection");
                        }
                    }
                    else{
                        usernameWrapper.getEditText().setError("UserName & Password may be  wrong !!!");
                    }
                }else{
                    usernameWrapper.getEditText().setError("You don't have permission to Auto-Login !!!");
                }
            }
        });
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void CallVolleyLogin(final String UserName, final String Password, final String DeviceID,final String Latitude,final String Longitude,final String AppVersion,final String AutoLoginStatus){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.STATIC_BASE_URL+"LogInUser", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if(Status==1){
                        CommanStatic.LogIN_UserName = UserName;
                        JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("Result"));
                        JSONArray jsonArrayUserDataRights = jsonObjectResult.getJSONArray("UserDataRights");
                        List<Map<String,String>> listUserDataRights = new ArrayList<>();
                        for(int i=0; i<jsonArrayUserDataRights.length();i++) {
                            String CompanyID = jsonArrayUserDataRights.getJSONObject(i).getString("CompanyID");
                            String CompanyName = jsonArrayUserDataRights.getJSONObject(i).getString("CompanyName");
                            String DBName = jsonArrayUserDataRights.getJSONObject(i).getString("DBName");
                            int IsDefault = jsonArrayUserDataRights.getJSONObject(i).getInt("IsDefault");
                           // System.out.println("IsDefault:"+IsDefault);
                            JSONArray jsonArrayCompanyDataRights = jsonArrayUserDataRights.getJSONObject(i).getJSONArray("CompanyDataRights");
                            //Log.d(TAG, ""+jsonArrayCompanyDataRights.toString());
                            for(int j= 0; j<jsonArrayCompanyDataRights.length();j++){
                                Map<String,String> map=new HashMap<>();
                                map.put("ID", jsonArrayCompanyDataRights.getJSONObject(j).getString("ID"));
                                map.put("Name", jsonArrayCompanyDataRights.getJSONObject(j).getString("Name"));
                                map.put("Active", jsonArrayCompanyDataRights.getJSONObject(j).getString("Active"));
                                map.put("Type", jsonArrayCompanyDataRights.getJSONObject(j).getString("Type"));
                                map.put("UnderID", jsonArrayCompanyDataRights.getJSONObject(j).getString("UnderID"));
                                map.put("DataTypeName", jsonArrayCompanyDataRights.getJSONObject(j).getString("DataTypeName"));
                                map.put("DataType", jsonArrayCompanyDataRights.getJSONObject(j).getString("DataType"));
                                map.put("CompanyID",CompanyID);
                                map.put("CompanyName", CompanyName);
                                map.put("DBName", DBName);
                                map.put("IsDefault", String.valueOf(IsDefault));
                                listUserDataRights.add(map);
                            }
                        }
                        //Log.d(TAG,"list:"+listUserDataRights.toString());
                        String APIUrl = jsonObjectResult.getString("APIURL");
                        JSONArray jsonArrayCaptionRights = jsonObjectResult.getJSONArray("CaptionRights");
                        String SessionID = jsonObjectResult.getString("SessionID");
                        String BasicInfo = jsonObjectResult.getString("BasicInfo");
                        //TODO: Chronometer start
                        if (HomeAcitvity.chronometerActiveTime!=null) {
                            HomeAcitvity.chronometerActiveTime.start();
                        }
                        //TODO: Active Time Insert
                        DatabaseSqlLiteHandlerActiveSessionManage DBSession = new DatabaseSqlLiteHandlerActiveSessionManage(getApplicationContext());
                        if(SessionID!=null) {
                            //DBSession.ActiveSessionManageTableAllDelete();
                            DBSession.insertActiveSessionManageTable(SessionID, "00:00:01");
                            SessionManage.CallRetrofitSessionActiveTime(LoginActivity.this,0);
                        }
                        DatabaseSqlLiteHandlerUserInfo DBUserInfo=new DatabaseSqlLiteHandlerUserInfo(LoginActivity.this);
                        DBUserInfo.UserInfoTableDelete();
                        DBUserInfo.CaptionTableDelete();
                        DBUserInfo.BasicInfoTableDelete();
                        //TODO:Insert User Data Rights
                        DBUserInfo.insertUserInfoTable(listUserDataRights);
                        //TODO: Caption Rights
                        List<Map<String,String>> listCaptionRights = new ArrayList<>();
                        for(int i= 0; i<jsonArrayCaptionRights.length();i++){
                            Map<String,String> map=new HashMap<>();
                            map.put("CaptionID", jsonArrayCaptionRights.getJSONObject(i).getString("CaptionID"));
                            map.put("Caption", jsonArrayCaptionRights.getJSONObject(i).getString("Caption"));
                            map.put("Sequence", jsonArrayCaptionRights.getJSONObject(i).getString("Sequence"));
                            map.put("UnderID", (jsonArrayCaptionRights.getJSONObject(i).getString("UnderID").equals("null")?null:jsonArrayCaptionRights.getJSONObject(i).getString("UnderID")));
                            map.put("IsModule", jsonArrayCaptionRights.getJSONObject(i).getString("IsModule"));
                            map.put("ViewFlag", jsonArrayCaptionRights.getJSONObject(i).getString("ViewFlag"));
                            map.put("EditFlag", jsonArrayCaptionRights.getJSONObject(i).getString("EditFlag"));
                            map.put("CreateFlag", jsonArrayCaptionRights.getJSONObject(i).getString("CreateFlag"));
                            map.put("RemoveFlag", jsonArrayCaptionRights.getJSONObject(i).getString("RemoveFlag"));
                            map.put("PrintFlag", jsonArrayCaptionRights.getJSONObject(i).getString("PrintFlag"));
                            map.put("ImportFlag",jsonArrayCaptionRights.getJSONObject(i).getString("ImportFlag"));
                            map.put("ExportFlag",jsonArrayCaptionRights.getJSONObject(i).getString("ExportFlag"));
                            map.put("ContentClass", jsonArrayCaptionRights.getJSONObject(i).getString("ContentClass"));
                            map.put("VType", jsonArrayCaptionRights.getJSONObject(i).getString("VType"));
                            listCaptionRights.add(map);
                        }
                        DBUserInfo.insertCaptionTable(listCaptionRights);
                        //MessageDialog.MessageDialog(LoginActivity.this,"",DBUserInfo.getParentCaption().toString());

                        //TODO: Basic Info
                        JSONObject jsonObjectBasicInfo = new JSONObject(BasicInfo);
                        Map<String,String> mapBasicInfo=new HashMap<>();
                        CommanStatic.LogIN_UserPassword = jsonObjectBasicInfo.getString("Password");
                        mapBasicInfo.put("Password", jsonObjectBasicInfo.getString("Password"));
                        mapBasicInfo.put("UserID", jsonObjectBasicInfo.getString("UserID"));
                        mapBasicInfo.put("UserGroupID", jsonObjectBasicInfo.getString("UserGroupID"));
                        mapBasicInfo.put("AutoLaunchModule", jsonObjectBasicInfo.getString("AutoLaunchModule"));
                        mapBasicInfo.put("ID", jsonObjectBasicInfo.getString("ID"));
                        mapBasicInfo.put("Name", jsonObjectBasicInfo.getString("Name"));
                        mapBasicInfo.put("Code", jsonObjectBasicInfo.getString("Code"));
                        mapBasicInfo.put("MasterType", jsonObjectBasicInfo.getString("MasterType"));
                        mapBasicInfo.put("TypeName", jsonObjectBasicInfo.getString("TypeName"));
                        mapBasicInfo.put("CardNo", jsonObjectBasicInfo.getString("CardNo"));
                        mapBasicInfo.put("Address1", jsonObjectBasicInfo.getString("Address1"));
                        mapBasicInfo.put("Address2", jsonObjectBasicInfo.getString("Address2"));
                        mapBasicInfo.put("Address3", jsonObjectBasicInfo.getString("Address3"));
                        mapBasicInfo.put("City", jsonObjectBasicInfo.getString("City"));
                        mapBasicInfo.put("State", jsonObjectBasicInfo.getString("State"));
                        mapBasicInfo.put("Country", jsonObjectBasicInfo.getString("Country"));
                        mapBasicInfo.put("PIN", jsonObjectBasicInfo.getString("PIN"));
                        mapBasicInfo.put("CellNo", jsonObjectBasicInfo.getString("CellNo"));
                        mapBasicInfo.put("Image", jsonObjectBasicInfo.getString("Image"));
                        mapBasicInfo.put("UserFullName", jsonObjectBasicInfo.getString("UserFullName"));

                        DBUserInfo.insertBasicInfoTable(mapBasicInfo);
                        //TODO: Screenshot , AccessLogin, InternetLogin , MultiSession
                        CommanStatic.Screenshot = jsonObjectBasicInfo.getInt("Screenshot");
                        CommanStatic.AccessLogin = jsonObjectBasicInfo.getInt("AccessLogin");
                        CommanStatic.InternetAccess = jsonObjectBasicInfo.getInt("InternetAccess");
                        CommanStatic.MultiSession = jsonObjectBasicInfo.getInt("MultiSession");
                        //TODO: Auto Launch Module
                        CommanStatic.AutoLaunchModule = jsonObjectBasicInfo.getInt("AutoLaunchModule");
                        //TODO: Briefcase
                        CommanStatic.Briefcase = jsonObjectBasicInfo.getString("Briefcase");
                        Log.i(TAG,"Briefcase:"+CommanStatic.Briefcase);
                        //Log.d(TAG,"AutoLaunchModule: "+CommanStatic.AutoLaunchModule+"\nCommanStatic.Briefcase:"+CommanStatic.Briefcase);
                        //TODO: Set details of user in sharePreference
                        String DeviceID = getIntent().getExtras().getString("DeviceID");
                        String[][] str = DBUserInfo.getBasicInfo();
                        List<Map<String,String>> mapListCompany = DBUserInfo.getUserInfoCompanyList();
                        if (str!=null && !mapListCompany.isEmpty() && mapListCompany!=null) {
                            String MasterID = str[0][0];
                            String MasterType = str[0][3];
                            String UserID = str[0][15];
                            String UserGroupID = str[0][16];
                            String CompanyID = mapListCompany.get(0).get("CompanyID");
                            List<DivisionDataset > divisionDatasets = DBUserInfo.getUserInfoDivision(CompanyID);
                            String DivisionID = (divisionDatasets.isEmpty() ? "" : divisionDatasets.get(0).getDivisionID());
                            List<BranchDataset> branchDatasets = DBUserInfo.getUserInfoBranch(CompanyID,DivisionID);
                            String BranchID = (branchDatasets.isEmpty() ? "" : branchDatasets.get(0).getBranchID());
                            List<GodownDataset> godownDatasets = DBUserInfo.getUserInfoGodown(CompanyID,BranchID);
                            String GodownID = (godownDatasets.isEmpty() ? "": godownDatasets.get(0).getGodownID());

                            final String DefaultUser = getIntent().getExtras().getString("DefultUserID");
                            final String DefaultUserPassword = getIntent().getExtras().getString("DefultUserPassword");
                            final String Latitude = StaticValues.Latitude;//getIntent().getExtras().getString("Latitude");
                            final String Longitude = StaticValues.Longitude;//getIntent().getExtras().getString("Longitude");
                            int AllowUser = getIntent().getExtras().getInt("AllowUser");
                            final int AutoLogIN = getIntent().getExtras().getInt("AutoLogIN");

                            SharePreferenceSession(SessionID, APIUrl,MasterType,DeviceID,UserID,DivisionID,GodownID,DefaultUserPassword,Latitude,Longitude,AllowUser,AutoLogIN,DefaultUser,UserGroupID,CompanyID,BranchID,MasterID);
                            //TODO: Url Change at time of Login
                            StaticValues.BASE_URL = APIUrl;
                            //ApiClient.BASE_URL = list.getAPIURL();
                            Log.e(TAG,"Url: "+StaticValues.BASE_URL);
                            //TODO: Redirect to Home Screen 
                            int SingleModuleAutoLaunch = DBUserInfo.getModuleList().size();

                            if (SingleModuleAutoLaunch == 1 && CommanStatic.AutoLaunchModule>0){
                                System.out.println("Autolaunch");
                                int pos = 0;
                                List<Map<String,String>> mapList = DBUserInfo.getModuleList();
                                if (CommanStatic.AutoLaunchModule == Integer.valueOf(mapList.get(pos).get("Vtype"))) {
                                    AutoLaunchModule(mapList,pos);
                                }else{
                                    System.out.println("Home Screen");
                                    Intent intent = new Intent(LoginActivity.this, HomeAcitvity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    CommanStatic.RelaunchAutoModule = 0;
                                }
                            }else {
                                System.out.println("Home Screen");
                                Intent intent = new Intent(LoginActivity.this, HomeAcitvity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                CommanStatic.RelaunchAutoModule = 0;
                            }
                        }
                    }else{
                        MessageDialog.MessageDialog(LoginActivity.this, "", Msg);
                    }
                }catch (JSONException je){
                    MessageDialog.MessageDialog(LoginActivity.this,"",""+je.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("Error.Response", ""+error);
                    MessageDialog.MessageDialog(LoginActivity.this,"Error",""+error.toString());
                    hidepDialog();
                }
            } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("UserName", UserName);
                params.put("Password", Password);
                params.put("DeviceID", DeviceID);
                params.put("Latitude", (Latitude==null)?"":Latitude);
                params.put("Longitude", (Longitude==null)?"":Longitude);
                params.put("AppVersion", AppVersion);
                params.put("AppType", CommanStatic.AppType);
                params.put("AutoLoginStatus", AutoLoginStatus);
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
    public void SharePreferenceSession(String SessionID,String APIUrl,String MasterType,String DeviceID,String UserID,String DivisionID,String GodownID,String DefultUserPassword,String Latitude,String Longitude,int AllowUser,int AutoLogIN,String DefultUser,String UserGroupID,String CompanyID,String BranchID,String MasterID){

        SharedPreferences prefs = getSharedPreferences("MyPrefDeviceSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SessionID", SessionID);
        editor.putString("APIUrl", APIUrl);
        editor.putString("MasterType", MasterType);
        editor.putString("UserID", UserID);
        editor.putString("DeviceID", DeviceID);
        editor.putString("DivisionID", DivisionID);
        editor.putString("GodownID", GodownID);
        editor.putString("DefultUserPassword", DefultUserPassword);
        editor.putString("Latitude", Latitude);
        editor.putString("Longitude", Longitude);
        editor.putString("AllowUser", String.valueOf(AllowUser));
        editor.putString("AutoLogIN", String.valueOf(AutoLogIN));
        editor.putString("DefultUser", DefultUser);
        editor.putString("UserGroupID", UserGroupID);
        editor.putString("CompanyID", CompanyID);
        editor.putString("BranchID", BranchID);
        editor.putString("MasterID", MasterID);
        editor.commit();
    }
    public String[] GetSharePreferenceSession(Context context) {
        String[] str=new String[17];
        SharedPreferences pref = context.getSharedPreferences("MyPrefDeviceSession", MODE_PRIVATE);
        str[0] = pref.getString("SessionID", null);
        str[1] = pref.getString("APIUrl", null);
        str[2] = pref.getString("MasterType", null);
        str[3] = pref.getString("DeviceID", null);
        str[4] = pref.getString("UserID", null);
        str[5] = pref.getString("DivisionID", null);
        str[6] = pref.getString("GodownID", null);
        str[7] = pref.getString("DefultUserPassword", null);
        str[8] = pref.getString("Latitude", null);
        str[9] = pref.getString("Longitude", null);
        str[10] = pref.getString("AllowUser", null);
        str[11] = pref.getString("AutoLogIN", null);
        str[12] = pref.getString("DefultUser", null);
        str[13] = pref.getString("UserGroupID", null);
        str[14] = pref.getString("CompanyID", null);
        str[15] = pref.getString("BranchID", null);
        str[16] = pref.getString("MasterID", null);
        return str;
    }
    private void DialogChangePassword(int flag, final String UserName){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(LoginActivity.this, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_change_password);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        if(flag==0){
            final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
            final TextInputLayout OldPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_OldPassword);
            final TextInputLayout NewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_NewPassword);
            final TextInputLayout ConfirmPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_confirmPassword);
            CursorColor.CursorColor(OldPasswordWrapper.getEditText());
            CursorColor.CursorColor(NewPasswordWrapper.getEditText());
            CursorColor.CursorColor(ConfirmPasswordWrapper.getEditText());
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Change Password Request
                    String status = NetworkUtils.getConnectivityStatusString(LoginActivity.this);
                    if (!status.contentEquals("No Internet Connection")) {
                        OldPasswordWrapper.setError("");
                        NewPasswordWrapper.setError("");
                        ConfirmPasswordWrapper.setError("");
                        String OldPass = OldPasswordWrapper.getEditText().getText().toString();
                        String NewPass = NewPasswordWrapper.getEditText().getText().toString();
                        String ConfirmPass = ConfirmPasswordWrapper.getEditText().getText().toString();
                        String DeviceID = getIntent().getExtras().getString("DeviceID");
                        //String UserName = getIntent().getExtras().getString("DefultUserID");
                        if(!OldPass.isEmpty()) {
                            if(!NewPass.isEmpty() && !ConfirmPass.isEmpty()) {
                                if (ConfirmPass.contentEquals(NewPass)) {
                                    CallRetrofitChangePassword(DeviceID, UserName, OldPass, NewPass);
                                    dialog.dismiss();
                                } else {
                                    ConfirmPasswordWrapper.setError("New & Confirm password  mismatch!!! Please enter correct password");
                                }
                            }else{
                                if(NewPass.isEmpty()) {
                                    NewPasswordWrapper.setError("New password cann't be blank");
                                }else if (ConfirmPass.isEmpty()) {
                                    ConfirmPasswordWrapper.setError("Confirm password cann't be blank");
                                }
                            }
                        }else {
                            OldPasswordWrapper.setError("Old password cann't be blank");
                        }
                    } else {
                        MessageDialog.MessageDialog(LoginActivity.this,"","No Internet Connection");
                    }
                }
            });
        }else if(flag==1){
            final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
            final TextView txtViewMsg = (TextView) dialog.findViewById(R.id.text_Msg);
            RadioGroup radioGroupNewForgot = (RadioGroup) dialog.findViewById(R.id.RadioGroup_NewForgot);
            final TextInputLayout OldPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_OldPassword);
            final TextInputLayout NewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_NewPassword);
            final TextInputLayout ConfirmPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_confirmPassword);
            final Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            CursorColor.CursorColor(OldPasswordWrapper.getEditText());
            CursorColor.CursorColor(NewPasswordWrapper.getEditText());
            CursorColor.CursorColor(ConfirmPasswordWrapper.getEditText());
            txtViewTitle.setText("New User/Forgot Password");
            radioGroupNewForgot.setVisibility(View.VISIBLE);
            OldPasswordWrapper.setVisibility(View.GONE);
            radioGroupNewForgot.check(R.id.RadioButton_NewUser);
            radioGroupNewForgot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_NewUser){
                        txtViewMsg.setVisibility(View.GONE);
                        NewPasswordWrapper.setError("");
                        NewPasswordWrapper.getEditText().setText("");
                        ConfirmPasswordWrapper.setError("");
                        ConfirmPasswordWrapper.getEditText().setText("");

                    }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_ForgotPassword){
                        txtViewMsg.setVisibility(View.VISIBLE);
                        NewPasswordWrapper.setError("");
                        NewPasswordWrapper.getEditText().setText("");
                        ConfirmPasswordWrapper.setError("");
                        ConfirmPasswordWrapper.getEditText().setText("");
                    }
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: New User/Forgot Password Request
                    String status = NetworkUtils.getConnectivityStatusString(LoginActivity.this);
                    if (!status.contentEquals("No Internet Connection")) {
                        NewPasswordWrapper.setError("");
                        ConfirmPasswordWrapper.setError("");
                        String NewPass = NewPasswordWrapper.getEditText().getText().toString();
                        String ConfirmPass = ConfirmPasswordWrapper.getEditText().getText().toString();
                        String DeviceID = getIntent().getExtras().getString("DeviceID");
                        //String UserName = getIntent().getExtras().getString("DefultUserID");
                        if(!NewPass.isEmpty() && !ConfirmPass.isEmpty()) {
                            if (ConfirmPass.contentEquals(NewPass)) {
                                CallRetrofitChangePassword(DeviceID, UserName, "", NewPass);
                                dialog.dismiss();
                            } else {
                                ConfirmPasswordWrapper.setError("New & Confirm password  mismatch!!! Please enter correct password");
                            }
                        }else{
                            if(NewPass.isEmpty()) {
                                NewPasswordWrapper.setError("New password cann't be blank");
                            }else if (ConfirmPass.isEmpty()) {
                                ConfirmPasswordWrapper.setError("Confirm password cann't be blank");
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(LoginActivity.this,"","No Internet Connection");
                    }
                }
            });
        }
    }
    private void CallRetrofitChangePassword(final String DeviceID,final String UserName, final String OldPass, final String NewPass){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("UserName", UserName);
        params.put("OldPass", OldPass);
        params.put("NewPass", NewPass);
        params.put("AppType", "1");
        Log.d(TAG,"Parameters: "+params.toString());
        Call<ResponseChangePasswordDataset> call = apiService.getChangePassword(params);
        call.enqueue(new Callback<ResponseChangePasswordDataset>() {
            @Override
            public void onResponse(Call<ResponseChangePasswordDataset> call, retrofit2.Response<ResponseChangePasswordDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            MessageDialog.MessageDialog(LoginActivity.this,"Change Password",msg);
                        } else {
                            MessageDialog.MessageDialog(LoginActivity.this,"Change Password",msg);
                        }
                    }else {
                        MessageDialog.MessageDialog(LoginActivity.this,"","Server not responding: "+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG,"Login Exception:"+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(LoginActivity.this,"Exception","LogInUser API",e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseChangePasswordDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog.MessageDialog(LoginActivity.this,"Failure LogInUser API",t.toString());
                hidepDialog();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Log.d("BackKey","Back key pressed then restart app");
            finishAffinity();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return true;
    }
    private void AutoLaunchModule(List<Map<String,String>> mapList,int i){
        String ClassName = mapList.get(i).get("ContentClass");
        if (!ClassName.isEmpty() && ClassName != null) {
            try {
                //TODO: Set Bundle
                Bundle bundle = new Bundle();
                bundle.putString("Title", mapList.get(i).get("Name"));
                bundle.putInt("ViewFlag", Integer.valueOf(mapList.get(i).get("ViewFlag")));
                bundle.putInt("EditFlag", Integer.valueOf(mapList.get(i).get("EditFlag")));
                bundle.putInt("CreateFlag", Integer.valueOf(mapList.get(i).get("CreateFlag")));
                bundle.putInt("RemoveFlag", Integer.valueOf(mapList.get(i).get("RemoveFlag")));
                bundle.putInt("PrintFlag", Integer.valueOf(mapList.get(i).get("PrintFlag")));
                bundle.putInt("ImportFlag", Integer.valueOf(mapList.get(i).get("ImportFlag")));
                bundle.putInt("ExportFlag", Integer.valueOf(mapList.get(i).get("ExportFlag")));
                //TODO: Intent the Activities by Class
                Intent intent = new Intent(LoginActivity.this, Class.forName(ClassName));
                intent.putExtra("PermissionBundle", bundle);
                startActivity(intent);
                finish();
                CommanStatic.RelaunchAutoModule = 1;
            } catch (Exception e) {
                MessageDialog.MessageDialog(LoginActivity.this, "Error", ""+e.toString());
            }

        } else {
            MessageDialog messageDialog = new MessageDialog();
            messageDialog.MessageDialog(LoginActivity.this, "", "", "Comming soon.....");
        }
    }
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getBundleExtra("extras");
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            byte[] id = intent.getByteArrayExtra("icon");

            try {
                if (pack.contains("com.whatsapp")) {
                    Log.i(TAG, "Notification:" + title + "\nText:" + text);
                    //Toast.makeText(context, "" + title + "\nText:" + text, Toast.LENGTH_LONG).show();
                    //MessageDialog.MessageDialog(context,""+title,""+text);
                    //ShareOnWhatsApp(context,intent);
                }else {
                    //Toast.makeText(context, "Rest of whats app notification", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i(TAG,"Exception:"+e.toString());
            }
        }
    };
    private void ShareOnWhatsApp(Context context,Intent intent){
        Bundle extras = intent.getBundleExtra("extras");
        String pack = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        byte[] byteArray = intent.getByteArrayExtra("icon");
        String PostTime = intent.getStringExtra("PostTime");
        try {
            Intent shareIntent = new Intent();
            if (pack.contains("com.whatsapp")) {
                Log.i(TAG, "Notification:" + title + "\nText:" + text);
                //Toast.makeText(context, "" + title + "\nText:" + text+"\nIcon:"+id.toString(), Toast.LENGTH_LONG).show();
                Bitmap bmp = null;
                if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                    // this bitmap contain the picture attachment
                    bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, bmp));
                }
//                if(byteArray !=null) {
//                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, bmp));
//                }
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(shareIntent);

            }

        } catch (Exception e) {
            MessageDialog.MessageDialog(context,"Exception",""+e.toString());
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
