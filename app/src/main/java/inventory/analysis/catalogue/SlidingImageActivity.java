package inventory.analysis.catalogue;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.singlagroup.R;
import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DatabaseController.CommanStatic;
import inventory.analysis.catalogue.adapter.SlidingImageAdapter;
import inventory.analysis.catalogue.responsedataset.ResponseImageListDataset;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

public class SlidingImageActivity extends AppCompatActivity {
    private static ImageView imgViewClose,imgViewNext,imgViewPrevious;
    private static ViewPager mPager;
    private int selectedPage = 0;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    ProgressDialog spotsDialog;
    private static String TAG = SlidingImageActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.slidingimage_viewpager);
        //TODO:Call google analytics
        final String Path=getIntent().getExtras().getString("Path");
        String SelectedPath=getIntent().getExtras().getString("SelectedPath");
        spotsDialog = new ProgressDialog(SlidingImageActivity.this);
        spotsDialog.setCanceledOnTouchOutside(false);
        spotsDialog.setMessage("Loading...");
        //TODO: CallRetrofitImageList
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallRetrofitImageList(Path,SelectedPath);
        }else{
            Snackbar.make(mPager,status,Snackbar.LENGTH_LONG).show();
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    String SelectedPath=getIntent().getExtras().getString("SelectedPath");
                    CallRetrofitImageList(Path,SelectedPath);
                }else{
                    Snackbar.make(mPager,status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onPause(){
        super.onPause();

    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return true;
    }
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }
    private void CallRetrofitImageList(final String Path, final String SelectedPath){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("PrimaryImagePath", Path);
        Call<ResponseImageListDataset> call = apiService.getInventoryImageList(params);
        call.enqueue(new Callback<ResponseImageListDataset>() {
            @Override
            public void onResponse(Call<ResponseImageListDataset> call, retrofit2.Response<ResponseImageListDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            String[] strList = response.body().getResult();
                            ArrayList<String> imageList = new ArrayList<String>();
                            for (int i = 0; i < strList.length; i++) {
                                if(SelectedPath.equals(strList[i])){
                                    selectedPage=i;
                                }
                                String t = strList[i].replaceAll("440x600", "1025x1400");//"1025x1400"orignal
                                imageList.add(t);
                            }
                            init(imageList);
                            System.out.println("Url:"+imageList.toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }else{
                        ArrayList<String> imageList = new ArrayList<String>();
                        imageList.add(Path);
                        init(imageList);
                    }
                }catch (Exception e){
                    Log.e(TAG,"Image List Exception:"+e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseImageListDataset> call, Throwable t) {
                Log.e(TAG,"Image List Failure:"+t.toString());
                Toast.makeText(getApplicationContext(),"Image List Failure",Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void init(ArrayList<String> ImageArray){

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImageAdapter(SlidingImageActivity.this,ImageArray));
        imgViewClose = (ImageView)findViewById(R.id.imageView_close);
        imgViewNext = (ImageView)findViewById(R.id.imageView_Next);
        imgViewPrevious = (ImageView)findViewById(R.id.imageView_Previous);
        IconPageIndicator indicator = (IconPageIndicator) findViewById(R.id.iconPageIndicator);
        indicator.setViewPager(mPager);
        NUM_PAGES =ImageArray.size();
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        imgViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(Update);
            }
        });
        imgViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                else {
                    mPager.setCurrentItem(currentPage--, true);
                }
            }
        });
        imgViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
        mPager.setCurrentItem(selectedPage, true);
    }
}
