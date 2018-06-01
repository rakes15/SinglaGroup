package services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import com.singlagroup.LoginActivity;
import com.singlagroup.customwidgets.SessionManage;
import java.util.List;
import java.util.Map;
import DatabaseController.DatabaseSqlLiteHandlerActiveSessionManage;
/**
 * Created by Rakesh on 06-April-17.
 */
public class SinglaGroupsServices extends Service {
    private Context context;
    private boolean isRunning;
    private Thread backgroundThread;
    private final String TAG = SinglaGroupsServices.class.getSimpleName();
    public SinglaGroupsServices() {}
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }
    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            stopSelf();
        }
    };
    @Override
    public void onDestroy() {
        this.isRunning = false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
