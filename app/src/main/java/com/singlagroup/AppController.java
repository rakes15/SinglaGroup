package com.singlagroup;

import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singlagroup.customwidgets.SessionManage;
import com.singlagroup.datasets.DeviceInfoDataset;
import com.singlagroup.utils.LruBitmapCache;

import java.util.ArrayList;

import DatabaseController.DatabaseSqlLiteHandlerActiveSessionManage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import whatsapp.autoresponder.Api.RestApi;
import whatsapp.autoresponder.Model.Rest.ListResponse;
import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.NotiHandler.NotiListenerService;
import whatsapp.autoresponder.Service.BackgroundService;
import whatsapp.autoresponder.Service.SchedulerService;
import whatsapp.autoresponder.Utils.Global;
import whatsapp.autoresponder.Utils.Preferences;
import whatsapp.database.DBSqliteWhatsApp;

public class AppController extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
 
    public static final String TAG = AppController.class.getSimpleName();
 
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;
    public static final int STATE_UNKNOWN = 0x00;
    public static final int STATE_CREATED = 0x01;
    public static final int STATE_STARTED = 0x02;
    public static final int STATE_RESUMED = 0x03;
    public static final int STATE_PAUSED = 0x04;
    public static final int STATE_STOPPED = 0x05;
    public static final int STATE_DESTROYED = 0x06;

    private static final int FLAG_STATE_FOREGROUND = -1;
    private static final int FLAG_STATE_BACKGROUND = -2;

    private int mCurrentState = STATE_UNKNOWN;
    private int mStateFlag = FLAG_STATE_BACKGROUND;
    private DBSqliteWhatsApp DBWhatsApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mCurrentState = STATE_UNKNOWN;
        registerActivityLifecycleCallbacks(this);
        DBWhatsApp = new DBSqliteWhatsApp(getApplicationContext());
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public RequestQueue getRequestQueueParallel() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req) {
        //req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    //TODO:Background And Forground status
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // mCurrentState = STATE_CREATED;
    }
    @Override
    public void onActivityStarted(Activity activity) {
        if (mCurrentState == STATE_UNKNOWN || mCurrentState == STATE_STOPPED) {
            if (mStateFlag == FLAG_STATE_BACKGROUND) {
                //applicationWillEnterForeground();
                mStateFlag = FLAG_STATE_FOREGROUND;
            }
        }
        mCurrentState = STATE_STARTED;
    }
    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentState = STATE_RESUMED;
        Log.i(TAG,"Activity Resume: "+Activity.class.getSimpleName());
        String ActiveTime = "";
        if (HomeAcitvity.chronometerActiveTime!=null ) {
            HomeAcitvity.chronometerActiveTime.setBase(SystemClock.elapsedRealtime() + HomeAcitvity.timeWhenStopped);
            HomeAcitvity.chronometerActiveTime.start();
            ActiveTime = HomeAcitvity.chronometerActiveTime.getText().toString();
        }
        DatabaseSqlLiteHandlerActiveSessionManage DBSessionManage = new DatabaseSqlLiteHandlerActiveSessionManage(activity);
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(activity);
        if(str!=null && !ActiveTime.isEmpty()) {
            if (DBSessionManage.getAllDetailsExistBySessionID(str[0]).isEmpty()) {
                DBSessionManage.insertActiveSessionManageTable(str[0], "00:00:01");
            } else {
                DBSessionManage.UpdateLogin(str[0], (ActiveTime.length() == 5) ? "00:" + ActiveTime : ActiveTime);
            }
        }
        //TODO: Whats App Service Start
        UploadWhatsApp();
        managerForScrennLock();

        // Start NotiListner service
        //startService(new Intent(getApplicationContext(), NotiListenerService.class));
    }
    //todo: Screen over
    @Override
    public void onActivityPaused(Activity activity) {
        mCurrentState = STATE_PAUSED;
        Log.i(TAG,"Activity Pause: "+Activity.class.getSimpleName());
        String ActiveTime = "";
        if (HomeAcitvity.chronometerActiveTime!=null ) {
            HomeAcitvity.timeWhenStopped = HomeAcitvity.chronometerActiveTime.getBase() - SystemClock.elapsedRealtime();
            HomeAcitvity.chronometerActiveTime.stop();
            ActiveTime = HomeAcitvity.chronometerActiveTime.getText().toString();
            //Log.d(TAG, "SessionManage:" + ActiveTime);
        }
        DatabaseSqlLiteHandlerActiveSessionManage DBSessionManage = new DatabaseSqlLiteHandlerActiveSessionManage(activity);
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(activity);
        if(str!=null && !ActiveTime.isEmpty()) {
            DBSessionManage.UpdateLogout(str[0], (ActiveTime.length() == 5) ? "00:" + ActiveTime : ActiveTime);
            //Log.d(TAG, "SessionManage:" + ActiveTime);
            if (!DBSessionManage.getAllActiveTimesByLocalFlag().isEmpty()) {
                for (int i = 0; i < DBSessionManage.getAllActiveTimesByLocalFlag().size(); i++) {
                    SessionManage.CallRetrofitSessionActiveTimeBySessionID(AppController.this, str[3] , DBSessionManage.getAllActiveTimesByLocalFlag().get(i).get("SessionID"), str[4], str[14], DBSessionManage.getAllActiveTimesByLocalFlag().get(i).get("ActiveTime"));
                }
            }
        }else{
            Log.e(TAG, "Device info is null and Active time empty");
        }
        //TODO: Whats App Service Start
        managerForScrennLock();
        // Start NotiListner service
        //startService(new Intent(getApplicationContext(), NotiListenerService.class));
    }
    @Override
    public void onActivityStopped(Activity activity) {
        mCurrentState = STATE_STOPPED;
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        mCurrentState = STATE_DESTROYED;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (mCurrentState == STATE_STOPPED && level >= TRIM_MEMORY_UI_HIDDEN) {
            if (mStateFlag == FLAG_STATE_FOREGROUND) {
                //applicationDidEnterBackground();
                mStateFlag = FLAG_STATE_BACKGROUND;
            }
        }else if (mCurrentState == STATE_DESTROYED && level >= TRIM_MEMORY_UI_HIDDEN) {
            if (mStateFlag == FLAG_STATE_FOREGROUND) {
                //applicationDidDestroyed();
                mStateFlag = FLAG_STATE_BACKGROUND;
            }
        }
    }

    private void managerForScrennLock() {
        startService(new Intent(getApplicationContext(), BackgroundService.class));

//        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        if (km.inKeyguardRestrictedInputMode()) {
//            // Locked
////            startService(new Intent(getApplicationContext(), BackgroundService.class));
//        } else {
//            // Unlocked
            if (!Preferences.getPreferenceBoolean(getApplicationContext(), Preferences.KEY_IN_EXECUTION, false)) {

                ArrayList<SchedulerModel> queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
                int index = 0;
                if (queue != null && queue.size() > 0) {
                    // TODO : Update action flag
                    DBWhatsApp.UpdateByFlag(queue.get(index).getId(),DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);

                    Intent scheduler = new Intent(getApplicationContext(), SchedulerService.class);
                    scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, queue.get(index));
                    getApplicationContext().startService(scheduler);
                }
            }
