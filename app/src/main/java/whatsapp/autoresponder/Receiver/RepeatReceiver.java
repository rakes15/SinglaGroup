package whatsapp.autoresponder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.Service.SchedulerService;
import whatsapp.autoresponder.Utils.Preferences;
import whatsapp.database.DBSqliteWhatsApp;

public class RepeatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//      Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        DBSqliteWhatsApp DBWhatsApp = new DBSqliteWhatsApp(context);
//        if (!Preferences.getPreferenceBoolean(context, Preferences.KEY_IS_LOCK, false)) {
        if (!Preferences.getPreferenceBoolean(context, Preferences.KEY_IN_EXECUTION, false)) {
            ArrayList<SchedulerModel> queue = DBWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
            int index = 0;
            if (queue != null && queue.size() > 0) {
                // TODO : Update action flag
                DBWhatsApp.UpdateByFlag(queue.get(index).getId(),DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);

                Intent scheduler = new Intent(context, SchedulerService.class);
                scheduler.putExtra(SchedulerService.SCHEDULER_MODEL, queue.get(index));
                context.startService(scheduler);
            }
        }
    }
}