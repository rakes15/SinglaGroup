package administration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
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

import java.io.ByteArrayOutputStream;
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
 * Created by rakes on 25-May-18.
 */

public class ChequeActivity extends AppCompatActivity {
    private static String TAG = ChequeActivity.class.getSimpleName();
    private ActionBar actionBar;
    private Context context;
    private TextInputLayout edtDescription;
    private Spinner spnChequeType;
    private TextView txtChequeDate,txtPresentDate,txtCurrentDate,txtAttachment;
    private ImageView imgView;
    private RelativeLayout rLChequeDate,rLPresentDate,rLCurrentDate;
    private Button btnUpload;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private Uri fileUri;
    private int ChequeType=0;
    private String ChequeDt="",PresentDt="",CurrentDt="",Attachment="",Description="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_cheque);
        Initialization();
        ModulePermission();
        Validation();
        ClickEvent();
    }
    private void Initialization(){
        this.context = ChequeActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO: TextInputLayout
        edtDescription = (TextInputLayout) findViewById(R.id.editText_file_description);
        //TODO: Spinner
        spnChequeType = (Spinner) findViewById(R.id.spinner_cheque_type);
        //TODO: TextView
        txtChequeDate = (TextView) findViewById(R.id.text_cheque_date);
        txtPresentDate = (TextView) findViewById(R.id.text_present_date);
        txtCurrentDate = (TextView) findViewById(R.id.text_current_date);
        txtAttachment = (TextView) findViewById(R.id.text_attach_file);
        //TODO: Relative Layout
        rLChequeDate = (RelativeLayout) findViewById(R.id.RelativeLayout_cheque_date);
        rLPresentDate = (RelativeLayout) findViewById(R.id.RelativeLayout_present_date);
        rLCurrentDate = (RelativeLayout) findViewById(R.id.RelativeLayout_current_date);
        //TODO: Image View
        imgView = (ImageView) findViewById(R.id.image_view);
        //TODO: Button
        btnUpload = (Button) findViewById(R.id.btn_upload);
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
                        // Api Call
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
        //TODO: Cheque Type Spinner
        spnChequeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ChequeType = position;
                if (position == 0){
                    //TODO: Current / Post dated cheque
                    /* (a) Cheque date should be not be less than 60 days.
                       (b) Present date should be not be greater than 60 days from the Cheque date.
                       (c) Present date should be not be greater than 90 days from the Current date.
                       (d) Present date should be not be less than Current date or Cheque date.
                     */
                    rLChequeDate.setVisibility(View.VISIBLE);
                    rLPresentDate.setVisibility(View.VISIBLE);
                    rLCurrentDate.setVisibility(View.VISIBLE);

                }else if (position == 1){
                    //TODO: Security cheque
                    /* Present date and Cheque date are not asking   */
                    rLChequeDate.setVisibility(View.GONE);
                    rLPresentDate.setVisibility(View.GONE);
                    rLCurrentDate.setVisibility(View.VISIBLE);

                }else if (position == 2){
                    //TODO: Undated cheque
                    /* (a) Present date should be greater than Current date
                       (b) Only Present date will be asked not Cheque date
                       (c) Present date greater than 180 days from Current date*/
                    rLChequeDate.setVisibility(View.GONE);
                    rLPresentDate.setVisibility(View.VISIBLE);
                    rLCurrentDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
    private void ClickEvent(){
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtChequeDate.setError(null);
                txtPresentDate.setError(null);
                txtCurrentDate.setError(null);
                txtAttachment.setError(null);
                edtDescription.setError(null);

                ChequeDt    = txtChequeDate.getText().toString();
                PresentDt   = txtPresentDate.getText().toString();
                CurrentDt   = txtCurrentDate.getText().toString();
                Attachment  = txtAttachment.getText().toString();
                Description = edtDescription.getEditText().getText().toString();

                boolean cancel = false;
                View focusView = null,focusView2=null;
                if (ChequeDt.equals("DD - MM - YYYY")) {
                    SetSpinnerError(txtChequeDate,"Please select cheque date");
                    focusView2 = txtChequeDate;
                    cancel = true;
                }
                if (PresentDt.equals("DD - MM - YYYY")) {
                    SetSpinnerError(txtPresentDate,"Please select present date");
                    focusView2 = txtPresentDate;
                    cancel = true;
                }
                if (CurrentDt.equals("DD - MM - YYYY")) {
                    SetSpinnerError(txtCurrentDate,"Please select current date");
                    focusView2 = txtCurrentDate;
                    cancel = true;
                }
                if (Attachment.equals("Attach file")) {
                    SetSpinnerError(txtAttachment,"Please attach cheque image");
                    focusView2 = txtAttachment;
                    cancel = true;
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
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {

                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            }
        });
        //TODO : Date
        txtChequeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = formatter.format(dayOfMonth) + " - " + formatter.format(month + 1) + " - "+ year;
                        txtChequeDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Cheque Date");
                datePicker.show();
            }
        });
        txtPresentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = DateFormatsMethods.PastDateNotSelect( formatter.format(dayOfMonth) + " - " + formatter.format(month + 1) + " - "+ year );
                        txtPresentDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                //datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                datePicker.setTitle("Present Date");
                datePicker.show();
            }
        });
        txtCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = DateFormatsMethods.PastDateNotSelect( formatter.format(dayOfMonth) + " - " + formatter.format(month + 1) + " - "+ year );
                        txtCurrentDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                //datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                //datePicker.getDatePicker().setMinDate(cal);
                datePicker.setTitle("Current Date");
                datePicker.show();
            }
        });
        txtAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }
    private void CallVolleyChequeUpload(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID
            ,final String ChequeType,final String ChequeDt,final String PresentDt,final String CurrentDt,final String FileName,final String Image){
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
                params.put("ChequeType", ChequeType);
                params.put("ChequeDt", ChequeDt);
                params.put("PresentDt", PresentDt);
                params.put("CurrentDt", CurrentDt);
                params.put("FileName", FileName);
                params.put("Image", Image);

                Log.d(TAG,"Cheque Upload parameters:"+params.toString());
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
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void SetSpinnerError(TextView txtView,String errorString){
        txtView.setError(errorString);
        txtView.setTextColor(Color.RED); //text color Red
        txtView.setVisibility(View.VISIBLE);
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel"};//,"Remove Image" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //File file = getOutputMediaFile(RESULT_CAMERA_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/"+"img.jpg");
                        //Log.d(TAG, "File : "+file.toString());
                        fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        if (intent.resolveActivity(getPackageManager()) != null)
                            /*start activity for result pass intent as argument and request code */
                            startActivityForResult(intent, 1);
                    }else {
                        /*create instance of File with name img.jpg*/
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/"+"img.jpg");
                        /*put uri as extra in intent object*/
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        /*start activity for result pass intent as argument and request code */
                        startActivityForResult(intent, 1);
                    }

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
//                else if (options[item].equals("Remove Image")) {
//                    imgProfilePic.setImageBitmap(null);
////                    session.setBitmap("");
////                    deleteImage();
//                    dialog.dismiss();
//                }
//                checkPermissionForCamera();
//                requestPermissionForCamera();
//                checkPermissionForExternalStorage();
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/"+"img.jpg");
                bitmap =  decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 1000);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                //txtAttachment.setText(file.getPath().toString().lastIndexOf('/'));
                //uploadImage(bitmap);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap =  decodeSampledBitmapFromFile(picturePath, 1000, 1000);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                //txtAttachment.setText(selectedImage.getPath().toString().lastIndexOf('/'));
                //uploadImage(bitmap);
            }
        }
    }
    public static Bitmap decodeSampledBitmapFromFile(String path,int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //Query bitmap without allocating memory
        options.inJustDecodeBounds = true;
        //decode file from path
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        //decode according to configuration or according best match
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
        //if value is greater than 1,sub sample the original image
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
