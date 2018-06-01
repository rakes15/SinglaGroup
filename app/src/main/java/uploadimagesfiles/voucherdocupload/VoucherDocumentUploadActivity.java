package uploadimagesfiles.voucherdocupload;

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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;

import org.apache.commons.io.IOUtils;
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
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;
import uploadimagesfiles.adapter.RecyclerViewFileImageWithDescriptionAdapter;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;
import uploadimagesfiles.voucherdocupload.datasets.FileImageDataset;

/**
 * Created by Rakesh on 14-Jan-17.
 */

public class VoucherDocumentUploadActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private int MAX_ATTACHMENT_COUNT = 5;
    private ArrayList<String> AllFilePaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> cameraPaths = new ArrayList<>();
    private ArrayList<Uri> cameraUris = new ArrayList<Uri>();
    private static final String TAG = VoucherDocumentUploadActivity.class.getSimpleName();
    private int clicked=0;
    TextView tvStatus;
    ProgressBar progressBar;
    String EncodedString=null,ImagePath=null;
    Bitmap bitmap;
    private static final int RESULT_CAMERA_CAPTURE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Cam";
    public static final String IMAGE_NAME = "c.jpg";
    private Uri fileUri;
    private String VType="",VHeading="",VID="",Type="",ID="",Description="";
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
            this.context = VoucherDocumentUploadActivity.this;
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Document Attachment Upload");
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
            VType = getIntent().getExtras().getString("VType","");
            VHeading = getIntent().getExtras().getString("VHeading","");
            VID = getIntent().getExtras().getString("VID","");
            Type = getIntent().getExtras().getString("Type","");
            ID = getIntent().getExtras().getString("ID","");
            Description = getIntent().getExtras().getString("Description","");
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
                int c=0;
                String FileName = "";
                if(!photoPaths.isEmpty() || !docPaths.isEmpty() || !cameraPaths.isEmpty()){
                    //TODO: Upload Request
                    //DialogAskDescriptions();
                    List<FileImageDataset> datasetList = RecyclerViewFileImageWithDescriptionAdapter.mapList;
                    if (!datasetList.isEmpty()) {
                        c=0;
                        FileName = "";
                        for (int i=0; i<datasetList.size(); i++){
                            if (datasetList.get(i).getDescription().isEmpty()) {
                                c = 1;
                                File file = new File(datasetList.get(i).getPath());
                                FileName = file.getName();
                                break;
                            }
                        }
                        if (c == 1){
                            MessageDialog.MessageDialog(context,"Alert","Please add Discription in "+FileName);
                        }else if (c == 0){
                            if (clicked == 0) {
                                String status = NetworkUtils.getConnectivityStatusString(context);
                                if (!status.contentEquals("No Internet Connection")) {
                                    if (!VType.isEmpty() && !VHeading.isEmpty() && !VID.isEmpty()) {
                                        for (int index = 0; index < datasetList.size(); index++) {
                                            LoginActivity obj = new LoginActivity();
                                            String[] str = obj.GetSharePreferenceSession(context);
                                            if (str != null)
                                                clicked = 1;
                                                CallRetrofitGetItemInfo(datasetList.get(index).getPath(), str[3], str[0], str[4], str[6], str[5], str[14], str[15], VType, VHeading, VID, datasetList.get(index).getDescription(), "", Type, index, datasetList.size());
                                        }
                                    } else {
                                        MessageDialog.MessageDialog(context, "Alert", "Something went wrong");//VType:"+VType+ "\t VHeading:"+VHeading+"\tVID:"+VID);
                                    }
                                } else {
                                    MessageDialog.MessageDialog(context, "", status);
                                }
                            }else{
                                Toast.makeText(context,"already in process...",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{
                    MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this,"","Please attachment atleast one File or Image");
                }
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
            MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this,"Exception" ,"Convert into byte: "+e.toString());
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
            MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this,"Exception" ,"From Bitmap Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
    private void CallRetrofitGetItemInfo(final String Path, final String DeviceID, final String SessionID, final String UserID, String GodownID, final String DivisionID, final String CompanyID, final String BranchID , final String VType, final String VHeading , final String VID , String Description, String Remarks, final String Type, final int index, final int size){
        if (index==0){ showpDialog(); }
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
        Log.d(TAG, "Voucher upload Parameters: " + params.toString());
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
                            if (ID.isEmpty()) {
                                if (size==(index+1)) { MessageDialogByIntent(context, "", msg); }
                            }else {
                                CallVolleyDeleteAttachment(DeviceID, SessionID, UserID, CompanyID, DivisionID, BranchID,VType,VHeading,VID,Type,ID,msg);
                            }
                        } else {
                            if (size==(index+1)) { MessageDialogByIntent(context, "", msg); }
                        }
                        if (size==(index+1)) { hidepDialog(); }
                    } else {
                        //MessageDialog.MessageDialog(context, "", "Server response: " + response.code());
                        if (size==(index+1)) { hidepDialog(); }
                    }
                } catch (Exception e) {
                    Log.e(TAG, " Exception:" + e.getMessage());
                    MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this, "Item image Info API", "" + e.toString());
                    if (size==(index+1)) { hidepDialog(); }
                }
            }

            @Override
            public void onFailure(Call<ResponseFileImageUploadDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.MessageDialog(VoucherDocumentUploadActivity.this, "Failure", "Item image Info API", t.toString());
                if (size==(index+1)) { hidepDialog(); }
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
            case VoucherDocumentUploadActivity.RESULT_CAMERA_CAPTURE:
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
            //RecyclerViewFileImageAdapter adapter = new RecyclerViewFileImageAdapter(this, filePaths);
            List<FileImageDataset> fileImageDatasetList = new ArrayList<>();
            if (RecyclerViewFileImageWithDescriptionAdapter.mapList.isEmpty()){
                for(String path : filePaths){
                    fileImageDatasetList.add(new FileImageDataset(path, "", false));
                }
            }else {
                for(String path : filePaths){
                    String Des = "";
                    boolean flag = false;
                    for (int i = 0; i<RecyclerViewFileImageWithDescriptionAdapter.mapList.size(); i++) {
                        if (path.equals(RecyclerViewFileImageWithDescriptionAdapter.mapList.get(i).getPath())){
                            Des = RecyclerViewFileImageWithDescriptionAdapter.mapList.get(i).getDescription();
                            flag = RecyclerViewFileImageWithDescriptionAdapter.mapList.get(i).getDescFlag();
                        }
                    }
                    fileImageDatasetList.add(new FileImageDataset(path, Des, flag));
                }
            }
            RecyclerViewFileImageWithDescriptionAdapter adapter = new RecyclerViewFileImageWithDescriptionAdapter(this,fileImageDatasetList);
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
            MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this,"Exception",e.toString());
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
        TextView txtViewGallery = (TextView) dialog.findViewById(R.id.textView_Gallery);
        TextView txtViewDocument = (TextView) dialog.findViewById(R.id.textView_Document);
        TextView txtViewCamera = (TextView) dialog.findViewById(R.id.textView_Camera);
        txtViewGallery.setOnClickListener(VoucherDocumentUploadActivity.this);
        txtViewDocument.setOnClickListener(VoucherDocumentUploadActivity.this);
        txtViewCamera.setOnClickListener(VoucherDocumentUploadActivity.this);
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
                    finish();
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    private void DialogAskDescriptions(){//final String Path, final String DeviceID, final String SessionID, final String UserID, final String GodownID, final String DivisionID,final String CompanyID, final String BranchID , final String VType, final String VHeading , final String VID , final String Attachment,final String Type){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_name_remaks);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final EditText edtDescription=(EditText)dialog.findViewById(R.id.editTxt_name);
        final EditText edtRemarks=(EditText)dialog.findViewById(R.id.editTxt_Remarks);
        edtRemarks.setVisibility(View.GONE);
        edtDescription.setHint("Description");
        //edtRemarks.setHint("Remarks");
        edtDescription.setText(""+Description);
        Button upload=(Button)dialog.findViewById(R.id.button_Approve);
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        upload.setText("Upload");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Descrption = edtDescription.getText().toString().trim();
                String Remarks = "";//edtRemarks.getText().toString().trim();
                if (!Descrption.isEmpty() && Descrption !=null && !Type.isEmpty()) {
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        if (!AllFilePaths.isEmpty()) {
                            for (int index = 0; index < AllFilePaths.size(); index++) {
                                if (!VType.isEmpty() && !VHeading.isEmpty() && !VID.isEmpty()) {
                                    LoginActivity obj = new LoginActivity();
                                    String[] str = obj.GetSharePreferenceSession(VoucherDocumentUploadActivity.this);
                                    if (str != null)
                                        dialog.dismiss();
                                        CallRetrofitGetItemInfo(AllFilePaths.get(index), str[3], str[0], str[4], str[6], str[5], str[14], str[15], VType, VHeading, VID, Descrption, Remarks,Type,index,AllFilePaths.size());
                                        //DialogAskDescriptions(AllFilePaths.get(index), str[3], str[0], str[4], str[6], str[5], str[14], str[15], VType, VHeading, VID, Attachment, Type);
                                }
                            }
                        }

                    } else {
                        MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this, "", status);
                    }
                }else{
                    MessageDialog.MessageDialog(VoucherDocumentUploadActivity.this, "", "Description is mandatory!!!");
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
    private void CallVolleyDeleteAttachment(final String DeviceID,final String SessionID, final String UserID,final String CompanyID,final String DivisionID,final String BranchID,final String VType,final String VHeading,final String VID,final String Type,final String ID,final String Message) {
        //showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"DocAttechmentDel", new Response.Listener<String>()
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
                        //MessageDialog.MessageDialogShowFinish(context,"",Msg);
                        MessageDialogByIntent(context, "",Message);
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
                Map<String, String>  params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("DivisionID", DivisionID);
                params.put("BranchID", BranchID);
                params.put("VType", VType);
                params.put("VHeading", VHeading);
                params.put("VID", VID);
                params.put("Type", Type);
                params.put("ID", ID);
                Log.d(TAG,"Delete Attachment parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
