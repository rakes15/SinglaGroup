package orderbooking.customerlist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.singlagroup.RegistrationActivity;
import com.singlagroup.adapters.CityFilterableAdapter;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.adapters.StateFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MultiSelectionSpinner;
import com.singlagroup.datasets.CityDataset;
import com.singlagroup.datasets.StateDataset;
import com.singlagroup.responsedatasets.ResponseCityListDataset;
import com.singlagroup.responsedatasets.ResponseStateListDataset;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import orderbooking.customerlist.adapter.MstBussinessCPAdapter;
import orderbooking.customerlist.datasets.MstBussinessPartnerCpDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.view_order_details.adapter.OrderViewGroupAdapter;
import report.showroomitemcheck.ItemDetailsListActivity;
import report.showroomitemcheck.adapter.GroupAdapter;
import report.showroomitemcheck.model.Group;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by rakes on 12-Dec-17.
 */

public class PartyCreateOrUpdateActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Context context;
    private RecyclerView recyclerView;
    private Button btnAdd;
    private Dialog dialog;
    private TextView txtViewParty,txtViewSubParty,txtViewState,txtViewCity;
    private LinearLayout linearLayoutSubParty;
    Spinner spnDesignation;
    CommonSearchableSpinner spnPartyList,spnSubPartyList,spnState,spnCity;
    ProgressBar progressBarParty,progressBarSubParty,progressBarState,progressBarCity,progressBarDesignation;
    private ProgressDialog progressDialog;
    private String PartyID="",PartyName="",PartyType="",SubPartyID="",SubParty="",SubPartyApplicable="";
    private String DesignationID="",Designation="",CountryID="799EDAD2-1D14-4ACF-AA38-CC8A6D399939",Country="India",StateID="",StateName="",CityID="",CityName="";
    private int whatsAppFlag = 0;
    private ArrayList<Map<String,String>> cpDesignList;
    private MstBussinessCPAdapter adapter;
    private CommonSearchSpinnerFilterableAdapter stateAdapter,cityAdapter;
    private static String TAG = PartyCreateOrUpdateActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_button);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.context = PartyCreateOrUpdateActivity.this;
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.btnAdd = (Button) findViewById(R.id.button_add);
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PartyID.isEmpty())
                    DialogAddMstBussinessCP(null);
                else
                    MessageDialog.MessageDialog(context,"","Please select a Party or SubParty");
            }
        });
        AddContactPersonButton();
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
                //TODO: Filter Search
                DialogSearchFilter(context);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }

        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void AddContactPersonButton(){
        if (PartyID.isEmpty() || PartyID==null){
            btnAdd.setVisibility(View.GONE);
        }else {
            btnAdd.setVisibility(View.VISIBLE);
        }
    }
    //TODO: Dialog Filter Search
    private void DialogSearchFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_party_subparty_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        //TODO: Party
        txtViewParty = (TextView) dialog.findViewById(R.id.text_view_party);
        spnPartyList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_Party_List);
        progressBarParty = (ProgressBar) dialog.findViewById(R.id.ProgressBar_party);
        //TODO: Sub Party
        linearLayoutSubParty = (LinearLayout) dialog.findViewById(R.id.Linear_SubParty);
        txtViewSubParty = (TextView) dialog.findViewById(R.id.text_view_subparty);
        spnSubPartyList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_SubParty_List);
        progressBarSubParty = (ProgressBar) dialog.findViewById(R.id.ProgressBar_subparty);
        //TODO: Apply
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: PartyName Spinner AND SubParty Spinner
        if (PartyID.isEmpty()){
            //TODO: Party
            txtViewParty.setText("*Select Party");
            txtViewParty.setVisibility(View.VISIBLE);
            //TODO: Sub-Party
            if (SubPartyID.isEmpty())
                txtViewSubParty.setText("*Select Sub-Party");
                txtViewSubParty.setVisibility(View.VISIBLE);
        }else {
            //TODO: Party
            txtViewParty.setText(PartyName+" - "+SubPartyApplicable);
            txtViewParty.setVisibility(View.VISIBLE);
            //TODO: Sub-Party
            if (SubPartyApplicable.equals("0") && SubPartyApplicable!=null && !SubPartyApplicable.isEmpty()){
                linearLayoutSubParty.setVisibility(View.GONE);
            }else if (SubPartyApplicable.equals("1") && SubPartyApplicable!=null && !SubPartyApplicable.isEmpty()){
                if (!SubPartyID.isEmpty()) {
                    txtViewSubParty.setText("" + SubParty);
                    txtViewSubParty.setVisibility(View.VISIBLE);
                    linearLayoutSubParty.setVisibility(View.VISIBLE);
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null) {
                            CallVolleySelectSubCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5], str[14], PartyID);
                        }
                    } else {
                        MessageDialog.MessageDialog(context, "", status);
                    }
                }
            }
        }
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            //TODO: Party List Spinner
            String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
            if (!status.contentEquals("No Internet Connection")) {
                CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5], str[14]);
            } else {
                MessageDialog.MessageDialog(context, "", "No Internet Connection");
            }
        }
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                if (!PartyID.isEmpty()){
                    CallApiMethod(PartyID,SubPartyID);
                    dialog.dismiss();
                }else{
                    MessageDialog.MessageDialog(context,"Alert","Please select Party");
                }
            }
        });
    }
    private void CallApiMethod(String PartyID,String SubPartyID){
        AddContactPersonButton();
        if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
            String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    if (SubPartyApplicable.equals("1") && SubPartyID.isEmpty()){
                        MessageDialog.MessageDialog(context,"Alert","Please select SubParty");
                    }else {
                        CallVolleyMstBusinessPartnerCPList(str[3], str[4], str[0], str[5], str[14], PartyID, SubPartyID);
                    }
                }
            } else {
                MessageDialog.MessageDialog(context,"",status);
            }
        }else {
            MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
        }
    }
    private void CallVolleySelectCustomerForOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
        progressBarParty.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    final int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                        final ArrayList<Map<String,String>> list = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("ID",jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                            map.put("Name",jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                            map.put("Type",jsonArrayScfo.getJSONObject(i).getString("SubPartyApplicable"));
                            map.put("PartyType",jsonArrayScfo.getJSONObject(i).getString("PartyType"));
                            list.add(map);
                        }
                        spnPartyList.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, list,list));
                        spnPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                txtViewParty.setVisibility(View.GONE);
                                PartyID = list.get(position).get("ID");
                                PartyName = list.get(position).get("Name");
                                PartyType = list.get(position).get("PartyType");
                                SubPartyID = "";
                                SubParty = "";
                                SubPartyApplicable = list.get(position).get("Type");
                                if (!SubPartyApplicable.isEmpty() && SubPartyApplicable!=null){
                                    if (SubPartyApplicable.equals("1")){
                                        linearLayoutSubParty.setVisibility(View.VISIBLE);
                                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                                        if (!status.contentEquals("No Internet Connection")) {
                                            LoginActivity obj=new LoginActivity();
                                            String[] str = obj.GetSharePreferenceSession(context);
                                            if (str!=null) {
                                                CallVolleySelectSubCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],PartyID);
                                            }
                                        } else {
                                            MessageDialog.MessageDialog(context,"",status);
                                        }
                                    }else{
                                        linearLayoutSubParty.setVisibility(View.GONE);
                                        SubPartyID = "";
                                        SubParty = "";
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBarParty.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBarParty.setVisibility(View.GONE);
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                Log.d(TAG,"Select customer for order parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleySelectSubCustomerForOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID,final String PartyID){
        progressBarSubParty.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SubPartyList", new Response.Listener<String>()
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
                        final ArrayList<Map<String,String>> list = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("ID",jsonArrayScfo.getJSONObject(i).getString("SubPartyID"));
                            map.put("Name",jsonArrayScfo.getJSONObject(i).getString("SubPartyName"));
                            map.put("Type",jsonArrayScfo.getJSONObject(i).getString("SubPartyCode"));
                            list.add(map);
                        }
                        spnSubPartyList.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, list,list));
                        spnSubPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                txtViewSubParty.setVisibility(View.GONE);
                                SubPartyID = list.get(position).get("ID");
                                SubParty = list.get(position).get("Name");
                                //SubPartyApplicable = list.get(position).get("Type");
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBarSubParty.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBarSubParty.setVisibility(View.GONE);
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("PartyID", PartyID);
                Log.d(TAG,"Select sub customer for order parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: Activity Intent to Parent Caption
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.action_search:
                break;
            case R.id.action_filter_search:
                //TODO: Filter Search
                DialogSearchFilter(context);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TODO: Activity Finish
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //CallApiMethod();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem actionSearch = menu.findItem(R.id.action_search);
        actionSearch.setVisible(true);
        MenuItem actionFilterSearch = menu.findItem(R.id.action_filter_search);
        actionFilterSearch.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(actionSearch);
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
    private void DialogAddMstBussinessCP(final MstBussinessPartnerCpDataset dataset){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.FullHeightDialog));
        dialog.setContentView(R.layout.activity_mstbussiness_cp_add_update);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO:-----------------------Declarations---------------------------------
        //TODO: Title
        TextView txtTitle = (TextView) dialog.findViewById(R.id.text_Title);
        //TODO: Designation
        spnDesignation = (Spinner) dialog.findViewById(R.id.spinner_designation);
        progressBarDesignation = (ProgressBar) dialog.findViewById(R.id.ProgressBar_designation);
        //TODO: State
        txtViewState = (TextView) dialog.findViewById(R.id.text_view_state);
        spnState = (CommonSearchableSpinner) dialog.findViewById(R.id.Spinner_State);
        progressBarState = (ProgressBar) dialog.findViewById(R.id.ProgressBar_state);
        //TODO: City
        txtViewCity = (TextView) dialog.findViewById(R.id.text_view_city);
        spnCity = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_City);
        progressBarCity = (ProgressBar) dialog.findViewById(R.id.ProgressBar_city);
        //TODO:Edit Text
        final TextInputLayout edtName = (TextInputLayout) dialog.findViewById(R.id.editText_name);
        final TextInputLayout edtMobile = (TextInputLayout) dialog.findViewById(R.id.editText_mobile_no);
        final TextInputLayout edtPhone = (TextInputLayout) dialog.findViewById(R.id.editText_phone_no);
        final TextInputLayout edtEmail = (TextInputLayout) dialog.findViewById(R.id.editText_email);
        final TextInputLayout edtAddress1 = (TextInputLayout) dialog.findViewById(R.id.editText_address_1);
        final TextInputLayout edtAddress2 = (TextInputLayout) dialog.findViewById(R.id.editText_address_2);
        final TextInputLayout edtAddress3 = (TextInputLayout) dialog.findViewById(R.id.editText_address_3);
        final TextInputLayout edtCountry = (TextInputLayout) dialog.findViewById(R.id.editText_country);
        final TextInputLayout edtFax = (TextInputLayout) dialog.findViewById(R.id.editText_fax);
        final TextInputLayout edtPincode = (TextInputLayout) dialog.findViewById(R.id.editText_pincode);
        final TextInputLayout edtRemarks = (TextInputLayout) dialog.findViewById(R.id.editText_remarks);
        //TODO: Whats App Enable
        final RelativeLayout rLWhatsApp = (RelativeLayout) dialog.findViewById(R.id.relative_layout_whats_app);
        RadioGroup radioGroupWhatsApp = (RadioGroup) dialog.findViewById(R.id.radio_group_whats_app);
        final RadioButton radioButtonYes = (RadioButton) radioGroupWhatsApp.findViewById(R.id.radio_button_yes);
        final RadioButton radioButtonNo = (RadioButton) radioGroupWhatsApp.findViewById(R.id.radio_button_no);
        //TODO: Submit Button
        Button btnAddOrUpdate = (Button) dialog.findViewById(R.id.btn_submit);
        //TODO: Call Api's
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            //TODO: Party List Spinner
            String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
            if (!status.contentEquals("No Internet Connection")) {
                String PreviousDesignID = (dataset == null ? "" : (dataset.getDesignation() == null ? "" : dataset.getDesignation()));
                CallVolleyCPDesignationList(str[3], str[4], str[0], str[5], str[14],PreviousDesignID);
                String PreviousStateID = (dataset == null ? "" : (dataset.getStateID() == null ? "" : dataset.getStateID()));
                String PreviousCityID = (dataset == null ? "" : (dataset.getCityID() == null ? "" : dataset.getCityID()));
                CallRetrofitStateList(str[14],CountryID,PreviousStateID,PreviousCityID);
            } else {
                MessageDialog.MessageDialog(context, "", "No Internet Connection");
            }
        }
        //TODO: SetText
        if (dataset!=null){
            txtTitle.setText("Update Contact Person");
            btnAddOrUpdate.setText("Update Contact Person");
            edtName.getEditText().setText(""+dataset.getName());
            edtMobile.getEditText().setText(""+dataset.getCellNo());
            edtPhone.getEditText().setText(""+dataset.getPhoneNo());
            edtEmail.getEditText().setText(""+dataset.getEmail());
            edtAddress1.getEditText().setText(""+dataset.getAddress1());
            edtAddress2.getEditText().setText(""+dataset.getAddress2());
            edtAddress3.getEditText().setText(""+dataset.getAddress3());
            edtCountry.getEditText().setText(""+dataset.getCountry());
            edtFax.getEditText().setText(""+dataset.getFax());
            edtPincode.getEditText().setText(""+dataset.getPIN());
            edtRemarks.getEditText().setText(""+ dataset.getSG_Remarks());
            //TODO: Designation
//            if (cpDesignList!=null && !cpDesignList.isEmpty()){
//                int c=0;
//                for (int i=0; i<cpDesignList.size(); i++){
//                    if (dataset.getDesignation().equals(cpDesignList.get(i).get("Name"))){
//                        c = i;
//                        break;
//                    }
//                }
//                spnDesignation.setSelection(c);
//            }
        }
        //TODO: CellNo
        edtMobile.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()== 10){
                    rLWhatsApp.setVisibility(View.VISIBLE);
                    AlertDialogForWhatsApp(editable.toString(),radioButtonYes,radioButtonNo);
                }
