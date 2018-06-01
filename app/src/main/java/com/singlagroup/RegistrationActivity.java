package com.singlagroup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.singlagroup.adapters.CityFilterableAdapter;
import com.singlagroup.adapters.CountryFilterableAdapter;
import com.singlagroup.adapters.CustomSpinnerAdapter;
import com.singlagroup.adapters.DivisionEmpFilterableAdapter;
import com.singlagroup.adapters.StateFilterableAdapter;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SearchableSpinner;
import com.singlagroup.customwidgets.ValidationMethods;
import com.singlagroup.datasets.CityDataset;
import com.singlagroup.datasets.CompanyDataset;
import com.singlagroup.datasets.CountryDataset;
import com.singlagroup.datasets.DivisionEmpDataset;
import com.singlagroup.datasets.StateDataset;
import com.singlagroup.otpverification.OTPVerificationActivity;
import com.singlagroup.responsedatasets.ResponseCityListDataset;
import com.singlagroup.responsedatasets.ResponseCountryListDataset;
import com.singlagroup.responsedatasets.ResponseDivisionListDataset;
import com.singlagroup.responsedatasets.ResponseOTPSendDataset;
import com.singlagroup.responsedatasets.ResponseStateListDataset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DatabaseController.CommanStatic;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;

public class RegistrationActivity extends AppCompatActivity {

