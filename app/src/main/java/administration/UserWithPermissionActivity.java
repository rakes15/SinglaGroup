package administration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.customwidgets.AeSimpleSHA1;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.DivisionDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import administration.adapter.VoucherAuthorizeListAdapter;
import administration.database.DatabaseSqlLiteHandlerVoucherAuthorization;
import administration.datasets.Module;
import administration.datasets.UserWithPermission;
import administration.datasets.VoucherAuthorizeListDataset;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class UserWithPermissionActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private Spinner spnDivision;
    private CommonSearchableSpinner spnUser,spnModule;
    private ProgressBar progressBarUser,progressBarModule;
    private TextView txtUser,txtModule;
    private RadioGroup rGAccessLogin,rGMultiSession,rGInternetAccess,rGSnapshot;
    private Button btnSave;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private String ID = "",Name = "" ,VType = "" ,Caption = "",DivisionID="",DivisionName="",UserID="";
    private String AccessLogin = "0",MSession="0",InternetAccess="0",Snapshot="0",AutoLaunchModule="0";
    private static String TAG = UserWithPermissionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_user_permission);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = UserWithPermissionActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        spnDivision = (Spinner) findViewById(R.id.Spinner_Division);
        spnUser = (CommonSearchableSpinner) findViewById(R.id.spinner_User);
        spnModule = (CommonSearchableSpinner) findViewById(R.id.Spinner_Auto_launch_module);
        progressBarUser = (ProgressBar) findViewById(R.id.progressBarUser);
        progressBarModule = (ProgressBar) findViewById(R.id.progressBarModule);
        txtUser = (TextView) findViewById(R.id.text_User);
        txtModule = (TextView) findViewById(R.id.text_Module);
        rGAccessLogin = (RadioGroup) findViewById(R.id.radio_group_access_login);
        rGMultiSession = (RadioGroup) findViewById(R.id.radio_group_multi_session);
        rGInternetAccess = (RadioGroup) findViewById(R.id.radio_group_internet_access);
        rGSnapshot = (RadioGroup) findViewById(R.id.radio_group_snapshot);
        btnSave = (Button) findViewById(R.id.Button_Save);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this);
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
                CallApiMethod();
                ClickEvents();
            }else {
                MessageDialog.MessageDialog(UserWithPermissionActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(UserWithPermissionActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(UserWithPermissionActivity.this);
                    if (str!=null) {
                        setDivisionList();
                    }
                } else {
                    MessageDialog.MessageDialog(UserWithPermissionActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(UserWithPermissionActivity.this);
            if (str!=null) {
                setDivisionList();
            }
        } else {
            MessageDialog.MessageDialog(context,"","No Internet Connection");
        }
    }
    private void ClickEvents(){
        rGAccessLogin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_default_device){
                    AccessLogin = "0";
                }else if (checkedId == R.id.radio_button_all_device){
                    AccessLogin = "1";
                }
            }
        });
        rGMultiSession.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_ms_no){
                    MSession = "0";
                }else if (checkedId == R.id.radio_button_ms_yes){
                    MSession = "1";
                }
            }
        });
        rGInternetAccess.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_ia_no){
                    InternetAccess = "0";
                }else if (checkedId == R.id.radio_button_ia_yes){
                    InternetAccess = "1";
                }
            }
        });
        rGSnapshot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_snapshot_no){
                    Snapshot = "0";
                }else if (checkedId == R.id.radio_button_snapshot_yes){
                    Snapshot = "1";
                }
            }
        });
        //TODO: Button Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DivisionID.isEmpty() && !UserID.isEmpty()) {
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null) {
                            if (StaticValues.editFlag == 1) {
                                CallVolleyUserPermissionUpdate(str[3], str[0], str[14], str[4], DivisionID, UserID, AccessLogin, MSession, InternetAccess, Snapshot, AutoLaunchModule);
                            } else {
                                MessageDialog.MessageDialog(context, "", "You don't have permission to update");
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context, "", "No Internet Connection");
                    }
                } else {
                    if (DivisionID.isEmpty()) { MessageDialog.MessageDialog(context, "", "Please select Division"); }
                    if (UserID.isEmpty()) { MessageDialog.MessageDialog(context, "", "Please select User"); }
                }
            }
        });
    }
    private void setDivisionList(){
        LoginActivity obj=new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(context);
        if (str!=null) {
            final List<DivisionDataset> divisionDatasetList = DBHandler.getUserInfoDivision(str[14]);
            spnDivision.setAdapter(new DivisionFilterableAdapter(context, divisionDatasetList, divisionDatasetList));
            spnDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DivisionID = divisionDatasetList.get(position).getDivisionID();
                    DivisionName = divisionDatasetList.get(position).getDivisionName();
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(UserWithPermissionActivity.this);
                    if (str!=null) {
                        CallVolleyUserList(str[3], str[0], str[14], str[4], DivisionID);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }
    private void CallVolleyUserList(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SGUserListWithPermission", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                ArrayList<Map<String,String>> mapList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        if (jsonArrayResult.length() > 0){
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                UserWithPermission userWithPermission = new UserWithPermission();
                                userWithPermission.setUserID(jsonArrayResult.getJSONObject(i).optString("UserID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserID"));
                                userWithPermission.setUserName(jsonArrayResult.getJSONObject(i).optString("UserName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserName"));
                                userWithPermission.setUserGroupID(jsonArrayResult.getJSONObject(i).optString("UserGroupID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserGroupID"));
                                userWithPermission.setUserGroup(jsonArrayResult.getJSONObject(i).optString("UserGroup") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserGroup"));
                                userWithPermission.setFullName(jsonArrayResult.getJSONObject(i).optString("FullName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("FullName"));
                                userWithPermission.setTaggedName(jsonArrayResult.getJSONObject(i).optString("TaggedName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("TaggedName"));
                                userWithPermission.setEmailID(jsonArrayResult.getJSONObject(i).optString("EmailID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("EmailID"));
                                userWithPermission.setTypeName(jsonArrayResult.getJSONObject(i).optString("TypeName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("TypeName"));
                                userWithPermission.setPhone(jsonArrayResult.getJSONObject(i).optString("Phone") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Phone"));
                                userWithPermission.setSGDeviceAccess(jsonArrayResult.getJSONObject(i).optInt("SG_DeviceAccess"));
                                userWithPermission.setSGMultiSession(jsonArrayResult.getJSONObject(i).optInt("SG_MultiSession"));
                                userWithPermission.setSGWebAccessFlag(jsonArrayResult.getJSONObject(i).optInt("SG_WebAccessFlag"));
                                userWithPermission.setSGScreenshot(jsonArrayResult.getJSONObject(i).optInt("SG_Screenshot"));
                                userWithPermission.setSGAutoLaunchModule(jsonArrayResult.getJSONObject(i).optInt("SG_AutoLaunchModule"));
                                Map<String,String> map = new HashMap<>();
                                String ID = userWithPermission.getUserID()+"@"+userWithPermission.getUserGroupID()+"@"+userWithPermission.getSGDeviceAccess()+"@"+userWithPermission.getSGMultiSession()+"@"+userWithPermission.getSGWebAccessFlag()+"@"+userWithPermission.getSGScreenshot()+"@"+userWithPermission.getSGAutoLaunchModule();
                                String Name = userWithPermission.getFullName()+"("+userWithPermission.getUserName()+")"+userWithPermission.getTypeName()+"|"+userWithPermission.getTaggedName();
                                map.put("ID",ID);
                                map.put("Name",Name);
                                //map.put("Type",Type);
                                mapList.add(map);
                            }
                        }
                        setUserListSpinner(spnUser,mapList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                Log.i(TAG,"User list parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void setUserListSpinner(Spinner spn, final ArrayList<Map<String,String>> userWithPermissionList){
        spn.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,userWithPermissionList,userWithPermissionList));
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                txtUser.setVisibility(View.GONE);
                ID = userWithPermissionList.get(position).get("ID");
                Name = userWithPermissionList.get(position).get("Name");
                String[] array = ID.split("@");
                if (array.length > 0) {
                    //TODO: UserID
                    UserID = array[0];
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyModuleList(str[3], str[0], str[14], UserID, DivisionID);
                    }
                    //TODO: Access Login
                    AccessLogin = array[2];
                    RadioButton rb_def_device = (RadioButton) findViewById(R.id.radio_button_default_device);
                    RadioButton rb_all_device = (RadioButton) findViewById(R.id.radio_button_all_device);
                    if (AccessLogin.equals("0")){
                        rb_def_device.setChecked(true);
                    }else if (AccessLogin.equals("1")){
                        rb_all_device.setChecked(true);
                    }
                    //TODO: Multi session
                    MSession = array[3];
                    RadioButton rb_ms_yes = (RadioButton) findViewById(R.id.radio_button_ms_yes);
                    RadioButton rb_ms_no = (RadioButton) findViewById(R.id.radio_button_ms_no);
                    if (MSession.equals("0")){
                        rb_ms_no.setChecked(true);
                    }else if (MSession.equals("1")){
                        rb_ms_yes.setChecked(true);
                    }
                    //TODO: Internet Access
                    InternetAccess = array[4];
                    RadioButton rb_ia_yes = (RadioButton) findViewById(R.id.radio_button_ia_yes);
                    RadioButton rb_ia_no = (RadioButton) findViewById(R.id.radio_button_ia_no);
                    if (InternetAccess.equals("0")){
                        rb_ia_no.setChecked(true);
                    }else if (InternetAccess.equals("1")){
                        rb_ia_yes.setChecked(true);
                    }
                    //TODO: Snapshot
                    Snapshot = array[5];
                    RadioButton rb_snapshot_yes = (RadioButton) findViewById(R.id.radio_button_snapshot_yes);
                    RadioButton rb_snapshot_no = (RadioButton) findViewById(R.id.radio_button_snapshot_no);
                    if (Snapshot.equals("0")){
                        rb_snapshot_no.setChecked(true);
                    }else if (Snapshot.equals("1")){
                        rb_snapshot_yes.setChecked(true);
                    }
                    //TODO: Auto Launch Module
                    VType = array[6];
                    AutoLaunchModule = array[6];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void CallVolleyModuleList(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SGModuleList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                ArrayList<Map<String,String>> mapList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        if (jsonArrayResult.length() > 0){
                            Map<String,String> map = new HashMap<>();
                            map.put("ID","0");
                            map.put("Name","Select Default Auto launch module");
                            mapList.add(map);
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                Module module = new Module();
                                module.setVType(jsonArrayResult.getJSONObject(i).optInt("VType"));
                                module.setCaption(jsonArrayResult.getJSONObject(i).optString("Caption") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Caption"));
                                map = new HashMap<>();
                                map.put("ID",""+module.getVType());
                                map.put("Name",module.getCaption());
                                mapList.add(map);
                            }
                        }
                        setModuleListSpinner(spnModule,mapList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                Log.d(TAG,"Module list parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void setModuleListSpinner(Spinner spn, final ArrayList<Map<String,String>> moduleList){
        for (int i=0; i<moduleList.size(); i++){
            if (VType.equals(moduleList.get(i).get("ID"))){
                spn.setSelection(i);
                break;
            }
        }
        spn.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,moduleList,moduleList));
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                txtModule.setVisibility(View.GONE);
                VType = moduleList.get(position).get("ID");
                Caption = moduleList.get(position).get("Name");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void CallVolleyUserPermissionUpdate(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String ID,final String AccessLogIn,final String MSession,final String InternetAccess,final String Screenshot,final String AutoLaunchModule){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SGUserPermissionUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<Map<String,String>> mapList = new ArrayList<>();
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        MessageDialog.MessageDialog(context,"",Msg);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                params.put("ID", ID);
                params.put("AccessLogIn", AccessLogIn);
                params.put("MSession", MSession);
                params.put("InternetAccess", InternetAccess);
                params.put("Screenshot", Screenshot);
                params.put("AutoLaunchModule", AutoLaunchModule);
                Log.i(TAG,"User Permission Update parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
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
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
}