//                else{
//                    rLWhatsApp.setVisibility(View.GONE);
//                }
            }
        });
        //TODO: Whats App
        whatsAppFlag = 0;
        if(dataset!=null){
            whatsAppFlag = dataset.getSGWhatsappFLG();
            if (dataset.getSGWhatsappFLG()==0){
                radioButtonNo.setChecked(true);
            }else if (dataset.getSGWhatsappFLG()==1){
                radioButtonYes.setChecked(true);
            }
        }
        radioGroupWhatsApp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_yes){
                    whatsAppFlag = 1;
                }else if (checkedId == R.id.radio_button_no){
                    whatsAppFlag = 0;
                }
            }
        });
        //TODO: Button
        btnAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtName.setError(null);
                edtMobile.setError(null);
                edtPhone.setError(null);
                edtEmail.setError(null);
                edtAddress1.setError(null);
                edtAddress2.setError(null);
                edtAddress3.setError(null);
                edtCountry.setError(null);
                edtFax.setError(null);
                edtPincode.setError(null);
                //TODO: Get Details from Edittext
                String name = edtName.getEditText().getText().toString();
                String designation = Designation;
                String designationId = DesignationID;
                String mobile = edtMobile.getEditText().getText().toString();
                String phone = edtPhone.getEditText().getText().toString();
                String email = edtEmail.getEditText().getText().toString();
                String address1 = edtAddress1.getEditText().getText().toString();
                String address2 = edtAddress2.getEditText().getText().toString();
                String address3 = edtAddress3.getEditText().getText().toString();
                String country = CountryID;//edtCountry.getEditText().getText().toString();
                String state = StateID;
                String city = CityID;
                String fax = edtFax.getEditText().getText().toString();
                String pincode = edtPincode.getEditText().getText().toString();
                String remarks = edtRemarks.getEditText().getText().toString();

                boolean cancel = false;
                View focusView = null,focusView2 = null;

                //TODO Check for a Name Validation
                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Name is mandatory!!!");
                    focusView = edtName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(designationId)) {
                    MessageDialog.MessageDialog(context,"","Please select Designation");
                    focusView2 = spnDesignation;
                    cancel = true;
                }
                if (TextUtils.isEmpty(mobile)) {
                    if (!PartyType.equals("2007")) {
                        edtMobile.setError("Cell No is mandatory without prefix 0 or 91");
                        focusView = edtMobile;
                        cancel = true;
                    }
                }
                if (!TextUtils.isEmpty(mobile)){
                    if (mobile.length()!=10) {
                        edtMobile.setError("Please enter 10 digits Cell No without prefix 0 or 91");
                        focusView = edtMobile;
                        cancel = true;
                    }
                }
                if (TextUtils.isEmpty(phone)) {
                    if (PartyType.equals("2007")) {
                        edtPhone.setError("Phone No is mandatory!!!");
                        focusView = edtPhone;
                        cancel = true;
                    }
                }
                if (!TextUtils.isEmpty(phone)){
                    if (mobile.length() < 10) {
                        edtPhone.setError("Please enter correct Phone No./Landline No with STD code");
                        focusView = edtPhone;
                        cancel = true;
                    }
                }