    private Spinner spnUserType;
    private LinearLayout lLEmpForm,lLOtherForm;
    private EditText edtEmpID,edtEmpMobile,edtFirmName,edtName,edtDesignation,edtAddress,edtPincode,edtMobile,edtLandlineNo,edtFax,edtEmail,edtState,edtCity,edtCountry;
    private Button btnRegister;
    private Spinner spnState,spnCountry,spnDivision,spnDivision1;
    private SearchableSpinner spnCity;
    private ProgressDialog pDialog;
    private ProgressBar pBarCity,pBarState,pBarCountry,pBarDivision,pBarDivision1;
    CountryFilterableAdapter countryAdapter;
    StateFilterableAdapter StateAdapter;
    CityFilterableAdapter cityAdapter;
    private String UserType;
    private int userType;
    String CountryID="",CountryName="",StateID="",StateName="",CityID="",CityName="",DivisionID="",DivisionName="",CompanyID="",CompanyName="";
    private static String TAG = RegistrationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_registration);
        Initialization();
        UserTypeMethod();
    }
    private void Initialization(){
        spnUserType = (Spinner) findViewById(R.id.Spinner_UserType);
        lLEmpForm = (LinearLayout) findViewById(R.id.LinearLayout_Emp);
        lLOtherForm = (LinearLayout) findViewById(R.id.LinearLayout_OtherForm);
        btnRegister = (Button) findViewById(R.id.Button_Register);
        edtEmpID = (EditText)findViewById(R.id.EditText_EmpID);
        edtEmpMobile = (EditText)findViewById(R.id.EditText_RegMobileNo);
        spnDivision = (Spinner) findViewById(R.id.Spinner_Division);
        pBarDivision = (ProgressBar) findViewById(R.id.progressBarDivision);
        spnDivision1 = (Spinner) findViewById(R.id.Spinner_Division1);
        pBarDivision1 = (ProgressBar) findViewById(R.id.progressBarDivision1);
        edtFirmName = (EditText)findViewById(R.id.FirmName);
        edtName = (EditText)findViewById(R.id.Name);
        edtDesignation = (EditText)findViewById(R.id.Designation);
        edtAddress = (EditText)findViewById(R.id.Address);
        edtCity = (EditText) findViewById(R.id.EditText_searchFilterCity);
        spnCity = (SearchableSpinner) findViewById(R.id.spinner_City);
        pBarCity = (ProgressBar) findViewById(R.id.progressBarCity);
        edtState = (EditText) findViewById(R.id.EditText_searchFilterState);
        spnState = (Spinner) findViewById(R.id.Spinner_State);
        pBarState = (ProgressBar) findViewById(R.id.progressBarState);
        edtCountry = (EditText) findViewById(R.id.EditText_country);
        spnCountry = (Spinner) findViewById(R.id.Spinner_Country);
        pBarCountry = (ProgressBar) findViewById(R.id.progressBarCountry);
        edtPincode = (EditText)findViewById(R.id.pincode);
        edtMobile = (EditText)findViewById(R.id.mobile);
        edtLandlineNo = (EditText)findViewById(R.id.LandlineNo);
        edtFax = (EditText)findViewById(R.id.Fax);
        edtEmail = (EditText)findViewById(R.id.email);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserType.equals("Employee")) {
                    attemptEmpRegistration();
                }else{
                    attemptOtherRegistration();
                }
            }
        });
        //Todo: Call Division
        CallRetrofitDivisionList();
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    CallRetrofitDivisionList();
                } else {
                    MessageDialog.MessageDialog(RegistrationActivity.this,"",status);
                }
            }
        });

        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCanceledOnTouchOutside(false);
    }
    private void UserTypeMethod(){
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Select User Type...");
        arrayList.add("Employee");
        arrayList.add("Agent");
        arrayList.add("Customer");
        arrayList.add("Vendor");
        arrayList.add("Jobber");
		arrayList.add("Transporter");
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(RegistrationActivity.this,arrayList);
        if(spnUserType!=null) {
            spnUserType.setAdapter(adapter);
            spnUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    UserType = parent.getItemAtPosition(position).toString();

                    if (position == 1) {
                        userType = position;
                        if(lLEmpForm!=null && btnRegister!=null){
                            lLEmpForm.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            lLOtherForm.setVisibility(View.GONE);
                        }
                    } else if (position > 1) {
                        userType = position;
                        if(lLOtherForm!=null && btnRegister!=null){
                            lLOtherForm.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            lLEmpForm.setVisibility(View.GONE);
                        }
                    }
                    //Toast.makeText(parent.getContext(), "userType: " + userType, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        String mail=GetMailID();
        edtEmail.setText(""+mail);
        setDetails();
    }
    private String GetMailID() {
        String possibleEmail="";
        try {
            Account[] accounts = AccountManager.get(RegistrationActivity.this).getAccountsByType("com.google");
            String myEmailid = accounts[0].toString();
            Log.d("My email id that i want", myEmailid);
            for (Account account : accounts) {
                possibleEmail = account.name;
                Log.d("EmailID:", possibleEmail);
            }
        }catch (Exception e){
            Log.e("Error","Mail Exception:"+e.getMessage());
        }
        return possibleEmail;
    }
    private void attemptEmpRegistration() {
        edtEmpID.setError(null);
        edtEmpMobile.setError(null);

        String EmpID = edtEmpID.getText().toString();
        String EmpMobile = edtEmpMobile.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //TODO Check for a Name Validation
        if (TextUtils.isEmpty(EmpID)) {
            edtEmpID.setError("Employee Id is mandatory!!!");
            focusView = edtEmpID;
            cancel = true;
        }
        if (TextUtils.isEmpty(EmpMobile)) {
            edtEmpMobile.setError("Mobile No is mandatory!!!");
            focusView = edtEmpMobile;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //TODO: Async Registration Execute
            if (DivisionID.isEmpty()) {
                //Snackbar.make(edtEmpMobile,"Division selection is mandatory!!!",Snackbar.LENGTH_LONG).show();
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(RegistrationActivity.this,"","Division","Division selection is mandatory!!!");
            }
            if(!DivisionID.isEmpty() && !DivisionName.isEmpty()){
                String empID=edtEmpID.getText().toString().trim();
                String empMobile=edtEmpMobile.getText().toString().trim();
                if (empMobile.length()!=10) {
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(RegistrationActivity.this,"","Mobile No ","Do not enter mobile no. less than 10 digits...");
                }else{
                    SharePreferenceSession("", "", "", "", "", "", "", "", empMobile, "", "", "", "", "", "", DivisionID, DivisionName, empID, userType);
//                String[] Str=GetSharePreferenceRegistration(RegistrationActivity.this);
                    //TODO: Call OTP Verification
                    //CallRetrofitEmpRegistration(CommanStatic.Model_No,CommanStatic.MAC_ID,CommanStatic.IMEI_NO,Str);
                    if (!CompanyID.isEmpty() && !UserType.isEmpty() && !empMobile.isEmpty()) {
                        CallRetrofitOTPSend(CompanyID,empMobile, String.valueOf(userType), empID);
                    }
                }
            }
        }
    }
    private void attemptOtherRegistration() {

        // Reset errors.
        edtFirmName.setError(null);
        edtName.setError(null);
        edtDesignation.setError(null);
        edtAddress.setError(null);
        edtCity.setError(null);
        edtState.setError(null);
        edtCountry.setError(null);
        edtPincode.setError(null);
        edtMobile.setError(null);
        edtEmail.setError(null);

        // Store values at the time of the login attempt.
        String firmName = edtFirmName.getText().toString();
        String name = edtName.getText().toString();
        String designation = edtDesignation.getText().toString();
        String address = edtAddress.getText().toString();
        String city = CityName;
        String state = StateName;
        String country = CountryName;
        String pincode = edtPincode.getText().toString();
        String mobile = edtMobile.getText().toString().trim();
        String landline = edtLandlineNo.getText().toString();
        String fax = edtFax.getText().toString();
        String email = edtEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //TODO Check for a Name Validation
        if (TextUtils.isEmpty(firmName)) {
            edtFirmName.setError("Company/Firm name is mandatory!!!");
            focusView = edtFirmName;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name is mandatory!!!");
            focusView = edtName;
            cancel = true;
        }
        //TODO Check for a Destination Validation
        if (TextUtils.isEmpty(designation)) {
            edtDesignation.setError("Designation is mandatory!!!");
            focusView = edtDesignation;
            cancel = true;
        }
        //TODO Check for a Address Validation
        if (TextUtils.isEmpty(address)) {
            edtAddress.setError("Address is mandatory!!!");
            focusView = edtAddress;
            cancel = true;
        }
        //TODO Check for a City Validation
        if (TextUtils.isEmpty(city) && CityID.equals("")) {
            edtCity.setError("City is mandatory!!!");
            focusView = edtCity;
            cancel = true;
        }
        //TODO Check for a State Validation
        if (TextUtils.isEmpty(state) && StateID.equals("")) {
            edtState.setError("State is mandatory!!!");
            focusView = edtState;
            cancel = true;
        }
        //TODO Check for a Country Validation
        if (TextUtils.isEmpty(country) && CountryID.equals("")) {
            edtCountry.setError("Country is mandatory!!!");
            focusView = edtCountry;
            cancel = true;
        }
        //TODO Check for a Pincode Validation
        if (TextUtils.isEmpty(pincode)) {
            edtPincode.setError("Pincode is mandatory!!!");
            focusView = edtPincode;
            cancel = true;
        }
        //TODO Check for a mobile Validation
        if (TextUtils.isEmpty(mobile)) {
            edtMobile.setError("Mobile No is mandatory!!!");
            focusView = edtMobile;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email ID is mandatory!!!");
            focusView = edtEmail;
            cancel = true;
        }
        // Check for a valid email address.
        if (!email.isEmpty() && !ValidationMethods.isEmailValid(email)) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            focusView = edtEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //TODO: Async Registration Execute
            if(CountryID.isEmpty()){
                Snackbar.make(edtCountry,"Country selection is mandatory!!!", Snackbar.LENGTH_LONG).show();
            }else if(StateID.isEmpty()){
                Snackbar.make(edtState,"State selection is mandatory!!!", Snackbar.LENGTH_LONG).show();
            } else if(CityID.isEmpty()){
                Snackbar.make(edtCity,"City selection is mandatory!!!", Snackbar.LENGTH_LONG).show();
            }
            if(!CityID.isEmpty() && !city.isEmpty() && !StateID.isEmpty() && !state.isEmpty() && !CountryID.isEmpty() && !country.isEmpty()){

                if (mobile.length()!=10) {
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(RegistrationActivity.this,"","Mobile No","Do not enter mobile no. less than 10 digits...");
                }else{
                    if(!CompanyID.isEmpty() && !CompanyName.isEmpty() && !DivisionID.isEmpty() && !DivisionName.isEmpty()) {
                        SharePreferenceSession(firmName, name, designation, address, city, state, country, pincode, mobile, landline, fax, email, CountryID, StateID, CityID, DivisionID, DivisionName, "", userType);
                        if (!UserType.isEmpty() && !mobile.isEmpty()) {
                            CallRetrofitOTPSend(CompanyID,mobile, String.valueOf(userType), "");
                        }
                    }else{
                        MessageDialog messageDialog=new MessageDialog();
                        messageDialog.MessageDialog(RegistrationActivity.this,"","Division","Company/Division selection is mandatory!!!");
                    }
                }
                //CallRetrofitOtherRegistration(ModelNo,MacID,ImeiNo,Str);
            }else{

                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(RegistrationActivity.this,"","","Please fill all mandatory fields!!!");
                //Snackbar.make(edtCity,"Please fill all mandatory fields!!!",Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }
    private void getDetails(){
        String FirmName = edtFirmName.getText().toString();
        String Name = edtName.getText().toString();
        String Designation = edtDesignation.getText().toString();
        String Address = edtAddress.getText().toString();
        String city = CityName;
        String state = StateName;
        String country = CountryName;
        String pincode = edtPincode.getText().toString();
        String mobile = edtMobile.getText().toString();
        String landline = edtLandlineNo.getText().toString();
        String fax = edtFax.getText().toString();
        String email = edtEmail.getText().toString();
        SharePreferenceSession(FirmName,Name,Designation,Address,city,state,country,pincode,mobile,landline,fax,email,CountryID,StateID,CityID,"","","",userType);
        String[] Str=GetSharePreferenceRegistration(getApplicationContext());
        //TODO: Get DeviceInfo
        MainActivity obj=new MainActivity();
        String ModelNo = obj.GetSharePreferenceDeviceInfo(RegistrationActivity.this).getIMEINo();
        String MacID = obj.GetSharePreferenceDeviceInfo(RegistrationActivity.this).getIMEINo();
        String ImeiNo = obj.GetSharePreferenceDeviceInfo(RegistrationActivity.this).getIMEINo();

        //CallRetrofitTempServer(ModelNo,MacID,ImeiNo,Str);
    }
    private void setDetails(){
        try {
            String[] str = GetSharePreferenceRegistration(getApplicationContext());
            edtFirmName.setText(str[0]);
            edtName.setText(str[1]);
            edtDesignation.setText(str[2]);
            edtAddress.setText(str[3]);
            CityName=str[4];
            StateName=str[5];
            edtPincode.setText(str[7]);
            edtMobile.setText(str[8]);
            edtLandlineNo.setText(str[9]);
            edtFax.setText(str[10]);
            //edtEmail.setText(str[11]);
            StateID=str[13];
            CityID=str[14];
        }catch (Exception e){
            Log.e("Error","Registration Details Exception"+e.getMessage());
        }
    }
    private void RefreshedFilledForm(){
        edtFirmName.setText("");
        edtName.setText("");
        edtDesignation.setText("");
        edtAddress.setText("");
        CityName="";
        StateName="";
        edtPincode.setText("");
        edtMobile.setText("");
        edtLandlineNo.setText("");
        edtFax.setText("");
        //edtEmail.setText(str[11]);
        StateID="";
        CityID="";
    }
    private void CallRetrofitDivisionList(){
        pBarDivision.setVisibility(View.VISIBLE);
        pBarDivision1.setVisibility(View.VISIBLE);
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseDivisionListDataset> call = apiService.getDivisionList();
        call.enqueue(new Callback<ResponseDivisionListDataset>() {
            @Override
            public void onResponse(Call<ResponseDivisionListDataset> call, retrofit2.Response<ResponseDivisionListDataset> response) {
                int Status=response.body().getStatus();
                String msg=response.body().getMsg();
                if(Status==1) {
                    ArrayList<CompanyDataset> datasetCompany = response.body().getResult();
                    int c=0;
                    ArrayList<Map<String,String>> temp=new ArrayList<Map<String,String>>();
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("CompanyID","");
                    map.put("CompanyName","Company");
                    map.put("DivisionID","");
                    map.put("DivisionName","Division");
                    //DivisionEmpDataset divisionDataset=new DivisionEmpDataset("","Select Company(Division)");
                    temp.add(c,map);
                    for(int i=0;i<datasetCompany.size();i++){
                        ArrayList<DivisionEmpDataset> datasetList = datasetCompany.get(i).getDivisionInfo();
                        for (int j=0;j<datasetList.size();j++) {
                            c++;
                            map = new HashMap<String, String>();
                            map.put("CompanyID", datasetCompany.get(i).getID());
                            map.put("CompanyName", datasetCompany.get(i).getCompanyName());
                            map.put("DivisionID", datasetList.get(j).getDivisionID());
                            map.put("DivisionName", datasetList.get(j).getDivision());
                            //divisionDataset=new DivisionEmpDataset(datasetList.get(i).getDivisionID(),datasetList.get(i).getDivisionName());
                            temp.add(c, map);
                        }
                    }
                    DivisionEmpFilterableAdapter DivAdapter= new DivisionEmpFilterableAdapter(RegistrationActivity.this,temp,temp);
                    spnDivision.setAdapter(DivAdapter);
                    spnDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                            Map<String,String> map=(Map<String,String>)parent.getAdapter().getItem(position);
                            if(position>0)
                            CompanyID=map.get("CompanyID");
                            CompanyName=map.get("CompanyName");
                            DivisionID=map.get("DivisionID");
                            DivisionName=map.get("DivisionName");
                            RefreshedFilledForm();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    spnDivision1.setAdapter(DivAdapter);
                    spnDivision1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                            Map<String,String> map=(Map<String,String>)parent.getAdapter().getItem(position);
                            if(position>0)
                            CompanyID=map.get("CompanyID");
                            CompanyName=map.get("CompanyName");
                            DivisionID=map.get("DivisionID");
                            DivisionName=map.get("DivisionName");
                            RefreshedFilledForm();
                            if(!CompanyID.isEmpty())
                            CallRetrofitCountryList(CompanyID);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }else{
                    MessageDialog.MessageDialog(RegistrationActivity.this,"",""+msg);
                }
                pBarDivision.setVisibility(View.GONE);
                pBarDivision1.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseDivisionListDataset> call, Throwable t) {
                Log.e(TAG," Division Failure: "+t.toString());
                MessageDialog.MessageDialog(RegistrationActivity.this,"Api Failure","Division Failure");
                hidepDialog();
            }
        });
    }
    private void CallRetrofitCountryList(final String CompanyID){
        pBarCountry.setVisibility(View.VISIBLE);
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("CompanyID", CompanyID);
        System.out.println("Parameters:"+params.toString());
        Call<ResponseCountryListDataset> call = apiService.getCountryList(params);
        call.enqueue(new Callback<ResponseCountryListDataset>() {
            @Override
            public void onResponse(Call<ResponseCountryListDataset> call, retrofit2.Response<ResponseCountryListDataset> response) {
                int Status=response.body().getStatus();
                String msg=response.body().getMsg();
                if(Status==1) {
                    ArrayList<CountryDataset> datasetList = response.body().getResult();

                    RegistrationActivity.this.countryAdapter = new CountryFilterableAdapter(RegistrationActivity.this,datasetList,datasetList);
                    spnCountry.setAdapter(countryAdapter);
                    //spnCountry.setSelection(1);
                    spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                            CountryDataset dataset=(CountryDataset)parent.getAdapter().getItem(position);
                            CountryID=dataset.getCountryID();
                            CountryName=dataset.getCountryName();
                            //Toast.makeText(getApplicationContext(),"CountryName:"+CountryName,Toast.LENGTH_SHORT).show();
                            if (!CountryID.isEmpty()) {
                                CallRetrofitStateList(CompanyID,CountryID);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }else{
                    MessageDialog.MessageDialog(RegistrationActivity.this,"",""+msg);
                }
                pBarCountry.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseCountryListDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog.MessageDialog(RegistrationActivity.this,"Country Failure",""+t.toString());
                hidepDialog();
            }
        });
    }
    private void CallRetrofitStateList(final String CompanyID, String CountryID){
        pBarState.setVisibility(View.VISIBLE);
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
                            int c = 0;
                            ArrayList<StateDataset> temp = new ArrayList<>();
                            StateDataset stateDataset = new StateDataset("", "Select State Name", "");
                            temp.add(c, stateDataset);
                            for (int i = 0; i < datasetList.size(); i++) {
                                c++;
                                stateDataset = new StateDataset(datasetList.get(i).getStateID(), datasetList.get(i).getStateName(), datasetList.get(i).getCountryID());
                                temp.add(c, stateDataset);
                            }
                            RegistrationActivity.this.StateAdapter = new StateFilterableAdapter(RegistrationActivity.this, temp, temp);
                            spnState.setAdapter(StateAdapter);
                            spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    StateDataset dataset = (StateDataset) parent.getAdapter().getItem(position);
                                    StateID = dataset.getStateID();
                                    StateName = dataset.getStateName();
                                    //Toast.makeText(getApplicationContext(),"StateName:"+StateName,Toast.LENGTH_SHORT).show();
                                    if (!StateID.isEmpty()) {
                                        CallRetrofitCityList(CompanyID,StateID);
                                    }
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
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(RegistrationActivity.this,"Exception","State",e.getMessage());
                }
                pBarState.setVisibility(View.GONE);
                edtState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseStateListDataset> call, Throwable t) {
                Log.e(TAG,"State Failure: "+t.toString());
                Toast.makeText(getApplicationContext(),"State Failure",Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    //TODO: Async City Class
    private void CallRetrofitCityList(String CompanyID,String StateID){
        pBarCity.setVisibility(View.VISIBLE);
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
                            pBarCity.setVisibility(View.GONE);
                            RegistrationActivity.this.cityAdapter = new CityFilterableAdapter(RegistrationActivity.this, datasetList, datasetList);
                            spnCity.setAdapter(cityAdapter);
                            spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    CityID = datasetList.get(position).getCityID();
                                    CityName = datasetList.get(position).getCityName();
                                    //Toast.makeText(getApplicationContext(),"CityName:"+CityName+"\nCityID:"+CityID,Toast.LENGTH_SHORT).show();
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
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(RegistrationActivity.this,"Exception","City",e.getMessage());
                }
                pBarCity.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseCityListDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                Toast.makeText(getApplicationContext(),"City Reg Failure",Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void CallRetrofitOTPSend(final String CompanyID, final String Mobile, final String UserType, final  String EmpID){

        showpDialog();
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("CompanyID", CompanyID);
        params.put("Mobile", Mobile);
        params.put("UserType", UserType);
        params.put("EmpID", EmpID);
        Log.e(TAG,params.toString());
        Call<ResponseOTPSendDataset> call = apiService.requestOTP(params);
        call.enqueue(new Callback<ResponseOTPSendDataset>() {
            private static final long MIN_CLICK_INTERVAL = 20000; //in millis
            private long lastClickTime = 0;
            @Override
            public void onResponse(Call<ResponseOTPSendDataset> call, retrofit2.Response<ResponseOTPSendDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        Map<String,String> map = response.body().getResult();
                        if (Status == 1) {
                            Log.e(TAG,"OTP API Call:"+map.get("OTP"));
//                            long currentTime = SystemClock.elapsedRealtime();
//                            if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
//                                lastClickTime = currentTime;
                                Intent intent=new Intent(RegistrationActivity.this,OTPVerificationActivity.class);
                                intent.putExtra("CompanyID",CompanyID);
                                intent.putExtra("Mobile",Mobile);
                                intent.putExtra("UserType",UserType);
                                intent.putExtra("EmpID",EmpID);
                                intent.putExtra("OTP",map.get("OTP"));
                                startActivity(intent);
                                finish();
                            //}
                        } else {
                            MessageDialog messageDialog=new MessageDialog();
                            messageDialog.MessageDialog(RegistrationActivity.this,"","",msg);
                            //Snackbar.make(, "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"OTP Send Exception: "+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(RegistrationActivity.this,"","",e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseOTPSendDataset> call, Throwable t) {
                Log.e(TAG,"OTP Send Failure: "+t.toString());
                //Toast.makeText(getApplicationContext(),"OTP Send Failure",Toast.LENGTH_LONG).show();
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(RegistrationActivity.this,"OTPVerificationActivity","",t.toString());
                hidepDialog();
            }
        });
    }
    private void showpDialog() {
        pDialog.show();
    }
    private void hidepDialog() {
        pDialog.dismiss();
    }
    public void SharePreferenceSession(String FirmName,String name,String designation,String address,String city,String state,String country,String pincode,String mobile,String landline,String fax,String email,String countryID,String stateID,String cityID,String DivisionID,String DivisionName,String EmpID,int UserType){
        String[] str = GetSharePreferenceRegistration(RegistrationActivity.this);
        int count = Integer.valueOf(str[15]);
        int inc=count++;
        SharedPreferences prefs = getSharedPreferences("MyPrefReg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("FirmName", FirmName);
        editor.putString("name", name);
        editor.putString("designation", designation);
        editor.putString("address", address);
        editor.putString("city", city);
        editor.putString("state", state);
        editor.putString("country", country);
        editor.putString("pincode", pincode);
        editor.putString("mobile", mobile);
        editor.putString("landline", landline);
        editor.putString("fax", fax);
        editor.putString("email", email);
        editor.putString("countryID", countryID);
        editor.putString("stateID", stateID);
        editor.putString("cityID", cityID);
        editor.putInt("RegFailure", inc);
        editor.putString("DivisionID", DivisionID);
        editor.putString("DivisionName", DivisionName);
        editor.putString("EmpID", EmpID);
        editor.putInt("UserType", UserType);
        editor.commit();
    }
    public String[] GetSharePreferenceRegistration(Context context) {
        String[] Str=new String[20];
        SharedPreferences pref = context.getSharedPreferences("MyPrefReg", MODE_PRIVATE);
        Str[0]=pref.getString("FirmName", "");
        Str[1]=pref.getString("name", "");
        Str[2]=pref.getString("designation", "");
        Str[3]=pref.getString("address", "");
        Str[4]=pref.getString("city", "");
        Str[5]=pref.getString("state", "");
        Str[6]=pref.getString("country", "");
        Str[7]=pref.getString("pincode", "");
        Str[8]=pref.getString("mobile", "");
        Str[9]=pref.getString("landline", "");
        Str[10]=pref.getString("fax", "");
        Str[11]=pref.getString("email", "");
        Str[12]=pref.getString("countryID", "");
        Str[13]=pref.getString("stateID", "");
        Str[14]=pref.getString("cityID", "");
        Str[15]=String.valueOf(pref.getInt("RegFailure", 0));
        Str[16]=pref.getString("DivisionID", "");
        Str[17]=pref.getString("DivisionName", "");
        Str[18]=pref.getString("EmpID","");
        Str[19]=String.valueOf(pref.getInt("UserType",0));
        return Str;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Log.d("BackKey","Back key pressed then restart app");
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }

}