//        }
    }
    private void UploadWhatsApp(){
        MainActivity obj = new MainActivity();
        DeviceInfoDataset deviceInfo = obj.GetSharePreferenceDeviceInfo(getApplicationContext());
        if (deviceInfo != null) {
            //TODO: Call Send Status Api Registered
            ArrayList<SchedulerModel> queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
            for(int i = 0; i < queue.size(); i++) {
                whatsappMsgStatusUpdateApi( deviceInfo.getAndroidDUID(),
                        queue.get(i).getMessageId(),
                        DBSqliteWhatsApp.WHATS_APP_REGISTERED_FLAG,
                        queue.get(i).getUpdateTime(),
                        queue.get(i).getId()
                );
            }
            //TODO: Call Send Status Api Unregistered
            queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_UNREGISTERED_FLAG);
            for(int i = 0; i < queue.size(); i++) {
                whatsappMsgStatusUpdateApi( deviceInfo.getAndroidDUID(),
                        queue.get(i).getMessageId(),
                        DBSqliteWhatsApp.WHATS_APP_UNREGISTERED_FLAG,
                        queue.get(i).getUpdateTime(),
                        queue.get(i).getId()
                );
            }
        }
    }
    private void whatsappMsgStatusUpdateApi(String DeviceID, String MsgID,final int Status, String StatusDateTime,final String Id) {
        //Log.e(TAG,"Send Api Called:DeviceID:"+DeviceID+"\t MsgID:"+MsgID);
        RestApi api = Global.initRetrofit2(getApplicationContext());
        if (Global.isNetworkAvailable(getApplicationContext())) {

            Call<ListResponse<String>> listResponseCall = api.whatsappMsgStatusUpdateApi(DeviceID,MsgID,""+Status,StatusDateTime);

            listResponseCall.enqueue(new Callback<ListResponse<String>>() {
                @Override
                public void onResponse(Call<ListResponse<String>> call, Response<ListResponse<String>> response) {
                    Log.e("Scheduler Service", "onResponse:"+response);
                    // TODO: Delete row behalf of Action Flag 2 for registered and 5 for unregistered
                    DBWhatsApp.DeleteByFlag(Id, DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, Status);
                }
                @Override
                public void onFailure(Call<ListResponse<String>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("Scheduler Service", "onFailure"+t.toString());
                }
            });
        }
    }

}