package orderbooking.customerlist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.singlagroup.AppController;
import com.singlagroup.GlobleValues;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.PDFThumbnail;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import services.NetworkUtils;
import whatsapp.adapter.ImageAdapter;

/**
 * Created by rakes on 02-Dec-17.
 */

public class SubPartyCreationActivity extends AppCompatActivity {
    private static String TAG = SubPartyCreationActivity.class.getSimpleName();
    private ActionBar actionBar;
    private Context context;
    private RadioGroup rgInactive,rgGSTCat,rgSameAs,rgCpWhatsApp,rgCpSameAs;
    private TextInputLayout edtName,edtIDName,edtPanNo,edtCrLimit,edtCrDays,edtGstIn, edtGstEffectFrm;
    private TextInputLayout edtAddress1,edtAddress2,edtAddress3,edtPincode,edtCellNo,edtPhoneNo,edtEmail;
    private TextInputLayout edtDelAddress1,edtDelAddress2,edtDelAddress3,edtDelPincode,edtDelCellNo,edtDelPhoneNo,edtDelEmail;
    private TextInputLayout edtCpName,edtCpAddress1,edtCpAddress2,edtCpAddress3,edtCpPincode,edtCpCellNo,edtCpPhoneNo,edtCpEmail,edtCpFax,edtCpRemarks;
    private CommonSearchableSpinner spnParty,spnTransport,spnDesignation;
    private CommonSearchableSpinner spnCountry,spnState,spnCity,spnDelCountry,spnDelState,spnDelCity,spnCpCountry,spnCpState,spnCpCity;
    private ProgressBar pBarParty,pBarTransport,pBarDesignation;
    private ProgressBar pBarCountry,pBarState,pBarCity,pBarDelCountry,pBarDelState,pBarDelCity,pBarCpCountry,pBarCpState,pBarCpCity;
    private TextView txtParty,txtTransport,txtDesignation;
    private TextView txtCountry,txtState,txtCity,txtDelCountry,txtDelState,txtDelCity,txtCpCountry,txtCpState,txtCpCity;
    private LinearLayout lLDelAddress,lLCpAddress;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private String Inactive="0",GstCat="0",SameAs="1",CpSameAs="1",CpWhatsApp="0",Name="",IDName="",PartyID="",PartyType="",TransportID="",DesignationID="",Designation="",PanNo="",GstIN="",GSTEffectFrm="2017-07-01",CreditLimit="0",CreditDays="0";
    private String Address1="",Address2="",Address3="",CountryID="",StateID="",CityID="",PIN="",CellNo="",PhoneNo="",Email="";
    private String DelAddress1="",DelAddress2="",DelAddress3="",DelCountryID="",DelStateID="",DelCityID="",DelPIN="",DelCellNo="",DelPhoneNo="",DelEmail="";
    private String CpName="",CpAddress1="",CpAddress2="",CpAddress3="",CpCountryID="",CpStateID="",CpCityID="",CpPIN="",CpCellNo="",CpPhoneNo="",CpEmail="",CpFax="",CpRemarks="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sub_party);
        Initialization();
        ModulePermission();
        Validation();
        EditTextValidate();
        ClickEvent();
    }
    private void Initialization(){
        this.context = SubPartyCreationActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO: TextInputLayout
        edtName = (TextInputLayout) findViewById(R.id.editText_name);
        edtIDName = (TextInputLayout) findViewById(R.id.editText_id_name);
        edtCrLimit = (TextInputLayout) findViewById(R.id.editText_credit_limit);
        edtCrDays = (TextInputLayout) findViewById(R.id.editText_credit_days);
        edtPanNo = (TextInputLayout) findViewById(R.id.editText_pan_no);
        edtGstIn = (TextInputLayout) findViewById(R.id.editText_gstin);
        edtGstEffectFrm = (TextInputLayout) findViewById(R.id.editText_gst_effec_from);

        edtAddress1 = (TextInputLayout) findViewById(R.id.editText_address_1);
        edtAddress2 = (TextInputLayout) findViewById(R.id.editText_address_2);
        edtAddress3 = (TextInputLayout) findViewById(R.id.editText_address_3);
        edtPincode = (TextInputLayout) findViewById(R.id.editText_pincode);
        edtCellNo = (TextInputLayout) findViewById(R.id.editText_cell_no);
        edtPhoneNo = (TextInputLayout) findViewById(R.id.editText_phone_no);
        edtEmail = (TextInputLayout) findViewById(R.id.editText_email);
        //Delivery
        edtDelAddress1 = (TextInputLayout) findViewById(R.id.editText_del_address_1);
        edtDelAddress2 = (TextInputLayout) findViewById(R.id.editText_del_address_2);
        edtDelAddress3 = (TextInputLayout) findViewById(R.id.editText_del_address_3);
        edtDelPincode = (TextInputLayout) findViewById(R.id.editText_del_pincode);
        edtDelCellNo = (TextInputLayout) findViewById(R.id.editText_del_cell_no);
        edtDelPhoneNo = (TextInputLayout) findViewById(R.id.editText_del_phone_no);
        edtDelEmail = (TextInputLayout) findViewById(R.id.editText_del_email);
        //Contact Person
        edtCpName = (TextInputLayout) findViewById(R.id.editText_cp_name);
        edtCpAddress1 = (TextInputLayout) findViewById(R.id.editText_cp_address_1);
        edtCpAddress2 = (TextInputLayout) findViewById(R.id.editText_cp_address_2);
        edtCpAddress3 = (TextInputLayout) findViewById(R.id.editText_cp_address_3);
        edtCpPincode = (TextInputLayout) findViewById(R.id.editText_cp_pincode);
        edtCpCellNo = (TextInputLayout) findViewById(R.id.editText_cp_cell_no);
        edtCpPhoneNo = (TextInputLayout) findViewById(R.id.editText_cp_phone_no);
        edtCpEmail = (TextInputLayout) findViewById(R.id.editText_cp_email);
        edtCpFax = (TextInputLayout) findViewById(R.id.editText_cp_fax);
        edtCpRemarks = (TextInputLayout) findViewById(R.id.editText_cp_remarks);
        //TODO: Common Searchable Spinner
        spnParty = (CommonSearchableSpinner) findViewById(R.id.spinner_party_list);
        spnTransport = (CommonSearchableSpinner) findViewById(R.id.spinner_transporter);
        spnDesignation = (CommonSearchableSpinner) findViewById(R.id.spinner_designation);
        spnCountry = (CommonSearchableSpinner) findViewById(R.id.spinner_country);
        spnState = (CommonSearchableSpinner) findViewById(R.id.spinner_state);
        spnCity = (CommonSearchableSpinner) findViewById(R.id.spinner_city);
        //Delivery
        spnDelCountry = (CommonSearchableSpinner) findViewById(R.id.spinner_del_country);
        spnDelState = (CommonSearchableSpinner) findViewById(R.id.spinner_del_state);
        spnDelCity = (CommonSearchableSpinner) findViewById(R.id.spinner_del_city);
        //Contact Person
        spnCpCountry = (CommonSearchableSpinner) findViewById(R.id.spinner_cp_country);
        spnCpState = (CommonSearchableSpinner) findViewById(R.id.spinner_cp_state);
        spnCpCity = (CommonSearchableSpinner) findViewById(R.id.spinner_cp_city);
        //TODO: Progress Bar
        pBarParty = (ProgressBar) findViewById(R.id.progressBarParty);
        pBarTransport = (ProgressBar) findViewById(R.id.progressBarTransporter);
        pBarDesignation = (ProgressBar) findViewById(R.id.progressBarDesignation);
        pBarCountry = (ProgressBar) findViewById(R.id.progressBarCountry);
        pBarState = (ProgressBar) findViewById(R.id.progressBarState);
        pBarCity = (ProgressBar) findViewById(R.id.progressBarCity);
        //Delivery
        pBarDelCountry = (ProgressBar) findViewById(R.id.progressBarDelCountry);
        pBarDelState = (ProgressBar) findViewById(R.id.progressBarDelState);
        pBarDelCity = (ProgressBar) findViewById(R.id.progressBarDelCity);
        //Contact Person
        pBarCpCountry = (ProgressBar) findViewById(R.id.progressBarCPCountry);
        pBarCpState = (ProgressBar) findViewById(R.id.progressBarCPState);
        pBarCpCity = (ProgressBar) findViewById(R.id.progressBarCPCity);
        //TODO: TextView
        txtParty = (TextView) findViewById(R.id.text_party);
        txtTransport = (TextView) findViewById(R.id.text_transporter);
        txtDesignation = (TextView) findViewById(R.id.text_designation);
        txtCountry = (TextView) findViewById(R.id.text_country);
        txtState = (TextView) findViewById(R.id.text_state);
        txtCity = (TextView) findViewById(R.id.text_city);
        //Delivery
        txtDelCountry = (TextView) findViewById(R.id.text_del_country);
        txtDelState = (TextView) findViewById(R.id.text_del_state);
        txtDelCity = (TextView) findViewById(R.id.text_del_city);
        //Contact Person
        txtCpCountry = (TextView) findViewById(R.id.text_cp_country);
        txtCpState = (TextView) findViewById(R.id.text_cp_state);
        txtCpCity = (TextView) findViewById(R.id.text_cp_city);
        //TODO: Radio Group
        rgInactive = (RadioGroup) findViewById(R.id.radio_group_in_active);
        rgGSTCat = (RadioGroup) findViewById(R.id.radio_group_gst_category);
        rgSameAs = (RadioGroup) findViewById(R.id.radio_group_same_as_address);
        rgCpWhatsApp = (RadioGroup) findViewById(R.id.radio_group_cp_whats_app);
        rgCpSameAs = (RadioGroup) findViewById(R.id.radio_group_cp_same_as_address);
        //TODO: Linear Layout
        lLDelAddress = (LinearLayout) findViewById(R.id.linearLayout_delivery_address);
        lLCpAddress = (LinearLayout) findViewById(R.id.linearLayout_cp_address);
        //TODO: Button
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        //TODO: Progress Dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
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
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null) {
                        // Api Call for Party
                        CallVolleyPartyListOrTransporterListByType(str[3], str[4], str[0], str[5], str[14], str[15], str[16], str[2], "1", "0");
                        // Api Call for Transporter
                        CallVolleyPartyListOrTransporterListByType(str[3], str[4], str[0], str[5], str[14], "", "", "", "", "1");
                        // Api Call for Designation
                        CallVolleyPartyListOrTransporterListByType(str[3], str[4], str[0], str[5], str[14], "", "", "", "", "2");
                        // Api Call for Country
                        CallVolleyCountryStateCity(str[14], "", "0", "0");
                        // Api Call for Delivery Country
                        CallVolleyCountryStateCity(str[14], "", "1", "0");
                        // Api Call for Designation Country
                        CallVolleyCountryStateCity(str[14], "", "2", "0");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void Validation(){
        //TODO: Gst Category
        if (rgGSTCat.getCheckedRadioButtonId() == R.id.radio_button_gst_cat_registered_party){
            GstCat = "0";
            edtGstIn.setVisibility(View.VISIBLE);
        }else if (rgGSTCat.getCheckedRadioButtonId() == R.id.radio_button_gst_cat_unregistered_party){
            GstCat = "1";
            edtGstIn.setVisibility(View.GONE);
        }
        rgGSTCat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_gst_cat_registered_party){
                    GstCat = "0";
                    edtGstIn.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.radio_button_gst_cat_unregistered_party){
                    GstCat = "1";
                    edtGstIn.setVisibility(View.GONE);
                }
            }
        });

        //TODO: Same As
        if (rgSameAs.getCheckedRadioButtonId() == R.id.radio_button_same_as_address_no){
            SameAs = "0";
            lLDelAddress.setVisibility(View.VISIBLE);
        }else if (rgSameAs.getCheckedRadioButtonId() == R.id.radio_button_same_as_address_yes){
            SameAs = "1";
            lLDelAddress.setVisibility(View.GONE);
        }
        rgSameAs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_same_as_address_no){
                    SameAs = "0";
                    lLDelAddress.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.radio_button_same_as_address_yes){
                    SameAs = "1";
                    lLDelAddress.setVisibility(View.GONE);
                }
            }
        });

        //TODO: Whats App
        if (rgCpWhatsApp.getCheckedRadioButtonId() == R.id.radio_button_cp_yes){
            CpWhatsApp = "1";
        }else if (rgCpWhatsApp.getCheckedRadioButtonId() == R.id.radio_button_cp_no){
            CpWhatsApp = "0";
        }
        rgCpWhatsApp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_cp_yes){
                    CpWhatsApp = "1";
                }else if (checkedId == R.id.radio_button_cp_no){
                    CpWhatsApp = "0";
                }
            }
        });
        //TODO: Contact Person Same As
        if (rgCpSameAs.getCheckedRadioButtonId() == R.id.radio_button_cp_same_as_address_no){
            CpSameAs = "0";
            lLCpAddress.setVisibility(View.VISIBLE);
        }else if (rgCpSameAs.getCheckedRadioButtonId() == R.id.radio_button_cp_same_as_address_yes){
            CpSameAs = "1";
            lLCpAddress.setVisibility(View.GONE);

        }
        rgCpSameAs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_cp_same_as_address_no){
                    CpSameAs = "0";
                    lLCpAddress.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.radio_button_cp_same_as_address_yes){
                    CpSameAs = "1";
                    lLCpAddress.setVisibility(View.GONE);
                }
            }
        });
    }
    private void ClickEvent(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtName.setError(null);
                edtCellNo.setError(null);
                edtPanNo.setError(null);
                edtEmail.setError(null);
                edtPincode.setError(null);
                edtAddress1.setError(null);

                edtDelCellNo.setError(null);
                edtDelEmail.setError(null);
                edtDelPincode.setError(null);
                edtDelAddress1.setError(null);

                edtCpName.setError(null);
                edtCpCellNo.setError(null);
                edtCpEmail.setError(null);
                edtCpPincode.setError(null);
                edtCpAddress1.setError(null);


                Name     = edtName.getEditText().getText().toString();
                IDName   = edtIDName.getEditText().getText().toString();
                PanNo    = edtPanNo.getEditText().getText().toString();
                GstIN    = edtGstIn.getEditText().getText().toString();

                Address1 = edtAddress1.getEditText().getText().toString();
                Address2 = edtAddress2.getEditText().getText().toString();
                Address3 = edtAddress3.getEditText().getText().toString();
                PIN      = edtPincode.getEditText().getText().toString();
                CellNo   = edtCellNo.getEditText().getText().toString();
                PhoneNo  = edtPhoneNo.getEditText().getText().toString();
                Email    = edtEmail.getEditText().getText().toString();

                DelAddress1 = SameAs.equals("0") ? edtDelAddress1.getEditText().getText().toString() : Address1;
                DelAddress2 = SameAs.equals("0") ? edtDelAddress2.getEditText().getText().toString() : Address2;
                DelAddress3 = SameAs.equals("0") ? edtDelAddress3.getEditText().getText().toString() : Address3;
                DelPIN      = SameAs.equals("0") ? edtDelPincode.getEditText().getText().toString() : PIN;
                DelCellNo   = SameAs.equals("0") ? edtDelCellNo.getEditText().getText().toString() : CellNo;
                DelPhoneNo  = SameAs.equals("0") ? edtDelPhoneNo.getEditText().getText().toString() : PhoneNo;
                DelEmail    = SameAs.equals("0") ? edtDelEmail.getEditText().getText().toString() : Email;

                CpAddress1 = CpSameAs.equals("0") ? edtCpAddress1.getEditText().getText().toString() : Address1;
                CpAddress2 = CpSameAs.equals("0") ? edtCpAddress2.getEditText().getText().toString() : Address2;
                CpAddress3 = CpSameAs.equals("0") ? edtCpAddress3.getEditText().getText().toString() : Address3;
                CpPIN      = CpSameAs.equals("0") ? edtCpPincode.getEditText().getText().toString() : PIN;
                CpCellNo   = CpSameAs.equals("0") ? edtCpCellNo.getEditText().getText().toString() : CellNo;
                CpPhoneNo  = CpSameAs.equals("0") ? edtCpPhoneNo.getEditText().getText().toString() : PhoneNo;
                CpEmail    = CpSameAs.equals("0") ? edtCpEmail.getEditText().getText().toString() : Email;



                boolean cancel = false;
                View focusView = null,focusView2=null;
                if (TextUtils.isEmpty(PartyID)) {
                    SetSpinnerError(txtParty,"Please select Party");
                    focusView2 = spnParty;
                    cancel = true;
                }
                if (TextUtils.isEmpty(Name)) {
                    edtName.setError("Name cannot be blank");
                    focusView = edtName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(GstIN)) {
                    if (GstCat.equals("0")) {
                        edtCrDays.setError("Credit Days cannot be blank");
                        focusView = edtCrDays;
                        cancel = true;
                    }
                }
                if (TextUtils.isEmpty(Address1)) {
                    edtAddress1.setError("Address cannot be blank");
                    focusView = edtAddress1;
                    cancel = true;
                }
                if (TextUtils.isEmpty(PIN)) {
                    edtPincode.setError("Pincode cannot be blank");
                    focusView = edtPincode;
                    cancel = true;
                }
                if (TextUtils.isEmpty(CellNo)) {
                    edtCellNo.setError("Cell No cannot be blank");
                    focusView = edtCellNo;
                    cancel = true;
                }
                if (TextUtils.isEmpty(CountryID)) {
                    SetSpinnerError(txtCountry,"Please select Country");
                    focusView2 = spnCountry;
                    cancel = true;
                }
                if (TextUtils.isEmpty(StateID)) {
                    SetSpinnerError(txtState,"Please select State");
                    focusView2 = spnState;
                    cancel = true;
                }
                if (TextUtils.isEmpty(CityID)) {
                    SetSpinnerError(txtCity,"Please select City");
                    focusView2 = spnCity;
                    cancel = true;
                }
                if (SameAs.equals("0")) {
                    if (TextUtils.isEmpty(DelAddress1)) {
                        edtDelAddress1.setError("Delivery address cannot be blank");
                        focusView = edtDelAddress1;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(DelPIN)) {
                        edtDelPincode.setError("Delivery pincode cannot be blank");
                        focusView = edtDelPincode;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(DelCellNo)) {
                        edtDelCellNo.setError("Delivery cell no cannot be blank");
                        focusView = edtDelCellNo;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(DelCountryID)) {
                        SetSpinnerError(txtDelCountry, "Please select Country");
                        focusView2 = spnDelCountry;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(DelStateID)) {
                        SetSpinnerError(txtDelState, "Please select State");
                        focusView2 = spnDelState;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(DelCityID)) {
                        SetSpinnerError(txtDelCity, "Please select City");
                        focusView2 = spnDelCity;
                        cancel = true;
                    }
                }
                //TODO : Conatct Person
                if (TextUtils.isEmpty(DesignationID)) {
                    SetSpinnerError(txtDesignation, "Please select Designation");
                    focusView2 = spnDesignation;
                    cancel = true;
                }
                if (TextUtils.isEmpty(CpName)) {
                    edtCpName.setError("Contact person name cannot be blank");
                    focusView = edtCpName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(CpCellNo)) {
                    edtCpCellNo.setError("Contact person cell no cannot be blank");
                    focusView = edtCpCellNo;
                    cancel = true;
                }

                if (CpSameAs.equals("0")) {

                    if (TextUtils.isEmpty(CpAddress1)) {
                        edtCpAddress1.setError("Contact person address cannot be blank");
                        focusView = edtCpAddress1;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(CpPIN)) {
                        edtCpPincode.setError("Contact person pincode cannot be blank");
                        focusView = edtCpPincode;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(CpCountryID)) {
                        SetSpinnerError(txtCpCountry, "Please select Country");
                        focusView2 = spnCpCountry;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(CpStateID)) {
                        SetSpinnerError(txtCpState, "Please select State");
                        focusView2 = spnCpState;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(CpCityID)) {
                        SetSpinnerError(txtCpCity, "Please select City");
                        focusView2 = spnCpCity;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(CpEmail)) {
                        edtCpEmail.setError("Contact person email cannot be blank");
                        focusView = edtCpEmail;
                        cancel = true;
                    }
                }


                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    if (focusView!=null) {  focusView.requestFocus();  }
                    if (focusView2!=null) {
                        focusView2.setFocusable(true);
                        focusView2.setFocusableInTouchMode(true);
                        focusView2.requestFocus();
                    }
                } else {
                    //
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            if ((CellNo.length() == 10) && (PIN.length() == 6)){

                                if(SameAs.equals("0") && (DelCellNo.length() == 10) && (DelPIN.length() == 6) && CpSameAs.equals("0") && (CpCellNo.length() == 10) && (CpPIN.length() == 6)) {
                                    CallVolleyCreateSubParty(str[3], str[4], str[0], str[5], str[14], PartyID, Name, IDName, Inactive
                                            , Address1, Address2, Address3, CountryID, StateID, CityID, PIN, CellNo, PhoneNo, Email
                                            , DelAddress1, DelAddress2, DelAddress3, DelCountryID, DelStateID, DelCityID, DelPIN, DelCellNo, DelPhoneNo, DelEmail
                                            , PanNo, CreditLimit, CreditDays, GstIN, GSTEffectFrm, TransportID, GstCat
                                            , PartyType, CpName, Designation, DesignationID, CpAddress1, CpAddress2, CpAddress3
                                            , CpCountryID, CpStateID, CpCityID, CpPIN, CpCellNo, CpPhoneNo, CpEmail, CpFax, CpWhatsApp, CpRemarks
                                    );
                                    //MessageDialog.MessageDialog(context,"","Delivery & Contact Person Only");
                                }else if(SameAs.equals("0") && (DelCellNo.length() == 10) && (DelPIN.length() == 6)) {
                                    CallVolleyCreateSubParty(str[3], str[4], str[0], str[5], str[14], PartyID, Name, IDName, Inactive
                                            , Address1, Address2, Address3, CountryID, StateID, CityID, PIN, CellNo, PhoneNo, Email
                                            , DelAddress1, DelAddress2, DelAddress3, DelCountryID, DelStateID, DelCityID, DelPIN, DelCellNo, DelPhoneNo, DelEmail
                                            , PanNo, CreditLimit, CreditDays, GstIN, GSTEffectFrm, TransportID, GstCat
                                            , PartyType, CpName, Designation, DesignationID, CpAddress1, CpAddress2, CpAddress3
                                            , CpCountryID, CpStateID, CpCityID, CpPIN, CpCellNo, CpPhoneNo, CpEmail, CpFax, CpWhatsApp, CpRemarks
                                    );
                                    //MessageDialog.MessageDialog(context,"","Delivery Only");
                                }
                                if(CpSameAs.equals("0") && (CpCellNo.length() == 10) && (CpPIN.length() == 6)) {
                                    CallVolleyCreateSubParty(str[3], str[4], str[0], str[5], str[14], PartyID, Name, IDName, Inactive
                                            , Address1, Address2, Address3, CountryID, StateID, CityID, PIN, CellNo, PhoneNo, Email
                                            , DelAddress1, DelAddress2, DelAddress3, DelCountryID, DelStateID, DelCityID, DelPIN, DelCellNo, DelPhoneNo, DelEmail
                                            , PanNo, CreditLimit, CreditDays, GstIN, GSTEffectFrm, TransportID, GstCat
                                            , PartyType, CpName, Designation, DesignationID, CpAddress1, CpAddress2, CpAddress3
                                            , CpCountryID, CpStateID, CpCityID, CpPIN, CpCellNo, CpPhoneNo, CpEmail, CpFax, CpWhatsApp, CpRemarks
                                    );
                                    //MessageDialog.MessageDialog(context,"","Contact Person Only");
                                } else {
                                    CallVolleyCreateSubParty(str[3], str[4], str[0], str[5], str[14], PartyID, Name, IDName, Inactive
                                            , Address1, Address2, Address3, CountryID, StateID, CityID, PIN, CellNo, PhoneNo, Email
                                            , DelAddress1, DelAddress2, DelAddress3, DelCountryID, DelStateID, DelCityID, DelPIN, DelCellNo, DelPhoneNo, DelEmail
                                            , PanNo, CreditLimit, CreditDays, GstIN, GSTEffectFrm, TransportID, GstCat
                                            , PartyType, CpName, Designation, DesignationID, CpAddress1, CpAddress2, CpAddress3
                                            , CpCountryID, CpStateID, CpCityID, CpPIN, CpCellNo, CpPhoneNo, CpEmail, CpFax, CpWhatsApp, CpRemarks
                                    );
                                    //MessageDialog.MessageDialog(context,"","Normal");
                                }
                            }else{
                                if (CellNo.length() != 10) {
                                    edtCellNo.setError("Please enter valid 10 digits Cell number");
                                    focusView = edtCellNo;
                                }
                                if (PIN.length() != 6){
                                    edtPincode.setError("Please enter valid 6 digits pincode");
                                    focusView = edtPincode;
                                }
                                if (SameAs.equals("0") && CpSameAs.equals("0")){
                                    if (DelCellNo.length() != 10) {
                                        edtDelCellNo.setError("Please enter valid 10 digits delivery Cell number");
                                        focusView = edtDelCellNo;
                                    }
                                    if (DelPIN.length() != 6){
                                        edtDelPincode.setError("Please enter valid 6 digits delivery pincode");
                                        focusView = edtDelPincode;
                                    }
                                    if (CpCellNo.length() != 10) {
                                        edtCpCellNo.setError("Please enter valid 10 digits contact person Cell number");
                                        focusView = edtCpCellNo;
                                    }
                                    if (CpPIN.length() != 6){
                                        edtCpPincode.setError("Please enter valid 6 digits contact person pincode");
                                        focusView = edtCpPincode;
                                    }
                                }
                                if (SameAs.equals("0")){
                                    if (DelCellNo.length() != 10) {
                                        edtDelCellNo.setError("Please enter valid 10 digits delivery Cell number");
                                        focusView = edtDelCellNo;
                                    }
                                    if (DelPIN.length() != 6){
                                        edtDelPincode.setError("Please enter valid 6 digits delivery pincode");
                                        focusView = edtDelPincode;
                                    }
                                }
                                if (CpSameAs.equals("0")){
                                    if (CpCellNo.length() != 10) {
                                        edtCpCellNo.setError("Please enter valid 10 digits contact person Cell number");
                                        focusView = edtCpCellNo;
                                    }
                                    if (CpPIN.length() != 6){
                                        edtCpPincode.setError("Please enter valid 6 digits contact person pincode");
                                        focusView = edtCpPincode;
                                    }
                                }
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            }
        });

    }
    private void EditTextValidate(){
        //TODO: GSTIN
        edtGstIn.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(),GlobleValues.GSTIN);
                if (!result.isEmpty()){
                    edtGstIn.setError(result);
                }else{
                    edtGstIn.setError(null);
                }
            }
        });
        //TODO: PAN No
        edtPanNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.PAN_NO);
                if (!result.isEmpty()) {
                    edtPanNo.setError(result);
                }else{
                    edtPanNo.setError(null);
                }
            }
        });
        //TODO: Cell No
        edtCellNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.MOBILE);
                if (!result.isEmpty()) {
                    edtCellNo.setError(result);
                }else{
                    edtCellNo.setError(null);
                }
            }
        });
        //TODO: Delivery Cell No
        edtDelCellNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.MOBILE);
                if (!result.isEmpty()) {
                    edtDelCellNo.setError(result);
                }else{
                    edtDelCellNo.setError(null);
                }
            }
        });
        //TODO: Contact Person Cell No
        edtCpCellNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.MOBILE);
                if (!result.isEmpty()) {
                    edtCpCellNo.setError(result);
                }else{
                    edtCpCellNo.setError(null);
                }
            }
        });
        //TODO: Email
        edtEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.EMAIL);
                if (!result.isEmpty()) {
                    edtEmail.setError(result);
                }else{
                    edtEmail.setError(null);
                }
            }
        });
        //TODO: Delivery Email
        edtDelEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.EMAIL);
                if (!result.isEmpty()) {
                    edtDelEmail.setError(result);
                }else{
                    edtDelEmail.setError(null);
                }
            }
        });
        //TODO: Contact Person Email
        edtCpEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String result = GlobleValues.ValidateByRegex(editable.toString(), GlobleValues.EMAIL);
                if (!result.isEmpty()) {
                    edtCpEmail.setError(result);
                }else{
                    edtCpEmail.setError(null);
                }
            }
        });
    }

    private void CallVolleyCountryStateCity(final String CompanyID,final String UnderID,final String DelType,final String Type){
        String Url = "";
        if (DelType.equals("0")){
            //TODO: 0 Means Normal Country , State, City
            if (Type.equals("0")){
                //TODO: Country
                pBarCountry.setVisibility(View.VISIBLE);
                Url = "GetCountryList";
            }else if (Type.equals("1")){
                //TODO: State
                pBarState.setVisibility(View.VISIBLE);
                Url = "GetStateList";
            }else if (Type.equals("2")){
                //TODO: City
                pBarCity.setVisibility(View.VISIBLE);
                Url = "GetCityList";
            }
        }else if (DelType.equals("1")){
            //TODO: 1 Means Delivery Country , State, City
            if (Type.equals("0")){
                //TODO: Country
                pBarDelCountry.setVisibility(View.VISIBLE);
                Url = "GetCountryList";
            }else if (Type.equals("1")){
                //TODO: State
                pBarDelState.setVisibility(View.VISIBLE);
                Url = "GetStateList";
            }else if (Type.equals("2")){
                //TODO: City
                pBarDelCity.setVisibility(View.VISIBLE);
                Url = "GetCityList";
            }
        }else if (DelType.equals("2")){
            //TODO: 1 Means Delivery Country , State, City
            if (Type.equals("0")){
                //TODO: Country
                pBarCpCountry.setVisibility(View.VISIBLE);
                Url = "GetCountryList";
            }else if (Type.equals("1")){
                //TODO: State
                pBarCpState.setVisibility(View.VISIBLE);
                Url = "GetStateList";
            }else if (Type.equals("2")){
                //TODO: City
                pBarCpCity.setVisibility(View.VISIBLE);
                Url = "GetCityList";
            }
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + Url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        if (DelType.equals("0")){
                            //TODO: 0 Means Normal Country , State, City
                            if (Type.equals("0")){
                                //TODO: Country
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CountryName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryName"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarCountry.setVisibility(View.GONE);
                                spnCountry.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtCountry.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        CountryID = map.get("ID");
                                        if (!CountryID.isEmpty() && CountryID != null){
                                             CallVolleyCountryStateCity(CompanyID,CountryID,"0","1");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("1")){
                                //TODO: State
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("StateName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarState.setVisibility(View.GONE);
                                spnState.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtState.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        StateID = map.get("ID");
                                        if (!StateID.isEmpty() && StateID != null){
                                            CallVolleyCountryStateCity(CompanyID,StateID,"0","2");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("2")){
                                //TODO: City
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CityID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CityName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarCity.setVisibility(View.GONE);
                                spnCity.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtCity.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        CityID = map.get("ID");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }
                        }

                        else if (DelType.equals("1"))

                        {
                            //TODO: 1 Means Delivery Country , State, City
                            if (Type.equals("0")){
                                //TODO: Country
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CountryName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryName"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarDelCountry.setVisibility(View.GONE);
                                spnDelCountry.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnDelCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtDelCountry.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        DelCountryID = map.get("ID");
                                        if (!DelCountryID.isEmpty() && DelCountryID != null){
                                            CallVolleyCountryStateCity(CompanyID,DelCountryID,"1","1");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("1")){
                                //TODO: State
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("StateName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarDelState.setVisibility(View.GONE);
                                spnDelState.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnDelState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtDelState.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        DelStateID = map.get("ID");
                                        if (!DelStateID.isEmpty() && DelStateID != null){
                                            CallVolleyCountryStateCity(CompanyID,DelStateID,"1","2");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("2")){
                                //TODO: City
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CityID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CityName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarDelCity.setVisibility(View.GONE);
                                spnDelCity.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnDelCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtDelCity.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        DelCityID = map.get("ID");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }
                        }

                        else if (DelType.equals("2"))

                        {
                            //TODO: 2 Means Contact Person Country , State, City
                            if (Type.equals("0")){
                                //TODO: Country
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CountryName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryName"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarCpCountry.setVisibility(View.GONE);
                                spnCpCountry.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnCpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtCpCountry.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        CpCountryID = map.get("ID");
                                        if (!CpCountryID.isEmpty() && CpCountryID != null){
                                            CallVolleyCountryStateCity(CompanyID,CpCountryID,"2","1");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("1")){
                                //TODO: State
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("StateName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("CountryID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CountryID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarCpState.setVisibility(View.GONE);
                                spnCpState.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnCpState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtCpState.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        CpStateID = map.get("ID");
                                        if (!CpStateID.isEmpty() && CpStateID != null){
                                            CallVolleyCountryStateCity(CompanyID,CpStateID,"2","2");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }else if (Type.equals("2")){
                                //TODO: City
                                JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                                if (jsonArrayResult.length() > 0) {
                                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                                        Map<String,String> map = new HashMap<>();
                                        map.put("ID",jsonArrayResult.getJSONObject(i).optString("CityID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityID"));
                                        map.put("Name",jsonArrayResult.getJSONObject(i).optString("CityName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("CityName"));
                                        map.put("UnderID",jsonArrayResult.getJSONObject(i).optString("StateID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StateID"));
                                        arrayList.add(map);
                                    }
                                }
                                pBarCpCity.setVisibility(View.GONE);
                                spnCpCity.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,arrayList,arrayList));
                                spnCpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        txtCpCity.setVisibility(View.GONE);
                                        Map<String,String> map = (Map<String,String>) adapterView.getAdapter().getItem(i);
                                        CpCityID = map.get("ID");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }
                        }
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
                params.put("CompanyID", CompanyID);
                params.put(Type.equals("1") ? "CountryID" : Type.equals("2") ? "StateID" : "" , UnderID);
                Log.d(TAG," parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyPartyListOrTransporterListByType(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String MasterID,final String MasterType,final String SpApplicable,final String Type){
        String Url = "";
        if (Type.equals("0")){
            // TODO: 0 means Party
            Url = "PartyList";
        }else if (Type.equals("1")){
            // TODO: 1 means Transporter
            Url = "getTransporterList";
            pBarTransport.setVisibility(View.VISIBLE);
        }else if (Type.equals("2")){
            // TODO: 2 means CP Designation
            Url = "CPDesignationList";
            pBarDesignation.setVisibility(View.VISIBLE);
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + Url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        if (Type.equals("0")){
                            //TODO: Party
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            if (jsonArrayResult.length() > 0) {
                                for (int i = 0; i < jsonArrayResult.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("ID", jsonArrayResult.getJSONObject(i).optString("PartyID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyID"));
                                    map.put("Name", jsonArrayResult.getJSONObject(i).optString("PartyName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyName"));
                                    map.put("Type", jsonArrayResult.getJSONObject(i).optString("Mobile") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Mobile"));
                                    map.put("PartyType", jsonArrayResult.getJSONObject(i).optString("PartyType") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyType"));
                                    arrayList.add(map);
                                }
                            }
                            pBarParty.setVisibility(View.GONE);
                            spnParty.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, arrayList, arrayList));
                            spnParty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    txtParty.setVisibility(View.GONE);
                                    Map<String, String> map = (Map<String, String>) adapterView.getAdapter().getItem(i);
                                    PartyID = map.get("ID");
                                    PartyType = map.get("PartyType");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }else if (Type.equals("1")) {
                            //TODO: Transporter
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            if (jsonArrayResult.length() > 0) {
                                for (int i = 0; i < jsonArrayResult.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("ID", jsonArrayResult.getJSONObject(i).optString("ID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ID"));
                                    map.put("Name", jsonArrayResult.getJSONObject(i).optString("Name") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Name"));
                                    map.put("Type", jsonArrayResult.getJSONObject(i).optString("GSTIN") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GSTIN"));
                                    arrayList.add(map);
                                }
                            }
                            pBarTransport.setVisibility(View.GONE);
                            spnTransport.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, arrayList, arrayList));
                            spnTransport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    txtTransport.setVisibility(View.GONE);
                                    Map<String, String> map = (Map<String, String>) adapterView.getAdapter().getItem(i);
                                    TransportID = map.get("ID");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }else if (Type.equals("2")) { // Designation
                            //TODO: Designation
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            if (jsonArrayResult.length() > 0) {
                                for (int i = 0; i < jsonArrayResult.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("ID", jsonArrayResult.getJSONObject(i).optString("ID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ID"));
                                    map.put("Name", jsonArrayResult.getJSONObject(i).optString("Name") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Name"));
                                    //map.put("Type", jsonArrayResult.getJSONObject(i).optString("Type") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GSTIN"));
                                    arrayList.add(map);
                                }
                            }
                            pBarDesignation.setVisibility(View.GONE);
                            spnDesignation.setAdapter(new CommonSearchSpinnerFilterableAdapter(context, arrayList, arrayList));
                            spnDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    txtDesignation.setVisibility(View.GONE);
                                    Map<String, String> map = (Map<String, String>) adapterView.getAdapter().getItem(i);
                                    DesignationID = map.get("ID");
                                    Designation = map.get("Name");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                //hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                if (Type.equals("0")){
                    //TODO: Party
                    pBarParty.setVisibility(View.GONE);
                }else if (Type.equals("1")) {
                    //TODO: Transporter
                    pBarTransport.setVisibility(View.GONE);
                }else if (Type.equals("2")) {
                    //TODO: Designation
                    pBarDesignation.setVisibility(View.GONE);
                }

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
                if(Type.equals("0")){
                    params.put("BranchID", BranchID);
                    params.put("MasterID", MasterID);
                    params.put("MasterType", MasterType);
                    params.put("SpApplicable", SpApplicable);

                    Log.d(TAG,"Party List parameters:"+params.toString());
                }else{
                    Log.d(TAG,"Transporter List parameters:"+params.toString());
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyCreateSubParty(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID
            ,final String PartyID,final String Name,final String IDName,final String Inactive
            ,final String Address1,final String Address2,final String Address3,final String CountryID,final String StateID,final String CityID,final String PIN,final String CellNo,final String PhoneNo,final String Email
            ,final String DelAddress1,final String DelAddress2,final String DelAddress3,final String DelCountryID,final String DelStateID,final String DelCityID,final String DelPIN,final String DelCellNo,final String DelPhoneNo,final String DelEmail
            ,final String PANNo,final String CrediLimit,final String CreditDays,final String GSTIN,final String GSTEffectiveFrom,final String TransporterID,final String gst_category
            ,final String PartyType,final String CPName,final String CPDesignation,final String CPDesignationID,final String CPAddress1,final String CPAddress2,final String CPAddress3
            ,final String CPCountryID,final String CPStateID,final String CPCityID,final String CPPIN,final String CPCellNo,final String CPPhoneNo,final String CPEmail,final String CPFax
            ,final String CPWhatsApp,final String CPRemarks ){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CreateSubParty", new Response.Listener<String>()
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
                params.put("Name", Name);
                params.put("IDName", IDName);
                params.put("Inactive", Inactive);
                params.put("Address1", Address1);
                params.put("Address2", Address2);
                params.put("Address3", Address3);
                params.put("CountryID", CountryID);
                params.put("StateID", StateID);
                params.put("CityID", CityID);
                params.put("PIN", PIN);
                params.put("CellNo", CellNo);
                params.put("PhoneNo", PhoneNo);
                params.put("Email", Email);
                params.put("DelAddress1", DelAddress1);
                params.put("DelAddress2", DelAddress2);
                params.put("DelAddress3", DelAddress3);
                params.put("DelCountryID", DelCountryID);
                params.put("DelStateID", DelStateID);
                params.put("DelCityID", DelCityID);
                params.put("DelPIN", DelPIN);
                params.put("DelCellNo", DelCellNo);
                params.put("DelPhoneNo", DelPhoneNo);
                params.put("DelEmail", DelEmail);
                params.put("PANNo", PANNo);
                params.put("CrediLimit", CrediLimit);
                params.put("CreditDays", CreditDays);
                params.put("GSTIN", GSTIN);
                params.put("GSTEffectiveFrom", GSTEffectiveFrom);
                params.put("TransporterID", TransporterID);
                params.put("gst_category", gst_category);

                params.put("PartyType", PartyType);
                params.put("CPName", CPName);
                params.put("CPDesignation", CPDesignation);
                params.put("CPDesignationID", CPDesignationID);
                params.put("CPAddress1", CPAddress1);
                params.put("CPAddress2", CPAddress2);
                params.put("CPAddress3", CPAddress3);
                params.put("CPCountryID", CPCountryID);
                params.put("CPStateID", CPStateID);
                params.put("CPCityID", CPCityID);
                params.put("CPPIN", CPPIN);
                params.put("CPCellNo", CPCellNo);
                params.put("CPPhoneNo", CPPhoneNo);
                params.put("CPEmail", CPEmail);
                params.put("CPFax", CPFax);
                params.put("CPWhatsApp", CPWhatsApp);
                params.put("CPRemarks", CPRemarks);

                Log.d(TAG,"Create Subparty parameters:"+params.toString());
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
        return super.onCreateOptionsMenu(menu);
    }

    private void SetSpinnerError(TextView txtView,String errorString){
        txtView.setError(errorString);
        txtView.setTextColor(Color.RED); //text color Red
        txtView.setVisibility(View.VISIBLE);
    }
}
