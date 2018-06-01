package com.singlagroup.otpverification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.singlagroup.MainActivity;
import com.singlagroup.RegistrationActivity;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.responsedatasets.ResponseDeviceRegistrationDataset;
import com.singlagroup.responsedatasets.ResponseOTPSendDataset;
import com.singlagroup.R;

import java.util.HashMap;
import java.util.Map;

import DatabaseController.CommanStatic;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

public class OTPVerificationActivity extends AppCompatActivity {
    private EditText edtOTP;
    private Button btnVerify;
    private LinearLayout LinearLayoutManual;
    private RelativeLayout RelativeLayoutAuto;
    private TextView txtTime,txtMobile,txtResend,txtChangeMobile,txtVerifyMannual;
    private ProgressBar progressBarOtp;
    String MessageOTP="";
    private ProgressDialog pDialog;
    private CountDownTimer mCountDownTimer;
    int i=0;
    String MobileNo="",OTP="",UserType="",EmpID="",CompanyID="";
    private static String TAG = OTPVerificationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_otp_verify);
        //TODO:Call google analytics
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCanceledOnTouchOutside(false);
        try {
            CompanyID = getIntent().getExtras().getString("CompanyID","");
            MobileNo = getIntent().getExtras().getString("Mobile","");
            OTP = getIntent().getExtras().getString("OTP","");
            UserType = getIntent().getExtras().getString("UserType","");
            EmpID = getIntent().getExtras().getString("EmpID","");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        initialization();
        StartTimer();
    }
    private void initialization() {
        LinearLayoutManual = (LinearLayout) findViewById(R.id.LinearManual);
        RelativeLayoutAuto = (RelativeLayout) findViewById(R.id.RelativeAuto);
        txtMobile = (TextView)findViewById(R.id.textview_editMobileNo);
        txtMobile.setText("+91-"+MobileNo);
        txtTime  = (TextView)findViewById(R.id.textview_time);
        progressBarOtp = (ProgressBar)findViewById(R.id.progressBarOtpDetect);
        edtOTP = (EditText) findViewById(R.id.editText_OTP);
        txtVerifyMannual = (TextView) findViewById(R.id.textview_VerifyMannual);
        txtResend = (TextView)findViewById(R.id.Text_OTP_Resend);
        txtChangeMobile = (TextView) findViewById(R.id.Text_ChangeMobileToVerify);
        btnVerify = (Button) findViewById(R.id.Button_Verify);
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text",messageText);
                edtOTP.setText(""+messageText);
                MessageOTP=messageText+"";
                if (MessageOTP.equals(OTP)){
                    RegistrationActivity obj = new RegistrationActivity();
                    String[] Str = obj.GetSharePreferenceRegistration(OTPVerificationActivity.this);
                    CallRetrofit(CompanyID,CommanStatic.ANDROID_DEVICE_UNIQUE_ID,CommanStatic.Model_No, CommanStatic.MAC_ID, CommanStatic.IMEI_NO,Str);
                }else{
                    Snackbar.make(edtOTP,"OTP doen't match! Please try again", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        btnVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String Otp=edtOTP.getText().toString();
                if(Otp.equals(OTP)){
                    RegistrationActivity obj = new RegistrationActivity();
                    String[] Str = obj.GetSharePreferenceRegistration(OTPVerificationActivity.this);
                    CallRetrofit(CompanyID,CommanStatic.ANDROID_DEVICE_UNIQUE_ID,CommanStatic.Model_No, CommanStatic.MAC_ID, CommanStatic.IMEI_NO,Str);
                }else{
                    Snackbar.make(edtOTP,"OTP doen't match! Please try again", Snackbar.LENGTH_LONG).show();
                }

            }
        });
        txtResend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRetrofitOTPSend(CompanyID,MobileNo,UserType,EmpID);
            }
        });
        txtChangeMobile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            //edtMobile.setError("Change your mobile no. then again Register & Verify");
                mCountDownTimer.onFinish();
                Toast.makeText(getApplicationContext(),"Change your mobile no. then again Register & Verify", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtVerifyMannual.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.onFinish();
                RelativeLayoutAuto.setVisibility(View.GONE);
                LinearLayoutManual.setVisibility(View.VISIBLE);
            }
        });
    }
    private void StartTimer(){
        progressBarOtp.setMax(120);
        progressBarOtp.setProgress(i);
        mCountDownTimer=new CountDownTimer(120000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i);
                String v = String.format("%02d", millisUntilFinished/60000);
                int va = (int)( (millisUntilFinished%60000)/1000);
                txtTime.setText(v+":"+ String.format("%02d",va));
                progressBarOtp.setProgress(i);
                i++;
            }
            @Override
            public void onFinish() {
            }
        };
        mCountDownTimer.start();
    }
    //TODO: Async Registration Class
    private void CallRetrofit(String CompanyID,String DeviceUniqueID,String modelNo, String macID, String ImeiNo, final String[] Str){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("CompanyID", CompanyID);
        params.put("DeviceUniqueID", DeviceUniqueID);
        params.put("modelNo", modelNo);
        params.put("macID", macID);
        params.put("SerialNo", CommanStatic.SERIAL_NO);
        params.put("ImeiNo", ImeiNo);
        params.put("CompanyName", Str[0]);
        params.put("Name", Str[1]);
        params.put("Designation", Str[2]);
        params.put("Address", Str[3]);
        params.put("City", Str[4]);
        params.put("State", Str[5]);
        params.put("Country", Str[6]);
        params.put("Pincode", Str[7]);
        params.put("MobileNo", Str[8]);
        params.put("LandLineNo", Str[9]);
        params.put("FaxNo", Str[10]);
        params.put("EmailID", Str[11]);
        params.put("CountryID", Str[12]);
        params.put("CityID", Str[13]);
        params.put("StateID", Str[14]);
        params.put("DivisionID", Str[16]);
        params.put("DivisionName", Str[17]);
        params.put("EmpID", Str[18]);
        params.put("UserType", Str[19]);
        params.put("AppType", "1");
        Log.e(TAG,"Parameters:"+params.toString());
        Call<ResponseDeviceRegistrationDataset> call = apiService.insertUserRegistration(params);
        call.enqueue(new Callback<ResponseDeviceRegistrationDataset>() {
            @Override
            public void onResponse(Call<ResponseDeviceRegistrationDataset> call, retrofit2.Response<ResponseDeviceRegistrationDataset> response) {
                try {
                    //if(response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            Map<String, String> result = response.body().getResult();
                            if (result.get("Status").equals("1")) {
                                mCountDownTimer.cancel();
                                MessageDialog messageDialog=new MessageDialog();
                                messageDialog.MessageDialog(OTPVerificationActivity.this,"","","Registration is Successfull");
                                //Toast.makeText(getApplicationContext(), "Registration is Successfull...", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                MessageDialog messageDialog=new MessageDialog();
                                messageDialog.MessageDialog(OTPVerificationActivity.this,"","","Registration is unsuccessfull !!! Please try again..");
                                //Toast.makeText(getApplicationContext(), "Registration is unsuccessfull !!! Please try again..", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
//                    }else {
//                        Toast.makeText(getApplicationContext(), "Registration is unsuccessfull !!! Please try again..", Toast.LENGTH_LONG).show();
//                    }
                }catch (Exception e){
                    Log.e(TAG,"Exception Register :"+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(OTPVerificationActivity.this,"Exception","",e.getMessage().toString());
                }
                hidepDialog();
            }
            @Override
            public void onFailure(Call<ResponseDeviceRegistrationDataset> call, Throwable t) {
                Log.e(TAG,"Failure:"+t.toString());
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(OTPVerificationActivity.this,"Failure","",t.toString());
                //Toast.makeText(getApplicationContext(),"Registration Failure", Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void CallRetrofitOTPSend(final String CompanyID,final String Mobile, final String UserType,final  String EmpID){

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
                            Intent intent=new Intent(OTPVerificationActivity.this,OTPVerificationActivity.class);
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
                            messageDialog.MessageDialog(OTPVerificationActivity.this,"","",msg);
                            //Snackbar.make(, "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"OTP Send Exception: "+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(OTPVerificationActivity.this,"","",e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseOTPSendDataset> call, Throwable t) {
                Log.e(TAG,"OTP Send Failure: "+t.toString());
                //Toast.makeText(getApplicationContext(),"OTP Send Failure",Toast.LENGTH_LONG).show();
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(OTPVerificationActivity.this,"","",t.toString());
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }

}

