package uploadimagesfiles.documentattachment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.HomeAcitvity;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.CustomEditText;
import com.singlagroup.customwidgets.MessageDialog;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;
import uploadimagesfiles.adapter.RecyclerViewFileImageAdapter;
import uploadimagesfiles.adapter.RecyclerViewFileImageWithDescriptionAdapter;
import uploadimagesfiles.documentattachment.datasets.FileImageDataset;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;

/**
 * Created by Rakesh on 14-Jan-17.
 */

public class DocumentAttachmentUploadActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> AllFilePaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> cameraPaths = new ArrayList<>();
    private ArrayList<Uri> cameraUris = new ArrayList<Uri>();
    private static final String TAG = DocumentAttachmentUploadActivity.class.getSimpleName();
    private int clicked=0;
    TextView tvStatus;
    ProgressBar progressBar;
    String EncodedString=null,ImagePath=null;
    Bitmap bitmap;
    private static final int RESULT_CAMERA_CAPTURE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Cam";
    public static final String IMAGE_NAME = "c.jpg";
    private Uri fileUri;
    private String Title="",BillID="",DivisionID="",PartyID="",EmpID="",EmpName="";
    private CommonSearchSpinnerFilterableAdapter commonAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_recyclerview_upload);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        try {
            this.context = DocumentAttachmentUploadActivity.this;
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Customer Hand Over Upload");
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            //btnUpload = (Button) findViewById(R.id.FloatActionButton_Upload);
            progressBar = (ProgressBar) findViewById(R.id.ProgressBar1);
            tvStatus = (TextView) findViewById(R.id.TextView_Status);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            RecyclerViewFileImageWithDescriptionAdapter.mapList = new ArrayList<>();
        }catch (Exception e){
            MessageDialog.MessageDialog(this,"Initialization",""+e.toString());
        }
    }
    private void GetIntentMethod(){
        try {
            Title = getIntent().getExtras().getString("Title","");
            BillID = getIntent().getExtras().getString("BillID","");
            DivisionID = getIntent().getExtras().getString("DivisionID","");
            PartyID = getIntent().getExtras().getString("PartyID","");
            actionBar.setTitle(Title+" Upload");
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Intent Exception",e.toString());
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_Gallery:
                onPickPhoto();
                if(dialog!=null)
                    dialog.dismiss();
                break;
            case R.id.textView_Document:
                onPickDoc();
                if(dialog!=null)
                    dialog.dismiss();
                break;
            case R.id.textView_Camera:
                //Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
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
            case R.id.FloatActionButton_Upload:
                //TODO: Upload Call
                DialogAskDescriptions();
                break;
        }
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
            MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private String[] ConvertIntoBase64(String Path){
        String[] str=new String[3];
        String ext = "";
        try{
            //TODO: Get Extension
            int p = Path.lastIndexOf('.');
            if (p >= 0) {
                str[0] = ext = Path.substring(p);
            }
            //TODO: Get File name
            int fileName = Path.lastIndexOf('/');
            if (fileName >= 0){
                //str[2] = Path.substring(fileName);
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());
                str[2] = "FileViaApp_"+timeStamp+"_"+str[0];
            }
            //TODO: Get File And Image Encoded into Base64 String
            if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".gif")) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = Resize(BitmapFactory.decodeFile(Path, options), 0);
                if (bitmap != null)
                    str[1] = EncodeIntoBase64FromBitmap(bitmap);
            }else{
                InputStream inputStream = new FileInputStream(new File(Path));
                byte[] data;
                data = IOUtils.toByteArray(inputStream);
                // Encode Image to String
                str[1] = Base64.encodeToString(data, 0);
            }
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(context,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  str;
    }
    //TODO: AsyncTask - To convert Image to String
    private void EncodeImagetoString() {
        EncodedString=null;
        new AsyncTask<Void, Void, String>() {

        protected void onPreExecute() {
            progressDialog.setMessage("Converting...");
            progressDialog.show();
        };
        @Override
        protected String doInBackground(Void... params) {
            try{
                InputStream  inputStream = new FileInputStream(new File("pathfileName"));
                byte[] data;
                data = IOUtils.toByteArray(inputStream);
                System.out.println("covertByte:"+data.toString());
                // Encode Image to String
                EncodedString = Base64.encodeToString(data, 0);
            }catch (Exception e) {
                Log.i("Er", " "+e.getMessage());
            }
            return "";
        }
        @Override
        protected void onPostExecute(String msg) {
            progressDialog.setMessage("Calling Upload");
            // Put converted Image string into Async Http Post param
            //String[] arr=fileName.split('.');
            String ext="";
//            int i=fileName.lastIndexOf('.');
//            if(i>=0)
//            {
//                ext=fileName.substring(i);
//            }
//            params.put("image", encodedString);
//            params.put("Ext", ext);
//            params.put("UserID", UserName);
//            System.out.println(" aRRLength: "+ext);
//            System.out.println("UserName:"+UserName);
//            // Trigger Image upload
//            triggerImageUpload();
        }
        }.execute(null, null, null);
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
            MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this,"Exception" ,"From Bitmap Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private void CallRetrofitGetItemInfo(final String Path, final String DeviceID, final String SessionID, final String UserID, String GodownID, final String DivisionID, final String CompanyID, final String BranchID , final String VType, final String VHeading , final String VID , String Description, String Remarks, final String Type, final int index, final int size){
        showpDialog();
        final ApiInterface apiService = ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("GodownID", GodownID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("BranchID", BranchID);
        params.put("VType", VType);
        params.put("VHeading", VHeading);
        params.put("Description", Description);
        params.put("VID", VID);
        params.put("ThumbAttechment", "temp");
        params.put("FileName", ConvertIntoBase64(Path)[2]);
        params.put("Remarks", Remarks);
        params.put("Type", Type);
        Log.d(TAG, "Document upload Parameters: " + params.toString());
        params.put("Attechment", ConvertIntoBase64(Path)[1]);
        Call<ResponseFileImageUploadDataset> call = apiService.getDocAttechVoucherUpload(params);
        call.enqueue(new Callback<ResponseFileImageUploadDataset>() {
            @Override
            public void onResponse(Call<ResponseFileImageUploadDataset> call, retrofit2.Response<ResponseFileImageUploadDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            MessageDialogByIntent(context, "", msg);
                        } else {
                            MessageDialogByIntent(context, "", msg);
                        }
                        hidepDialog();
                    } else {
                        MessageDialog.MessageDialog(context, "", "Upload Unsuccessfull" + response.code());
                        hidepDialog();
                    }
                } catch (Exception e) {
                    Log.e(TAG, " Exception:" + e.getMessage());
                    MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this, "Item image Info API", "" + e.toString());
                    hidepDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseFileImageUploadDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
                MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this, "Failure", "Item image Info API"+t.toString());
                hidepDialog();
            }
        });
    }
    private void CallVolleyCustomerHandOverUpload(final String Path,final String DeviceID,final String SessionID,final String CompanyID,final String DivisionID, final String UserID, final String PartyID, final String BillID, final String EmpID, final String Remarks){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyClosedBillUpdate", new Response.Listener<String>()
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
                        MessageDialogByIntent(context, "", Msg);
                    } else {
                        MessageDialogByIntent(context, "", Msg);
                    }
                    hidepDialog();
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
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("DivisionID", DivisionID);
                params.put("UserID", UserID);
                params.put("PartyID", PartyID);
                params.put("BillID", BillID);
                params.put("EmpID", EmpID);
                params.put("Remarks", Remarks);
                params.put("FileName", Path.isEmpty() ? "" : ConvertIntoBase64(Path)[2]);
                params.put("Attachment", Path.isEmpty() ? "" : ConvertIntoBase64(Path)[1]);
                Log.i(TAG,"Customer Hand Over Upload parameters:"+params.toString());
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
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                }
                addThemToView(photoPaths,docPaths,cameraPaths);
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                addThemToView(photoPaths,docPaths,cameraPaths);
                break;
            case DocumentAttachmentUploadActivity.RESULT_CAMERA_CAPTURE:
                if (resultCode == Activity.RESULT_OK && requestCode==RESULT_CAMERA_CAPTURE) {

                    photoPaths = new ArrayList<>();
                    docPaths = new ArrayList<>();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // images
                    options.inSampleSize = 2;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (fileUri.getPath().contains("Cam/")) {
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/" + fileUri.getLastPathSegment());
                            cameraUris.add(Uri.fromFile(file));
                            ImagePath=file.getPath();
                            cameraPaths.add(ImagePath);
                            addThemToView(photoPaths, docPaths, cameraPaths);
                        }
                    }else {
                        cameraUris.add(fileUri);
                        ImagePath=fileUri.getPath();
                        cameraPaths.add(ImagePath);
                        addThemToView(photoPaths, docPaths, cameraPaths);
                    }
