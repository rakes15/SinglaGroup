package com.singlagroup;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.singlagroup.adapters.RecyclerViewPrinterServicesAdapter;
import com.singlagroup.customwidgets.AppPrinterService;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.PDFThumbnail;
import com.singlagroup.datasets.AppCheckDataset;
import com.singlagroup.datasets.DeviceInfoDataset;
import com.singlagroup.datasets.RecommandedAppsDataset;
import com.singlagroup.datasets.Result;
import com.singlagroup.responsedatasets.ResponseAppCheckDataset;
import com.singlagroup.responsedatasets.ResponseAppCheckDatasets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqliteRootHandler;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerChangeParty;
import orderbooking.StaticValues;
import orderbooking.catalogue.ItemListActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import services.SinglaGroupsServices;
import whatsapp.autoresponder.Utils.AccessibillityManagerC;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ProgressBar progressBar;
    private ImageView imgViewLogo;
    TextView txtMessage,txtMessagePending,txtVersion;
    String IMEINo="",VersionName="";
    private String UniqueID="";
    private static String TAG = MainActivity.class.getSimpleName();
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;
    private List<RecommandedAppsDataset> recommandedAppsDatasetList;
    String FCM_ID,Latitude,Longitude;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;
    String[] permissions= new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isGooglePlayServicesAvailable(this)){
            //Toast.makeText(this, "Avialable", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Not Avialable", Toast.LENGTH_SHORT).show();
        }
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        this.context = MainActivity.this;
        //permissionToDrawOverlays();
        FirstMethod();
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);

        if (status != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }

        return true;
    }

    private void FirstMethod(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermissions();
//            System.out.println("checkPermissions");
        } else {
            CallMainMethod();
//            System.out.println("Call Main Method");
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    if (FCM_ID != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkPermissions();
                        } else {
                            CallMainMethod();
                        }
                    }
                } else {
                    txtMessage.setText(status);
                }
            }
        });
    }
    private void CallMainMethod() {
  //      System.out.println("Call Main Method-------------");
        ScreenSize();
        Initialization();
        addContact();
        //TODO:FCM Token
        FirebaseMessaging.getInstance().subscribeToTopic("SinglaGroups");
        FCM_ID = FirebaseInstanceId.getInstance().getToken();
        FCM_ID = (FCM_ID == null) ? "":FCM_ID;

        GetDeviceInfo();

        if (!CommanStatic.ANDROID_DEVICE_UNIQUE_ID.isEmpty()) {
            //TODO: Call Recommanded Api
            //CallRecommandedApi();
            NotificationPermission();
            //TODO: GPS Location
            buildGoogleApiClient();
        }
//        //Todo:Start service
//        Intent intent = new Intent(PartyInfoActivity.this, SinglaGroupsServices.class);
//        startService(intent);

        //TODO: Make Singla Groups folder in sdcard
        File folder = new File(Environment.getExternalStorageDirectory() + "/SinglaGroups/");
        if (!folder.exists()) {
            folder.mkdir();
        }else{
            //TODO: Delete Temp File or Directory
            File fileOrFolder = new File(Environment.getExternalStorageDirectory() + "/SinglaGroups/temp/");
            if (fileOrFolder.exists()) PDFThumbnail.DeleteFileOrDirectory(fileOrFolder);
            //TODO: Delete PhoneCallRecording File or Directory
            fileOrFolder = new File(Environment.getExternalStorageDirectory() + "/SinglaGroups/PhoneCallRecording/");
            if (fileOrFolder.exists()) PDFThumbnail.DeleteFileOrDirectory(fileOrFolder);
            //TODO: Delete WhatsApp File or Directory
            fileOrFolder = new File(Environment.getExternalStorageDirectory() + "/SinglaGroups/WhatsApp/");
            if (fileOrFolder.exists()) PDFThumbnail.DeleteFileOrDirectory(fileOrFolder);
        }
    }
    private void NotificationPermission(){
        //TODO: Notification Service
        if (NotificationManagerCompat.getEnabledListenerPackages(context).contains(getApplicationContext().getPackageName())){
            //service is enabled do something
            //TODO: Notification On then next Permission
            AccessbilityPermission();
        } else {
            //service is not enabled try to enabled by calling...
            //TODO: Notification Off to go on Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            else
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }
    private void AccessbilityPermission(){
        //TODO: Accessbility Service
        if (AccessibillityManagerC.isAccessibilitySettingsOn(context)){
            //service is enabled do something
            //TODO: Accessbility On then next Process
            CallRecommandedApi();
        }else{
            //service is not enabled try to enabled by calling...
            //TODO: Accessbility Off to go on Accessbility
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }
    private void CallAuthorizationAPI(){
        //TODO: Check Connection request
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallRetrofitAuthorization(GetSharePreferenceDeviceInfo(MainActivity.this).getSerialNo(), GetSharePreferenceDeviceInfo(MainActivity.this).getMacID(), GetSharePreferenceDeviceInfo(MainActivity.this).getModelNo(), GetSharePreferenceDeviceInfo(MainActivity.this).getIMEINo(), GetSharePreferenceDeviceInfo(MainActivity.this).getVersion(), CommanStatic.AppType, GetSharePreferenceDeviceInfo(MainActivity.this).getFCM_ID(), GetSharePreferenceDeviceInfo(MainActivity.this).getAndroidDUID());
        } else {
            txtMessage.setText(status);
        }
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //CallVolleyRecommandedApps(CommanStatic.AppType);
                    CallRetrofitAuthorization(GetSharePreferenceDeviceInfo(MainActivity.this).getSerialNo(), GetSharePreferenceDeviceInfo(MainActivity.this).getMacID(), GetSharePreferenceDeviceInfo(MainActivity.this).getModelNo(), GetSharePreferenceDeviceInfo(MainActivity.this).getIMEINo(), GetSharePreferenceDeviceInfo(MainActivity.this).getVersion(), CommanStatic.AppType, GetSharePreferenceDeviceInfo(MainActivity.this).getFCM_ID(), GetSharePreferenceDeviceInfo(MainActivity.this).getAndroidDUID());
                } else {
                    txtMessage.setText(status);
                }
            }
        });
    }
    private void CallRecommandedApi(){
        //TODO: Check Connection request
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            if (FCM_ID != null) {
                CallVolleyRecommandedApps(CommanStatic.AppType);
            }
        } else {
            txtMessage.setText(status);
        }
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    if (FCM_ID != null) {
                        CallVolleyRecommandedApps(CommanStatic.AppType);
                    }
                } else {
                    txtMessage.setText(status);
                }
            }
        });
    }
    private void CallRetrofitAuthorization(final String SerialNo,final String MacID, final String ModelNo,final String ImeiNo, final String Version, final String AppType, final String FCM_ID, final String DeviceUniqueID){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("SerialNo", SerialNo);
        params.put("MacID", MacID);
        params.put("ModelNo", ModelNo);
        params.put("ImeiNo", ImeiNo);
        params.put("AppType", AppType);
        params.put("AppVersion", Version);
        params.put("FCM_ID", FCM_ID);
        params.put("DeviceUniqueID", DeviceUniqueID);
        Log.e(TAG,"Authorization parameters:"+params.toString());
        Call<ResponseAppCheckDatasets> call = apiService.getAppCheck(params);
        call.enqueue(new Callback<ResponseAppCheckDatasets>() {
            @Override
            public void onResponse(Call<ResponseAppCheckDatasets> call, retrofit2.Response<ResponseAppCheckDatasets> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            Result dataset = response.body().getResult();
                            int RequestStatus = dataset.getRequestStatus();
                            String Message = dataset.getMsg();
                            final String DownloadLink = dataset.getAppDownloadLink();
                            String DeviceID = dataset.getDeviceID();
                            txtMessage.setText("");
                            if (RequestStatus==1) {
                                InsertDeviceInfo(dataset);
                                int MandatoryApp = RecommandedAppCondition() ;
                                if (MandatoryApp == 0){
                                    Intent intent = new Intent(MainActivity.this,RecommandedAppsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    String DefultUserID = dataset.getDefaultUser();
                                    String DefultUserPassword = dataset.getPassword();
                                    int AllowUser = dataset.getAllowUser();
                                    int AutoLogIN = dataset.getAutoLogIN();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.putExtra("DeviceID", DeviceID);
                                    intent.putExtra("DefultUserID", DefultUserID);
                                    intent.putExtra("DefultUserPassword", DefultUserPassword);
                                    intent.putExtra("AllowUser", AllowUser);
                                    intent.putExtra("AutoLogIN", AutoLogIN);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } else if (RequestStatus==2) {
                                //TODO: Call share Method
                                txtMessage.setText(""+Message);
                                txtMessagePending.setVisibility(View.GONE);

                            } else if (RequestStatus==3) {
                                imgViewLogo.setVisibility(View.VISIBLE);
                                imgViewLogo.setImageResource(R.drawable.update1);
                                txtMessage.setText("");
                                DialogPlayOrLocalStore(DownloadLink, CommanStatic.LocalStoreLink);
                                imgViewLogo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DialogPlayOrLocalStore(DownloadLink, CommanStatic.LocalStoreLink);
                                    }
                                });
                            } else if (RequestStatus==4) {

                                txtMessage.setText(""+Message);
                                Intent in = new Intent(getApplicationContext(), RegistrationActivity.class);
                                startActivity(in);
                                finish();
                            } else if (RequestStatus==0) {
                                //TODO: Call share Method
                                txtMessage.setText(""+Message);
                                txtMessagePending.setVisibility(View.VISIBLE);
                            }else {
                                //TODO: else Part
                                txtMessage.setText(""+Message);
                                txtMessagePending.setVisibility(View.GONE);
                            }
                        } else {
                            txtMessage.setText("" + msg);
                        }
                    }else {
                        MessageDialog messageDialog=new MessageDialog();
                        messageDialog.MessageDialog(MainActivity.this,"","","API response: "+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG,"Login Exception:"+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(MainActivity.this,"AppCheck","Exception autho",""+e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseAppCheckDatasets> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(MainActivity.this,"AppCheck","Failure",t.toString());
                hidepDialog();
            }
        });
    }
    private void showpDialog() {
        if(progressBar!=null && txtMessage!=null) {
            progressBar.setVisibility(View.VISIBLE);
            txtMessage.setText("Please wait...");
        }
    }
    private void hidepDialog() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void Initialization(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        imgViewLogo = (ImageView) findViewById(R.id.imageView_logo);
        txtMessage = (TextView) findViewById(R.id.textView_message);
        txtMessagePending = (TextView) findViewById(R.id.textView_messagePending);
        txtVersion = (TextView) findViewById(R.id.textView_version);
        txtMessagePending.setVisibility(View.GONE);
        //TODO: Get App Version
        VersionName = getVersion(getApplicationContext());
        if (txtVersion!=null) {
            txtVersion.setText("Singla Groups  v" + VersionName);
            CommanStatic.App_Version = VersionName;
        }
    }
    private void GetDeviceInfo(){
        //TODO: get Android Device UniqueID
        String AndroidDUID = AndroidDeviceUniqueID();
        if (AndroidDUID.isEmpty()){
            String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
            if (!status.contentEquals("No Internet Connection")) {
                CallVolleyGenerateUniqueID();
            } else {
                txtMessage.setText(status);
            }
        }else{
            CommanStatic.ANDROID_DEVICE_UNIQUE_ID = AndroidDUID;
        }
        //CommanStatic.ANDROID_DEVICE_UNIQUE_ID = AndroidDUID;
        //ToDo: Get MAC Address
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        final String MACAddress = (wInfo.getMacAddress()==null)?"02:00:00:00:00:00":wInfo.getMacAddress();
        String SerialNo= Build.SERIAL;
        CommanStatic.MAC_ID = MACAddress;
        CommanStatic.SERIAL_NO = SerialNo;

        //TODO: Get IMEI Number
        TelephonyManager telemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try{
            IMEINo = (telemanager.getDeviceId()==null)? "012345678901234" : telemanager.getDeviceId();
            CommanStatic.IMEI_NO=IMEINo;
        }catch (Exception e){
            Log.i("Security Exception", ""+e.getMessage());
        }
        //TODO: Get Model Number
        final String ModelNo = getDeviceName();
        CommanStatic.Model_No=ModelNo;

        //TODO: Device Info save
        SharePreferenceDeviceInfo(MACAddress,SerialNo,IMEINo,ModelNo,VersionName,FCM_ID,AndroidDUID);
    }
    private String getVersion(Context context){
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String myVersionName = "not available"; // initialize String
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return myVersionName;
    }
    public String getDeviceName(){
        String manufacturer= Build.MANUFACTURER;
        String model=Build.MODEL;
        if(model.startsWith(manufacturer)){
            return capitalize(model);
        }
        else{
            return capitalize(manufacturer)+""+model;
        }
    }
    private String capitalize(String s){
        if(s==null || s.length()==0){
            return "";
        }
        char first=s.charAt(0);
        if(Character.isUpperCase(first)){
            return s;
        }
        else{
            return Character.toTitleCase(first)+s.substring(1);
        }
    }
    public void SharePreferenceDeviceInfo(String MacID,String SerialNo,String IMEINo,String ModelNo,String Version,String FCM_ID,String AndroidDUID){

        SharedPreferences prefs = getSharedPreferences("MyPrefDeviceInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("MacID", MacID);
        editor.putString("SerialNo", SerialNo);
        editor.putString("IMEINo", IMEINo);
        editor.putString("ModelNo", ModelNo);
        editor.putString("Version", Version);
        editor.putString("FCM_ID", FCM_ID);
        editor.putString("AndroidDUID", AndroidDUID);
        editor.commit();
    }
    public DeviceInfoDataset GetSharePreferenceDeviceInfo(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MyPrefDeviceInfo", MODE_PRIVATE);
        DeviceInfoDataset deviceInfoDataset = new DeviceInfoDataset
                (
                    pref.getString("MacID", ""),
                    pref.getString("SerialNo", ""),
                    pref.getString("IMEINo", ""),
                    pref.getString("ModelNo", ""),
                    pref.getString("Version", ""),
                    pref.getString("FCM_ID", null),
                    pref.getString("AndroidDUID", "")
                );
        return deviceInfoDataset;
    }
    private void InsertDeviceInfo(Result result){
        CommanStatic.DeviceInfo = new String[22][22];
        CommanStatic.DeviceInfo[0][0] = result.getDefUserID();
        CommanStatic.DeviceInfo[0][1] = result.getDeviceRequestNo();
        CommanStatic.DeviceInfo[0][2] = result.getDeviceType();
        CommanStatic.DeviceInfo[0][3] = result.getAssetNo();
        CommanStatic.DeviceInfo[0][4] = result.getRemarks();
        CommanStatic.DeviceInfo[0][5] = String.valueOf((result.getAllowUser()==0 ? "Mapped user only": "Any active user"));
        CommanStatic.DeviceInfo[0][6] = String.valueOf((result.getAutoLogIN()==0 ? "No": "Yes"));
        CommanStatic.DeviceInfo[0][7] = "v"+CommanStatic.App_Version;
        CommanStatic.DeviceInfo[0][8] = CommanStatic.MAC_ID;
        CommanStatic.DeviceInfo[0][9] = CommanStatic.SERIAL_NO;
        CommanStatic.DeviceInfo[0][10] = CommanStatic.Model_No;
        CommanStatic.DeviceInfo[0][11] = CommanStatic.IMEI_NO;

        CommanStatic.DeviceInfo[0][12] = String.valueOf(result.getDefaultUser());//result.();//result.();
        CommanStatic.DeviceInfo[0][13] = String.valueOf(result.getUserFullName());
        CommanStatic.DeviceInfo[0][14] = String.valueOf(result.getEmpCVType());
        CommanStatic.DeviceInfo[0][15] = String.valueOf(result.getEmpCVName());
        CommanStatic.DeviceInfo[0][16] = String.valueOf(result.getCompanyName());
        CommanStatic.DeviceInfo[0][17] = String.valueOf(result.getDefDivision());
        CommanStatic.DeviceInfo[0][18] = String.valueOf(result.getDefBranch());
        CommanStatic.DeviceInfo[0][19] = String.valueOf(result.getDefGodown());
        CommanStatic.DeviceInfo[0][20] = "";
        CommanStatic.DeviceInfo[0][21] = "";
        //TODO: Header
        CommanStatic.DeviceInfo[0][0] = "";
        CommanStatic.DeviceInfo[1][0]= "Request No";
        CommanStatic.DeviceInfo[2][0]= "Device Type";
        CommanStatic.DeviceInfo[3][0] = "Asset No";
        CommanStatic.DeviceInfo[4][0] = "Remarks";
        CommanStatic.DeviceInfo[5][0] = "Allowed User";
        CommanStatic.DeviceInfo[6][0] = "Auto Login";//"User Name";
        CommanStatic.DeviceInfo[7][0] = "App version";//"";
        CommanStatic.DeviceInfo[8][0] = "Mac ID";
        CommanStatic.DeviceInfo[9][0] = "Serial No";
        CommanStatic.DeviceInfo[10][0] = "Model No";//"Allowed User";
        CommanStatic.DeviceInfo[11][0] = "IMEI No";

        CommanStatic.DeviceInfo[12][0] = "Login ID";
        CommanStatic.DeviceInfo[13][0] = "User Name";
        CommanStatic.DeviceInfo[14][0] = "User Type";
        CommanStatic.DeviceInfo[15][0] = "Name";
        CommanStatic.DeviceInfo[16][0] = "Def.Company Name";
        CommanStatic.DeviceInfo[17][0] = "Def.Division";
        CommanStatic.DeviceInfo[18][0] = "Def.Branch";
        CommanStatic.DeviceInfo[19][0] = "Def.Godown";
        CommanStatic.DeviceInfo[20][0] = "";
        CommanStatic.DeviceInfo[21][0] = "";
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirstMethod();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
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
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                //System.out.println("permission not granted: "+p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            //System.out.println("listPermissionsNeeded:"+listPermissionsNeeded.toString());
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
        }else {
            CallMainMethod();
            //System.out.println("CallMainMethod");
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS)
        {
            int neverAskAgain=0;
            List<String> listPermissionsDenied = new ArrayList<>();
            for(String permission: permissions){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    //denied
                    listPermissionsDenied.add(permission);
                    System.out.println("permission is denied: "+permission);
                }else{
                    if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        System.out.println("permission is granted: "+permission);
                    } else{
                        //set to never ask again
                        neverAskAgain = 1;
                        System.out.println("permission is never ask denied: "+permission);
                        //Log.e("set to never ask again", permission);
                    }
                }
            }
            //TODO: Open App permission settings
            if (neverAskAgain == 1){
                //startInstalledAppDetailsActivity(PartyInfoActivity.this);
                MessageDialogByIntent(MainActivity.this,"Permission Alert","Please allow all permissions to access this application");
                //System.out.println("permission is never ask denied:");
            }else{
                if(!listPermissionsDenied.isEmpty()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkPermissions();
                    }else{
                        CallMainMethod();
                    }
                }else{
                    CallMainMethod();
                }
            }
