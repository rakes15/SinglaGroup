package whatsapp.autoresponder.Extractor.Manager;

import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import whatsapp.autoresponder.Extractor.Manager.NotificationWearr;
import whatsapp.autoresponder.Extractor.Manager.NotificationsWearManager;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;


public class MessageSender {
    private NotificationsWearManager notificationsWearManager = NotificationsWearManager.getInstance();
    private Context context;

    public MessageSender(Context context) {
        this.context = context;
    }

    public boolean sendMessage(String conversationName, String textToSend) {
        Log.e("my", "in 1 ");
        NotificationWearr notificationByConverName = this.notificationsWearManager.getNotificationByConverName(conversationName);
//        if (notificationByConverName == null) {
//            Log.e("my", "in 2 ");
//            return false;
//        }
        Intent intent = new Intent();
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        intent.addFlags(268435456);
        Bundle bundle = new Bundle();
        for (RemoteInput resultKey : notificationByConverName.getRemoteInputs()) {
            bundle.putCharSequence(resultKey.getResultKey(), textToSend);
        }
        RemoteInput.addResultsToIntent(notificationByConverName.getRemoteInputs(), intent, bundle);
        try {
            Action action = notificationByConverName.getAction();
            if (action == null) {
                Log.e("my", "in 3 ");
                return false;
            }
            action.actionIntent.send(this.context, 0, intent);
            return true;
        } catch (CanceledException e) {
            Log.e("my", "in 1 " + e);
            return false;
        }
    }
}
