package report.pendingdispatch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.GodownDataset;

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
import orderbooking.StaticValues;
import report.DatabaseSqlite.DBSqlLitePendingDispatch;
import report.pendingdispatch.adapter.GroupWiseAdapter;
import report.pendingdispatch.adapter.OrderWiseAdapter;
import report.pendingdispatch.adapter.PartyWiseAdapter;
import report.pendingdispatch.adapter.ShowroomAdapter;
import report.pendingdispatch.adapter.SpinnerCommonAdapter;
import report.pendingdispatch.model.GroupWise;
import report.pendingdispatch.model.OrderWise;
import report.pendingdispatch.model.PartyWise;
import services.NetworkUtils;

/**
 * Created by Rakesh on 30-July-17.
 */

public class PendingDispatchActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> Header;
    private Context context;
    private ProgressDialog progressDialog;
    private Bundle bundle;
    private static int pagerPosition=0;
    int  appType=777 ,StockAvail=0 , FinishType=2;
    private String ShowroomID = "777",Showroom="";
    private static String TAG = PendingDispatchActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.viewpager_design);
        DBSqlLitePendingDispatch DB= new DBSqlLitePendingDispatch(getApplicationContext());
        DB.deleteReportData();
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = PendingDispatchActivity.this;
        actionBar=getSupportActionBar();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void ModulePermission(){
        try {
            bundle = getIntent().getBundleExtra("PermissionBundle");
            StaticValues.Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            StaticValues.Vtype = bundle.getInt("Vtype");
            if (StaticValues.Vtype == 17) {
                StaticValues.OrderForceCloseFlag = 0;
            } else if (StaticValues.Vtype == 18) {
                StaticValues.OrderForceCloseFlag = 1;
            }
            actionBar.setTitle(StaticValues.Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //TODO: Call Api Method
                CallApiMethod(String.valueOf(appType), String.valueOf(StockAvail), String.valueOf(FinishType), ShowroomID, 1);
                LoadViewPager();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+StaticValues.Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void LoadViewPager(){
        Header=new ArrayList<String>();
        Header.add("Group Wise");
        Header.add("Party Wise");
        Header.add("Order Wise");
        ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),Header);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(pagerPosition,true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                pagerPosition = position;
                mViewPager.setCurrentItem(pagerPosition,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                context.deleteDatabase(DBSqlLitePendingDispatch.DATABASE_NAME);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.action_filter_search:
                //TODO: Filter Search
                DialogSearchFilter(context);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        MenuItem filterSearchItem = menu.findItem(R.id.action_filter_search);
        filterSearchItem.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){
            //TODO: Activity finish
            context.deleteDatabase(DBSqlLitePendingDispatch.DATABASE_NAME);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void DialogSearchFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_pending_dispatch_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final EditText edtFromDate = (EditText) dialog.findViewById(R.id.EditText_FromDate);
        final EditText edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
        Spinner spnAppType = (Spinner) dialog.findViewById(R.id.spinner_AppType);
        Spinner spnStockAvl = (Spinner) dialog.findViewById(R.id.spinner_StockAvl);
        Spinner spnFinishType = (Spinner) dialog.findViewById(R.id.spinner_FinishType);
        Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String CurrentDate = df.format(c.getTime());
        edtFromDate.setInputType(InputType.TYPE_NULL);
        //TODO: Get Yesterday Date
        c.add(Calendar.DATE, -1);
        String YesterdayDate = df.format(c.getTime());
        edtFromDate.setText(YesterdayDate);
        edtToDate.setInputType(InputType.TYPE_NULL);
        edtToDate.setText(CurrentDate);
        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                        edtFromDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select From Date");
                datePicker.show();
            }
        });
        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                        edtToDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select To Date");
                datePicker.show();
            }
        });
        //TODO: App Type Spinner
        String[] arrayAppType = getResources().getStringArray(R.array.AppType);
        for (int i=0; i<arrayAppType.length; i++) {
            if (appType == 777){
                spnAppType.setSelection(0);
                break;
            }else if (appType == i){
                spnAppType.setSelection(i+1);
                break;
            }
        }
        spnAppType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                String Name=parent.getItemAtPosition(position).toString();
                if(Name.contains("ALL"))
                {
                    appType=777;//AppType All=777
                }
                else if(Name.equals("0-FCA"))
                {
                    appType=Integer.parseInt(Name.substring(0,1));
                }
                else if(Name.equals("1-Singla Groups"))
                {
                    appType=Integer.parseInt(Name.substring(0,1));
                }
                else if(Name.equals("2-La'Scoot Android App"))
                {
                    appType=Integer.parseInt(Name.substring(0,1));
                }
                else if(Name.equals("3-La'Scoot IOS App"))
                {
                    appType=Integer.parseInt(Name.substring(0,1));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Stock Available Spinner
        String[] arrayStockAvl = getResources().getStringArray(R.array.StockAvl);
        for (int i=0; i<arrayStockAvl.length; i++) {
            if (StockAvail == i){
                spnStockAvl.setSelection(i);
                break;
            }
        }
        spnStockAvl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                StockAvail = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Finish Type Spinner
        spnFinishType.setSelection(2);
        String[] arrayFinishType = getResources().getStringArray(R.array.FinishType);
        for (int i=0; i<arrayFinishType.length; i++) {
            if (FinishType == i){
                spnFinishType.setSelection(i);
                break;
            }
        }
        spnFinishType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                FinishType = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Showroom Spinner
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
            final List<GodownDataset> godownDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
            ShowroomAdapter adapter = new ShowroomAdapter(context, godownDatasetList);
            spnShowroom.setAdapter(adapter);
            for (int i=0; i<godownDatasetList.size(); i++) {
                if (ShowroomID.equals("777")){
                    spnShowroom.setSelection(i);
                    break;
                }else if (ShowroomID.equals(godownDatasetList.get(i).getGodownID())){
                    spnShowroom.setSelection(i);
                    break;
                }
            }
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    if (position == 0) {
                        ShowroomID = "777";
                        Showroom = "ALL Showrooms";
                    }else{
                        ShowroomID = godownDatasetList.get(position).getGodownID();
                        Showroom = godownDatasetList.get(position).getGodownName();
                    }
//                            if (Showroom.equals("Fair")) {
//                                ShowroomID = godownDatasetList.get(position).getGodownID();
//                                Showroom = godownDatasetList.get(position).getGodownName();
//                            } else {
//                                ShowroomID = godownDatasetList.get(position).getGodownID();
//                                Showroom = godownDatasetList.get(position).getGodownName();
//                            }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String FromDate = edtFromDate.getText().toString();
                String ToDate = edtToDate.getText().toString();
                CallApiMethod(String.valueOf(appType), String.valueOf(StockAvail), String.valueOf(FinishType), ShowroomID, 1);
                dialog.dismiss();
            }
        });
    }
    //TODO: View Pager Adapter
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> Header;
        public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> Header) {
            super(fragmentManager);
            this.Header=Header;
        }
        @Override
        public Fragment getItem(int position) {
            return PendingDispatchFragment.newInstance(Header.get(position),position);
        }
        public CharSequence getPageTitle(int position) {
            return Header.get(position);
        }
        @Override
        public int getCount() {
            return Header.size();
        }
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return POSITION_NONE;
        }
    }
    //TODO: Group Wise fragment
    public static class PendingDispatchFragment extends Fragment{
        private static final String HEADER = "Header";
        private static final String POSITION = "Position";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private ProgressDialog progressDialog;
        private Dialog dialog;
        private GroupWiseAdapter gAdapter;
        private PartyWiseAdapter pAdapter;
        private OrderWiseAdapter oAdapter;
        private DBSqlLitePendingDispatch DBPendingDispatch;
        private int HeaderPos = 0, appType=0 ,StockAvail=0 , FinishType=0;
        private String ShowroomID="";
        private int custFlag=0,pendingFlag=0,exDelFlag=0;
        private int agentPos=0,showroomPos=0,OrderTypePos=0;
        private String ShowroomId="",Showroom="",Agent="",OrderType="",OrderTypeName="",FromDT="",ToDT="";
        public static PendingDispatchFragment newInstance(String Header,int Position) {
            PendingDispatchFragment fragment = new PendingDispatchFragment();
            Bundle args = new Bundle();
            args.putString(HEADER, Header);
            args.putInt(POSITION, Position);
            fragment.setArguments(args);
            return fragment;
        }
        public PendingDispatchFragment() {}
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getActivity().supportInvalidateOptionsMenu();
            setHasOptionsMenu(true);
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_swipe_refresh_layout, null);
            this.context = getActivity();
            this.HeaderPos = getArguments().getInt(POSITION,0);
            Initialization(rootView);
            //TODO: Module Permission
            ModulePermission(HeaderPos);
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            DBPendingDispatch = new DBSqlLitePendingDispatch(context);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        private void ModulePermission(final int Position){
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //CallApiMethod(0);
                LoadRecyclerView(Position);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //CallApiMethod(0);
                        LoadRecyclerView(Position);
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void LoadRecyclerView(int Position){
            if (Position == 0){
                //TODO: Load Adapter
                gAdapter = new GroupWiseAdapter(context,DBPendingDispatch.getGroupWise());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setAdapter(gAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        GroupWise dataset = (GroupWise) gAdapter.getItem(position);
                        Intent intent = new Intent(context, ItemDetailsListByAllWiseActivity.class);
                        intent.putExtra("Key",dataset);
                        startActivity(intent);
                    }
                });
            }else  if (Position == 1){
                //TODO: Module Permission
                pAdapter = new PartyWiseAdapter(context,DBPendingDispatch.getPartyWise());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setAdapter(pAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        PartyWise dataset = (PartyWise) pAdapter.getItem(position);
                        Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                        OrderWise orderWise=null;
                        intent.putExtra("PartyKey",dataset);
                        intent.putExtra("OrderKey",orderWise);
                        startActivity(intent);
                    }
                });
            }else  if (Position == 2){
                //TODO: Module Permission
                oAdapter = new OrderWiseAdapter(context,DBPendingDispatch.getOrderWise());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setAdapter(oAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                        Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                        PartyWise partyWise=null;
                        intent.putExtra("PartyKey",partyWise);
                        intent.putExtra("OrderKey",orderWise);
                        startActivity(intent);
                    }
                });
            }
            hidepDialog();
        }
        private void showpDialog() {
            if(progressDialog!=null) {
                progressDialog.show();
            }
        }
        private void hidepDialog() {
            if(progressDialog!=null || swipeRefreshLayout!=null) {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        private void DialogSortFilter(final Context context){
            final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_pending_dispatch_filter_sort);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            //TODO: Declarations
            TextView txtViewClear = (TextView) dialog.findViewById(R.id.TextView_Clear);
            RadioGroup radioGroupCustomerWise = (RadioGroup) dialog.findViewById(R.id.Radio_CustomerWise);
            RadioGroup radioGroupPendingDaysWise = (RadioGroup) dialog.findViewById(R.id.Radio_PendingDaysWise);
            RadioGroup radioGroupExDeliveryDateWise = (RadioGroup) dialog.findViewById(R.id.Radio_ExpectedDelDateWise);
            final Spinner spnAgent = (Spinner) dialog.findViewById(R.id.spinner_Agent);
            final Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
            final Spinner spnAppType = (Spinner) dialog.findViewById(R.id.spinner_AppType);
            final EditText edtFromDt = (EditText) dialog.findViewById(R.id.EditText_FromDate);
            final EditText edtToDt = (EditText) dialog.findViewById(R.id.EditText_ToDate);
            Button btnApply = (Button) dialog.findViewById(R.id.Button_Apply);
            //TODO: Get Current Date
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String CurrentDate = df.format(c.getTime());
            edtFromDt.setInputType(InputType.TYPE_NULL);
            //TODO: Get Yesterday Date
            c.add(Calendar.DATE, -1);
            String YesterdayDate = df.format(c.getTime());
            //edtFromDt.setText(YesterdayDate);
            edtToDt.setInputType(InputType.TYPE_NULL);
            //edtToDt.setText(CurrentDate);
            edtFromDt.setText(FromDT);
            edtToDt.setText(ToDT);
            edtFromDt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            DecimalFormat formatter = new DecimalFormat("00");
                            String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                            edtFromDt.setText(Date);
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setTitle("Select From Date");
                    datePicker.show();
                }
            });
            edtToDt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            DecimalFormat formatter = new DecimalFormat("00");
                            String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                            edtToDt.setText(Date);
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setTitle("Select To Date");
                    datePicker.show();
                }
            });
            if(custFlag == 1 && pendingFlag == 1 && exDelFlag == 1){
                RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Pending);
                radioBtn.setChecked(true);
                RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_ExDelDate);
                radioBtnSt.setChecked(true);
                RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Customer);
                radioBtnSt2.setChecked(true);

            }else if(custFlag == 1 || pendingFlag == 1 || exDelFlag == 1){

                if(custFlag == 1 && pendingFlag == 1){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Pending);
                    radioBtn.setChecked(true);
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(exDelFlag == 1 && pendingFlag == 1){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Pending);
                    radioBtn.setChecked(true);
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_ExDelDate);
                    radioBtnSt.setChecked(true);
                }else if(exDelFlag == 1 && custFlag == 1){
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_ExDelDate);
                    radioBtnSt.setChecked(true);
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(custFlag == 1){
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(pendingFlag == 1){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Pending);
                    radioBtn.setChecked(true);
                }else if(exDelFlag == 1){
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_ExDelDate);
                    radioBtnSt.setChecked(true);
                }
            }else if(custFlag == 2 && pendingFlag == 2 && exDelFlag == 2){
                RadioButton radioBtn1=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Pending);
                radioBtn1.setChecked(true);
                RadioButton radioBtnSt1=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_ExDelDate);
                radioBtnSt1.setChecked(true);
                RadioButton radioBtnSt3=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Customer);
                radioBtnSt3.setChecked(true);
            }else if(custFlag == 2 || pendingFlag == 2 || exDelFlag == 2){

                if(custFlag == 2 && pendingFlag == 2){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Pending);
                    radioBtn.setChecked(true);
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(exDelFlag == 2 && pendingFlag == 2){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Pending);
                    radioBtn.setChecked(true);
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_ExDelDate);
                    radioBtnSt.setChecked(true);
                }else if(exDelFlag == 2 && custFlag == 2){
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_ExDelDate);
                    radioBtnSt.setChecked(true);
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(custFlag == 2){
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Customer);
                    radioBtnSt2.setChecked(true);
                }else if(pendingFlag == 2){
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Pending);
                    radioBtn.setChecked(true);
                }else if(exDelFlag == 2){
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_ExDelDate);
                    radioBtnSt.setChecked(true);
                }
            }
            List<Map<String,String>> mapListAgent = DBPendingDispatch.getAgentList();
            List<Map<String,String>> mapListShowroom = DBPendingDispatch.getShowroomList();
            List<Map<String,String>> mapListAppType = DBPendingDispatch.getOrderTypeList();
            spnAgent.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAgent));
            spnAgent.setSelection(agentPos);
            spnAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    // TODO Auto-generated method stub
                    agentPos = position;
                    if(position>0){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map=(Map<String, Object>) parent.getAdapter().getItem(position);
                        Agent = map.get("ID").toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }
            });
            spnShowroom.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListShowroom));
            spnShowroom.setSelection(showroomPos);
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    // TODO Auto-generated method stub
                    showroomPos = position;
                    if(position>0){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map=(Map<String, Object>) parent.getAdapter().getItem(position);
                        ShowroomId = map.get("ID").toString();
                        Showroom = map.get("Name").toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }
            });
            spnAppType.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAppType));
            spnAppType.setSelection(OrderTypePos);
            spnAppType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    // TODO Auto-generated method stub
                    OrderTypePos = position;
                    if(position>0){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map=(Map<String, Object>) parent.getAdapter().getItem(position);
                        OrderType = map.get("ID").toString();
                        OrderTypeName = map.get("Name").toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }
            });
            radioGroupCustomerWise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    if(checkedId == R.id.RadioButton_Asc_Customer){
                        custFlag  = 1;
                    }else if(checkedId == R.id.RadioButton_Desc_Customer){
                        custFlag  = 2;
                    }
                }
            });
            radioGroupPendingDaysWise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    if(checkedId == R.id.RadioButton_Asc_Pending){
                        pendingFlag = 1;
                    }else if(checkedId == R.id.RadioButton_Desc_Pending){
                        pendingFlag = 2;
                    }
                }
            });
            radioGroupExDeliveryDateWise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    if(checkedId == R.id.RadioButton_Asc_ExDelDate){
                        exDelFlag = 1;
                    }else if(checkedId == R.id.RadioButton_Desc_ExDelDate){
                        exDelFlag = 2;
                    }
                }
            });
            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Filter Apply
                    String FromDate = edtFromDt.getText().toString();
                    String ToDate = edtToDt.getText().toString();
                    FromDT = FromDate;
                    ToDT = ToDate;
                    List<OrderWise> OrderList = DBPendingDispatch.getOrderListFiltered(String.valueOf(custFlag), String.valueOf(pendingFlag), String.valueOf(exDelFlag), Agent, Showroom, OrderTypeName, DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate), DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDT));
                    oAdapter = new OrderWiseAdapter(context,OrderList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setAdapter(oAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                            Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                            PartyWise partyWise=null;
                            intent.putExtra("PartyKey",partyWise);
                            intent.putExtra("OrderKey",orderWise);
                            startActivity(intent);
                        }
                    });
                    dialog.dismiss();
                }
            });
            txtViewClear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Pending);
                    radioBtn.setChecked(false);
                    RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_ExDelDate);
                    radioBtnSt.setChecked(false);
                    RadioButton radioBtnSt2=(RadioButton) dialog.findViewById(R.id.RadioButton_Asc_Customer);
                    radioBtnSt2.setChecked(false);
                    RadioButton radioBtn1=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Pending);
                    radioBtn1.setChecked(false);
                    RadioButton radioBtnSt1=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_ExDelDate);
                    radioBtnSt1.setChecked(false);
                    RadioButton radioBtnSt3=(RadioButton) dialog.findViewById(R.id.RadioButton_Desc_Customer);
                    radioBtnSt3.setChecked(false);
                    custFlag=0;
                    pendingFlag=0;
                    exDelFlag=0;
                    agentPos=0;
                    showroomPos=0;
                    OrderTypePos=0;
                    Agent="";
                    Showroom="";
                    ShowroomId="";
                    OrderTypeName="";
                    OrderType="";
                    edtFromDt.setText("");
                    edtToDt.setText("");
                    FromDT = "";
                    ToDT = "";
                    List<Map<String,String>> mapListAgent = DBPendingDispatch.getAgentList();
                    List<Map<String,String>> mapListShowroom = DBPendingDispatch.getShowroomList();
                    List<Map<String,String>> mapListAppType = DBPendingDispatch.getOrderTypeList();
                    spnAgent.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAgent));
                    spnShowroom.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListShowroom));
                    spnAppType.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAppType));
                }
            });
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch(item.getItemId()){
                case R.id.action_search:
                    break;
                case android.R.id.home:
                    //TODO: Activity finish
                    context.deleteDatabase(DBSqlLitePendingDispatch.DATABASE_NAME);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
                case R.id.action_filter_sort:
                    DialogSortFilter(context);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.menu_main, menu);
            MenuItem filterSearchItem = menu.findItem(R.id.action_filter_search);
            filterSearchItem.setVisible(true);
            MenuItem sortFilterItem = menu.findItem(R.id.action_filter_sort);
            if (HeaderPos == 2) {   sortFilterItem.setVisible(true);   }
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
                    if (HeaderPos == 0) {
                        if (gAdapter != null)
                            gAdapter.filter(newText);
                    }else if (HeaderPos == 1) {
                        if (pAdapter != null)
                            pAdapter.filter(newText);
                    }else if (HeaderPos == 2) {
                        if (oAdapter != null)
                            oAdapter.filter(newText);
                    }