//            List<String> listPermissionsDenied = new ArrayList<>();
//            for (int i = 0; i < permissions.length; i++) {
//                String permission = permissions[i];
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    listPermissionsDenied.add(permission);
//                }
//            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
            mLocationRequest.setInterval(10000); // Update location every second
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //Log.d(TAG, "mGoogleApiClient:"+mGoogleApiClient+"\nmLocationRequest:"+mLocationRequest+"\nmLastLocation:"+mLastLocation);
            if (mLastLocation != null) {
                Latitude = String.valueOf(mLastLocation.getLatitude());
                Longitude = String.valueOf(mLastLocation.getLongitude());
                StaticValues.Latitude = Latitude;
                StaticValues.Longitude = Longitude;
                Log.d(TAG, "Latitude:" + Latitude + "\tLongitude:" + Longitude);
            }else{
                StaticValues.Latitude = "28.644800";
                StaticValues.Longitude = "77.216721";
            }
        }catch (Exception e){
            Log.e(TAG,"Exc:Location:"+e.toString());
        }
        //updateUI();
    }
    @Override
    public void onConnectionSuspended(int i) { }
    @Override
    public void onLocationChanged(Location location) {
        Latitude = String.valueOf(location.getLatitude());
        Longitude = String.valueOf(location.getLongitude());
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    private void CallVolleyRecommandedApps(final String AppType){
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
                        recommandedAppsDatasetList = new ArrayList<>();
                        for (int i=0; i<jsonArrayResult.length(); i++){
                            RecommandedAppsDataset dataset = new RecommandedAppsDataset(jsonArrayResult.getJSONObject(i).getString("ID"),jsonArrayResult.getJSONObject(i).getString("Name"),jsonArrayResult.getJSONObject(i).getString("URL"),jsonArrayResult.getJSONObject(i).getString("PackageName"),jsonArrayResult.getJSONObject(i).getInt("IsMandatory"),jsonArrayResult.getJSONObject(i).getInt("AppType"),jsonArrayResult.getJSONObject(i).getString("Icon"));
                            recommandedAppsDatasetList.add(dataset);
                        }
                        CallAuthorizationAPI();
                    }else{
                        MessageDialog.MessageDialog(context,"",""+Msg);
                    }

                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception Recomanded",""+e.toString());
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
    private int RecommandedAppCondition(){
        int MandatoryApp = 0;
        if(!recommandedAppsDatasetList.isEmpty()){
            for(int position=0; position<recommandedAppsDatasetList.size(); position++){
                String PackageName = recommandedAppsDatasetList.get(position).getPackageName();
                String URL = recommandedAppsDatasetList.get(position).getURL();
                int Mandatory = recommandedAppsDatasetList.get(position).getIsMandatory();
                if (Mandatory == 1) {
                    AppPrinterService appPrinterService = new AppPrinterService();
                    int status = appPrinterService.AppPrinterServiceCheck(MainActivity.this, PackageName, URL);
                    if (status == 1) {
                        MandatoryApp = 1;
                    }else {
                        MandatoryApp = 0;
                        break;
                    }
                }else {
                    MandatoryApp = 1;
                }
            }
        }else {
            MandatoryApp = 0;
        }
        return MandatoryApp;
    }
    public void ScreenSize() {
        double screenSize = 0;
        try {
            // Compute screen size
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidth  = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            screenSize = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
            Log.e(TAG,"Devicee Screen Size Width:"+screenWidth+"\tHeight:"+screenHeight);
            if(screenSize>=7) {
                //TODO: Tablet
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
            else if(screenSize<7 && screenSize>=5.5){
                //TODO: Contents
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
            else {
                //TODO: Contents
                StaticValues.sViewWidth=((int)(dm.heightPixels));
                StaticValues.sViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.sDetailsViewWidth=((int)(dm.heightPixels));
                StaticValues.sDetailsViewHeight=(int)((float)(dm.widthPixels)*1.366);
                StaticValues.mViewWidth=(int)((float)(dm.widthPixels/2));
                StaticValues.mViewHeight=(int)((float)(dm.widthPixels/2)*1.366) ;
                StaticValues.mBoxViewWidth=((int)(dm.widthPixels)*22/100);
                StaticValues.mBoxViewHeight=((int)(dm.heightPixels)*18/100) ;
                StaticValues.imgWidth=((int)(dm.widthPixels)*10/100) ;
                StaticValues.imgHeight=((int)(dm.widthPixels)*15/100) ;
            }
        } catch(Throwable t) {
            Log.e(TAG,"Exception:"+t.toString());
        }
    }
    private void DialogPlayOrLocalStore(final String PlayStoreLink, final String LocalServerLink){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
        final TextView txtViewMsg = (TextView) dialog.findViewById(R.id.text_Msg);
        RadioGroup radioGroupPlayOrLocalStore = (RadioGroup) dialog.findViewById(R.id.RadioGroup_NewForgot);
        TextInputLayout OldPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_OldPassword);
        TextInputLayout NewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_NewPassword);
        TextInputLayout ConfirmPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_confirmPassword);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        RadioButton radioButtonPlayStore = (RadioButton) dialog.findViewById(R.id.RadioButton_NewUser);
        radioButtonPlayStore.setText("Play Store");
        RadioButton radioButtonLocalStore = (RadioButton) dialog.findViewById(R.id.RadioButton_ForgotPassword);
        radioButtonLocalStore.setText("Local Store");
        CursorColor.CursorColor(OldPasswordWrapper.getEditText());
        CursorColor.CursorColor(NewPasswordWrapper.getEditText());
        CursorColor.CursorColor(ConfirmPasswordWrapper.getEditText());
        txtViewTitle.setText("Play or Local Store link");
        radioGroupPlayOrLocalStore.setVisibility(View.VISIBLE);
        OldPasswordWrapper.setVisibility(View.GONE);
        NewPasswordWrapper.setVisibility(View.GONE);
        ConfirmPasswordWrapper.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        radioGroupPlayOrLocalStore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_NewUser){
                    dialog.dismiss();
                    //TODO: Play Store
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreLink));
                    startActivity(intent);
                    
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_ForgotPassword){
                    dialog.dismiss();
                    //TODO: Local Store
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LocalServerLink));
                    startActivity(intent);
                }
            }
        });
    }
    private String AndroidDeviceUniqueID(){
        String UniqueID = "";
        //String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //System.out.println("UniqueID:"+changeParty.insertUniqueID());

        File folder = new File(Environment.getExternalStorageDirectory(), "/Android/data/"+".SGDeviceID");
        if (!folder.exists()) { folder.mkdir(); }
        final File file = new File(folder, ".deviceUniqueID.txt");
        try
        {
            if (file.exists()){
                //Read text from file
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                UniqueID = text.toString();
                br.close();
            }
        }catch (IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Log.d("Android","Android ID : "+UniqueID.toUpperCase());
        //MessageDialog.MessageDialog(context,"Android ID",""+android_id.toUpperCase());

        return UniqueID.toUpperCase();
    }
    private void CallVolleyGenerateUniqueID(){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.STATIC_BASE_URL+"GetGUID", new Response.Listener<String>()
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
                        Log.d(TAG,"Result:"+jsonObjectResult.toString());

                        File folder = new File(Environment.getExternalStorageDirectory(), "/Android/data/"+".SGDeviceID");
                        if (!folder.exists()) { folder.mkdir(); }
                        final File file = new File(folder, ".deviceUniqueID.txt");
                        if (!file.exists()) {
                            file.createNewFile();
                            FileOutputStream fOut = new FileOutputStream(file);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            //TODO: Unique ID
                            CommanStatic.ANDROID_DEVICE_UNIQUE_ID = jsonObjectResult.getString("ID");
                            Log.d("Android", "UniqueID : " + CommanStatic.ANDROID_DEVICE_UNIQUE_ID.toUpperCase());
                            myOutWriter.append(CommanStatic.ANDROID_DEVICE_UNIQUE_ID.toUpperCase());
                            myOutWriter.close();

                            fOut.flush();
                            fOut.close();

                            CallMainMethod();
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"",""+Msg);
                    }

                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception Unique ID generate",""+e.toString());
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
                //params.put("AppType", AppType);
                Log.d(TAG,"Recommanded Apps parameters:"+params.toString());
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
            btnOK.setText("Go");
            txtViewMessageTitle.setText(Title);
            txtViewMessage.setText(Mesaage);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    startInstalledAppDetailsActivity(MainActivity.this);
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    public void startInstalledAppDetailsActivity(Activity context) {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
        finish();
    }
    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        checkPermissions();
//                    }else{
//                        CallMainMethod();
//                    }
                    System.out.println("not Overlay");
                }else{
                    System.out.println("Overlay");
                }
            }
        }
    }
    private void addContact() {
        try {
            String name = "La'Scoot Customer Care", phone = "+919350350350";
            boolean contact = false;
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String existName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String existNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (name.equals(existName) || phone.equals(existNumber)) {
                    contact = true;
                    break;
                }
            }
            cursor.close();
            if (contact == false) {
                ContentValues values = new ContentValues();
                values.put(Contacts.People.NUMBER, phone);
                values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
                values.put(Contacts.People.LABEL, name);
                values.put(Contacts.People.NAME, name);
                Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
                Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
                values.clear();
                values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
                values.put(Contacts.People.NUMBER, phone);
                updateUri = getContentResolver().insert(updateUri, values);
            }
        }catch(Exception e){
            Log.e(TAG,"Contact Exception :"+e.toString());
        }
    }
}
