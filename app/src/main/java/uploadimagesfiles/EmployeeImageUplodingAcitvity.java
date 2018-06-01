package uploadimagesfiles;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import DatabaseController.CommanStatic;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;
import uploadimagesfiles.responsedatasets.ResponseItemInfoDataset;
import uploadimagesfiles.responsedatasets.ResponseUploadItemImageDataset;

/**
 * Created by Rakesh on 23-Jan-18.
 */

public class EmployeeImageUplodingAcitvity extends AppCompatActivity{
    private Context context;
    private ActionBar actionBar;
    private Dialog dialog;
    private ImageView imgMain;
    private TextView txtScanContent;
    private EditText edtEmpCode;
    private Button btnScan,btnUpload;
    private FloatingActionButton fabUpload,fabCamera,fabGallery;
    private TableLayout tableLayout;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private Uri fileUri;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private Map<String,String> map;
    private static final String TAG = EmployeeImageUplodingAcitvity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.employee_image_upload);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = EmployeeImageUplodingAcitvity.this;
            this.actionBar = getSupportActionBar();
            this.actionBar.setDisplayHomeAsUpEnabled(true);
            imgMain = (ImageView) findViewById(R.id.imgView_current_img);
            txtScanContent = (TextView) findViewById(R.id.scan_content);
            edtEmpCode = (EditText) findViewById(R.id.EditText_EmpCode);
            edtEmpCode.setHint("Employee Code");
            btnScan = (Button) findViewById(R.id.Button_Scan);
            btnUpload = (Button) findViewById(R.id.Button_Upload);
            btnUpload.setVisibility(View.GONE);
            fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
            fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
            fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
            tableLayout = (TableLayout) findViewById(R.id.tableLayout_ItemInfo);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }catch (Exception e){
            MessageDialog.MessageDialog(this,"Initialization",""+e.toString());
        }
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
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod();
                ClickEvents();
            }else {
                MessageDialog.MessageDialog(EmployeeImageUplodingAcitvity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(EmployeeImageUplodingAcitvity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //LoginConditions();
                }else{
                    MessageDialog.MessageDialog(EmployeeImageUplodingAcitvity.this,"",status);
                }
            }
        });
    }
    private void ClickEvents(){
        //TODO: Call OnKey
        edtEmpCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    String EmpCode = edtEmpCode.getText().toString().toUpperCase().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(EmployeeImageUplodingAcitvity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtEmpCode.getWindowToken(), 0);
                    if (!EmpCode.equals("")) {
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj= new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if(str!=null)
                                CallVolleyEmpImageGetInfoAndUpload(str[3], str[0], str[4],str[14],"0",EmpCode,"");
                        }else{
                            MessageDialog.MessageDialog(EmployeeImageUplodingAcitvity.this,"",status);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        //TODO: Upload Button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap!=null && map!=null) {
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        String EndodedImage = BitMapToString(bitmap);
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            DialogConfirmation(str[3], str[0], str[4],str[14], "1", ""+map.get("CardNo"),EndodedImage);
                    } else {
                        MessageDialog.MessageDialog(context, "", status);
                    }
                }else{
                    if (bitmap==null) { MessageDialog.MessageDialog(context, "", "Please attach image first");  }
                    if (map==null) { MessageDialog.MessageDialog(context, "", "Result not found");  }
                }
            }
        });
    }
    private void SetTableLayout(Map<String,String> map){
        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();
        //TODO: Name
        View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        String CardNo =  map.get("CardNo") == null ? "" : map.get("CardNo");
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Employee Code",CardNo));
        //TODO: Name
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        String Name =  map.get("Title") == null ? "" : map.get("Name")== null ? "" : map.get("Title")+" "+map.get("Name");
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Name",Name));
        //TODO: Father's Name
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        String Father =  map.get("FatherName") == null ? "" : map.get("FatherName");
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Father's Name",Father));
        //TODO: Mobile and Dob
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        String MobDob =  map.get("CellNo") == null  && map.get("CellNo").equals("null") ? "" : map.get("DOB") == null && map.get("DOB").equals("null") ? "" : map.get("CellNo")+" / "+ DateFormatsMethods.DateFormat_DD_MM_YYYY(map.get("DOB"));
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mobile / Dob",MobDob));
        //TODO: Mobile and Dob
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        String Address1 =  map.get("Address1") == null ? "" : map.get("Address1");
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Address",Address1));
    }
    private void CallVolleyEmpImageGetInfoAndUpload(final String DeviceID, final String SessionID, final String UserID, final String CompanyID, final String Type, final String EmployeeCode, final String Image){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"EmployeeImageGetUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.i("Response", response);
                map = new HashMap<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    if (Status == 1) {
                        if (Type.equals("0")) {
                            JSONObject jsonResult = new JSONObject(jsonObject.getString("Result"));
                            if (jsonResult.length() > 0) {
                                map.put("ID", jsonResult.optString("ID") == null ? "" : jsonResult.optString("ID"));
                                map.put("Title", jsonResult.optString("Title") == null ? "" : jsonResult.optString("Title"));
                                map.put("Name", jsonResult.optString("Name") == null ? "" : jsonResult.optString("Name"));
                                map.put("CardNo", jsonResult.optString("CardNo") == null ? "" : jsonResult.optString("CardNo"));
                                map.put("Address1", jsonResult.optString("Address1") == null ? "" : jsonResult.optString("Address1"));
                                map.put("CellNo", jsonResult.optString("CellNo") == null ? "" : jsonResult.optString("CellNo"));
                                map.put("FatherName", jsonResult.optString("FatherName") == null ? "" : jsonResult.optString("FatherName"));
                                map.put("DOB", jsonResult.optString("DOB") == null ? "" : jsonResult.optString("DOB"));
                                map.put("EmpImage", jsonResult.optString("EmpImage") == null ? "" : jsonResult.optString("EmpImage"));
                                SetTableLayout(map);
                                edtEmpCode.setText("");
                                btnUpload.setVisibility(View.VISIBLE);
                            } else {
                                MessageDialog.MessageDialog(context,"", "" + Msg);
                            }
                        }else if (Type.equals("1")) {
                            MessageDialogByIntent(context,"", "" + Msg);
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"", "" + Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception", "" + e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.i("Error.Response", ""+error);
                Toast.makeText(context,"VolleyError :"+error.toString(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("UserID", UserID);
                params.put("CompanyID", CompanyID);
                params.put("Type", Type);
                params.put("EmployeeCode", EmployeeCode);
                params.put("Image", Image);
                Log.i(TAG,"Employee Image Get Info and Update parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
        try {
            final Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.cardview_message_box);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
            TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
            Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
            txtViewMessageTitle.setText(Title);
            txtViewMessage.setText(Mesaage);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent = getIntent();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
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
                        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
                        Log.d(TAG, "File : "+file.toString());
                        fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        if (intent.resolveActivity(getPackageManager()) != null)
                           /*start activity for result pass intent as argument and request code */
                            startActivityForResult(intent, 1);
                    }else {
                         /*create instance of File with name img.jpg*/
                        File file = new File(Environment.getExternalStorageDirectory()+File.separator +"img.jpg");
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
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void DialogConfirmation(final String DeviceID, final String SessionID, final String UserID,final String CompanyID, final String Type, final String EmployeeCode, final String Image){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to replace it?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    CallVolleyEmpImageGetInfoAndUpload(DeviceID, SessionID, UserID,CompanyID, Type, EmployeeCode,Image);
                }else{
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File file = new File(Environment.getExternalStorageDirectory()+File.separator +"img.jpg");
                bitmap =  decodeSampledBitmapFromFile(file.getAbsolutePath(), 600, 800);
                imgMain.setImageBitmap(bitmap);
                //uploadImage(bitmap);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap =  decodeSampledBitmapFromFile(picturePath, 600, 800);
                imgMain.setImageBitmap(bitmap);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case  R.id.action_attachment:
                selectImage();
                break;
        }
        return true;
    }

}
