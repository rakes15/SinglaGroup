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
import android.graphics.Matrix;
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
import android.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.singlagroup.HomeAcitvity;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.DivisionDataset;
import com.singlagroup.datasets.GodownDataset;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;
import orderbooking.StaticValues;
import orderbooking.customerlist.BookOrdersActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;
import uploadimagesfiles.datasets.ItemInfoDataset;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;
import uploadimagesfiles.responsedatasets.ResponseItemInfoDataset;
import uploadimagesfiles.responsedatasets.ResponseUploadItemImageDataset;

/**
 * Created by Rakesh on 19-Oct-16.
 */

public class ImageUplodingAcitvity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private ActionBar actionBar;
    private Dialog dialog;
    private CircleImageView imgThumbnail;
    private ImageView imgMain;
    private TextView txtScanContent;
    private EditText edtStyleBarcode,edtBarcode;
    private Button btnScan,btnUpload;
    private FloatingActionButton fabUpload,fabCamera,fabGallery;
    private TableLayout tableLayout;
    private ProgressDialog progressDialog;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private static final String TAG = ImageUplodingAcitvity.class.getSimpleName();
    public static final int RESULT_BARCODE_SCAN = 0x0000c0de;
    private static final int RESULT_CAMERA_CAPTURE = 1;
    public static final String IMAGE_NAME = "c.jpg";
    String scanContent=null;
    String ImagePath=null,ItemID=null,Remark="Item image upload via app",ItemImage=null,ItemImageThumb=null,ItemCode;
    int ImageStatus;
    Bitmap bitmap;
    private static final String IMAGE_DIRECTORY_NAME = "Cam";
    private Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.item_image_upload);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = ImageUplodingAcitvity.this;
            this.actionBar = getSupportActionBar();
            this.actionBar.setDisplayHomeAsUpEnabled(true);
            imgThumbnail = (CircleImageView) findViewById(R.id.imgView_thumb);
            imgMain = (ImageView) findViewById(R.id.imgView_current_img);
            txtScanContent = (TextView) findViewById(R.id.scan_content);
            edtStyleBarcode = (EditText) findViewById(R.id.editText_style);
            edtStyleBarcode.setHint("Style/Barcode");
            edtBarcode = (EditText) findViewById(R.id.editText_barcode);
            edtBarcode.setVisibility(View.GONE);
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
            //TODO: Call Onclick
            btnScan.setOnClickListener(this);
            fabUpload.setOnClickListener(this);
            fabCamera.setOnClickListener(this);
            fabGallery.setOnClickListener(this);
            btnUpload.setOnClickListener(this);
            //TODO: Call OnKey
            edtStyleBarcode.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                   if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    String Str = edtStyleBarcode.getText().toString().toUpperCase().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(ImageUplodingAcitvity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtStyleBarcode.getWindowToken(), 0);
                    if (!Str.equals("")) {
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj= new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(ImageUplodingAcitvity.this);
                            if(str!=null)
                                CallRetrofitGetItemInfo(str[3], str[0], str[4],str[5],str[14],Str);
                        }else{
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",status);
                        }
                    }
                    return true;
                }
                    return false;
                }
            });
            edtBarcode.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    String Str = edtBarcode.getText().toString().toUpperCase().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(ImageUplodingAcitvity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtStyleBarcode.getWindowToken(), 0);
                    if (!Str.equals("")) {
                        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj= new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(ImageUplodingAcitvity.this);
                            if(str!=null)
                                CallRetrofitGetItemInfo(str[3], str[0], str[4],str[5],str[14],Str);
                        }else{
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",status);
                        }
                    }
                    return true;
                }
                    return false;
                }
            });

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
            }else {
                MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception",e.toString());
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
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",status);
                }
            }
        });
    }
    @Override public void onClick(View v) {
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        switch (v.getId()) {
            case R.id.Button_Scan:
                //TODO: scan
//                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//                scanIntegrator.initiateScan();
                break;
            case R.id.Button_Upload:
                if (ImagePath!=null && this.bitmap!=null) {
                    //String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        String EndodedImageThumb = EncodeIntoBase64FromBitmap(Resize(this.bitmap,1));
//                        String EndodedImage = EncodeIntoBase64(ImagePath);
                        this.bitmap = imageOreintationValidator(this.bitmap,ImagePath);
                        String EndodedImage = EncodeIntoBase64FromBitmap(Resize(this.bitmap,0));
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(ImageUplodingAcitvity.this);
                        if (str != null) {
                            //DialogAskRemarks(str[3], str[0], str[4], str[5],str[14], ItemID, Remark,EndodedImageThumb,EndodedImage);
                            DilaogConfirmation(str[3], str[0], str[4], str[5],str[14], ItemID, Remark+" UserName: "+str[4]+" UserID:"+str[12],EndodedImageThumb,EndodedImage);
//                            CallRetrofitUploadItemImage(str[3], str[0], str[4], str[5], ItemID, Remark,EndodedImageThumb,EndodedImage);
                        }else {
                            Toast.makeText(this,"str",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", status);
                    }
                }else{
                    Toast.makeText(this,"ImagePath",Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(this,"Upload",Toast.LENGTH_LONG).show();
                break;
            case R.id.textView_Gallery:
                if (photoPaths.size()==MAX_ATTACHMENT_COUNT){
                    Toast.makeText(this,"Cannot select more than "+MAX_ATTACHMENT_COUNT+ " items",Toast.LENGTH_LONG).show();
                }else {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setSelectedFiles(photoPaths)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto(this);
                }
                if(dialog!=null)
                    dialog.dismiss();
                break;
            case R.id.textView_Camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //File file = getOutputMediaFile(RESULT_CAMERA_CAPTURE);
                    File file = getPhotoFileUri(IMAGE_NAME);
                    Log.d(TAG, "File : "+file.toString());
                    fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    if (intent.resolveActivity(getPackageManager()) != null)
                        // start the image capture Intent
                        startActivityForResult(intent, RESULT_CAMERA_CAPTURE);
                }else {
                    fileUri = getOutputMediaFileUri(RESULT_CAMERA_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // start the image capture Intent
                    startActivityForResult(intent, RESULT_CAMERA_CAPTURE);
                }
                if(dialog!=null)
                    dialog.dismiss();
                break;
            case R.id.ImageView_UploadIntoFolder:
                if (!status.contentEquals("No Internet Connection")) {
                    if(ItemImage!=null && ItemID!=null)
                        ImageDownload(ItemImage,ItemCode,1);
                    else
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"","No Image found!!!");
                } else {
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", status);
                }
                break;
            case R.id.ImageView_Download:
                if (!status.contentEquals("No Internet Connection")) {
                    if(ItemImage!=null && ItemID!=null)
                        ImageDownload(ItemImage,ItemCode,0);
                    else
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"","No Image found!!!");
                } else {
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", status);
                }
                break;
            case R.id.ImageView_WhatsApp:
                if (!status.contentEquals("No Internet Connection")) {
                    if(ItemImage!=null && ItemID!=null)
                        ImageDownload(ItemImage,ItemCode,2);
                    else
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"","No Image found!!!");
                } else {
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", status);
                }
                break;
            default:
                break;
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
            case R.id.action_attachment:
                CustomAttachmentDialog();
                break;
        }
        return true;
    }
    private void CustomAttachmentDialog(){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_attachment);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView txtViewCamera = (TextView) dialog.findViewById(R.id.textView_Camera);
        TextView txtViewGallery = (TextView) dialog.findViewById(R.id.textView_Gallery);
        TextView txtViewDocument = (TextView) dialog.findViewById(R.id.textView_Document);
        txtViewDocument.setVisibility(View.GONE);
        txtViewGallery.setOnClickListener(ImageUplodingAcitvity.this);
        txtViewCamera.setOnClickListener(ImageUplodingAcitvity.this);
    }
    private String EncodeIntoBase64(String Path){
        //showpDialog();
        String EncodedString="";
        try{
            System.out.println("Path: "+Path);
            InputStream inputStream = new FileInputStream(new File(Path));
            byte[] data;
            data = IOUtils.toByteArray(inputStream);
            System.out.println("covertByte: "+data.toString());
            // Encode Image to String
            EncodedString = Base64.encodeToString(data, 0);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private String EncodeIntoBase64FromBitmap(Bitmap bitmap){
        //showpDialog();
        String EncodedString="";
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            System.out.println("From Bitmap covertByte: "+byteArray.toString());
            // Encode Image to String
            EncodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception" ,"From Bitmap Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private Bitmap DecodeIntoBase64(String EncodedImage){
        Bitmap bitmap=null;
        try{
            byte[] decodedString = Base64.decode(EncodedImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception" ,"Decode into Bitmap: "+e.toString());
        }
        return  bitmap;
    }
    private void CallRetrofitGetItemInfo( String DeviceID, String SessionID, String UserID,String DivisionID,String CompanyID,final String ItemCode){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("ItemCode", ItemCode);
        Log.d(TAG,"Parameters:"+params.toString());
        Call<ResponseItemInfoDataset> call = apiService.getItemInfoWithImage(params);
        call.enqueue(new Callback<ResponseItemInfoDataset>() {
            @Override
            public void onResponse(Call<ResponseItemInfoDataset> call, retrofit2.Response<ResponseItemInfoDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            Map<String,String> map = response.body().getResult();
                            String Style = edtStyleBarcode.getText().toString().trim();
                            DisplayItemInfo(map,Style);
                            edtStyleBarcode.setText("");
                        } else {
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",msg);
                            edtStyleBarcode.setText("");
                        }
                    }else {
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Server response",""+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG," Exception:"+e.getMessage());
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Item image Info API",e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseItemInfoDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(ImageUplodingAcitvity.this,"Failure","Item image Info API",t.toString());
                hidepDialog();
            }
        });
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
    private void DisplayItemInfo(Map<String,String> dataset,String StyleBarcode){

        if(!StyleBarcode.isEmpty()) {
            txtScanContent.setText("Search Style/Barcode is : "+StyleBarcode);
            ItemCode=StyleBarcode;
        }
        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();
        //TODO: 2nd Row
        View v1 = this.getLayoutInflater().inflate(R.layout.table_row_single_column, tableLayout, false);
//        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
//        txtHeader1.setText("Item Name");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+dataset.get("ItemName"));
        v1.setBackgroundColor(getResources().getColor(R.color.Bisque));
        tableLayout.addView(v1);
        //TODO: 3rd Row
        View v2 = this.getLayoutInflater().inflate(R.layout.table_row_single_column, tableLayout, false);
        //TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        //txtHeader2.setText("MainGroup\nGroup\nSubGroup");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+dataset.get("MainGroup")+"   "+dataset.get("GroupName")+"   "+dataset.get("SubGroup")+"     MRP: ₹"+dataset.get("MRP")+"    Rate: ₹"+dataset.get("Rate"));
        v2.setBackgroundColor(getResources().getColor(R.color.Color_lightGrey));
        tableLayout.addView(v2);

        //TODO: 4rd Row
        View v3 = this.getLayoutInflater().inflate(R.layout.table_row_single_column, tableLayout, false);
        //TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
        //txtHeader3.setText("MRP / Rate");

        TextView txt3= (TextView) v3.findViewById(R.id.content);
        txt3.setText("MRP: ₹"+dataset.get("MRP")+"  /  Rate: ₹"+dataset.get("Rate"));
        v3.setBackgroundColor(getResources().getColor(R.color.Bisque));
        //tableLayout.addView(v3);

        ItemID = dataset.get("ItemID");
        ItemImage = dataset.get("ItemImage");
        ItemImageThumb = dataset.get("SmallImage");
        ImageStatus = Integer.parseInt(dataset.get("ImageStatus"));
        if(ItemImage!=null || ItemImageThumb!=null) {
            Picasso.with(this).load(ItemImage).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgMain);
            Picasso.with(this).load(ItemImageThumb).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgThumbnail);
        } else {
            imgMain.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            imgThumbnail.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        Log.d(TAG,"ImageUrl:" +ItemImage+"\nThumbUrl:"+ItemImageThumb);
        //TODO: Image Button
        btnUpload.setVisibility(View.VISIBLE);
        if(ImageStatus == 1){
            btnUpload.setText("Replace");

        }else{
            btnUpload.setText("Upload");
        }
    }
    private void CallRetrofitUploadItemImage( String DeviceID, String SessionID, String UserID,String DivisionID,String CompanyID,final String ItemID, final String Remarks, final String ThumbImage, final String ItemImage){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("ItemID", ItemID);
        params.put("Remarks", Remarks);
        params.put("ThumbImage", ThumbImage);
        params.put("ItemImage", ItemImage);
        Log.d(TAG,"Parameters:"+params.toString());
        Call<ResponseUploadItemImageDataset> call = apiService.postItemImageUpload(params);
        call.enqueue(new Callback<ResponseUploadItemImageDataset>() {
            @Override
            public void onResponse(Call<ResponseUploadItemImageDataset> call, retrofit2.Response<ResponseUploadItemImageDataset> response) {
                try {
                    MessageDialog messageDialog=new MessageDialog();
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            messageDialog.MessageDialog(ImageUplodingAcitvity.this,"","",msg);
                            if(fileUri!=null){CameraFileDelete(fileUri,null);}
                            startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                            finish();
                        } else {
                            messageDialog.MessageDialog(ImageUplodingAcitvity.this,"","",msg);
                        }
                    }else {
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Server response",""+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG," Exception:"+e.getMessage());
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception","Item image Info API",e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseUploadItemImageDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(ImageUplodingAcitvity.this,"Failure","Item image Info API",t.toString());
                hidepDialog();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        photoPaths = new ArrayList<>();
                        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    }
                    addThemToView(photoPaths);
                    break;
                case ImageUplodingAcitvity.RESULT_CAMERA_CAPTURE:
                    if (resultCode == Activity.RESULT_OK && requestCode==RESULT_CAMERA_CAPTURE) {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // images
                        options.inSampleSize = 2;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (fileUri.getPath().contains("Cam/")) {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/" + fileUri.getLastPathSegment());
                                ImagePath=file.getPath();
                            }
                        }else {
                            ImagePath=fileUri.getPath();
                        }

                        Bitmap bitmap = BitmapFactory.decodeFile(ImagePath, options);
                        ResizeImages(bitmap, ImagePath);
                        this.bitmap = bitmap;
                    }
                    break;
                case ImageUplodingAcitvity.RESULT_BARCODE_SCAN:
                    if (resultCode == Activity.RESULT_OK && resultCode ==ImageUplodingAcitvity.RESULT_BARCODE_SCAN) {
                        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                        if (scanningResult != null) {
                            scanContent = scanningResult.getContents();
                            if(scanContent!=null) {
                                //TODO: Call API Method
                                //new LoadBarcode().execute(scanContent);
                            }
                        } else {
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Scan Barcode","No scan data received!");
                        }
                    }
            }
    }
    //TODO: Gallery Image View
    private void addThemToView(ArrayList<String> imagePaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if(imagePaths!=null)
            filePaths.addAll(imagePaths);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if(recyclerView!=null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        try
        {
            if (!filePaths.isEmpty()) {
                File imgFile = new File(filePaths.get(0));
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ResizeImages(bitmap, imgFile.getAbsolutePath());
                    ImagePath = imgFile.getAbsolutePath();
                    this.bitmap = imageOreintationValidator(bitmap, ImagePath);
                }
            }else{
                MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"","Image not selected");
            }
        }catch (OutOfMemoryError e) {
            Log.e("ERRor", "Exception: "+e.getMessage());
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Gallery","Exception:"+e.toString());
        }
        //Toast.makeText(this, "Num of files selected: "+ filePaths.size()+"\nPath:"+filePaths.get(0), Toast.LENGTH_SHORT).show();
    }
    private void ResizeImages(Bitmap bitmap,String path){
        Bitmap tBitmap = imageOreintationValidator(bitmap, path);
        if(tBitmap!=null){
            imgMain.setImageBitmap(tBitmap);
            //imgMain.setImageBitmap(Resize(tBitmap,0));
        }else{
            imgMain.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        if(Resize(tBitmap,1)!=null){
            imgThumbnail.setImageBitmap(Resize(tBitmap,0));
        }else {
            imgThumbnail.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        //MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Scan Barcode","Mainwidth:"+Resize(tBitmap,0).getWidth()+"\nMainheight:"+Resize(tBitmap,0).getHeight()+"\nThwidth:"+Resize(tBitmap,1).getWidth()+"\nThheight:"+Resize(tBitmap,1).getHeight());
    }
    private Bitmap Resize(Bitmap bitmap,int flag){
        if(flag==0){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int boundBoxInDp1=800,boundBoxInDp2=800;

            // Determine how much to scale: the dimension requiring less scaling is
            // closer to the its side. This way the image always stays inside your
            // bounding box AND either x/y axis touches it.
            float xScale = ((float) boundBoxInDp1) / width;
            float yScale = ((float) boundBoxInDp2) / height;
            float scale = (xScale <= yScale) ? xScale : yScale;

            // Create a matrix for the scaling and add the scaling data
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a new bitmap and convert it to a format understood by the ImageView
            Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            return finalBitmap;
        }else if(flag==1){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int boundBoxInDp1=200,boundBoxInDp2=200;

            // Determine how much to scale: the dimension requiring less scaling is
            // closer to the its side. This way the image always stays inside your
            // bounding box AND either x/y axis touches it.
            float xScale = ((float) boundBoxInDp1) / width;
            float yScale = ((float) boundBoxInDp2) / height;
            float scale = (xScale <= yScale) ? xScale : yScale;

            // Create a matrix for the scaling and add the scaling data
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a new bitmap and convert it to a format understood by the ImageView
            Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            return finalBitmap;
        }
        return bitmap;
    }
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "+ IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == ImageUplodingAcitvity.RESULT_CAMERA_CAPTURE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }
        else
        {
            return null;
        }

        return mediaFile;
    }
    public File getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/", IMAGE_DIRECTORY_NAME);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            // Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + fileName);

            return file;
        }
        return null;
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    //TODO: Image Rotation
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }
    private void CameraFileDelete(Uri fileUri,String Path){
        try {
            if(fileUri!=null) {
                File file = new File(fileUri.getPath());
                file.delete();
                if (file.exists()) {
                    file.getCanonicalFile().delete();
                    if (file.exists()) {
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
            }else if (Path!=null){
                File file = new File(Path);
                file.delete();
                if (file.exists()) {
                    file.getCanonicalFile().delete();
                    if (file.exists()) {
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
            }
        }catch (IOException e){
            MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Exception",e.toString());
        }
    }
    private void DialogAskRemarks(final String DeviceID, final String SessionID, final String UserID, final String DivisionID,final String CompanyID, final String ItemID, final String Remarks, final String ThumbImage, final String ItemImage){
        dialog = new Dialog(new ContextThemeWrapper(ImageUplodingAcitvity.this, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_name_remaks);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Enter Remarks");
        final EditText edtDescription=(EditText)dialog.findViewById(R.id.editTxt_name);
        final EditText edtRemarks=(EditText)dialog.findViewById(R.id.editTxt_Remarks);
        edtDescription.setVisibility(View.GONE);
        edtRemarks.setHint("Remarks");
        Button upload=(Button)dialog.findViewById(R.id.button_Approve);
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        upload.setText("Upload");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String Descrption = edtDescription.getText().toString().trim();
                String Remarks = edtRemarks.getText().toString().trim();
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    CallRetrofitUploadItemImage(DeviceID, SessionID, UserID,DivisionID,CompanyID, ItemID, Remarks, ThumbImage,ItemImage);
                }else{
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",status);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void DilaogConfirmation(final String DeviceID, final String SessionID, final String UserID, final String DivisionID,final String CompanyID, final String ItemID, final String Remarks, final String ThumbImage, final String ItemImage){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to replace it?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                                if (!status.contentEquals("No Internet Connection")) {
                                    CallRetrofitUploadItemImage(DeviceID, SessionID, UserID,DivisionID,CompanyID, ItemID, Remarks, ThumbImage,ItemImage);
                                }else{
                                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",status);
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
    protected void onPause() {
        super.onPause();
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(getApplicationContext());
        //SessionManage.CallRetrofitSessionLogout(str[3],str[0],str[4],str[14]);
    }
    private void CallRetrofitUploadToFolder(final String Path, String DeviceID, String SessionID, String UserID, String GodownID, String DivisionID, String UserName){
        showpDialog();
        String ext="";
        int p = Path.lastIndexOf('.');
        if (p >= 0) {
            ext = Path.substring(p);
        }
        final ApiInterface apiService = ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("Ext", ext);
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("GodownID", GodownID);
        params.put("DivisionID", DivisionID);
        params.put("UserName", UserName);
        params.put("image", EncodeIntoBase64(Path));
        Log.d(TAG, "File upload Parameters: " + params.toString());
        Call<ResponseFileImageUploadDataset> call = apiService.getFileImageUpload(params);
        call.enqueue(new Callback<ResponseFileImageUploadDataset>() {
            @Override
            public void onResponse(Call<ResponseFileImageUploadDataset> call, retrofit2.Response<ResponseFileImageUploadDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", msg);
                            CameraFileDelete(null,Path);
                        } else {
                            MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", msg);
                        }
                    } else {
                        MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "API response", ""+response.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, " Exception:" + e.getMessage());
                    MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "Item image Upload to folder API", "" + e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseFileImageUploadDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
                MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "Failure",  "Item image Upload to folder API \n"+t.toString());
                hidepDialog();
            }
        });
    }
    private void ImageDownload(String ImageUrl, final String ItemCode, final int flag){
        //Log.d(TAG,"ImageUrl:"+ImageUrl);
        Picasso.with(ImageUplodingAcitvity.this)
                .load(ImageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {

                                  String root = Environment.getExternalStorageDirectory().toString();
                                  File myDir = new File(root + "/SinglaGroups/");

                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  String name = ItemCode + ".jpg";
                                  myDir = new File(myDir, name);
                                  String Msg = "Image already downloaded ";
                                  if (!myDir.exists()) {
                                      FileOutputStream out = new FileOutputStream(myDir);
                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                                      out.flush();
                                      out.close();
                                      Msg = "Image downloaded successfully";
                                  }else{
                                      myDir.delete();
                                      FileOutputStream out = new FileOutputStream(myDir);
                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                                      out.flush();
                                      out.close();
                                      Msg = "Existing downloaded image replaced";
                                  }
                                  if (flag==0) {
                                      MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", Msg);

                                  }else if(flag==1){
                                      if(myDir.exists()) {
                                          LoginActivity obj= new LoginActivity();
                                          String[] str = obj.GetSharePreferenceSession(ImageUplodingAcitvity.this);
                                          if(str!=null)
                                              CallRetrofitUploadToFolder(root + "/SinglaGroups/"+name, str[3], str[0], str[4], str[6], str[5], str[12]);
                                      }else{
                                          MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", "File not exist!!!");
                                      }
                                  }else if(flag==2){
                                      if(myDir.exists()) {
                                          try {
                                              Uri uri = null;
                                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                  uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", myDir);
                                              }else {
                                                  uri = Uri.fromFile(myDir);
                                              }
                                              Intent intent1 = new Intent();
                                              intent1.setAction(Intent.ACTION_SEND);
                                              intent1.putExtra(Intent.EXTRA_TEXT, (ItemCode == null ? "" : ItemCode));
                                              intent1.setType("text/plain");
                                              intent1.putExtra(Intent.EXTRA_STREAM, uri);
                                              intent1.setType("image/jpeg");
                                              intent1.setPackage("com.whatsapp");
                                              //intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                              startActivity(intent1);
                                          } catch (Exception e) {
                                              MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "Url Exception", "" + e.toString());
                                          }
                                      }else{
                                          MessageDialog.MessageDialog(ImageUplodingAcitvity.this, "", "File not exist!!!");
                                      }
                                  }

                              } catch(Exception e){
                                  // some action
                                  MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"",""+e.toString());
                              }
                              hidepDialog();
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                              MessageDialog.MessageDialog(ImageUplodingAcitvity.this,"Failed",""+errorDrawable.toString());
                              hidepDialog();
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                              showpDialog();
                          }
                      }
                );
    }
}
