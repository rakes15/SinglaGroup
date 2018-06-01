package uploadimagesfiles;

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
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.SessionManage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import DatabaseController.CommanStatic;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import uploadimagesfiles.adapter.RecyclerViewFileImageAdapter;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;

/**
 * Created by Rakesh on 19-Oct-16.
 */

public class FileImageUplodingAcitvity extends AppCompatActivity implements View.OnClickListener {
    private ActionBar actionBar;
    private Context context;
    private RecyclerView recyclerView;
    private Button btnUpload;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private int MAX_ATTACHMENT_COUNT = 5;
    private ArrayList<String> AllFilePaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> cameraPaths = new ArrayList<>();
    private ArrayList<Uri> cameraUris = new ArrayList<Uri>();
    private static final String TAG = FileImageUplodingAcitvity.class.getSimpleName();
    private int index=0,flag=0;
    TextView tvStatus;
    ProgressBar progressBar;
    private static final int RESULT_CAMERA_CAPTURE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "Cam";
    public static final String IMAGE_NAME = "c.jpg";
    private Uri fileUri;
    String ImagePath;
    boolean apiResponse = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_upload);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = FileImageUplodingAcitvity.this;
            this.actionBar = getSupportActionBar();
            this.actionBar.setDisplayHomeAsUpEnabled(true);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            btnUpload = (Button) findViewById(R.id.FloatActionButton_Upload);
            progressBar = (ProgressBar) findViewById(R.id.ProgressBar1);
            tvStatus = (TextView) findViewById(R.id.TextView_Status);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
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
            }else {
                MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"Exception",e.toString());
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
                    MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"",status);
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override public void onClick(View v) {
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
                if(!photoPaths.isEmpty() || !docPaths.isEmpty() || !cameraPaths.isEmpty()){
                    //TODO: Login Request
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj= new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(FileImageUplodingAcitvity.this);
                        if(str!=null)
                        if(!AllFilePaths.isEmpty()) {
                            Log.d(TAG,"AllPaths: "+AllFilePaths.toString());
                            for (int index = 0; index < AllFilePaths.size(); index++) {
                                CallRetrofitGetItemInfo(AllFilePaths.get(index), str[3], str[0], str[4], str[6], str[5], str[14], str[12], index, AllFilePaths.size());
                            }
                        }
                    }else{
                        MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"",status);
                    }
                }else{
                    MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"","Please attachment atleast one File or Image");
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
            MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  str;
    }
    private void CallRetrofitGetItemInfo(final String Path, String DeviceID, String SessionID, String UserID, String GodownID, String DivisionID, String CompanyID, String UserName, final int index, final int size){
        if (index==0){ showpDialog(); }
        final ApiInterface apiService = ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("Ext", ConvertIntoBase64(Path)[0]);
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("GodownID", GodownID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("UserName", UserName);
        params.put("image", ConvertIntoBase64(Path)[1]);
        params.put("FileName", ConvertIntoBase64(Path)[2].substring(1));
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
                            if (size==(index+1)) { MessageDialogByIntent(FileImageUplodingAcitvity.this, "", msg); }
                        } else {
                            if (size==(index+1)) { MessageDialogByIntent(FileImageUplodingAcitvity.this, "", msg); }
                        }
                        if (size==(index+1)) { hidepDialog(); }
                    } else {
                        MessageDialog.MessageDialog(FileImageUplodingAcitvity.this, "", "Server response: " + response.code());
                        if (size==(index+1)) { hidepDialog(); }
                    }
                } catch (Exception e) {
                    Log.e(TAG, " Exception:" + e.getMessage());
                    MessageDialog.MessageDialog(FileImageUplodingAcitvity.this, "Item image Info API", "" + e.toString());
                    if (size==(index+1)) { hidepDialog(); }
                }
            }

            @Override
            public void onFailure(Call<ResponseFileImageUploadDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
                MessageDialog.MessageDialog(FileImageUplodingAcitvity.this, "Failure", "Item image Info API:\n"+t.toString());
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
                case RESULT_CAMERA_CAPTURE:
                    if (resultCode == Activity.RESULT_OK && requestCode==RESULT_CAMERA_CAPTURE) {

                        //cameraPaths = new ArrayList<>();
//                        docPaths = new ArrayList<>();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // images
                        options.inSampleSize = 2;
                        Log.d(TAG,"Captured Image:"+fileUri.getPath()+"\nLastPathSegment:"+fileUri.getLastPathSegment());
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
//                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
//                        //ResizeImages(bitmap, ImagePath);
//                        this.bitmap = bitmap;
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
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
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

            int boundBoxInDp1=600,boundBoxInDp2=800;

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
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/",IMAGE_DIRECTORY_NAME);

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
        if (type == RESULT_CAMERA_CAPTURE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
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
            MessageDialog.MessageDialog(FileImageUplodingAcitvity.this,"Exception",e.toString());
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
        dialog = new Dialog(new ContextThemeWrapper(FileImageUplodingAcitvity.this, R.style.DialogSlideAnim));
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
        txtViewGallery.setOnClickListener(FileImageUplodingAcitvity.this);
        txtViewDocument.setOnClickListener(FileImageUplodingAcitvity.this);
        txtViewCamera.setOnClickListener(FileImageUplodingAcitvity.this);
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
                    if (!cameraUris.isEmpty()){
                        for(int i=0; i<cameraUris.size();i++){
                            CameraFileDelete(cameraUris.get(i));
                        }
                    }
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
}
