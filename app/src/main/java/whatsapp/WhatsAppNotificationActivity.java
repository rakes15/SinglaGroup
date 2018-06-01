package whatsapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;
import com.singlagroup.customwidgets.PDFThumbnail;
import com.zopim.android.sdk.model.Attachment;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import report.showroomitemcheck.model.Group;
import services.NetworkUtils;
import whatsapp.adapter.ImageAdapter;

/**
 * Created by rakes on 02-Dec-17.
 */

public class WhatsAppNotificationActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private WebView mWebView;
    private RadioGroup rgIsGroup,rgScheduleTime,rgMsgType;
    private TextInputLayout edtConverNumber,edtSrNumber, edtMessage,edtNTime,edtFileDescription;
    private TextView txtSchlDate,txtSchlTime,txtAttachFile;
    private LinearLayout llScheduleTime,llAttachment;
    private ImageView image;
    private Button btnSubmit;
    private ProgressDialog progressDialog;
    private String isGroup="",ConverNumber="",SRNumber="",NTime="",ScheduleTime="",msgType="",text="",description="",fileName="",attachment="";
    private String date="",time="";
    private Dialog dialog;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private static String TAG = WhatsAppNotificationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_push_notification);
        Initialization();
        ModulePermission();
        Validation();
        ClickEvent();
    }
    private void Initialization(){
        this.context = WhatsAppNotificationActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO:  WebView
        mWebView = (WebView) findViewById(R.id.web_view);
        //TODO: TextInputLayout
        edtConverNumber = (TextInputLayout) findViewById(R.id.editText_conversion_number);
        edtSrNumber = (TextInputLayout) findViewById(R.id.editText_sr_number);
        edtMessage = (TextInputLayout) findViewById(R.id.editText_text_message);
        edtNTime = (TextInputLayout) findViewById(R.id.editText_number_of_times);
        edtFileDescription = (TextInputLayout) findViewById(R.id.editText_file_description);
        //TODO: TextView
        txtSchlDate = (TextView) findViewById(R.id.text_date);
        txtSchlTime = (TextView) findViewById(R.id.text_time);
        txtAttachFile = (TextView) findViewById(R.id.text_attach_file);
        //TODO: Radio Group
        rgIsGroup = (RadioGroup) findViewById(R.id.radio_group_is_group);
        rgScheduleTime = (RadioGroup) findViewById(R.id.radio_group_schedule_time);
        rgMsgType = (RadioGroup) findViewById(R.id.radio_group_message_type);
        //todo: Linear Layout
        llScheduleTime = (LinearLayout) findViewById(R.id.Linear_schedule_time);
        llAttachment = (LinearLayout) findViewById(R.id.Linear_attachment);
        //TODO: Button
        image = (ImageView) findViewById(R.id.image_view);
        //TODO: Button
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        //TODO: Progress Dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
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

            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void Validation(){
        //TODO: Is Group
        //if((RadioButton)findViewById(R.id.R.id.radio_button_is_group_yes).is)
        if (rgIsGroup.getCheckedRadioButtonId() == R.id.radio_button_is_group_yes){
            isGroup = "1";
            edtConverNumber.getEditText().setText("");
            edtConverNumber.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
            edtConverNumber.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
        }else if (rgIsGroup.getCheckedRadioButtonId() == R.id.radio_button_is_group_no){
            isGroup = "0";
            edtConverNumber.getEditText().setText("");
            edtConverNumber.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
            edtConverNumber.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        }
        rgIsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_is_group_yes){
                    isGroup = "1";
                    edtConverNumber.getEditText().setText("");
                    edtConverNumber.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                    edtConverNumber.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
                }else if (checkedId == R.id.radio_button_is_group_no){
                    isGroup = "0";
                    edtConverNumber.getEditText().setText("");
                    edtConverNumber.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                    edtConverNumber.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                }
            }
        });
        //TODO: Schedule Time
        if (rgScheduleTime.getCheckedRadioButtonId() == R.id.radio_button_schedule_time_now){
            ScheduleTime = DateFormatsMethods.getDateTime();
            llScheduleTime.setVisibility(View.GONE);
            date = DateFormatsMethods.getDateTime().substring(0,10);
            time = DateFormatsMethods.getDateTime().substring(11,19);
            txtSchlDate.setText(DateFormatsMethods.DateFormat_DD_MM_YYYY(date));
            txtSchlTime.setText(time);
        }else if (rgScheduleTime.getCheckedRadioButtonId() == R.id.radio_button_schedule_time_custom){
            date = DateFormatsMethods.getDateTime().substring(0,10);
            time = DateFormatsMethods.getDateTime().substring(11,19);
            ScheduleTime = DateFormatsMethods.getDateTime();
            llScheduleTime.setVisibility(View.VISIBLE);
            txtSchlDate.setText(DateFormatsMethods.DateFormat_DD_MM_YYYY(date));
            txtSchlTime.setText(time);
        }
        rgScheduleTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_schedule_time_now){
                    ScheduleTime = DateFormatsMethods.getDateTime();
                    llScheduleTime.setVisibility(View.GONE);
                    date = DateFormatsMethods.getDateTime().substring(0,10);
                    time = DateFormatsMethods.getDateTime().substring(11,19);
                    txtSchlDate.setText(DateFormatsMethods.DateFormat_DD_MM_YYYY(date));
                    txtSchlTime.setText(time);
                }else if (checkedId == R.id.radio_button_schedule_time_custom){
                    date = DateFormatsMethods.getDateTime().substring(0,10);
                    time = DateFormatsMethods.getDateTime().substring(11,19);
                    ScheduleTime = DateFormatsMethods.getDateTime();
                    llScheduleTime.setVisibility(View.VISIBLE);
                    txtSchlDate.setText(DateFormatsMethods.DateFormat_DD_MM_YYYY(date));
                    txtSchlTime.setText(time);
                }
            }
        });
        //TODO: Message Type
        if (rgMsgType.getCheckedRadioButtonId() == R.id.radio_button_message_type_text){
            msgType = "0";
            edtMessage.getEditText().setText("");
            llAttachment.setVisibility(View.GONE);
            txtAttachFile.setText("Attach file");
            fileName = "";
            attachment = "";
            edtFileDescription.getEditText().setText("");
        }else if (rgMsgType.getCheckedRadioButtonId() == R.id.radio_button_message_type_attachment){
            msgType = "1";
            edtMessage.getEditText().setText("");
            llAttachment.setVisibility(View.VISIBLE);
        }
        rgMsgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_button_message_type_text){
                    msgType = "0";
                    edtMessage.getEditText().setText("");
                    llAttachment.setVisibility(View.GONE);
                    txtAttachFile.setText("Attach file");
                    fileName = "";
                    attachment = "";
                    edtFileDescription.getEditText().setText("");
                }else if (checkedId == R.id.radio_button_message_type_attachment){
                    msgType = "1";
                    edtMessage.getEditText().setText("");
                    llAttachment.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void ClickEvent(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtConverNumber.setError(null);
                edtSrNumber.setError(null);
                edtMessage.setError(null);
                edtNTime.setError(null);
                edtFileDescription.setError(null);

                ConverNumber = edtConverNumber.getEditText().getText().toString();
                SRNumber = edtSrNumber.getEditText().getText().toString();
                text = edtMessage.getEditText().getText().toString();
                NTime = edtNTime.getEditText().getText().toString();
                description = edtFileDescription.getEditText().getText().toString();

                boolean cancel = false;
                View focusView = null,focusView2=null;
                if (TextUtils.isEmpty(ConverNumber)) {
                    edtConverNumber.setError("Conversion number cannot be blank");
                    focusView = edtConverNumber;
                    cancel = true;
                }
                if (TextUtils.isEmpty(SRNumber)) {
                    edtSrNumber.setError("SR number cannot be blank");
                    focusView = edtSrNumber;
                    cancel = true;
                }
                if (TextUtils.isEmpty(NTime)) {
                    edtNTime.setError("Number of time cannot be blank");
                    focusView = edtNTime;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    if (focusView!=null) {  focusView.requestFocus();  }
                } else {
                    //
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            if ((SRNumber.length() == 10) && (Integer.valueOf(NTime) > 0)){
                                if (Integer.valueOf(msgType) == 0) {
                                    CallVolleyPushNotification(str[3], str[4], str[0], str[5], str[14], ScheduleTime, ConverNumber, ConverNumber, SRNumber, text, NTime, isGroup, fileName, attachment, description, msgType);
                                }else if (Integer.valueOf(msgType) == 1) {
                                    if (!attachment.isEmpty() && !fileName.isEmpty())
                                        CallVolleyPushNotification(str[3], str[4], str[0], str[5], str[14], ScheduleTime, ConverNumber, ConverNumber, SRNumber, text, NTime, isGroup, fileName, attachment, description, msgType);
                                    else
                                        if(attachment.isEmpty())  MessageDialog.MessageDialog(context,"","Please attach any file");
                                        if (fileName.isEmpty())   MessageDialog.MessageDialog(context,"","Please attach any file");
                                }
                            }else{
                                if (SRNumber.length() != 10) {
                                    edtSrNumber.setError("Please enter valid 10 digits whats app number");
                                    focusView = edtSrNumber;
                                }
                                if (Integer.valueOf(NTime) < 0){
                                    edtNTime.setError("Please enter greater than 0 number of time");
                                    focusView = edtNTime;
                                }
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            }
        });
        txtSchlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = DateFormatsMethods.PastDateNotSelect( formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year );
                        ScheduleTime = Date +" "+time;
                        txtSchlDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Schedule Date");
                datePicker.show();
            }
        });
        txtSchlTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        try{
                            DecimalFormat formatter = new DecimalFormat("00");
                            String Time = formatter.format(hourOfDay)+":"+formatter.format(minute)+":00";
                            ScheduleTime = date +" "+Time;
                            txtSchlTime.setText(Time);
                        }catch (Exception e) {
                            // TODO: handle exception
                            Log.e("ERRor", ""+e.toString());
                        }
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));
                timePicker.setTitle("Schedule Time");
                timePicker.show();
            }
        });
        txtAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAttachment();
            }
        });
    }
    private void WhatsAppSend(){
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String Mobile = edtConverNumber.getEditText().getText().toString().trim().replaceFirst("^0*","");
//                edtConverNumber.setError(null);
//                boolean cancel = false;
//                View focusView = null;
//                //TODO Check for a Whats App No Validation
//                if (TextUtils.isEmpty(Mobile)) {
//                    edtConverNumber.setError("Whats app No is mandatory!!!");
//                    focusView = edtConverNumber;
//                    cancel = true;
//                }
//                if (cancel) {
//                    // There was an error; don't attempt login and focus the first
//                    // form field with an error.
//                    focusView.requestFocus();
//                } else {
//                    if (Mobile.length() == 10){
//                        String WhatsAppNo = "91"+Mobile;
//                        String Message = edtMessage.getEditText().getText().toString();
//                        mWebView.setWebViewClient(new MyWebViewClient(context));
//                        mWebView.getSettings().setJavaScriptEnabled(true);
//                        mWebView.loadUrl("https://api.whatsapp.com/send?phone="+WhatsAppNo+"&text="+Message);
//                    }else{
//                        edtConverNumber.setError("Please enter valid Whats app No!!!");
//                        focusView = edtConverNumber;
//                    }
//                }
//            }
//        });
    }
    private void CallVolleyPushNotification(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String ScheduleTime,final String ConverName,final String ConverNumber,final String SRNumber,final String text,final String Ntime,final String isGroup,final String FileName,final String Attachment,final String Description,final String MsgType){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"whatsapp_SendMsg", new Response.Listener<String>()
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
                params.put("ScheduleTime", ScheduleTime);
                params.put("ConverName", ConverName);
                params.put("ConverNumber", ConverNumber);
                params.put("SRNumber", SRNumber);
                params.put("text", text);
                params.put("Ntime", Ntime);
                params.put("isGroup", isGroup);
                params.put("FileName", FileName);
                params.put("Description", Description);
                params.put("MsgType", MsgType);
                Log.d(TAG," Push Notification parameters1:"+params.toString());
                params.put("Attachment", Attachment);
                Log.d(TAG," Push Notification parameters2:"+params.toString());
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
            case R.id.action_search:
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
//                if(adapter!=null) {
//                    adapter.filter(newText);
//                }
//                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
//                if(adapter!=null)
//                {
//                    adapter.filter(query);
//                }
//                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                addThemToView(photoPaths, docPaths);
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                addThemToView(photoPaths, docPaths);
                break;
            default:
                break;
        }
    }
    private void DialogAttachment(){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_attachment);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView txtViewCamera = (TextView) dialog.findViewById(R.id.textView_Camera);
        TextView txtViewGallery = (TextView) dialog.findViewById(R.id.textView_Gallery);
        TextView txtViewDocument = (TextView) dialog.findViewById(R.id.textView_Document);
        txtViewCamera.setVisibility(View.GONE);
        txtViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoPaths=new ArrayList<>();
                docPaths=new ArrayList<>();
                onPickPhoto();
                dialog.dismiss();
            }
        });
        txtViewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoPaths=new ArrayList<>();
                docPaths=new ArrayList<>();
                onPickDoc();
                dialog.dismiss();
            }
        });
    }
    //TODO: Gallery Image View
    private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if(imagePaths!=null)
            filePaths.addAll(imagePaths);
        if(docPaths!=null)
            filePaths.addAll(docPaths);

        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recyclerview);
        if(recyclerView1!=null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView1.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);
            recyclerView1.setAdapter(imageAdapter);
            recyclerView1.setItemAnimator(new DefaultItemAnimator());
        }
        if(!filePaths.isEmpty()) {
            File file = new File(filePaths.get(0));
            if(file.exists()) {
                txtAttachFile.setText("Attach file: \t" + file.getName());
                fileName = file.getName().replaceAll("\\s","");
                attachment = ConvertIntoBase64(filePaths.get(0))[1];
                if (fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png")) {
                    Glide.with(context).load(file).apply(RequestOptions.centerCropTransform().override(StaticValues.mViewWidth, StaticValues.mViewHeight).placeholder(R.mipmap.ic_launcher)).thumbnail(0.5f).into(image);
                    image.setVisibility(View.VISIBLE);
                }else if (fileName.contains(".pdf")) {
                    image.setImageBitmap(PDFThumbnail.GenerateImageFromPdf(context,file.getPath().toString()));
                    image.setVisibility(View.VISIBLE);
                }else{
                    image.setVisibility(View.GONE);
                }
            }
        }else{
            Snackbar.make(txtAttachFile,"Documents & Images not selected",Snackbar.LENGTH_LONG).show();
        }
    }
    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT-docPaths.size();
        if((docPaths.size()+photoPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this);
    }
    public void onPickDoc() {
        int maxCount = MAX_ATTACHMENT_COUNT-photoPaths.size();
        if((docPaths.size()+photoPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.AppTheme)
                    .pickFile(this);
    }
    private String[] ConvertIntoBase64(String Path){
        String[] str=new String[3];
        try{
            int p = Path.lastIndexOf('.');
            if (p >= 0) {
                str[0] = Path.substring(p);
            }
            int fileName = Path.lastIndexOf('/');
            if (fileName >= 0){
                str[2] = Path.substring(fileName);
            }
            InputStream inputStream = new FileInputStream(new File(Path));
            byte[] data;
            data = IOUtils.toByteArray(inputStream);
            //System.out.println("covertByte: "+data.toString());
            // Encode Image to String
            str[1] = Base64.encodeToString(data, 0);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
        }
        return  str;
    }
}
