package whatsapp.autoresponder.Extractor.Manager;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import java.util.HashMap;


public class NotificationsWearManager {
    @SuppressLint({"StaticFieldLeak"})
    private static NotificationsWearManager notificationsWearManager;
    @SuppressWarnings("unchecked")
    private HashMap<String, NotificationWearr> hashMap = new HashMap<String, NotificationWearr>();

    private NotificationsWearManager() {
    }

    public static NotificationsWearManager getInstance() {
        if (notificationsWearManager == null) {
            notificationsWearManager = new NotificationsWearManager();
        }
        return notificationsWearManager;
    }

    public void addNotificationWear(NotificationWearr notiWear) {
        if (!this.hashMap.containsKey(notiWear.getIdentifier()) && notiWear.getAction() != null) {
            this.hashMap.put(notiWear.getIdentifier(), notiWear);
        }
    }

    @Nullable
    public NotificationWearr getNotificationByConverName(String conversationName) {
        if (isQuickReplyAvailable(conversationName)) {
            return (NotificationWearr) this.hashMap.get(conversationName);
        }
        return null;
    }

    public String[] getAvailableConversationNames() {
        String[] strArr = (String[]) this.hashMap.keySet().toArray(new String[this.hashMap.size()]);
        if (strArr.length != 0) {
            return strArr;
        }
        return new String[]{""};
    }

    public boolean isQuickReplyAvailable(String conversationName) {
        return this.hashMap.containsKey(conversationName);
    }
}
