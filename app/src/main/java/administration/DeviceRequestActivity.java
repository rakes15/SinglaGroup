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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.DivisionDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import administration.adapter.DeviceListAdapter;
import administration.datasets.Device;
import administration.datasets.Module;
import administration.datasets.UserWithPermission;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class DeviceRequestActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private RecyclerView recyclerView;
    private CommonSearchableSpinner spnUser;
    private ProgressBar progressBarUser;
    private TextView txtUser;
    private RadioGroup rGAllowedUser,rGRequestStatus,rGOwnership,rGAutoLogin;
    private RadioButton rBAUAnyUser,rBAUDefUser,rBRSApproved,rBRSReject,rBOwnPersnal,rBOwnCompany,rBALYes,rBALNo;
    private TextInputLayout edtDeviceNo,edtRemarks;
    private Device device;
    private ProgressDialog progressDialog;
    private DeviceListAdapter adapter;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private String ID = "",Name = "" ,VType = "" ,Caption = "",DivisionID="",DivisionName="",UserID="";
    private String Status="0";
    private String AllowedUser = "0",RequestStatus="0",Ownership="0",DeviceNo="",Remarks="",AutoLogin="0";
    private static String TAG = DeviceRequestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = DeviceRequestActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        spnDivision = (Spinner) findViewById(R.id.Spinner_Division);