//                if (TextUtils.isEmpty(email)) {
//                    edtEmail.setError("Email is mandatory!!!");
//                    focusView = edtEmail;
//                    cancel = true;
//                }
//                if (TextUtils.isEmpty(address1)) {
//                    edtAddress1.setError("Address1 is mandatory!!!");
//                    focusView = edtAddress1;
//                    cancel = true;
//                }
                if (TextUtils.isEmpty(country)) {
                    edtCountry.setError("Country is mandatory!!!");
                    focusView = edtCountry;
                    cancel = true;
                }
                if (TextUtils.isEmpty(state)) {
                    MessageDialog.MessageDialog(context,"","Please select State");
                    focusView2 = spnState;
                    cancel = true;
                }
                if (TextUtils.isEmpty(city)) {
                    MessageDialog.MessageDialog(context,"","Please select City");
                    focusView2 = spnCity;
                    cancel = true;
                }
//                if (TextUtils.isEmpty(pincode)) {
//                    edtPincode.setError("Pincode is mandatory!!!");
//                    focusView = edtPincode;
//                    cancel = true;
//                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                    if (focusView2!=null)
                        focusView2.setFocusable(true);
                } else {
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        String Address = address2 + (address3.isEmpty() ? "" : ", "+address3);
                        String EdID = (dataset == null ? "" : (dataset.getID()==null ? "" : dataset.getID()));
                        CallVolleyMstBusinessPartnerCPUpdate(str[3], str[4], str[0], str[5], str[14],PartyID,SubPartyID,name,designation,designationId,address1,Address,country,state,city,mobile,email,fax,phone,pincode,String.valueOf(whatsAppFlag),remarks,EdID,"",PartyType,CommanStatic.LogIN_UserName);
                    } else {
                        MessageDialog.MessageDialog(context, "", "No Internet Connection");
                    }
                }
            }
        });
    }
    private void AlertDialogForWhatsApp(final String Mobile, final RadioButton radioButtonYes, final RadioButton radioButtonNo){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Does ("+Mobile+") have whats app?");
        alertDialogBuilder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                radioButtonNo.setChecked(true);
                whatsAppFlag = 0;
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                radioButtonYes.setChecked(true);
                whatsAppFlag = 1;
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    radioButtonNo.setChecked(true);
                    whatsAppFlag = 0;
                    dialog.dismiss();
                }
                return true;
            }
        });
    }
    private void CallRetrofitStateList(final String CompanyID, String CountryID, final String PreviousStateID, final String PreviousCityID){
        progressBarState.setVisibility(View.VISIBLE);
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("CompanyID", CompanyID);
        params.put("CountryID", CountryID);
        Call<ResponseStateListDataset> call = apiService.getStateList(params);
        call.enqueue(new Callback<ResponseStateListDataset>() {
            @Override
            public void onResponse(Call<ResponseStateListDataset> call, retrofit2.Response<ResponseStateListDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            ArrayList<StateDataset> datasetList = response.body().getResult();
                            int c = 0, selectedStatePos = 0;
                            ArrayList<Map<String,String>> temp = new ArrayList<>();
                            Map<String,String> map = new HashMap<>();
                            map.put("ID","");
                            map.put("Name","Select State");
                            temp.add(c, map);
                            for (int i = 0; i < datasetList.size(); i++) {
                                c++;
                                map = new HashMap<>();
                                map.put("ID",datasetList.get(i).getStateID());
                                map.put("Name",datasetList.get(i).getStateName());
                                temp.add(c, map);
                                if (!PreviousStateID.isEmpty() && PreviousStateID.equals(datasetList.get(i).getStateID())){
                                    selectedStatePos = c;
                                }
                            }
                            stateAdapter = new CommonSearchSpinnerFilterableAdapter(context, temp,temp);
                            spnState.setAdapter(stateAdapter);
                            spnState.setSelection(selectedStatePos);
                            spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    txtViewState.setVisibility(View.GONE);
                                    Map<String,String> map = (Map<String,String>) parent.getAdapter().getItem(position);
                                    if (map!=null)
                                        StateID = map.get("ID");
                                        StateName = map.get("Name");
                                        if (!StateID.isEmpty())
                                            CallRetrofitCityList(CompanyID,StateID,PreviousCityID);
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                    MessageDialog.MessageDialog(context,"Exception","State"+e.toString());
                }
                progressBarState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseStateListDataset> call, Throwable t) {
                Log.e(TAG,"State Failure: "+t.toString());
                MessageDialog.MessageDialog(context,"State Failure",""+t.toString());
                progressBarState.setVisibility(View.GONE);
            }
        });
    }
    //TODO: City Api
    private void CallRetrofitCityList(String CompanyID, String StateID, final String PreviousCityID){
        progressBarCity.setVisibility(View.VISIBLE);
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("CompanyID", CompanyID);
        params.put("StateID", StateID);
        Call<ResponseCityListDataset> call = apiService.getCityList(params);
        call.enqueue(new Callback<ResponseCityListDataset>() {
            @Override
            public void onResponse(Call<ResponseCityListDataset> call, retrofit2.Response<ResponseCityListDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            final ArrayList<CityDataset> datasetList = response.body().getResult();
                            int c = 0, selectedCityPos = 0;
                            ArrayList<Map<String,String>> temp = new ArrayList<>();
                            Map<String,String> map = new HashMap<>();
                            map.put("ID","");
                            map.put("Name","Select City");
                            temp.add(c, map);
                            for (int i = 0; i < datasetList.size(); i++) {
                                c++;
                                map = new HashMap<>();
                                map.put("ID",datasetList.get(i).getCityID());
                                map.put("Name",datasetList.get(i).getCityName());
                                temp.add(c, map);
                                if (!PreviousCityID.isEmpty() && PreviousCityID.equals(datasetList.get(i).getCityID())){
                                    selectedCityPos = c;
                                }
                            }
                            cityAdapter = new CommonSearchSpinnerFilterableAdapter(context, temp,temp);
                            spnCity.setAdapter(cityAdapter);
                            spnCity.setSelection(selectedCityPos);
                            spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    txtViewCity.setVisibility(View.GONE);
                                    Map<String,String> map = (Map<String,String>) parent.getAdapter().getItem(position);
                                    if (map!=null)
                                        CityID = map.get("ID");
                                        CityName = map.get("Name");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                    MessageDialog.MessageDialog(context,"Exception","City"+e.toString());
                }
                progressBarCity.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseCityListDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog.MessageDialog(context,"City Reg Failure",""+t.toString());
                progressBarCity.setVisibility(View.GONE);
            }
        });
    }
    private void CallVolleyCPDesignationList(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String PreviousDesign){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CPDesignationList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    cpDesignList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        int selectedPos = 0;
                        if (jsonArrayResult.length() > 0) {
                            int c=0;
                            Map<String,String> map = new HashMap<>();
                            map.put("ID","");
                            map.put("Name","*Select Designation");
                            cpDesignList.add(c,map);
                            for (int i = 0; i<jsonArrayResult.length(); i++) {
                                c++;
                                map = new HashMap<>();
                                String Name = ((jsonArrayResult.getJSONObject(i).optString("Name")==null || jsonArrayResult.getJSONObject(i).optString("Name").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Name")));
                                map.put("ID",((jsonArrayResult.getJSONObject(i).optString("ID")==null || jsonArrayResult.getJSONObject(i).optString("ID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("ID"))));
                                map.put("Name", Name);
                                //TODO: To add map
                                cpDesignList.add(c,map);
                                //TODO: Check Previous Designation selection
                                if (!PreviousDesign.isEmpty() && !Name.isEmpty()){
                                    if (PreviousDesign.toLowerCase().equals(Name.toLowerCase()))
                                        selectedPos = c;
                                }
                            }
                        }
                        spnDesignation.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, cpDesignList,cpDesignList));
                        spnDesignation.setSelection(selectedPos);
                        spnDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Map<String,String> map = (Map<String,String>) parent.getAdapter().getItem(position);
                                if (map!=null)
                                    DesignationID = map.get("ID");
                                    Designation = map.get("Name");
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        //LoadRecyclerView(cpDatasetList);
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
                Log.d(TAG,"CPDesignationList parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyMstBusinessPartnerCPList(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String PartyID,final String SubPartyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"MstBusinessPartnerCPList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    List<MstBussinessPartnerCpDataset> cpDatasetList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        if (jsonArrayResult.length() > 0) {
                            for (int i = 0; i<jsonArrayResult.length(); i++) {
                                MstBussinessPartnerCpDataset cpDataset = new MstBussinessPartnerCpDataset();
                                cpDataset.setID((jsonArrayResult.getJSONObject(i).optString("ID")==null || jsonArrayResult.getJSONObject(i).optString("ID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("ID")));
                                cpDataset.setPartyID((jsonArrayResult.getJSONObject(i).optString("PartyID")==null || jsonArrayResult.getJSONObject(i).optString("PartyID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("PartyID")));
                                cpDataset.setSubPartyID((jsonArrayResult.getJSONObject(i).optString("SubPartyID")==null || jsonArrayResult.getJSONObject(i).optString("SubPartyID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SubPartyID")));
                                cpDataset.setName((jsonArrayResult.getJSONObject(i).optString("Name")==null || jsonArrayResult.getJSONObject(i).optString("Name").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Name")));
                                cpDataset.setDesignation((jsonArrayResult.getJSONObject(i).optString("Designation")==null || jsonArrayResult.getJSONObject(i).optString("Designation").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Designation")));
                                cpDataset.setAddress1((jsonArrayResult.getJSONObject(i).optString("Address1")==null || jsonArrayResult.getJSONObject(i).optString("Address1").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Address1")));
                                cpDataset.setAddress2((jsonArrayResult.getJSONObject(i).optString("Address2")==null || jsonArrayResult.getJSONObject(i).optString("Address2").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Address2")));
                                cpDataset.setAddress3((jsonArrayResult.getJSONObject(i).optString("Address3")==null || jsonArrayResult.getJSONObject(i).optString("Address3").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Address3")));
                                cpDataset.setPartyName((jsonArrayResult.getJSONObject(i).optString("PartyName")==null || jsonArrayResult.getJSONObject(i).optString("PartyName").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("PartyName")));
                                cpDataset.setSubParty((jsonArrayResult.getJSONObject(i).optString("SubParty")==null || jsonArrayResult.getJSONObject(i).optString("SubParty").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SubParty")));
                                cpDataset.setCountryID((jsonArrayResult.getJSONObject(i).optString("CountryID")==null || jsonArrayResult.getJSONObject(i).optString("CountryID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID")));
                                cpDataset.setCountry((jsonArrayResult.getJSONObject(i).optString("Country")==null || jsonArrayResult.getJSONObject(i).optString("Country").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Country")));
                                cpDataset.setStateID((jsonArrayResult.getJSONObject(i).optString("StateID")==null || jsonArrayResult.getJSONObject(i).optString("StateID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("StateID")));
                                cpDataset.setState((jsonArrayResult.getJSONObject(i).optString("State")==null || jsonArrayResult.getJSONObject(i).optString("State").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("State")));
                                cpDataset.setCityID((jsonArrayResult.getJSONObject(i).optString("CityID")==null || jsonArrayResult.getJSONObject(i).optString("CityID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("CityID")));
                                cpDataset.setCity((jsonArrayResult.getJSONObject(i).optString("City")==null || jsonArrayResult.getJSONObject(i).optString("City").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("City")));
                                cpDataset.setCellNo((jsonArrayResult.getJSONObject(i).optString("CellNo")==null || jsonArrayResult.getJSONObject(i).optString("CellNo").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("CellNo")));
                                cpDataset.setEmail((jsonArrayResult.getJSONObject(i).optString("Email")==null || jsonArrayResult.getJSONObject(i).optString("Email").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Email")));
                                cpDataset.setFax((jsonArrayResult.getJSONObject(i).optString("Fax")==null || jsonArrayResult.getJSONObject(i).optString("Fax").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Fax")));
                                cpDataset.setPhoneNo((jsonArrayResult.getJSONObject(i).optString("PhoneNo")==null || jsonArrayResult.getJSONObject(i).optString("PhoneNo").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("PhoneNo")));
                                cpDataset.setPIN((jsonArrayResult.getJSONObject(i).optString("PIN")==null || jsonArrayResult.getJSONObject(i).optString("PIN").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("PIN")));
                                cpDataset.setSGWhatsappFLG(jsonArrayResult.getJSONObject(i).optInt("SG_WhatsappFLG"));
                                cpDataset.setSG_Remarks((jsonArrayResult.getJSONObject(i).optString("SG_Remarks")==null || jsonArrayResult.getJSONObject(i).optString("SG_Remarks").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SG_Remarks")));
                                //TODO: To add dataset
                                cpDatasetList.add(cpDataset);
                            }
                        }
                        LoadRecyclerView(cpDatasetList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        LoadRecyclerView(cpDatasetList);
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
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                Log.d(TAG,"MstBusinessPartnerCPList parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyMstBusinessPartnerCPUpdate(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String PartyID,final String SubPartyID,final String Name,final String Designation,final String DesignationID,final String Address1,final String Address2,final String CountryID,final String StateID,final String CityID,final String CellNo,final String Email,final String Fax,final String PhoneNo,final String PIN,final String WhatsApp,final String Remarks,final String edID,final String delFlag,final String PartyType,final String UserName){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"MstBusinessPartnerCPUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    List<MstBussinessPartnerCpDataset> cpDatasetList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        MessageDialog.MessageDialog(context,"",Msg);
                        if (dialog!=null) { dialog.dismiss(); }
                        if (!PartyID.isEmpty())
                            CallApiMethod(PartyID,SubPartyID);
                    } else {
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
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("Name", Name);
                params.put("Designation", Designation);
                params.put("DesignationID", DesignationID);
                params.put("Address1", Address1);
                params.put("Address2", Address2);
                params.put("CountryID", CountryID);
                params.put("StateID", StateID);
                params.put("CityID", CityID);
                params.put("CellNo", CellNo);
                params.put("Email", Email);
                params.put("Fax", Fax);
                params.put("PhoneNo", PhoneNo);
                params.put("PIN", PIN);
                params.put("WhatsApp", WhatsApp);
                params.put("Remarks", Remarks);
                params.put("edID", edID);
                params.put("delFlag", delFlag);
                params.put("PartyType", PartyType);
                params.put("UserName", UserName);
                Log.d(TAG,"MstBusinessPartnerCPUpdate parameters:"+params.toString());
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
        if(progressDialog!=null ){// || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            //swipeRefreshLayout.setRefreshing(false);
        }
    }
    //TODO:Load Recycler View
    private void LoadRecyclerView(final List<MstBussinessPartnerCpDataset> cpDatasetList){
        adapter = new MstBussinessCPAdapter(context,cpDatasetList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                MstBussinessPartnerCpDataset dataset = (MstBussinessPartnerCpDataset) adapter.getItem(position);
                if (StaticValues.editFlag == 1) {
                    DialogAddMstBussinessCP(dataset);
                } else {
                    MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                }
            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                MstBussinessPartnerCpDataset cpDataset = (MstBussinessPartnerCpDataset) adapter.getItem(position);
                if (StaticValues.removeFlag == 1) {
                    AlertDialogForDeleteCP(cpDataset,"1");
                } else {
                    MessageDialog.MessageDialog(context,"Alert","You don't have delete permission of this module");
                }
                return false;
            }
        });
    }
    private void AlertDialogForDeleteCP(final MstBussinessPartnerCpDataset cpDataset, final String deleteFlag){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Do you want delete? This contact person.");
        alertDialogBuilder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    final String[] str=obj.GetSharePreferenceSession(context);
                    String EdID = (cpDataset == null ? "" : (cpDataset.getID()==null ? "" : cpDataset.getID()));
                    if (!EdID.isEmpty() && str!=null)
                        CallVolleyMstBusinessPartnerCPUpdate(str[3], str[4], str[0], str[5], str[14], PartyID, SubPartyID, cpDataset.getName(), cpDataset.getDesignation(), "", cpDataset.getAddress1(), cpDataset.getAddress2(), cpDataset.getCountryID(), cpDataset.getStateID(), cpDataset.getCityID(), cpDataset.getCellNo(), cpDataset.getEmail(), cpDataset.getFax(), cpDataset.getPhoneNo(), cpDataset.getPIN(), String.valueOf(whatsAppFlag), cpDataset.getSG_Remarks(), EdID, deleteFlag, PartyType, CommanStatic.LogIN_UserName);
                        dialog.dismiss();
                } else {
                    MessageDialog.MessageDialog(context, "", "No Internet Connection");
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
