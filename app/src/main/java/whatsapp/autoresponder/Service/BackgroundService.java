package whatsapp.autoresponder.Service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.NotiHandler.NotiListenerService;
import whatsapp.autoresponder.Utils.Preferences;
import whatsapp.database.DBSqliteWhatsApp;

/**
 * Created by iqor on 2/23/2018.
 */

public class BackgroundService extends Service {

    DBSqliteWhatsApp DBWhatsApp;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    Timer timer = new Timer();
    TimerTask updateProfile = new CustomTimerTask(BackgroundService.this);

    @Override
    public void onCreate() {
        super.onCreate();
        DBWhatsApp = new DBSqliteWhatsApp(getApplicationContext());
        timer.scheduleAtFixedRate(updateProfile, 0, 5000);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer.cancel();
    }

    public class CustomTimerTask extends TimerTask {

        private Context context;
        private Handler mHandler = new Handler();

        public CustomTimerTask(Context con) {
            this.context = con;
        }

        @Override
        public void run() {
            new Thread(new Runnable() {

                public void run() {

                    mHandler.post(new Runnable() {
                        public void run() {
//                            Toast.makeText(context, "DISPLAY YOUR MESSAGE", Toast.LENGTH_SHORT).show();
                            //TODO: --------------  Update flag 1 for Intrrupted Whats app msg
                            ArrayList<SchedulerModel> schmArrList = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
                            if (!schmArrList.isEmpty()){
                                for (int i = 0; i < schmArrList.size(); i++){
                                    long diff = DateFormatsMethods.getTimeDifferenceInMinutes(schmArrList.get(i).getUpdateTime(),DateFormatsMethods.getDateTime());
                                    if (diff > 2){
                                        DBWhatsApp.UpdateByFlag(schmArrList.get(i).getId() , DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG , DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
                                    }
                                }
                            }
                            // Power Manager
                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                                if (pm.isInteractive()) {
                                    managerForScrennLock();
                                }
                            } else {
                                if (pm.isScreenOn()) {
                                    managerForScrennLock();
                                }
                            }
                        }
                    });
                }
            }).start();

        }
    }

    private void managerForScrennLock() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            // Locked

        } else {
            // Unlocked
//            if (!Preferences.getPreferenceBoolean(getApplicationContext(), Preferences.KEY_IS_LOCK, false)) {
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
//                else {
//                    stopSelf();
//                }
            }
        }
    }
}