//        spnUser = (CommonSearchableSpinner) findViewById(R.id.spinner_User);
//        spnModule = (CommonSearchableSpinner) findViewById(R.id.Spinner_Auto_launch_module);
//        progressBarUser = (ProgressBar) findViewById(R.id.progressBarUser);
//        progressBarModule = (ProgressBar) findViewById(R.id.progressBarModule);
//        txtUser = (TextView) findViewById(R.id.text_User);
//        txtModule = (TextView) findViewById(R.id.text_Module);
//        rGAccessLogin = (RadioGroup) findViewById(R.id.radio_group_access_login);
//        rGMultiSession = (RadioGroup) findViewById(R.id.radio_group_multi_session);
//        rGInternetAccess = (RadioGroup) findViewById(R.id.radio_group_internet_access);
//        rGSnapshot = (RadioGroup) findViewById(R.id.radio_group_snapshot);
//        btnSave = (Button) findViewById(R.id.Button_Save);
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
                DialogFilter(context);
                //ClickEvents();
            }else {
                MessageDialog.MessageDialog(DeviceRequestActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(DeviceRequestActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(DeviceRequestActivity.this);
                    if (str!=null && !DivisionID.isEmpty()) {
                        CallVolleyDeviceList(str[3], str[0], str[14], str[4],DivisionID,Status,1);
                    }
                } else {
                    MessageDialog.MessageDialog(DeviceRequestActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(DeviceRequestActivity.this);
            if (str!=null && !DivisionID.isEmpty()) {
                CallVolleyDeviceList(str[3], str[0], str[14], str[4],DivisionID,Status,1);
            }
        } else {
            MessageDialog.MessageDialog(context,"","No Internet Connection");
        }
    }
    private void DialogFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        TextView txtHeader = (TextView) dialog.findViewById(R.id.textView_Header);
        Spinner spn = (Spinner) dialog.findViewById(R.id.spinner);
        RelativeLayout rLDeviceStatus = (RelativeLayout) dialog.findViewById(R.id.RelativeLayout_Device_Status);
        RadioGroup radioGroup  = (RadioGroup) dialog.findViewById(R.id.radio_group);
        RadioButton rBDevicePending = (RadioButton) dialog.findViewById(R.id.radio_button_pending);
        RadioButton rBDeviceActive = (RadioButton) dialog.findViewById(R.id.radio_button_active);
        RadioButton rBDeviceReject = (RadioButton) dialog.findViewById(R.id.radio_button_reject);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Header
        txtHeader.setText("Division");
        //TODO: Spinner
        setDivisionList(spn);
        //TODO: Device Status
        rLDeviceStatus.setVisibility(View.VISIBLE);
        if (Status.equals("0")){
            //TODO: Pending
            rBDevicePending.setChecked(true);
        }else if (Status.equals("1")){
            //TODO: Active
            rBDeviceActive.setChecked(true);
        }else if (Status.equals("2")){
            //TODO: Reject
            rBDeviceReject.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId ==R.id.radio_button_pending){
                    //TODO: Pending
                    Status = "0";
                }else if (checkId ==R.id.radio_button_active){
                    //TODO: Active
                    Status = "1";
                }else if (checkId ==R.id.radio_button_reject){
                    //TODO: Reject
                    Status = "2";
                }
            }
        });
        //tODO: Button Apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null && !DivisionID.isEmpty())
                        CallVolleyDeviceList(str[3], str[0], str[14], str[4],DivisionID,Status,0);
                        dialog.dismiss();
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
                //CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate),String.valueOf(Type), 1);
            }
        });
    }
    private void setDivisionList(Spinner spn){
        LoginActivity obj=new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(context);
        if (str!=null) {
            final List<DivisionDataset> divisionDatasetList = DBHandler.getUserInfoDivision(str[14]);
            int pos = 0;
            for (int i=0; i<divisionDatasetList.size(); i++){
                if (divisionDatasetList.get(i).getDivisionID().equals(DivisionID)){
                    pos = i;
                    break;
                }
            }
            spn.setAdapter(new DivisionFilterableAdapter(context, divisionDatasetList, divisionDatasetList));
            spn.setSelection(pos);
            spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DivisionID = divisionDatasetList.get(position).getDivisionID();
                    DivisionName = divisionDatasetList.get(position).getDivisionName();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }
    private void CallVolleyDeviceList(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String Status,final int flag){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"IAppDeviceList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<Device> deviceList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        if (jsonArrayResult.length() > 0){
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                Device device = new Device();
                                device.setID(jsonArrayResult.getJSONObject(i).optString("ID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ID"));
                                device.setUserID(jsonArrayResult.getJSONObject(i).optString("UserID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserID"));
                                device.setFullName(jsonArrayResult.getJSONObject(i).optString("FullName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("FullName"));
                                device.setUserName(jsonArrayResult.getJSONObject(i).optString("UserName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserName"));
                                device.setUKey(jsonArrayResult.getJSONObject(i).optString("UKey") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UKey"));
                                device.setIMEINo(jsonArrayResult.getJSONObject(i).optString("IMEINo") == null ? "" : jsonArrayResult.getJSONObject(i).optString("IMEINo"));
                                device.setDModel(jsonArrayResult.getJSONObject(i).optString("DModel") == null ? "" : jsonArrayResult.getJSONObject(i).optString("DModel"));
                                device.setMobileNo(jsonArrayResult.getJSONObject(i).optString("MobileNo") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MobileNo"));
                                device.setUserID(jsonArrayResult.getJSONObject(i).optString("UserID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UserID"));
                                device.setReqDt(jsonArrayResult.getJSONObject(i).optString("ReqDt") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ReqDt"));
                                device.setRefName(jsonArrayResult.getJSONObject(i).optString("RefName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("RefName"));
                                device.setRefRemarks(jsonArrayResult.getJSONObject(i).optString("RefRemarks") == null ? "" : jsonArrayResult.getJSONObject(i).optString("RefRemarks"));
                                device.setMacID(jsonArrayResult.getJSONObject(i).optString("MacID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MacID"));
                                device.setSerialNo(jsonArrayResult.getJSONObject(i).optString("SerialNo") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SerialNo"));
                                device.setAllowedUser(jsonArrayResult.getJSONObject(i).optInt("AllowedUser"));
                                device.setUserType(jsonArrayResult.getJSONObject(i).optInt("UserType"));
                                device.setAutoLogIN(jsonArrayResult.getJSONObject(i).optInt("AutoLogIN"));
                                device.setOwnership(jsonArrayResult.getJSONObject(i).optInt("Ownership"));
                                device.setReqID(jsonArrayResult.getJSONObject(i).optInt("ReqID"));
                                deviceList.add(device);
                            }
                        }
                        SetRecyclerView(deviceList);
                    } else {
                        if (flag == 0) {    MessageDialog.MessageDialog(context,"",Msg);    }
                        SetRecyclerView(deviceList);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    SetRecyclerView(deviceList);
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
                params.put("Status", Status);
                Log.i(TAG,"Device list parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void SetRecyclerView(List<Device> deviceList){
        adapter = new DeviceListAdapter(context,deviceList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                device = (Device) adapter.getItem(position);
                DialogDeviceRequestUpdate(context);
            }
        });
    }

    private void DialogDeviceRequestUpdate(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.FullHeightDialog));
        dialog.setContentView(R.layout.dialog_device_request_update);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        TextView txtHeader = (TextView) dialog.findViewById(R.id.textView_Header);
        spnUser = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_User);
        progressBarUser = (ProgressBar) dialog.findViewById(R.id.progressBarUser);
        txtUser = (TextView) dialog.findViewById(R.id.text_User);
        rGAllowedUser  = (RadioGroup) dialog.findViewById(R.id.radio_group_allowed_user);
        rGRequestStatus  = (RadioGroup) dialog.findViewById(R.id.radio_group_request_status);
        rGOwnership  = (RadioGroup) dialog.findViewById(R.id.radio_group_ownership);
        rGAutoLogin  = (RadioGroup) dialog.findViewById(R.id.radio_group_auto_login);
        rBAUAnyUser = (RadioButton) dialog.findViewById(R.id.radio_button_allowed_any_user);
        rBAUDefUser = (RadioButton) dialog.findViewById(R.id.radio_button_allowed_def_user);
        rBRSApproved = (RadioButton) dialog.findViewById(R.id.radio_button_rs_approved);
        rBRSReject = (RadioButton) dialog.findViewById(R.id.radio_button_rs_reject);
        rBOwnPersnal = (RadioButton) dialog.findViewById(R.id.radio_button_ownership_personal);
        rBOwnCompany = (RadioButton) dialog.findViewById(R.id.radio_button_ownership_company);
        rBALYes = (RadioButton) dialog.findViewById(R.id.radio_button_auto_login_yes);
        rBALNo = (RadioButton) dialog.findViewById(R.id.radio_button_auto_login_no);
        edtDeviceNo = (TextInputLayout) dialog.findViewById(R.id.editText_DeviceNo);
        edtRemarks = (TextInputLayout) dialog.findViewById(R.id.editText_remarks);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_update);
        //TODO: Spinner
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyUserList(str[3], str[0], str[14], str[4],DivisionID);
            }
        } else {
            MessageDialog.MessageDialog(context,"","No Internet Connection");
        }
        //tODO: Refresh Data
        RefreshData();
        //tODO: Button Apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null && !DivisionID.isEmpty() && device!=null)
                        if (RequestStatus.equals("1")  && !UserID.isEmpty()) {
                            CallVolleyDeviceRequestStatusUpdate(str[3], str[0], str[14], str[4], DivisionID, device.getID(), UserID, AllowedUser, Ownership, AutoLogin, DeviceNo, Remarks, RequestStatus);
                            dialog.dismiss();
                        }else if (RequestStatus.equals("1")  && UserID.isEmpty()) {
                            MessageDialog.MessageDialog(context,"","Please select User");
                        }else if (RequestStatus.equals("2")){
                            CallVolleyDeviceRequestStatusUpdate(str[3], str[0], str[14], str[4], DivisionID, device.getID(), UserID, AllowedUser, Ownership, AutoLogin, DeviceNo, Remarks, RequestStatus);
                            dialog.dismiss();
                        }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
    }
    private void RefreshData(){
        //TODO: Allowed User
        AllowedUser = ""+device.getAllowedUser();
        if (AllowedUser.equals("0")){
            //TODO: Any User
            rBAUAnyUser.setChecked(true);
        }else if (Status.equals("1")){
            //TODO: Default User
            rBAUDefUser.setChecked(true);
        }
        rGAllowedUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId ==R.id.radio_button_allowed_any_user){
                    //TODO: Any User
                    AllowedUser = "0";
                }else if (checkId ==R.id.radio_button_allowed_def_user){
                    //TODO: Default User
                    AllowedUser = "1";
                }
            }
        });
        //TODO: Request Status
        RequestStatus = (Status.equals("0") ? "1": Status);
        if (RequestStatus.equals("1")){
            //TODO: Approved
            rBRSApproved.setChecked(true);
        }else if (RequestStatus.equals("2")){
            //TODO: Reject
            rBRSReject.setChecked(true);
        }
        rGRequestStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId ==R.id.radio_button_rs_approved){
                    //TODO: Approved
                    RequestStatus = "1";
                }else if (checkId ==R.id.radio_button_rs_reject){
                    //TODO: Reject
                    RequestStatus = "2";
                }
            }
        });
        //TODO: Ownership
        Ownership = ""+device.getOwnership();
        if (Ownership.equals("0")){
            //TODO: Personal
            rBOwnPersnal.setChecked(true);
            edtDeviceNo.getEditText().setEnabled(false);
        }else if (Ownership.equals("1")){
            //TODO: Company
            rBOwnCompany.setChecked(true);
            edtDeviceNo.getEditText().setEnabled(true);
        }
        rGOwnership.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId ==R.id.radio_button_ownership_personal){
                    //TODO: Personal
                    Ownership = "0";
                    edtDeviceNo.getEditText().setEnabled(false);
                }else if (checkId ==R.id.radio_button_ownership_company){
                    //TODO: Company
                    Ownership = "1";
                    edtDeviceNo.getEditText().setEnabled(true);
                }
            }
        });
        //TODO: Auto Login
        AutoLogin = ""+device.getAutoLogIN();
        if (AutoLogin.equals("0")){
            //TODO: No
            rBALNo.setChecked(true);
        }else if (AutoLogin.equals("1")){
            //TODO: Yes
            rBALYes.setChecked(true);
        }
        rGAutoLogin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId ==R.id.radio_button_auto_login_no){
                    //TODO: No
                    AutoLogin = "0";
                }else if (checkId ==R.id.radio_button_auto_login_yes){
                    //TODO: Yes
                    AutoLogin = "1";
                }
            }
        });
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
                            Map<String,String> map = new HashMap<>();
                            map.put("ID","");
                            map.put("Name","Select User");
                            //map.put("Type",Type);
                            mapList.add(map);
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

                                String ID = userWithPermission.getUserID()+"@"+userWithPermission.getUserGroupID()+"@"+userWithPermission.getSGDeviceAccess()+"@"+userWithPermission.getSGMultiSession()+"@"+userWithPermission.getSGWebAccessFlag()+"@"+userWithPermission.getSGScreenshot()+"@"+userWithPermission.getSGAutoLaunchModule();
                                String Name = userWithPermission.getFullName()+"("+userWithPermission.getUserName()+")"+userWithPermission.getTypeName()+"|"+userWithPermission.getTaggedName();
                                map = new HashMap<>();
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
        int pos = 0;
        for (int i=0; i<userWithPermissionList.size(); i++){
            if (userWithPermissionList.get(i).get("Name").contains(device.getUserName())){
                pos = i;
                break;
            }
        }
        spn.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,userWithPermissionList,userWithPermissionList));
        spn.setSelection(pos);
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
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void CallVolleyDeviceRequestStatusUpdate(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String ID,final String DefUserID,final String AllowedUser,final String Ownership,final String AutoLogin,final String RefName,final String Remarks,final String Status){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UpdateIAppDeviceStataus", new Response.Listener<String>()
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
                        CallApiMethod();
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
                params.put("DefUserID", DefUserID);
                params.put("AllowedUser", AllowedUser);
                params.put("Ownership", Ownership);
                params.put("AutoLogin", AutoLogin);
                params.put("RefName", RefName);
                params.put("Remarks", Remarks);
                params.put("Status", Status);
                Log.i(TAG,"Device Request Status Update parameters:"+params.toString());
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
            case R.id.action_filter_search:
                ModulePermission();
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
        MenuItem searchFilterItem = menu.findItem(R.id.action_filter_search);
        searchFilterItem.setVisible(true);
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
