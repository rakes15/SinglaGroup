package report.forceclose;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.GlobleValues;
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
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.forceclose.adapter.GroupWiseAdapter;
import report.forceclose.adapter.OrderWiseAdapter;
import report.forceclose.adapter.PartyWiseAdapter;
import report.forceclose.adapter.ShowroomAdapter;
import report.forceclose.adapter.SpinnerCommonAdapter;
import report.forceclose.model.GroupWise;
import report.forceclose.model.OrderWise;
import report.forceclose.model.PartyWise;
import services.NetworkUtils;

/**
 * Created by Rakesh on 30-July-17.
 */

public class ForceCloseActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> Header;
    private Context context;
    private ProgressDialog progressDialog;
    private Bundle bundle;
    private static int pagerPosition=0;
    int Type = 0;
    private static String TAG = ForceCloseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.viewpager_design);
        DBSqlLiteForceClose DB= new DBSqlLiteForceClose(getApplicationContext());
        DB.deleteReportData();
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = ForceCloseActivity.this;
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
                ConditionByVType(StaticValues.Vtype);
                LoadViewPager();
                //TODO: Get Current Date
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String CurrentDate = df.format(c.getTime());
                //TODO: Get 6 Month Date
                c.add(Calendar.MONTH, -6);
                String YesterdayDate = df.format(c.getTime());
                CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(YesterdayDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(CurrentDate),String.valueOf(Type), 1);
                //DialogSearchFilter(context);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void ConditionByVType(int VType){
        //TODO: Sales Report with Rate flag 0
        if (VType == 19){
            //TODO: Sales Order
            Type = 0;
            GlobleValues.multi_action_flag = 0;
        }else if (VType == 45){
            //TODO: Purchase Order
            Type = 1;
            GlobleValues.multi_action_flag = 0;
        }else if (VType == 46){
            //TODO: Producation Order
            Type = 2;
            GlobleValues.multi_action_flag = 0;
        }
        //TODO: Force Close flag 1
        else if (VType == 18){
            //TODO: Sales Order force close
            Type = 0;
            GlobleValues.multi_action_flag = 1;
        }else if (VType == 36){
            //TODO: Purchase Order force close
            Type = 1;
            GlobleValues.multi_action_flag = 1;
        }else if (VType == 23){
            //TODO: Producation Order force close
            Type = 2;
            GlobleValues.multi_action_flag = 1;
        }
        //TODO: Expected Delivery Date  flag 2
        else if (VType == 39){
            //TODO: Sales Order
            Type = 0;
            GlobleValues.multi_action_flag = 2;
        }else if (VType == 43){
            //TODO: Purchase Order
            Type = 1;
            GlobleValues.multi_action_flag = 2;
        }else if (VType == 44){
            //TODO: Producation Order
            Type = 2;
            GlobleValues.multi_action_flag = 2;
        }
    }
    private void LoadViewPager(){
        Header=new ArrayList<String>();
        //TODO: VType 39-Sales Order, 43-Purchase Order,44-Production Order
        if (StaticValues.Vtype == 39 || StaticValues.Vtype == 43 || StaticValues.Vtype == 44){
            Header.add("Order Wise");
        }else {
            Header.add("Group Wise");
            Header.add("Party Wise");//Type == 2 ? "Jobber Wise" : "Party Wise");
            Header.add("Order Wise");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){
            //TODO: Activity finish
            context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
                finish();
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
    private void DialogSearchFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_force_close_search_filter);
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
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.Linear_Type);
        Spinner spnType = (Spinner) dialog.findViewById(R.id.spinner_Type);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Order Type Hide
        linearLayout.setVisibility(View.GONE);
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
        //TODO: Order Type Spinner
