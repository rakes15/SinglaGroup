package administration;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.DivisionDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import administration.adapter.ModuleWithPermissionAdapter;
import administration.adapter.UserGroupPermissionAdapter;
import administration.datasets.Module;
import administration.datasets.Permission;
import administration.datasets.UserGroupPermission;
import administration.datasets.UserWithPermission;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class UserGroupPermissionActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private TextView txtHeader;
    private TableLayout tableLayout,tableLayout2,tableLayout3;
    private GridLayout gridLayout;
    private Button btnSave;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private String UserGroupID = "",UserGroupName = "" ,VType = "" ,Caption = "",DivisionID="",DivisionName="",UserID="";
    private List<Permission> permissionList;
    private String AccessLogin = "0",MSession="0",InternetAccess="0",Snapshot="0",AutoLaunchModule="0";

    private static String TAG = UserGroupPermissionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_user_group_permission);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = UserGroupPermissionActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        txtHeader = (TextView) findViewById(R.id.textView_Header);
        tableLayout = (TableLayout) findViewById(R.id.table_Layout);
        tableLayout2 = (TableLayout) findViewById(R.id.table_Layout2);
        tableLayout3 = (TableLayout) findViewById(R.id.table_Layout3);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
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
                DialogFilter(context);
                CallApiMethod();
                //ClickEvents();
            }else {
                MessageDialog.MessageDialog(UserGroupPermissionActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(UserGroupPermissionActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(UserGroupPermissionActivity.this);
                    if (str!=null) {

                    }
                } else {
                    MessageDialog.MessageDialog(UserGroupPermissionActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(UserGroupPermissionActivity.this);
            if (str!=null) {
                //setDivisionList();
            }
        } else {
            MessageDialog.MessageDialog(context,"","No Internet Connection");
        }
    }
    private void ClickEvents(){
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
                            CallVolleyUserPermissionUpdate(str[3], str[0], str[14], str[4], DivisionID, UserID, AccessLogin, MSession, InternetAccess, Snapshot, AutoLaunchModule);
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
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Header
        txtHeader.setText("User Groups");
        //TODO: Spinner
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null)
                CallVolleyUserGroupList(str[3], str[0], str[14], str[4],str[5],spn);
        } else {
            MessageDialog.MessageDialog(context,"","No Internet Connection");
        }
        //tODO: Button Apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null)
                        CallVolleyModuleList(str[3], str[0], str[14], str[4],str[5]);
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
                //CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate),String.valueOf(Type), 1);
                dialog.dismiss();
            }
        });
    }
    private void CallVolleyUserGroupList(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final Spinner spn){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UserGroupList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<UserGroupPermission> userGroupPermissionList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        if (jsonArrayResult.length() > 0){
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                UserGroupPermission userGroupPermission = new UserGroupPermission();
                                userGroupPermission.setID(jsonArrayResult.getJSONObject(i).optString("ID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ID"));
                                userGroupPermission.setName(jsonArrayResult.getJSONObject(i).optString("Name") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Name"));
                                JSONArray jsonArrayPermission = new JSONArray(jsonArrayResult.getJSONObject(i).optString("Permission") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Permission"));
                                List<Permission> permissionList = new ArrayList<>();
                                if (jsonArrayPermission.length() > 0) {
                                    for (int j = 0; j< jsonArrayPermission.length(); j++) {
                                        Permission permission = new Permission();
                                        permission.setCaptionID(jsonArrayPermission.getJSONObject(j).optString("CaptionID") == null ? "" : jsonArrayPermission.getJSONObject(j).optString("CaptionID"));
                                        permission.setViewFlag(jsonArrayPermission.getJSONObject(j).optInt("ViewFlag"));
                                        permission.setEditFlag(jsonArrayPermission.getJSONObject(j).optInt("EditFlag"));
                                        permission.setCreateFlag(jsonArrayPermission.getJSONObject(j).optInt("CreateFlag"));
                                        permission.setRemoveFlag(jsonArrayPermission.getJSONObject(j).optInt("RemoveFlag"));
                                        permission.setPrintFlag(jsonArrayPermission.getJSONObject(j).optInt("PrintFlag"));
                                        permission.setImportFlag(jsonArrayPermission.getJSONObject(j).optInt("ImportFlag"));
                                        permission.setExportFlag(jsonArrayPermission.getJSONObject(j).optInt("ExportFlag"));
                                        permission.setIsModule(jsonArrayPermission.getJSONObject(j).optInt("IsModule"));
                                        permissionList.add(permission);
                                    }
                                }
                                userGroupPermission.setPermission(permissionList);
                                userGroupPermissionList.add(userGroupPermission);
                            }
                        }
                        setUserGroupListSpinner(spn,userGroupPermissionList);
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
                Log.i(TAG,"User Group list parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void setUserGroupListSpinner(Spinner spn, final List<UserGroupPermission> userWithPermissionList){
        spn.setAdapter(new UserGroupPermissionAdapter(context,userWithPermissionList));
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                UserGroupID = userWithPermissionList.get(position).getID();
                UserGroupName = userWithPermissionList.get(position).getName();
                permissionList = userWithPermissionList.get(position).getPermission();
                txtHeader.setText(""+UserGroupName);
                txtHeader.setVisibility(View.VISIBLE);
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
                List<Module> moduleList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = new JSONArray(jsonObject.getString("Result"));
                        if (jsonArrayResult.length() > 0){
                            for (int i=0; i<jsonArrayResult.length(); i++) {
                                Module module = new Module();
                                String CaptionID = jsonArrayResult.getJSONObject(i).optString("ID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ID");
                                module.setVType(jsonArrayResult.getJSONObject(i).optInt("VType"));
                                module.setID(CaptionID);
                                module.setCaption(jsonArrayResult.getJSONObject(i).optString("Caption") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Caption"));
                                Permission permission = new Permission();
                                if (permissionList!=null && !permissionList.isEmpty()) {
                                    for (int j=0; j<permissionList.size(); j++){
                                        if (CaptionID.toLowerCase().equals(permissionList.get(j).getCaptionID().toLowerCase())){
                                            permission = new Permission();
                                            permission.setViewFlag(permissionList.get(j).getViewFlag());
                                            permission.setCreateFlag(permissionList.get(j).getCreateFlag());
                                            permission.setEditFlag(permissionList.get(j).getEditFlag());
                                            permission.setRemoveFlag(permissionList.get(j).getRemoveFlag());
                                            permission.setPrintFlag(permissionList.get(j).getPrintFlag());
                                            permission.setImportFlag(permissionList.get(j).getImportFlag());
                                            permission.setExportFlag(permissionList.get(j).getExportFlag());
                                            break;
                                        }
                                    }
                                }
                                module.setPermission(permission);
                                moduleList.add(module);
                            }
                        }
                        setViewModule(moduleList);
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
    private void setViewModule(final List<Module> moduleList){
        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();

        //TODO: Set Table Row and TextView Grid
        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 1);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        TextView txt = new TextView(context);
        txt.setId(i + 100);
        txt.setText("Module");
        txt.setWidth(StaticValues.mViewWidth);
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        List<String> stringList = new ArrayList<>();
        stringList.add("All");
        stringList.add("View");
        stringList.add("Create");
        stringList.add("Edit");
        stringList.add("Delete");
        stringList.add("Print");
        stringList.add("Import");
        stringList.add("Export");
        for (int j = 0; j<stringList.size(); j++) {
            txt = new TextView(context);
            txt.setId(j + 50);
            txt.setText("" + stringList.get(j));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }
        tableLayout.addView(tableRow);

        for (int k = 0; k< moduleList.size(); k++) {
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(k + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(i + 25);
            txt.setWidth(StaticValues.mViewWidth);
            txt.setText(moduleList.get(k).getCaption());
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            List<String> integerList = new ArrayList<>();
            integerList.add("" + moduleList.get(k).getPermission().getViewFlag());
            integerList.add("" + moduleList.get(k).getPermission().getViewFlag());
            integerList.add("" + moduleList.get(k).getPermission().getCreateFlag());
            integerList.add("" + moduleList.get(k).getPermission().getEditFlag());
            integerList.add("" + moduleList.get(k).getPermission().getRemoveFlag());
            integerList.add("" + moduleList.get(k).getPermission().getPrintFlag());
            integerList.add("" + moduleList.get(k).getPermission().getImportFlag());
            integerList.add("" + moduleList.get(k).getPermission().getExportFlag());
            for (int m = 0; m < integerList.size(); m++) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setId(m + 150);
                checkBox.setTag(moduleList.get(k).getID()+"@"+integerList.get(m));
                checkBox.setChecked(integerList.get(m).equals("1") ? true : false);
                checkBox.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                checkBox.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                checkBox.setPadding(10, 10, 10, 10);
                tableRow1.addView(checkBox);
            }
            tableLayout.addView(tableRow1);
        }
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
            case R.id.action_filter_search:  //TODO: Search Filter
                DialogFilter(context);
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
        MenuItem searchFilterItem = menu.findItem(R.id.action_filter_search);
        searchFilterItem.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
}
