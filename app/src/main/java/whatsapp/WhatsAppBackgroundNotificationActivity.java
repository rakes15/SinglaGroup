package whatsapp;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import whatsapp.adapter.SchedulerAdapter;
import whatsapp.autoresponder.Model.Rest.ListResponse;
import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.Service.SchedulerService;
import whatsapp.autoresponder.Utils.Global;
import whatsapp.database.DBSqliteWhatsApp;

/**
 * Created by rakes on 02-Dec-17.
 */

public class WhatsAppBackgroundNotificationActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private Spinner spnIncomOutgoing;
    private TableLayout tableLayout;
    private RecyclerView recyclerView;
    private Button btnRefresh;
    private ProgressDialog progressDialog;
    private DBSqliteWhatsApp DBWhatsApp;
    private SchedulerAdapter adapter;
    private int iType = 1;
    private static String TAG = WhatsAppBackgroundNotificationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_table_layout_with_button);
        Initialization();
        ModulePermission();
        ClickEvent();
    }
    private void Initialization() {
        this.context = WhatsAppBackgroundNotificationActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO: Spinner
        spnIncomOutgoing = (Spinner) findViewById(R.id.spinner_incoming_outgoing);
        spnIncomOutgoing.setVisibility(View.VISIBLE);
        //TODO: Table Layout
        tableLayout = (TableLayout) findViewById(R.id.table_Layout);
        //TODO: Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //TODO: Button
        btnRefresh = (Button) findViewById(R.id.Button_Refresh);
        //TODO: Progress Dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        //TODO: Database whats app
        this.DBWhatsApp = new DBSqliteWhatsApp(context);
    }
    private void ModulePermission() {
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
                LoadRecyclerView(iType);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void ClickEvent() {
        spnIncomOutgoing.setSelection(iType-1);
        spnIncomOutgoing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                iType = i+1;
                LoadRecyclerView(iType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<SchedulerModel> queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
                int index = 0;
                if (queue != null && queue.size() > 0) {

                    Intent scheduler = new Intent(context, SchedulerService.class);
                    scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, queue.get(index));
                    context.startService(scheduler);

                    // TODO : Update action flag
                    DBWhatsApp.UpdateByFlag(queue.get(index).getId(),DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
                }
            }
        });
    }
    private void LoadTableLayout(int iType) {
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();;
        //TODO: Get Data from Preferences
        ArrayList<SchedulerModel> schedulerModelList = DBWhatsApp.GetInOutDataByType(iType);
//        String schedulerModelListStr = Preferences.getPreferenceString(getApplicationContext(), Preferences.KEY_QUEUE, "");
//        Gson gson = new Gson();
//
//        if (!schedulerModelListStr.equals("")) {
//            schedulerModelList = gson.fromJson(schedulerModelListStr, new TypeToken<ArrayList<SchedulerModel>>() {
//            }.getType());
//        }
        View v = LayoutInflater.from(context).inflate(R.layout.table_row_7_col_header, tableLayout, false);
        tableLayout.addView(v);

        if (schedulerModelList != null && schedulerModelList.size() > 0) {
            for (int i = 0; i < schedulerModelList.size(); i++){

                String ConverName = schedulerModelList.get(i).getConversationName();
                String Phone = (iType == 2 ? schedulerModelList.get(i).getPhnNumber() : ConverName);
                String Text = schedulerModelList.get(i).getText();
                String ActivityType = ""+schedulerModelList.get(i).getActivityType();
                String MsgType = ""+schedulerModelList.get(i).getMessageType();
                String MessageID = schedulerModelList.get(i).getMessageId()== null || schedulerModelList.get(i).getMessageId().equals("null") ? "" : schedulerModelList.get(i).getMessageId();
                //MessageID = MessageID;//.length() > 5 ? MessageID.substring(0,8) : MessageID;
                String DeviceID = schedulerModelList.get(i).getDeviceId() == null  || schedulerModelList.get(i).getDeviceId().equals("null") ? "" : schedulerModelList.get(i).getDeviceId();
                //DeviceID = DeviceID;//.length() > 5 ? DeviceID.substring(0,8) : DeviceID;
                String FileUrl = schedulerModelList.get(i).getFileUrl()==null  || schedulerModelList.get(i).getFileUrl().equals("null") ? "" : schedulerModelList.get(i).getFileUrl();
                //FileUrl = FileUrl.length() > 5 ? FileUrl.substring(0,8) : FileUrl;

                v = LayoutInflater.from(context).inflate(R.layout.table_row_7_col_content, tableLayout, false);
                ((TextView) v.findViewById(R.id.content1)).setText("" + Phone);
                ((TextView) v.findViewById(R.id.content2)).setText("" + Text);
                ((TextView) v.findViewById(R.id.content3)).setText("" + MsgType);
                ((TextView) v.findViewById(R.id.content4)).setText("" + ActivityType);
                ((TextView) v.findViewById(R.id.content5)).setText("" + MessageID);
                ((TextView) v.findViewById(R.id.content6)).setText("" + DeviceID);
                ((TextView) v.findViewById(R.id.content7)).setText("" + FileUrl);
                tableLayout.addView(v);
            }
            //if (!checkServiceRunning()) {
//                Intent scheduler = new Intent(context, SchedulerService.class);
//                scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, schedulerModelList.get(0));
//                startService(scheduler);
            //}
        }

    }
    private void LoadRecyclerView(int iType){
        ArrayList<SchedulerModel> schedulerModelList = DBWhatsApp.GetInOutDataByType(iType);
        adapter = new SchedulerAdapter(context,schedulerModelList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void ServiceStarted(){
        ArrayList<SchedulerModel> queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
        int index = 0;
        if (queue != null && queue.size() > 0) {
            Intent scheduler = new Intent(context, SchedulerService.class);
            //scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, gson.toJson(queue.get(0)));
            scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, queue.get(index));
            getApplicationContext().startService(scheduler);
            // TODO : Update action flag
            DBWhatsApp.UpdateByFlag(queue.get(index).getId(),DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
        }
    }
    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){

            if ("whatsapp.autoresponder.Service.SchedulerService".equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null ){// || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            //swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Hide();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                finish();
                break;
            case R.id.action_logout: // Refresh
                ModulePermission();
                ServiceStarted();
                Toast.makeText(context,"Service started...",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_search: // search
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
        MenuItem refreshItem = menu.findItem(R.id.action_logout);//Refresh
        refreshItem.setVisible(true);
        refreshItem.setIcon(context.getResources().getDrawable(R.drawable.ic_action_refresh));
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