//        String[] arrayAppType = getResources().getStringArray(R.array.ForceCloseType);
//        for (int i=0; i<arrayAppType.length; i++) {
//            if (Type == i){
//                spnType.setSelection(i);
//                break;
//            }
//        }
//        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//                Type = position;
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String FromDate = edtFromDate.getText().toString();
                String ToDate = edtToDate.getText().toString();
                CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate),String.valueOf(Type), 1);
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
            return ForceCloseFragment.newInstance(Header.get(position),position);
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
    public static class ForceCloseFragment extends Fragment{
        private static final String HEADER = "Header";
        private static final String POSITION = "Position";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private TableLayout tableLayoutFooter;
        private ProgressDialog progressDialog;
        private Dialog dialog;
        private GroupWiseAdapter gAdapter;
        private PartyWiseAdapter pAdapter;
        private OrderWiseAdapter oAdapter;
        private DBSqlLiteForceClose DBForceClose;
        private int HeaderPos = 0,urgencyLevel=0;
        public static ForceCloseFragment newInstance(String Header,int Position) {
            ForceCloseFragment fragment = new ForceCloseFragment();
            Bundle args = new Bundle();
            args.putString(HEADER, Header);
            args.putInt(POSITION, Position);
            fragment.setArguments(args);
            return fragment;
        }
        public ForceCloseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_swipe_refresh_layout_with_footer, null);
            setHasOptionsMenu(true);
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
            tableLayoutFooter = (TableLayout) view.findViewById(R.id.table_Layout_footer);
            DBForceClose = new DBSqlLiteForceClose(context);
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
            //TODO: Expected Delivery Date  flag 2
            if (GlobleValues.multi_action_flag == 2){
                //TODO: Module Permission
                oAdapter = new OrderWiseAdapter(context, DBForceClose.getOrderWise());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setAdapter(oAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                        Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                        PartyWise partyWise = null;
                        intent.putExtra("PartyKey", partyWise);
                        intent.putExtra("OrderKey", orderWise);
                        startActivity(intent);
                    }
                });
                ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                        DialogExpectedDeliveryDatetimeUpdate(orderWise);
                        return false;
                    }
                });
            }else {
                if (Position == 0) {
                    //TODO: Load Adapter
                    gAdapter = new GroupWiseAdapter(context, DBForceClose.getGroupWise());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setAdapter(gAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            GroupWise dataset = (GroupWise) gAdapter.getItem(position);
                            Intent intent = new Intent(context, ItemDetailsListByAllWiseActivity.class);
                            intent.putExtra("Key", dataset);
                            startActivity(intent);
                        }
                    });
                } else if (Position == 1) {
                    //TODO: Module Permission
                    pAdapter = new PartyWiseAdapter(context, DBForceClose.getPartyWise());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setAdapter(pAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            PartyWise dataset = (PartyWise) pAdapter.getItem(position);
                            Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                            OrderWise orderWise = null;
                            intent.putExtra("PartyKey", dataset);
                            intent.putExtra("OrderKey", orderWise);
                            startActivity(intent);
                        }
                    });
                    //TODO: Force Close Flag 1
                    if (GlobleValues.multi_action_flag == 1) {
                        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                                PartyWise partyWise = (PartyWise) pAdapter.getItem(position);
                                AlertDialogMethod(partyWise, null);
                                return false;
                                }
                        });
                    }
                } else if (Position == 2) {
                    //TODO: Module Permission
                    oAdapter = new OrderWiseAdapter(context, DBForceClose.getOrderWise());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setAdapter(oAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                            Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                            PartyWise partyWise = null;
                            intent.putExtra("PartyKey", partyWise);
                            intent.putExtra("OrderKey", orderWise);
                            startActivity(intent);
                        }
                    });
                    //TODO: Force Close Flag 1
                    if (GlobleValues.multi_action_flag == 1) {
                        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                                OrderWise orderWise = (OrderWise) oAdapter.getItem(position);
                                AlertDialogMethod(null, orderWise);
                                return false;
                                }
                        });
                    }
                }
            }
            //TODO: Add Footer
            AddFooter();
            hideDialog();
        }
        private void AddFooter(){
            if (GlobleValues.multi_action_flag == 0){
                String Footer1="",Footer2="",Footer3="";
                if (DBForceClose.getTotalCount()!=null && !DBForceClose.getTotalCount().isEmpty()) {
                    Footer1 = "Total Items : "+DBForceClose.getTotalCount().get("TotalItems");//+dataset.getPendingItems();
                    Footer2 = "Total Qty : "+DBForceClose.getTotalCount().get("TotalPendingQty");//+dataset.getOrderQty();
                    Footer3 = "Total Amt : "+DBForceClose.getTotalCount().get("TotalPendingAmt");//+dataset.getPendingAmt();
                }
                tableLayoutFooter.removeAllViewsInLayout();
                tableLayoutFooter.removeAllViews();
                View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayoutFooter, false);

                TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
                txtContent1.setText(Footer1);

                TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
                txtContent2.setText(Footer2);

                TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
                txtContent3.setText(Footer3);
                tableLayoutFooter.addView(vt1);
            }
        }
        private void showDialog() {
            if(progressDialog!=null) {
                progressDialog.show();
            }
        }
        private void hideDialog() {
            if(progressDialog!=null || swipeRefreshLayout!=null) {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        private void AlertDialogMethod(final PartyWise partyWise, final OrderWise orderWise){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            final View dialogView = LayoutInflater.from(context).inflate(R.layout.cardview_message_with_edittext, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(true);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            TextView txtTitle = (TextView) dialogView.findViewById(R.id.textView_message);
            final TextInputLayout edtReason = (TextInputLayout) dialogView.findViewById(R.id.editText_remarks);
            TextView txtNo = (TextView) dialogView.findViewById(R.id.textView_No);
            TextView txtYes = (TextView) dialogView.findViewById(R.id.textView_Yes);
            txtTitle.setText("Are you sure? You want to Force Close.");
            txtYes.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      edtReason.setError(null);
                      String Reason = edtReason.getEditText().getText().toString().trim();
                      boolean cancel=false;
                      View focusView=null;
                      if (TextUtils.isEmpty(Reason)){
                          edtReason.setError("Reason cannot be blank");
                          focusView = edtReason;
                          cancel = true;
                      }
                      if (cancel){
                          if (focusView!=null)
                              focusView.requestFocus();
                      }else {
                          if (Reason.length()>4){
                              String status = NetworkUtils.getConnectivityStatusString(context);
                              if (!status.contentEquals("No Internet Connection")) {
                                  LoginActivity obj = new LoginActivity();
                                  String[] str = obj.GetSharePreferenceSession(context);
                                  if (str != null)
                                      if (StaticValues.removeFlag == 1) {
                                          if (partyWise!=null){
                                              String VType = partyWise.getVType();
                                              CallVolleyForceClose(str[3], str[4], str[0], str[5],str[14],str[15],partyWise.getPartyID(),"","",DateFormatsMethods.getDateTime(),DateFormatsMethods.getDateTime(),VType,Reason,"3");
                                              alertDialog.dismiss();
                                          }else if (orderWise!=null){
                                              String VType = orderWise.getVType();
                                              CallVolleyForceClose(str[3], str[4], str[0], str[5],str[14],str[15],"",orderWise.getOrderID(),"",DateFormatsMethods.getDateTime(),DateFormatsMethods.getDateTime(),VType,Reason,"4");
                                              alertDialog.dismiss();
                                          }
                                      }else {
                                          MessageDialog.MessageDialog(context,"Permission denied","You don't have delete permission to delete this module.");
                                      }
                              } else {
                                  MessageDialog.MessageDialog(context,"",""+status);
                              }
                          }else {
                              edtReason.setError("Please enter *Reason minimum 4 Characters");
                          }
                      }
                  }
            });
            txtNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        }
        private void DialogExpectedDeliveryDatetimeUpdate(final OrderWise orderWise){
            final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_expected_delivery_date);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            final EditText editTextDelDate = (EditText) dialog.findViewById(R.id.ex_del_date);
            final EditText editTextDelTime = (EditText) dialog.findViewById(R.id.ex_del_time);
            LinearLayout layoutUrgency = (LinearLayout) dialog.findViewById(R.id.Linear_Urgency);
            LinearLayout layoutRemarks = (LinearLayout) dialog.findViewById(R.id.Linear_Remarks);
            Spinner spnUrgencyLevel = (Spinner) dialog.findViewById(R.id.spinner_Urgency_Level);
            final EditText edtRemarks = (EditText) dialog.findViewById(R.id.editText_remarks);
            Button btnUpdate = (Button) dialog.findViewById(R.id.button_Update);
            Button btnCancel = (Button) dialog.findViewById(R.id.button_Cancel);
            //TODO: Linear Layout
            layoutUrgency.setVisibility(View.VISIBLE);
            layoutRemarks.setVisibility(View.VISIBLE);
            //TODO: Edit text
            editTextDelDate.setInputType(InputType.TYPE_NULL);
            editTextDelTime.setInputType(InputType.TYPE_NULL);
            editTextDelDate.setText(""+DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
            editTextDelTime.setText(""+DateFormatsMethods.getDateTime().substring(11,16));
            editTextDelDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            DecimalFormat formatter = new DecimalFormat("00");
                            String ExDelDate = DateFormatsMethods.PastDateNotSelect(formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year);
                            editTextDelDate.setText(ExDelDate);
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setTitle("Select the date");
                    datePicker.show();
                }
            });
            editTextDelTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // TODO Auto-generated method stub
                            try{
                                DecimalFormat formatter = new DecimalFormat("00");
                                String ExDelTime = formatter.format(hourOfDay)+":"+formatter.format(minute);
                                editTextDelTime.setText(""+ExDelTime);
                            }catch (Exception e) {
                                // TODO: handle exception
                                Log.e("ERRor", ""+e.toString());
                            }
                        }
                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));
                    timePicker.setTitle("Select the Time");
                    timePicker.show();
                }
            });
            //TODO: Spinner Urgency
            spnUrgencyLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                       urgencyLevel = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            //TODO: Btn Click
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ExDate = editTextDelDate.getText().toString();
                    String ExTime = editTextDelTime.getText().toString();
                    String Remarks = edtRemarks.getText().toString().toString();
                    if (!ExDate.isEmpty() && !ExTime.isEmpty() && urgencyLevel!=0) {
                        String ExDateTime = DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDate) + " " + ExTime + ":00";
                        String status = NetworkUtils.getConnectivityStatusString(context);
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj = new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if (str != null)
                                if (StaticValues.editFlag == 1) {
                                    CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], orderWise.getOrderID(),"","", ExDateTime,""+orderWise.getVType(),Remarks,"2",""+urgencyLevel,CommanStatic.LogIN_UserName);
                                    dialog.dismiss();
                                }else{
                                    MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                                }
                        }else {
                            MessageDialog.MessageDialog(context,"",status);
                        }
                    }else {
                        if (urgencyLevel == 0){MessageDialog.MessageDialog(context,"Alert","Please select urgency level");}
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        public void CallVolleyForceClose(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String PartyID,final String OrderID,final String ItemID,final String FromDt,final String ToDt,final String VType,final String Remarks,final String Type){
            showDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderForceClosed", new Response.Listener<String>()
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
                            MessageDialog.MessageDialog(context,"",Msg);
                            DBForceClose.ForceCloseByType(Integer.valueOf(Type),"","",PartyID,OrderID);
                            LoadRecyclerView(HeaderPos);
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
                    params.put("UserID", UserID);
                    params.put("SessionID", SessionID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("BranchID", BranchID);
                    params.put("PartyID", PartyID);
                    params.put("OrderID", OrderID);
                    params.put("ItemID", ItemID);
                    params.put("FromDt", FromDt);
                    params.put("ToDt", ToDt);
                    params.put("VType", VType);
                    params.put("Remarks", Remarks);
                    params.put("Type", Type);
                    Log.i(TAG,"Force Close parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID, final String ItemID,final String ExpectedDatetime, final String VType, final String Remarks, final String Type, final String UrgencyLevel,final String UserName){
            showDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ExpDelDtUpdate", new Response.Listener<String>()
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
                            MessageDialog.MessageDialog(context,"",Msg);
                            DBForceClose.UpdateExpDelDate(ExpectedDatetime,OrderID,"","");
                            LoadRecyclerView(0);
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
                    params.put("UserID", UserID);
                    params.put("SessionID", SessionID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("BranchID", BranchID);
                    params.put("OrderID", OrderID);
                    params.put("GroupID", GroupID);
                    params.put("ItemID", ItemID);
                    params.put("ExpDelDt", ExpectedDatetime);
                    params.put("VType", VType);
                    params.put("Remarks", Remarks);
                    params.put("Type", Type);
                    params.put("UrgLab", UrgencyLevel);
                    params.put("UserName", UserName);
                    Log.d(TAG,"Update Expected Del datetime parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch(item.getItemId()){
                case R.id.action_search:
                    break;
                case android.R.id.home:
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.menu_main, menu);
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
    private void CallApiMethod(final String FromDt,final String ToDt, final String Type, final int flag){
        //TODO: Call Volley Api
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyForceCloseReport(str[3], str[4], str[0], str[5],str[14],str[15],FromDt,ToDt,Type,flag);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Call Volley Order ForceClose
    public void CallVolleyForceCloseReport(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String FromDt,final String ToDt,final String Type,final int flag){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"VoucherListForOrderClosed", new Response.Listener<String>()
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
                            map.put("VType",(jsonArrayScfo.getJSONObject(i).optString("VType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VType")));
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
                            map.put("pendingQty",(jsonArrayScfo.getJSONObject(i).optString("PendingQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PendingQty")));
                            map.put("bookedQty",(jsonArrayScfo.getJSONObject(i).optString("CSBkQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CSBkQty")));
                            map.put("DispatchQty",(jsonArrayScfo.getJSONObject(i).optString("DispatchQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("DispatchQty")));
                            map.put("colorSizeStock",(jsonArrayScfo.getJSONObject(i).optString("CSStk")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CSStk")));
                            map.put("subItemStock",(jsonArrayScfo.getJSONObject(i).optString("SubItemStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemStock")));
                            map.put("ColorFamilyName",(jsonArrayScfo.getJSONObject(i).optString("CFName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CFName")));
                            map.put("AStatus",(jsonArrayScfo.getJSONObject(i).optString("AStatus")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AStatus")));
                            map.put("RefrenceName",(jsonArrayScfo.getJSONObject(i).optString("RefrenceName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("RefrenceName")));
                            map.put("OrderType",(jsonArrayScfo.getJSONObject(i).optString("OrderType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderType")));
                            map.put("OrderQty",(jsonArrayScfo.getJSONObject(i).optString("OrderQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderQty")));
                            map.put("OrderTypeVal",(jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")));
                            map.put("ShowroomID",(jsonArrayScfo.getJSONObject(i).optString("ShowroomID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ShowroomID")));
                            map.put("Showroom",(jsonArrayScfo.getJSONObject(i).optString("Showroom")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Showroom")));
                            map.put("ExpectedDeliveryDate",(jsonArrayScfo.getJSONObject(i).optString("ExpDelDt")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ExpDelDt")));
                            map.put("urgencyLevel",(jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")));
                            map.put("Agent",(jsonArrayScfo.getJSONObject(i).optString("Agent")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Agent")));
                            map.put("MDApplicable",(jsonArrayScfo.getJSONObject(i).optString("MDApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MDApp")));
                            map.put("SubItemApplicable",(jsonArrayScfo.getJSONObject(i).optString("SIApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SIApp")));

                            map.put("BranchName",(jsonArrayScfo.getJSONObject(i).optString("BranchName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("BranchName")));
                            map.put("AppType",(jsonArrayScfo.getJSONObject(i).optString("AppType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AppType")));
                            map.put("ItemRowID",(jsonArrayScfo.getJSONObject(i).optString("ItemRowID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemRowID")));
                            map.put("ItemCat",(jsonArrayScfo.getJSONObject(i).optString("ItemCat")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCat")));

                            //TODO: Add map into list
                            mapList.add(map);
                        }
                        context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
                        DBSqlLiteForceClose DB= new DBSqlLiteForceClose(context);
                        DB.deleteReportData();
                        DB.insertForceCloseTable(mapList);
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
                params.put("FromDt", FromDt);
                params.put("ToDt", ToDt);
                params.put("Type", Type);
                Log.i(TAG,"Force Close parameters:"+params.toString());
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
        if(progressDialog!=null ) {
            progressDialog.dismiss();
        }
    }
}
