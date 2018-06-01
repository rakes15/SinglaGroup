package whatsapp.autoresponder.FCM;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.singlagroup.MainActivity;
import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;
import java.util.Map;
import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.Service.BackgroundService;
import whatsapp.autoresponder.Service.SchedulerService;
import whatsapp.autoresponder.Utils.Preferences;
import whatsapp.database.DBSqliteWhatsApp;

/**
 * Created by iqor on 1/19/2018.
 */

public class ARFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = ARFirebaseMessagingService.class.getSimpleName();

    private PowerManager.WakeLock wakeLock;
    private Context context;
    private String DeviceID = "";
    private DBSqliteWhatsApp DBwhatsApp;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        context = getApplicationContext();
        DBwhatsApp = new DBSqliteWhatsApp(context);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        Map<String, String> data = remoteMessage.getData();
        //Log.e(TAG,"Messaging Service:"+data.toString());
        String phoneNumber = data.get(SchedulerService.PHONE_NUMBER);
        String textMessage = data.get(SchedulerService.TEXT_MESSAGE);
        String isGroup = phoneNumber.contains(SchedulerService.SYMBOL_TO_IDENTIFY_GROUP) ? "1" : "0";
        String messageType = data.get(SchedulerService.MESSAGE_TYPE);
        String messageId = data.get(SchedulerService.MESSAGE_ID);
        messageId = messageId == null ? "" : messageId;
        String activityType = data.get(SchedulerService.ACTIVITY_TYPE);
        String fileName = data.get(SchedulerService.FILE_NAME);
        String fileUrl = data.get(SchedulerService.FILE_URL);
        String time = (data.get(SchedulerService.TIME) == null || data.get(SchedulerService.TIME).equals("null") ? DateFormatsMethods.getDateTime() : data.get(SchedulerService.TIME));
        String schduletime = (data.get(SchedulerService.SCHEDULE_TIME) == null || data.get(SchedulerService.SCHEDULE_TIME).equals("null") ? DateFormatsMethods.getDateTime() : data.get(SchedulerService.SCHEDULE_TIME));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                DeviceID = new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID() == null ? "" : new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID();
            }
        }, 1000 );
        //String DeviceID = new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID() == null ? "" : new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID();

        if (activityType!=null && activityType.equals("1")){

            // TO Store data into Database sqlite
            SendStoreInDB(phoneNumber,phoneNumber,isGroup,messageId,messageType,activityType,textMessage,time,fileName,fileUrl,DeviceID);
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void managerForScrennLock(SchedulerModel schedulerModel) {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            // Locked
            //Preferences.storeMessage(getApplicationContext(), schedulerModel);
            startService(new Intent(getApplicationContext(), BackgroundService.class));
        } else {
            // Unlocked
            if (Preferences.getPreferenceBoolean(getApplicationContext(), Preferences.KEY_IS_LOCK, false) || Preferences.getPreferenceBoolean(this, Preferences.KEY_IN_EXECUTION, false)) {
                //Preferences.storeMessage(getApplicationContext(), schedulerModel);
            } else {
                //Gson gson = new Gson();
//                Intent scheduler = new Intent(getApplicationContext(), SchedulerService.class);
//                scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, gson.toJson(schedulerModel));
//                startService(scheduler);

                ArrayList<SchedulerModel> queue = DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
                int index = 0;
                if (queue != null && queue.size() > 0) {
                    // TODO : Update action flag
                    DBwhatsApp.UpdateByFlag(queue.get(index).getId(),DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);

                    Intent scheduler = new Intent(getApplicationContext(), SchedulerService.class);
                    scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, queue.get(index));
                    startService(scheduler);

                    //Preferences.getPreferenceBoolean(getApplicationContext(), Preferences.KEY_IS_LOCK, true);

                }
            }
        }
    }
//    @Override
//    public void handleIntent(Intent intent) {
//        super.handleIntent(intent);
//    }

    private void SendStoreInDB(String converName,String phnNumber, String isFromGroup,String MessageID,String MessageType,String activityType, String text, String time,String FileName, String FileUrl,String DeviceID){
        // Scheduler Model
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setConversationName(converName);
        schedulerModel.setPhnNumber(phnNumber);
        schedulerModel.setIsFromGroup(isFromGroup);
        schedulerModel.setMessageId(MessageID);
        schedulerModel.setMessageType(MessageType);
        schedulerModel.setActivityType(activityType);
        schedulerModel.setText(text);
        schedulerModel.setTime(time);
        schedulerModel.setFileName(FileName);
        schedulerModel.setFileUrl(FileUrl);
        schedulerModel.setDeviceId(DeviceID);
        // insert data into db
        DBwhatsApp.insertWhatsAppTable(schedulerModel,DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG);
        CallSendApi();
    }

    private void CallSendApi() {
        if (!DBwhatsApp.GetInOutDataByType(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG).isEmpty()) {

            SchedulerModel schedulerModel = DBwhatsApp.GetInOutDataByType(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG).get(0);

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (pm.isInteractive()) {
                    managerForScrennLock(schedulerModel);
                } else {
                    //Preferences.storeMessage(getApplicationContext(), schedulerModel);
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                }
            } else {
                if (pm.isScreenOn()) {
                    managerForScrennLock(schedulerModel);
                } else {
                    //Preferences.storeMessage(getApplicationContext(), schedulerModel);
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                }
            }
        }
    }
}