//                System.out.println("on text chnge text: "+newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    // this is your adapter that will be filtered
                    if (HeaderPos == 0) {
                        if (gAdapter != null)
                            gAdapter.filter(query);
                    }else if (HeaderPos == 1) {
                        if (pAdapter != null)
                            pAdapter.filter(query);
                    }else if (HeaderPos == 2) {
                        if (oAdapter != null)
                            oAdapter.filter(query);
                    }
//                System.out.println("on query submit: "+query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(textChangeListener);
            super.onCreateOptionsMenu(menu,inflater);
        }
        @Override
        public void onResume(){
            super.onResume();
            //CallApiMethod(0);
        }

    }
    //TODO: Call Api Method
    private void CallApiMethod(final String AppType, final String StockAvl, final String ItemCategory, final String ShowroomID, final int flag){
        //TODO: Call Volley Api
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyPendingDispatch(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Call Volley PendingDispatch With Order ForceClose
    public void CallVolleyPendingDispatch(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String AppType,final String StockAvl,final String ItemCategory,final String ShowroomID,final int flag){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"GetPendingDispatch", new Response.Listener<String>()
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
                        JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                        List<Map<String,String>> mapList = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("MainGroupName",(jsonArrayScfo.getJSONObject(i).optString("MainGroup")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MainGroup")));
                            map.put("GroupID",(jsonArrayScfo.getJSONObject(i).optString("GroupID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupID")));
                            map.put("GroupName",(jsonArrayScfo.getJSONObject(i).optString("GroupName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupName")));
                            map.put("CustID",(jsonArrayScfo.getJSONObject(i).optString("PartyID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyID")));
                            map.put("CustName",(jsonArrayScfo.getJSONObject(i).optString("PartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyName")));
                            map.put("SubCustID",(jsonArrayScfo.getJSONObject(i).optString("SubPartyID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyID")));
                            map.put("SubCustName",(jsonArrayScfo.getJSONObject(i).optString("SubPartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyName")));
                            map.put("OrderID",(jsonArrayScfo.getJSONObject(i).optString("OrderID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderID")));
                            map.put("OrderNo",(jsonArrayScfo.getJSONObject(i).optString("OrderNo")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderNo")));
                            map.put("OrderDate",(jsonArrayScfo.getJSONObject(i).optString("OrderDate")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderDate")));
                            map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
                            map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
                            map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
                            map.put("SubItemName",(jsonArrayScfo.getJSONObject(i).optString("SubItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemName")));
                            map.put("ColorID",(jsonArrayScfo.getJSONObject(i).optString("ColorID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorID")));
                            map.put("ColorName",(jsonArrayScfo.getJSONObject(i).optString("ColorName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorName")));
                            map.put("SizeID",(jsonArrayScfo.getJSONObject(i).optString("SizeID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SizeID")));
                            map.put("SizeName",(jsonArrayScfo.getJSONObject(i).optString("SizeName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SizeName")));
                            map.put("price",(jsonArrayScfo.getJSONObject(i).optString("Price")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Price")));
                            map.put("stock",(jsonArrayScfo.getJSONObject(i).optString("ItemStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemStock")));
                            map.put("pendingQty",(jsonArrayScfo.getJSONObject(i).optString("PenQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PenQty")));
                            map.put("bookedQty",(jsonArrayScfo.getJSONObject(i).optString("CSBookQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CSBookQty")));
                            map.put("DispatchQty",(jsonArrayScfo.getJSONObject(i).optString("DispQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("DispQty")));
                            map.put("colorSizeStock",(jsonArrayScfo.getJSONObject(i).optString("CLStk")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CLStk")));
                            map.put("subItemStock",(jsonArrayScfo.getJSONObject(i).optString("SubItStk")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItStk")));
                            map.put("ColorFamilyName",(jsonArrayScfo.getJSONObject(i).optString("ColorFamily")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorFamily")));
                            map.put("AStatus",(jsonArrayScfo.getJSONObject(i).optString("AStatus")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AStatus")));
                            map.put("RefrenceName",(jsonArrayScfo.getJSONObject(i).optString("RefrenceName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("RefrenceName")));
                            map.put("OrderType",(jsonArrayScfo.getJSONObject(i).optString("OrderType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderType")));
                            map.put("OrderQty",(jsonArrayScfo.getJSONObject(i).optString("OrderQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderQty")));
                            map.put("OrderTypeVal",(jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")));
                            map.put("ShowroomID",(jsonArrayScfo.getJSONObject(i).optString("ShowroomID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ShowroomID")));
                            map.put("Showroom",(jsonArrayScfo.getJSONObject(i).optString("Showroom")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Showroom")));
                            map.put("ExpectedDeliveryDate",(jsonArrayScfo.getJSONObject(i).optString("ExpDT")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ExpDT")));
                            map.put("urgencyLevel",(jsonArrayScfo.getJSONObject(i).optString("UrgLabel")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("UrgLabel")));
                            map.put("Agent",(jsonArrayScfo.getJSONObject(i).optString("Agent")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Agent")));
                            map.put("MDApplicable",(jsonArrayScfo.getJSONObject(i).optString("MDApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MDApp")));
                            map.put("SubItemApplicable",(jsonArrayScfo.getJSONObject(i).optString("SIApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SIApp")));
                            //TODO: Add map into list
                            mapList.add(map);
                        }
                        context.deleteDatabase(DBSqlLitePendingDispatch.DATABASE_NAME);
                        DBSqlLitePendingDispatch DB= new DBSqlLitePendingDispatch(context);
                        DB.deleteReportData();
                        DB.insertPendingVsDispatchTable(mapList);
                        //TODO: Load View Pager
                        LoadViewPager();
                    } else {
                        //if (flag == 1)
                        MessageDialog.MessageDialog(context,"",Msg);
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
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("AppType", AppType);
                params.put("StockAvl", StockAvl);
                params.put("ItemCategory", ItemCategory);
                params.put("ShowroomID", ShowroomID);
                Log.i(TAG,"Pending Dispatch parameters:"+params.toString());
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
