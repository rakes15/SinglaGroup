package report.godownwiseorderitem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
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
import report.DatabaseSqlite.DBSqlLiteGodownWiseOrderItem;
import report.DatabaseSqlite.DBSqlLitePendingDispatch;
import report.godownwiseorderitem.adapter.OrderWiseAdapter;
import report.godownwiseorderitem.adapter.GroupWiseAdapter;
import report.godownwiseorderitem.adapter.PartyWiseAdapter;
import report.godownwiseorderitem.adapter.ShowroomAdapter;
import report.godownwiseorderitem.adapter.SpinnerCommonAdapter;
import report.godownwiseorderitem.model.OrderWise;
import report.godownwiseorderitem.model.GroupWise;
import report.godownwiseorderitem.model.PartyWise;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 11-AUG-17.
 */

public class GodownWiseOrderItemActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> Header;
    private Context context;
    private ProgressDialog progressDialog;
    private Bundle bundle;
    private static int pagerPosition=0;
    private static String AppType="",StockAvl="",ItemCategory="",ShowroomID="";
    private static String TAG = GodownWiseOrderItemActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.viewpager_design);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = GodownWiseOrderItemActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                //CallApiMethod(1);
                LoadViewPager();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                finish();
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
        return super.onCreateOptionsMenu(menu);
    }
    //TODO: View Pager Adapter
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> Header;
        public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> Header) {
            super(fragmentManager);
            this.Header=Header;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                {
                    return GroupWiseFragment.newInstance("GroupWise");
                }
                case 1:
                {
                    return PartyWiseFragment.newInstance("JobberWise");
                }
                case 2:
                {
                    return OrderWiseFragment.newInstance("OrderWise");
                }
            }
            return null;
        }
        public CharSequence getPageTitle(int position) {
            return Header.get(position);
        }
        @Override
        public int getCount() {
            return Header.size();
        }
    }
    //TODO: Group Wise fragment
    public static class GroupWiseFragment extends Fragment{
        private static final String GROUP_WISE = "GroupWise";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private TableLayout tableLayoutFooter;
        private ProgressDialog progressDialog;
        private Dialog dialog;
        private GroupWiseAdapter adapter;
        private DBSqlLiteGodownWiseOrderItem DBSalesReport;
        private int appType=0 ,StockAvail=0 , FinishType=0;
        private String ShowroomID="",Showroom="";
        public static GroupWiseFragment newInstance(String GroupWise) {
            GroupWiseFragment fragment = new GroupWiseFragment();
            Bundle args = new Bundle();
            args.putString(GROUP_WISE, GroupWise);
            fragment.setArguments(args);
            return fragment;
        }
        public GroupWiseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_with_footer_swipe_refresh_layout, null);
            setHasOptionsMenu(true);
            this.context = getActivity();
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            tableLayoutFooter = (TableLayout) view.findViewById(R.id.table_Layout);
            DBSalesReport = new DBSqlLiteGodownWiseOrderItem(context);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            //TODO: Module Permission
            ModulePermission();
        }
        private void ModulePermission(){
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //CallApiMethod(0);
                //LoadRecyclerView();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //CallApiMethod(0);
                        //LoadRecyclerView();
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void LoadRecyclerView(){
            adapter = new GroupWiseAdapter(context,DBSalesReport.getGroupWise());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    GroupWise dataset = (GroupWise) adapter.getItem(position);
                    Intent intent = new Intent(context, ItemDetailsListByAllWiseActivity.class);
                    intent.putExtra("Key",dataset);
                    startActivity(intent);
                }
            });
            hidepDialog();
        }
        private void setTableLayoutFooter(){
            tableLayoutFooter.removeAllViews();
            tableLayoutFooter.removeAllViewsInLayout();

            Map<String,Integer> map = DBSalesReport.getGrandTotalByAllWise();
            if (map!=null){
                //TODO: 1th Row
                View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayoutFooter, false);

                TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
                txtContent1.setText("Total Items : "+map.get("TotalItems"));

                TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
                txtContent2.setText("Total Qty: "+map.get("TotalQty"));

                TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
                txtContent3.setText("Total Amt: ₹"+map.get("TotalAmt"));
                tableLayoutFooter.addView(vt1);
            }
        }
        private void CallApiMethod(final String AppType, final String StockAvl, final String ItemCategory, final String ShowroomID, final int flag){
            //TODO: Call Volley Api
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    new GodownWiseOrderItemActivity().CallVolleySalesReportWithAppType(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
                }
            } else {
                MessageDialog.MessageDialog(context,"",status);
            }
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
        private void DialogSearchFilter(final Context context){
            dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_godown_order_item_search_filter);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            //TODO: Declarations
            final EditText edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
            final Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Godown);
            final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
            //TODO: Get Current Date
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String CurrentDate = df.format(c.getTime());
            //TODO: Get Yesterday Date
            edtToDate.setInputType(InputType.TYPE_NULL);
            edtToDate.setText(CurrentDate);
            edtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
            //TODO: Showroom Spinner
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    if (position == 0) {
                        ShowroomID = "777";
                        Showroom = "ALL Showrooms";
                    }else{
                        GodownDataset dataset = (GodownDataset) parent.getAdapter().getItem(position);
                        ShowroomID = dataset.getGodownID();
                        Showroom = dataset.getGodownName();
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
            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Filter Apply
                    //String FromDate = edtFromDate.getText().toString();
                    String ToDate = edtToDate.getText().toString();
//                    CallApiMethod(String.valueOf(appType), String.valueOf(StockAvail), String.valueOf(FinishType), ShowroomID, 1);
                    dialog.dismiss();
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
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
                case R.id.action_filter_search:
                    DialogSearchFilter(context);
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
            super.onCreateOptionsMenu(menu,inflater);
        }
        @Override
        public void onResume(){
            super.onResume();
            //CallApiMethod(0);
        }
    }
    //TODO: Party Wise fragment
    public static class PartyWiseFragment extends Fragment {
        private static final String PARTY_WISE = "JobberWise";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private TableLayout tableLayoutFooter;
        private ProgressDialog progressDialog;
        private Dialog dialog;
        private PartyWiseAdapter adapter;
        private DBSqlLiteGodownWiseOrderItem DBSalesReport;
        private int appType=0 ,StockAvail=0 , FinishType=0;
        private String ShowroomID="",Showroom="";
        public static GodownWiseOrderItemActivity.PartyWiseFragment newInstance(String PartyWise) {
            PartyWiseFragment fragment = new PartyWiseFragment();
            Bundle args = new Bundle();
            args.putString(PARTY_WISE, PartyWise);
            fragment.setArguments(args);
            return fragment;
        }
        public PartyWiseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_with_footer_swipe_refresh_layout, null);
            setHasOptionsMenu(true);
            //StaticValues.PushOrderFlag = 0;
            this.context = getActivity();
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            tableLayoutFooter = (TableLayout) view.findViewById(R.id.table_Layout);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //LoadRecyclerView();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //LoadRecyclerView();
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void ModulePermission(){
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //CallApiMethod(0);
                //LoadRecyclerView();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //CallApiMethod(0);
                        //LoadRecyclerView();
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void LoadRecyclerView(){
            adapter = new PartyWiseAdapter(context,DBSalesReport.getPartyWise());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    PartyWise dataset = (PartyWise) adapter.getItem(position);
                    Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                    OrderWise orderWise =null;
                    intent.putExtra("PartyKey",dataset);
                    intent.putExtra("OrderKey", orderWise);
                    startActivity(intent);
                }
            });
            hidepDialog();
        }
        private void setTableLayoutFooter(){
            tableLayoutFooter.removeAllViews();
            tableLayoutFooter.removeAllViewsInLayout();

            Map<String,Integer> map = DBSalesReport.getGrandTotalByAllWise();
            if (map!=null){
                //TODO: 1th Row
                View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayoutFooter, false);

                TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
                txtContent1.setText("Total Items : "+map.get("TotalItems"));

                TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
                txtContent2.setText("Total Qty: "+map.get("TotalQty"));

                TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
                txtContent3.setText("Total Amt: ₹"+map.get("TotalAmt"));
                tableLayoutFooter.addView(vt1);
            }
        }
        private void CallApiMethod(final String AppType, final String StockAvl, final String ItemCategory, final String ShowroomID, final int flag){
            //TODO: Call Volley Api
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    new GodownWiseOrderItemActivity().CallVolleySalesReportWithAppType(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
                }
            } else {
                MessageDialog.MessageDialog(context,"",status);
            }
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
        private void DialogSearchFilter(final Context context){
            dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_sales_report_search_filter);
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
            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.Radio_Group_Sales_Report);
            final LinearLayout linearLayoutReportType = (LinearLayout) dialog.findViewById(R.id.Linear_ReportType);
            final LinearLayout linearLayoutAppType = (LinearLayout) dialog.findViewById(R.id.Linear_AppType);
            final Spinner spnReportType = (Spinner) dialog.findViewById(R.id.spinner_Report_Type);
            final Spinner spnFinishType = (Spinner) dialog.findViewById(R.id.spinner_FinishType);
            final Spinner spnAppType = (Spinner) dialog.findViewById(R.id.spinner_AppType);
            final Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
            final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
            //TODO: Radio Group
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    // TODO Auto-generated method stub
                    if(checkedId == R.id.Radio_Button_SaleReport){
                        linearLayoutReportType.setVisibility(View.VISIBLE);
                        linearLayoutAppType.setVisibility(View.GONE);
                        spnReportType.setAdapter(ArrayAdapter.createFromResource(context, R.array.ReportType, android.R.layout.simple_spinner_dropdown_item));
                        spnFinishType.setAdapter(ArrayAdapter.createFromResource(context, R.array.FinishType, android.R.layout.simple_spinner_dropdown_item));
                        btnApply.setVisibility(View.VISIBLE);
                    }else if(checkedId == R.id.Radio_Button_SaleReport_With_AppType){
                        linearLayoutReportType.setVisibility(View.GONE);
                        linearLayoutAppType.setVisibility(View.VISIBLE);
                        spnAppType.setAdapter(ArrayAdapter.createFromResource(context, R.array.AppType, android.R.layout.simple_spinner_dropdown_item));
                        LoginActivity obj=new LoginActivity();
                        final String[] str=obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
                            spnShowroom.setAdapter(new ShowroomAdapter(context, DBHandler.getReserveGodownList(str[14], str[5], str[15])));
                        }
                        btnApply.setVisibility(View.VISIBLE);
                    }
                }
            });
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
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
            spnReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    StockAvail = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Finish Type Spinner
            spnFinishType.setSelection(2);
            spnFinishType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    FinishType = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Showroom Spinner
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    if (position == 0) {
                        ShowroomID = "777";
                        Showroom = "ALL Showrooms";
                    }else{
                        GodownDataset dataset = (GodownDataset) parent.getAdapter().getItem(position);
                        ShowroomID = dataset.getGodownID();
                        Showroom = dataset.getGodownName();
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
            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Filter Apply
                    String FromDate = edtFromDate.getText().toString();
                    String ToDate = edtToDate.getText().toString();
//                    CallApiMethod(String.valueOf(appType), String.valueOf(StockAvail), String.valueOf(FinishType), ShowroomID, 1);
                    dialog.dismiss();
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
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
                case R.id.action_filter_search:
                    DialogSearchFilter(context);
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
//                    if(adapter!=null) {
//                        adapter.filter(newText);
//                    }
//                System.out.println("on text chnge text: "+newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    // this is your adapter that will be filtered
//                    if(adapter!=null)
//                    {
//                        adapter.filter(query);
//                    }
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
//            LoadRecyclerView();
        }
    }
    //TODO: Order Wise fragment
    public static class OrderWiseFragment extends Fragment {
        private static final String DOCUMENT_WISE = "OrderWise";
        private Context context;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private TableLayout tableLayoutFooter;
        private ProgressDialog progressDialog;
        private Dialog dialog;
        private int custFlag=0,pendingFlag=0,exDelFlag=0;
        private int agentPos=0,showroomPos=0,OrderTypePos=0;
        private String ShowroomId="",Showroom="",Agent="",OrderType="",OrderTypeName="",FromDT="",ToDT="";
        private OrderWiseAdapter adapter;
        private DBSqlLiteGodownWiseOrderItem DBSalesReport;
        private int appType=0 ,StockAvail=0 , FinishType=0;
        private String ShowroomID="",ShowroomName="";
        private DBSqlLiteGodownWiseOrderItem DBHandler;
        public static OrderWiseFragment newInstance(String OrderWise) {
            OrderWiseFragment fragment = new OrderWiseFragment();
            Bundle args = new Bundle();
            args.putString(DOCUMENT_WISE, OrderWise);
            fragment.setArguments(args);
            return fragment;
        }
        public OrderWiseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.activity_recyclerview_with_footer_swipe_refresh_layout, null);
            setHasOptionsMenu(true);
            //StaticValues.PushOrderFlag = 0;
            this.context = getActivity();
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_Refresh_Layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            tableLayoutFooter = (TableLayout) view.findViewById(R.id.table_Layout);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            this.DBHandler = new DBSqlLiteGodownWiseOrderItem(context);
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //LoadRecyclerView();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //LoadRecyclerView();
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
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
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
                case R.id.action_filter_search:
                    DialogSearchFilter(context);
                    break;
                case R.id.action_logout://TODO: Filter Or Sort
                    DialogFilterSort();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.menu_main, menu);
            //TODO: Filter Search
            MenuItem filterSearchItem = menu.findItem(R.id.action_filter_search);
            filterSearchItem.setVisible(true);
            //TODO: Filter Or Sort
            MenuItem filterSortItem = menu.findItem(R.id.action_logout);//TODO: Filter Or Sort
            filterSortItem.setVisible(true);
            filterSortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_filter_sort));
            //TODO: Search
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
//                    if(adapter!=null) {
//                        adapter.filter(newText);
//                    }
//                System.out.println("on text chnge text: "+newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    // this is your adapter that will be filtered
//                    if(adapter!=null)
//                    {
//                        adapter.filter(query);
//                    }
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
            //LoadRecyclerView();
        }
        private void ModulePermission(){
            //TODO: Call Api
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                //CallApiMethod(0);
                //LoadRecyclerView(DBSalesReport.getOrderWise());
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
            }
            //TODO
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        //CallApiMethod(0);
                        //LoadRecyclerView(DBSalesReport.getOrderWise());
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }
            });
        }
        private void LoadRecyclerView(List<OrderWise> orderWiseList){
            adapter = new OrderWiseAdapter(context, orderWiseList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    OrderWise orderWise = (OrderWise) adapter.getItem(position);
                    Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                    PartyWise partyWise=null;
                    intent.putExtra("PartyKey",partyWise);
                    intent.putExtra("OrderKey", orderWise);
                    startActivity(intent);
                }
            });
            hidepDialog();
        }
        private void setTableLayoutFooter(){
            tableLayoutFooter.removeAllViews();
            tableLayoutFooter.removeAllViewsInLayout();

            Map<String,Integer> map = DBSalesReport.getGrandTotalByAllWise();
            if (map!=null){
                //TODO: 1th Row
                View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayoutFooter, false);

                TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
                txtContent1.setText("Total Items : "+map.get("TotalItems"));

                TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
                txtContent2.setText("Total Qty: "+map.get("TotalQty"));

                TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
                txtContent3.setText("Total Amt: ₹"+map.get("TotalAmt"));
                tableLayoutFooter.addView(vt1);
            }
        }
        private void CallApiMethod(final String AppType, final String StockAvl, final String ItemCategory, final String ShowroomID, final int flag){
            //TODO: Call Volley Api
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    new GodownWiseOrderItemActivity().CallVolleySalesReportWithAppType(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
                }
            } else {
                MessageDialog.MessageDialog(context,"",status);
            }
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
        private void DialogSearchFilter(final Context context){
            dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_sales_report_search_filter);
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
            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.Radio_Group_Sales_Report);
            final LinearLayout linearLayoutReportType = (LinearLayout) dialog.findViewById(R.id.Linear_ReportType);
            final LinearLayout linearLayoutAppType = (LinearLayout) dialog.findViewById(R.id.Linear_AppType);
            final Spinner spnReportType = (Spinner) dialog.findViewById(R.id.spinner_Report_Type);
            final Spinner spnFinishType = (Spinner) dialog.findViewById(R.id.spinner_FinishType);
            final Spinner spnAppType = (Spinner) dialog.findViewById(R.id.spinner_AppType);
            final Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
            final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
            //TODO: Radio Group
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    // TODO Auto-generated method stub
                    if(checkedId == R.id.Radio_Button_SaleReport){
                        linearLayoutReportType.setVisibility(View.VISIBLE);
                        linearLayoutAppType.setVisibility(View.GONE);
                        spnReportType.setAdapter(ArrayAdapter.createFromResource(context, R.array.ReportType, android.R.layout.simple_spinner_dropdown_item));
                        spnFinishType.setAdapter(ArrayAdapter.createFromResource(context, R.array.FinishType, android.R.layout.simple_spinner_dropdown_item));
                        btnApply.setVisibility(View.VISIBLE);
                    }else if(checkedId == R.id.Radio_Button_SaleReport_With_AppType){
                        linearLayoutReportType.setVisibility(View.GONE);
                        linearLayoutAppType.setVisibility(View.VISIBLE);
                        spnAppType.setAdapter(ArrayAdapter.createFromResource(context, R.array.AppType, android.R.layout.simple_spinner_dropdown_item));
                        LoginActivity obj=new LoginActivity();
                        final String[] str=obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
                            spnShowroom.setAdapter(new ShowroomAdapter(context, DBHandler.getReserveGodownList(str[14], str[5], str[15])));
                        }
                        btnApply.setVisibility(View.VISIBLE);
                    }
                }
            });
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
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
            spnReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    StockAvail = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Finish Type Spinner
            spnFinishType.setSelection(2);
            spnFinishType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    FinishType = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Showroom Spinner
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    if (position == 0) {
                        ShowroomID = "777";
                        Showroom = "ALL Showrooms";
                    }else{
                        GodownDataset dataset = (GodownDataset) parent.getAdapter().getItem(position);
                        ShowroomID = dataset.getGodownID();
                        Showroom = dataset.getGodownName();
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
            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Filter Apply
                    String FromDate = edtFromDate.getText().toString();
                    String ToDate = edtToDate.getText().toString();
//                    CallApiMethod(String.valueOf(appType), String.valueOf(StockAvail), String.valueOf(FinishType), ShowroomID, 1);
                    dialog.dismiss();
                }
            });
        }
        private void DialogFilterSort(){
            dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_filter_sort);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            TextView txtViewClear = (TextView) dialog.findViewById(R.id.TextView_Clear);
            Button btnApply = (Button) dialog.findViewById(R.id.Button_Apply);
            RadioGroup radioGroupCustomerWise = (RadioGroup) dialog.findViewById(R.id.Radio_CustomerWise);
            RadioGroup radioGroupPendingDaysWise = (RadioGroup) dialog.findViewById(R.id.Radio_PendingDaysWise);
            RadioGroup radioGroupExDeliveryDateWise = (RadioGroup) dialog.findViewById(R.id.Radio_ExpectedDelDateWise);
            final Spinner spnAgent = (Spinner) dialog.findViewById(R.id.spinner_Agent);
            final Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
            final Spinner spnAppType = (Spinner) dialog.findViewById(R.id.spinner_AppType);
            final EditText edtFromDt = (EditText) dialog.findViewById(R.id.EditText_FromDate);
            final EditText edtToDt = (EditText) dialog.findViewById(R.id.EditText_ToDate);
            edtFromDt.setInputType(InputType.TYPE_NULL);
            edtToDt.setInputType(InputType.TYPE_NULL);
            edtFromDt.setText(FromDT);
            edtToDt.setText(ToDT);
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
            List<Map<String,String>> mapListAgent = DBHandler.getAgentList();
            List<Map<String,String>> mapListShowroom = DBHandler.getShowroomList();
            List<Map<String,String>> mapListAppType = DBHandler.getOrderTypeList();
            spnAgent.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAgent));
            spnAgent.setSelection(agentPos);
            spnAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    agentPos = position;
                    if(position>0){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map=(Map<String, Object>) parent.getAdapter().getItem(position);
                        Agent = map.get("ID").toString();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
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
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            spnAppType.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAppType));
            spnAppType.setSelection(OrderTypePos);
            spnAppType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                public void onNothingSelected(AdapterView<?> parent) {}
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
                    // TODO Auto-generated method stub
                    String ExDelFromDt = edtFromDt.getText().toString();
                    String ExDelToDt = edtToDt.getText().toString();
                    FromDT = ExDelFromDt;
                    ToDT = ExDelToDt;
                    LoadRecyclerView(DBHandler.getOrderListFiltered(String.valueOf(custFlag), String.valueOf(pendingFlag), String.valueOf(exDelFlag), Agent, Showroom, OrderTypeName, ExDelFromDt, ExDelToDt));
                    dialog.dismiss();
                }
            });
            edtFromDt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                    List<Map<String,String>> mapListAgent = DBHandler.getAgentList();
                    List<Map<String,String>> mapListShowroom = DBHandler.getShowroomList();
                    List<Map<String,String>> mapListAppType = DBHandler.getOrderTypeList();
                    spnAgent.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAgent));
                    spnShowroom.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListShowroom));
                    spnAppType.setAdapter(new SpinnerCommonAdapter(getActivity(), mapListAppType));
                }
            });
        }
    }
    //TODO: Call Api Method
    private void CallApiMethod(final int flag){
        //TODO: Book Groups Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleySalesReportWithAppType(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
                    }
                } else {
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(context,"","",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleySalesReportWithAppType(str[3], str[4], str[0], str[5],str[14],str[15],AppType,StockAvl,ItemCategory,ShowroomID,flag);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Call Volley SalesReport With AppType
    public void CallVolleySalesReportWithAppType(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String AppType,final String StockAvl,final String ItemCategory,final String ShowroomID,final int flag){
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
                            map.put("MainGroupName",(jsonArrayScfo.getJSONObject(i).optString("MainGroupName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MainGroupName")));
                            map.put("GroupID",(jsonArrayScfo.getJSONObject(i).optString("GroupID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupID")));
                            map.put("GroupName",(jsonArrayScfo.getJSONObject(i).optString("GroupName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupName")));
                            map.put("SubGroupName",(jsonArrayScfo.getJSONObject(i).optString("SubGroupName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubGroupName")));
                            map.put("CustID",(jsonArrayScfo.getJSONObject(i).optString("CustID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CustID")));
                            map.put("CustName",(jsonArrayScfo.getJSONObject(i).optString("CustName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CustName")));
                            map.put("SubCustID",(jsonArrayScfo.getJSONObject(i).optString("SubCustID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubCustID")));
                            map.put("SubCustName",(jsonArrayScfo.getJSONObject(i).optString("SubCustName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubCustName")));
                            map.put("OrderID",(jsonArrayScfo.getJSONObject(i).optString("OrderID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderID")));
                            map.put("OrderNo",(jsonArrayScfo.getJSONObject(i).optString("OrderNo")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderNo")));
                            map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
                            map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
                            map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
                            map.put("SubItemName",(jsonArrayScfo.getJSONObject(i).optString("SubItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemName")));
                            map.put("ColorName",(jsonArrayScfo.getJSONObject(i).optString("ColorName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorName")));
                            map.put("SizeName",(jsonArrayScfo.getJSONObject(i).optString("SizeName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SizeName")));
                            map.put("price",(jsonArrayScfo.getJSONObject(i).optString("price")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("price")));
                            map.put("stock",(jsonArrayScfo.getJSONObject(i).optString("stock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("stock")));
                            map.put("pendingQty",(jsonArrayScfo.getJSONObject(i).optString("pendingQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("pendingQty")));
                            map.put("bookedQty",(jsonArrayScfo.getJSONObject(i).optString("bookedQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("bookedQty")));
                            map.put("DispatchQty",(jsonArrayScfo.getJSONObject(i).optString("DispatchQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("DispatchQty")));
                            map.put("colorSizeStock",(jsonArrayScfo.getJSONObject(i).optString("colorSizeStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("colorSizeStock")));
                            map.put("subItemStock",(jsonArrayScfo.getJSONObject(i).optString("subItemStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("subItemStock")));
                            map.put("Date",(jsonArrayScfo.getJSONObject(i).optString("Date")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Date")));
                            map.put("ColorFamilyName",(jsonArrayScfo.getJSONObject(i).optString("ColorFamilyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorFamilyName")));
                            map.put("AStatus",(jsonArrayScfo.getJSONObject(i).optString("AStatus")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AStatus")));
                            map.put("RefrenceName",(jsonArrayScfo.getJSONObject(i).optString("RefrenceName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("RefrenceName")));
                            map.put("OrderType",(jsonArrayScfo.getJSONObject(i).optString("OrderType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderType")));
                            map.put("OrderQty",(jsonArrayScfo.getJSONObject(i).optString("OrderQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderQty")));
                            map.put("OrderTypeVal",(jsonArrayScfo.getJSONObject(i).optString("OrderTypeVal")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderTypeVal")));
                            map.put("ShowroomID",(jsonArrayScfo.getJSONObject(i).optString("ShowroomID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ShowroomID")));
                            map.put("Showroom",(jsonArrayScfo.getJSONObject(i).optString("Showroom")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Showroom")));
                            map.put("ExpectedDeliveryDate",(jsonArrayScfo.getJSONObject(i).optString("ExpectedDeliveryDate")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ExpectedDeliveryDate")));
                            map.put("urgencyLevel",(jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")));
                            map.put("Agent",(jsonArrayScfo.getJSONObject(i).optString("Agent")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Agent")));
                            map.put("MDApplicable",(jsonArrayScfo.getJSONObject(i).optString("MDApplicable")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MDApplicable")));
                            map.put("SubItemApplicable",(jsonArrayScfo.getJSONObject(i).optString("SubItemApplicable")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemApplicable")));
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
                Log.d(TAG,"Pending Dispatch parameters:"+params.toString());
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