//                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
//                    //ResizeImages(bitmap, ImagePath);
//                    this.bitmap = bitmap;
                }
                break;
        }
    }
    //TODO: Gallery Image View
    private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths,ArrayList<String> cameraPaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if(imagePaths!=null)
            filePaths.addAll(imagePaths);
        if(docPaths!=null)
            filePaths.addAll(docPaths);
        if(cameraPaths!=null)
            filePaths.addAll(cameraPaths);

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
            AllFilePaths = new ArrayList<String>();
            AllFilePaths.addAll(filePaths);
            RecyclerViewFileImageAdapter adapter = new RecyclerViewFileImageAdapter(this, filePaths);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else{
            Snackbar.make(recyclerView,"Documents & Images not selected",Snackbar.LENGTH_LONG).show();
        }
    }
    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT-docPaths.size();
        if((docPaths.size()+photoPaths.size()+cameraPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this);
    }
    public void onPickDoc() {
        int maxCount = MAX_ATTACHMENT_COUNT-photoPaths.size();
        if((docPaths.size()+photoPaths.size()+cameraPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.AppTheme)
                    .pickFile(this);
    }
    private Bitmap Resize(Bitmap bitmap,int flag){
        if(flag==0){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int boundBoxInDp1=1000,boundBoxInDp2=1000;

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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
        if (type == RESULT_CAMERA_CAPTURE)
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
    private void CameraFileDelete(Uri fileUri){
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
            }
        }catch (IOException e){
            MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this,"Exception",e.toString());
        }
    }
    //TODO: Image Rotation
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }
    private void CustomAttachmentDialog(){
        dialog = new Dialog(new ContextThemeWrapper(DocumentAttachmentUploadActivity.this, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_attachment);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView txtViewGallery = (TextView) dialog.findViewById(R.id.textView_Gallery);
        TextView txtViewDocument = (TextView) dialog.findViewById(R.id.textView_Document);
        TextView txtViewCamera = (TextView) dialog.findViewById(R.id.textView_Camera);
        txtViewGallery.setOnClickListener(DocumentAttachmentUploadActivity.this);
        txtViewDocument.setOnClickListener(DocumentAttachmentUploadActivity.this);
        txtViewCamera.setOnClickListener(DocumentAttachmentUploadActivity.this);
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
                    if (fileUri!=null){CameraFileDelete(fileUri);}
                    dialog.dismiss();
                    DatabaseSqlLiteHandlerUserInfo DBUserInfo =  new DatabaseSqlLiteHandlerUserInfo(context);
                    Map<String,String> map = DBUserInfo.getModulePermissionByVtype(32);
                    String ClassName = map.get("ContentClass");
                    if (!ClassName.isEmpty() && ClassName!=null) {
                        try {
                            //TODO: Set Bundle
                            Bundle bundle = new Bundle();
                            bundle.putString("Title",map.get("Name"));
                            bundle.putInt("ViewFlag",Integer.valueOf(map.get("ViewFlag")));
                            bundle.putInt("EditFlag",Integer.valueOf(map.get("EditFlag")));
                            bundle.putInt("CreateFlag",Integer.valueOf(map.get("CreateFlag")));
                            bundle.putInt("RemoveFlag",Integer.valueOf(map.get("RemoveFlag")));
                            bundle.putInt("PrintFlag",Integer.valueOf(map.get("PrintFlag")));
                            bundle.putInt("ImportFlag",Integer.valueOf(map.get("ImportFlag")));
                            bundle.putInt("ExportFlag",Integer.valueOf(map.get("ExportFlag")));
                            //TODO: Intent the Activities by Class
                            Intent intent = new Intent(context, Class.forName(ClassName));
                            intent.putExtra("PermissionBundle",bundle);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            MessageDialog.MessageDialog(context, "Error", ""+e.toString());
                        }
                    }
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    private void DialogAskDescriptions(){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_document_attach_emp_remark);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //TODO: Declaration
        final TextView textView = (TextView) dialog.findViewById(R.id.text_view);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
        final CommonSearchableSpinner spnEmpList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_Emp_List);
        final EditText edtRemarks=(EditText)dialog.findViewById(R.id.editText_remarks);
        Button btnUpload=(Button)dialog.findViewById(R.id.btn_Upload);
        //TODO: Spinner Employee List
        LoginActivity obj=new LoginActivity();
        final String[] str = obj.GetSharePreferenceSession(context);
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallVolleyEmployeeList(progressBar, spnEmpList, textView, str[3], str[0],str[14], str[5], str[4]);
        }else{
            MessageDialog.MessageDialog(context,"",status);
        }
        //TODO: Remark
        edtRemarks.setFilters(new InputFilter[] {CustomEditText.SetFilter()});
        //TODO: Button Upload
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Remarks = edtRemarks.getText().toString().trim();
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    if (str != null && !EmpID.isEmpty()) {
                        if (!AllFilePaths.isEmpty()) {
                            int index = 0;
                            dialog.dismiss();
                            CallVolleyCustomerHandOverUpload(AllFilePaths.get(index), str[3], str[0], str[14], DivisionID, str[4], PartyID, BillID, EmpID, Remarks);
                        } else {
                            dialog.dismiss();
                            CallVolleyCustomerHandOverUpload("", str[3], str[0], str[14], DivisionID, str[4], PartyID, BillID, EmpID, Remarks);
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","Please Select an Employee");
                    }
                } else {
                    MessageDialog.MessageDialog(DocumentAttachmentUploadActivity.this, "", status);
                }
            }
        });
        dialog.show();
    }
    //TODO: Call Volley Employee List type Spinner
    private void CallVolleyEmployeeList(final ProgressBar progressBar, final CommonSearchableSpinner spnEmpList,final TextView textView, final String DeviceID,final String SessionID,final String CompanyID,final String DivisionID, final String UserID){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"EmployeeList", new Response.Listener<String>()
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
                        ArrayList<Map<String,String>> maplist = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", jsonArrayScfo.getJSONObject(i).getString("ID"));
                            map.put("Name", jsonArrayScfo.getJSONObject(i).getString("Name"));
                            maplist.add(map);
                        }
                        if (maplist.isEmpty()){
                            MessageDialog.MessageDialog(context,"","No Record found");
                            commonAdapter = new CommonSearchSpinnerFilterableAdapter(context, maplist, maplist);
                            spnEmpList.setAdapter(commonAdapter);
                            EmpID = "";
                            EmpName = "";
                        }else {
                            commonAdapter = new CommonSearchSpinnerFilterableAdapter(context, maplist, maplist);
                            spnEmpList.setAdapter(commonAdapter);
                            spnEmpList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    textView.setVisibility(View.GONE);
                                    Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                                    EmpID = map.get("ID");
                                    EmpName = map.get("Name");
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBar.setVisibility(View.GONE);
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("DivisionID", DivisionID);
                params.put("UserID", UserID);
                Log.d(TAG,"Select customer for order parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
