package com.singlagroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.adapters.RecyclerViewParentCaptionAdapter;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.MenuDataset;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerActiveSessionManage;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;

/**
 * Created by Rakesh on 05-Oct-16.
 */

public class HomeAcitvity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = HomeAcitvity.class.getSimpleName();
    private RecyclerView recyclerView;
    public static Chronometer chronometerActiveTime;
    public static long timeWhenStopped = 0;
    private Context context;
    private CircleImageView circleImageView;
    private TextView txtPicChangeOrUpload,txtPicCancel;
    private FloatingActionButton floatingActionButtonHome,floatingActionButtonBack;
    private Toolbar toolbar;
    private List<MenuItem> menuItems;
    private ProgressDialog progressDialog;
    private RecyclerViewParentCaptionAdapter adapter;
    DatabaseSqlLiteHandlerUserInfo DBHandler;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private String ImageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        Initialization();
        int c = 0;
        DBHandler=new DatabaseSqlLiteHandlerUserInfo(getApplicationContext());
        List<Map<String,String>> mapList=DBHandler.getModuleList();
        System.out.println("Module:"+mapList.toString());
        for(int i=0;i<mapList.size();i++){

            int vType = (mapList.get(i).get("Vtype")==null) ? 0 : Integer.valueOf(mapList.get(i).get("Vtype"));
            if(CommanStatic.AutoLaunchModule ==  vType && vType>0){
                c=1;
                String ClassName = mapList.get(i).get("ContentClass");
                if (!ClassName.isEmpty() && ClassName!=null) {
                    try {
                        //TODO: Set Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("Title",mapList.get(i).get("Name"));
                        bundle.putInt("ViewFlag",Integer.valueOf(mapList.get(i).get("ViewFlag")));
                        bundle.putInt("EditFlag",Integer.valueOf(mapList.get(i).get("EditFlag")));
                        bundle.putInt("CreateFlag",Integer.valueOf(mapList.get(i).get("CreateFlag")));
                        bundle.putInt("RemoveFlag",Integer.valueOf(mapList.get(i).get("RemoveFlag")));
                        bundle.putInt("PrintFlag",Integer.valueOf(mapList.get(i).get("PrintFlag")));
                        bundle.putInt("ImportFlag",Integer.valueOf(mapList.get(i).get("ImportFlag")));
                        bundle.putInt("ExportFlag",Integer.valueOf(mapList.get(i).get("ExportFlag")));
                        //TODO: Intent the Activities by Class
                        Intent intent = new Intent(HomeAcitvity.this, Class.forName(ClassName));
                        intent.putExtra("PermissionBundle",bundle);
                        startActivity(intent);
                        //finish();

                    } catch (Exception e) {
                        MessageDialog.MessageDialog(HomeAcitvity.this, "Error", ""+e.toString());
                    }

                }else {
                    if (Integer.valueOf(mapList.get(i).get("Vtype"))>0) {
                        MessageDialog messageDialog = new MessageDialog();
                        messageDialog.MessageDialog(HomeAcitvity.this, "", "", "Comming soon.....");
                    }
                }

            }else{
                c=0;
            }
        }
        if(c==0){
            List<Map<String,String>> mapListBriefcase=DBHandler.getParentCaptionWithBriefcase();
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HomeAcitvity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new RecyclerViewParentCaptionAdapter(HomeAcitvity.this,mapListBriefcase,recyclerView);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        //TODO: Navigation method Call
        NavigationCall();
        floatingActionButtonHome.setVisibility(View.GONE);
        floatingActionButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Map<String,String>> mapList=DBHandler.getParentCaption();
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HomeAcitvity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                adapter = new RecyclerViewParentCaptionAdapter(HomeAcitvity.this,mapList,recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
        floatingActionButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Map<String,String>> mapList=DBHandler.getParentCaptionWithBriefcase();
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HomeAcitvity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                adapter = new RecyclerViewParentCaptionAdapter(HomeAcitvity.this,mapList,recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {

                }else{
                    MessageDialog.MessageDialog(HomeAcitvity.this,"",status);
                }
            }
        });
    }
    private void Initialization(){
        this.context = HomeAcitvity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        floatingActionButtonHome = (FloatingActionButton) findViewById(R.id.fab_home);
        floatingActionButtonBack = (FloatingActionButton) findViewById(R.id.fab_back);
//        linearLayoutBriefcase = (LinearLayout) findViewById(R.id.Linear);
//        btnBriefcaseAdd = (Button) findViewById(R.id.Button_Add);
//        btnBriefcaseRemove = (Button) findViewById(R.id.Button_Remove);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            List<Map<String,String>> mapList=DBHandler.getParentCaptionWithBriefcase();
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HomeAcitvity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(new RecyclerViewParentCaptionAdapter(HomeAcitvity.this,mapList,recyclerView));
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return true;
    }
    private void NavigationCall(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && toolbar!=null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View view = (View) navigationView.inflateHeaderView(R.layout.nav_header_home);
            final TextView txtViewUserName = (TextView) view.findViewById(R.id.txtView_UserName);
            final TextView txtViewUserType = (TextView) view.findViewById(R.id.txtView_UserType);
            final TextView txtViewDefaultCompany = (TextView) view.findViewById(R.id.txtView_default_comapny);
            txtPicChangeOrUpload = (TextView) view.findViewById(R.id.TextView_ChangeProfile_Photo);
            txtPicCancel = (TextView) view.findViewById(R.id.TextView_CancelProfile_Photo);
            txtPicCancel.setVisibility(View.GONE);
            //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    // Do whatever you want here
                    String[] BasicInfo = DBHandler.getSomeBasicInfo();
                    if (BasicInfo!=null) {
                        txtViewUserName.setText(BasicInfo[17]);
                        txtViewUserType.setText(BasicInfo[4]+"\n"+BasicInfo[1]);
                        txtViewDefaultCompany.setText(BasicInfo[18]+"\n"+BasicInfo[19]+"\n"+BasicInfo[20]);
                        Picasso.with(HomeAcitvity.this).load(BasicInfo[14]).placeholder(R.drawable.ic_profile_pic_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                        ImageUrl = BasicInfo[14];
                    }
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // Do whatever you want here
                    String[] BasicInfo = DBHandler.getSomeBasicInfo();
                    if (BasicInfo!=null) {
                        txtViewUserName.setText(BasicInfo[17]);
                        txtViewUserType.setText(BasicInfo[4]+"\n"+BasicInfo[1]);
                        txtViewDefaultCompany.setText(BasicInfo[18]+"\n"+BasicInfo[19]+"\n"+BasicInfo[20]);
                        Picasso.with(HomeAcitvity.this).load(BasicInfo[14]).placeholder(R.drawable.ic_profile_pic_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                        ImageUrl = BasicInfo[14];
                    }
                }
            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            //TODO NavigationView Left
            List<String> stringList = new ArrayList<String>();
            stringList.add("Profile");
            stringList.add("Settings");
            stringList.add("Company Settings");
            stringList.add("Recommanded Apps");
            stringList.add("Device Info");

            Menu drawerMenu = navigationView.getMenu();
            menuItems = new ArrayList<MenuItem>();
            List<MenuDataset> items = new ArrayList<>();
            NavMenuClass navMenuObject = new NavMenuClass(drawerMenu, items);
            Menu menu = navMenuObject.getMenu();
            for (int temp = 0; temp < stringList.size(); temp++) {
                menu.add(R.id.group1, Menu.NONE, Menu.NONE, stringList.get(temp));
                menuItems.add(drawerMenu.getItem(temp));
            }

            circleImageView = (CircleImageView) view.findViewById(R.id.circleView);
            chronometerActiveTime = (Chronometer) view.findViewById(R.id.chronometer_active_time);
            chronometerActiveTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                @Override
                public void onChronometerTick(Chronometer cArg) {
                    long time = SystemClock.elapsedRealtime() - cArg.getBase();
                    int h   = (int)(time /3600000);
                    int m = (int)(time - h*3600000)/60000;
                    int s= (int)(time - h*3600000- m*60000)/1000 ;
                    String hh = h < 10 ? "0"+h: h+"";
                    String mm = m < 10 ? "0"+m: m+"";
                    String ss = s < 10 ? "0"+s: s+"";
                    cArg.setText(hh+":"+mm+":"+ss);
                }
            });
            chronometerActiveTime.setBase(SystemClock.elapsedRealtime());
            chronometerActiveTime.start();

            //TODO: Basic info
            String[] BasicInfo = DBHandler.getSomeBasicInfo();
            if (BasicInfo!=null) {
                txtViewUserName.setText(BasicInfo[17]);
                txtViewUserType.setText(BasicInfo[4]+"\n"+BasicInfo[1]);
                txtViewDefaultCompany.setText(BasicInfo[18]+"\n"+BasicInfo[19]+"\n"+BasicInfo[20]);
                Picasso.with(this).load(BasicInfo[14]).placeholder(R.drawable.ic_profile_pic_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                ImageUrl = BasicInfo[14];
            }
            txtPicChangeOrUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtPicChangeOrUpload.getText().toString().equals("Change")){
                        if (photoPaths.size()==MAX_ATTACHMENT_COUNT){
                            Toast.makeText(HomeAcitvity.this,"Cannot select more than "+MAX_ATTACHMENT_COUNT+ " items",Toast.LENGTH_LONG).show();
                        }else {
                            FilePickerBuilder.getInstance().setMaxCount(1)
                                    .setSelectedFiles(photoPaths)
                                    .setActivityTheme(R.style.AppTheme)
                                    .pickPhoto(HomeAcitvity.this);
                        }
                    }else if (txtPicChangeOrUpload.getText().toString().equals("Upload")) {
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj = new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(HomeAcitvity.this);
                            if (str != null)
                                if (!photoPaths.isEmpty()) {
                                    String Path = EncodeIntoBase64(photoPaths.get(0));
                                    CallVolleyProfilePicUploadOrChange(str[3], str[4], str[0], Path,str[14]);
                                } else {
                                    MessageDialog.MessageDialog(HomeAcitvity.this, "", "Please select an image");
                                }
                        } else {
                            MessageDialog.MessageDialog(HomeAcitvity.this, "", "" + status);
                        }
                    }
                }
            });
            txtPicCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtPicChangeOrUpload.setText("Change");
                    txtPicCancel.setVisibility(View.GONE);
                    photoPaths = new ArrayList<String>();
                    Picasso.with(context).load(ImageUrl).placeholder(R.drawable.ic_profile_pic_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                }
            });
        }
    }
    class NavMenuClass{
        Menu menu;
        List<MenuDataset> items;
        public NavMenuClass(Menu menu,List<MenuDataset> items){
            this.items = items;
            this.menu = menu;
        }
        public Menu getMenu(){
            return menu;
        }
        public List<MenuDataset> getItems(){
            return items;
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String Title=item.toString();
        //item.setChecked(true);
        int position=menuItems.indexOf(item);

        if(Title.equals("Settings")){

            Intent intent=new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        if(Title.equals("Company Settings")){

            Intent intent=new Intent(getApplicationContext(), SettingsCompanyActivity.class);
            startActivity(intent);
        }
        if(Title.equals("Profile")){

            Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
        if(Title.equals("Recommanded Apps")){

            Intent intent=new Intent(getApplicationContext(), RecommandedAppsActivity.class);
            startActivity(intent);
        }
        if(Title.equals("Device Info")){

            Intent intent=new Intent(getApplicationContext(), DeviceInfoActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logout = menu.findItem(R.id.action_logout);
        logout.setVisible(true);
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
                //System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                if(adapter!=null)
                {
                    adapter.filter(query);
                    //System.out.println("on query submit: "+query);
                }

                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                String[] BasicInfo = DBHandler.getSomeBasicInfo();
                System.out.println(""+BasicInfo[18]+"\n"+BasicInfo[19]+"\n"+BasicInfo[20]);
                NavigationCall();
                break;
            case R.id.action_search:
                //Snackbar.make(recyclerView,"Comming Soon....",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.action_logout:
                LogoutDialog(HomeAcitvity.this);
                //Snackbar.make(btnLogin,"Comming Soon....",Snackbar.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void LogoutDialog(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure ?,You wanted to Logout...");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                if (HomeAcitvity.chronometerActiveTime != null) {
                    //TODO: Logout
                    DatabaseSqlLiteHandlerActiveSessionManage DBSessionManage = new DatabaseSqlLiteHandlerActiveSessionManage(context);

                    String ActiveTime = HomeAcitvity.chronometerActiveTime.getText().toString();
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(getApplicationContext());
                    if (str != null && !ActiveTime.isEmpty() && ActiveTime != null)
                        //DBSessionManage.UpdateLogout(str[0],ActiveTime);
                        SessionManage.CallRetrofitSessionLogout(context,str[3], str[0], str[4], str[14]);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(context, "Logout successfully...", Toast.LENGTH_LONG).show();
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
    protected void onPause() {
        super.onPause();
//        HomeAcitvity.timeWhenStopped = HomeAcitvity.chronometerActiveTime.getBase() - SystemClock.elapsedRealtime();
//        HomeAcitvity.chronometerActiveTime.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        HomeAcitvity.chronometerActiveTime.setBase(SystemClock.elapsedRealtime() + HomeAcitvity.timeWhenStopped);
//        HomeAcitvity.chronometerActiveTime.start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                addThemToView(photoPaths);
                break;
        }
    }
    //TODO: Gallery Image View
    private void addThemToView(ArrayList<String> imagePaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if(imagePaths!=null)
            filePaths.addAll(imagePaths);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if(recyclerView!=null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        try{
            if (!filePaths.isEmpty()) {
                File imgFile = new File(filePaths.get(0));
                if (imgFile.exists()) {
                    circleImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                    txtPicChangeOrUpload.setText("Upload");
                    txtPicCancel.setVisibility(View.VISIBLE);
                }
            }
        }catch (OutOfMemoryError e) {
            Log.e("ERRor", "Exception: "+e.toString());
            MessageDialog.MessageDialog(HomeAcitvity.this,"Gallery","Exception:"+e.toString());
        }catch (Exception e){
            MessageDialog.MessageDialog(HomeAcitvity.this,"Gallery","Exception:"+e.toString());
        }
        //Toast.makeText(this, "Num of files selected: "+ filePaths.size()+"\nPath:"+filePaths.get(0), Toast.LENGTH_SHORT).show();
    }
    private String EncodeIntoBase64(String Path){
        //showpDialog();
        String EncodedString="";
        try{
            System.out.println("Path: "+Path);
            InputStream inputStream = new FileInputStream(new File(Path));
            byte[] data;
            data = IOUtils.toByteArray(inputStream);
            System.out.println("covertByte: "+data.toString());
            // Encode Image to String
            EncodedString = Base64.encodeToString(data, 0);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(HomeAcitvity.this,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private void CallVolleyProfilePicUploadOrChange(final String DeviceID, final String UserID, final String SessionID,final String Image,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UserProfilePicUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    Intent intent = getIntent();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                }catch (Exception e){
                    MessageDialog.MessageDialog(HomeAcitvity.this,"Exception",""+e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(HomeAcitvity.this,"Error",""+error.toString());
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
                params.put("Image", Image);
                params.put("CompanyID", CompanyID);
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
