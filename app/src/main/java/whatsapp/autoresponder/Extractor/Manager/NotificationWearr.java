package whatsapp.autoresponder.Extractor.Manager;

import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.RemoteInput;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class NotificationWearr implements Serializable {
    private String identifier;
    private RemoteInput[] remoteInputs;
    private Action action;

    public NotificationWearr(WearableExtender wearableExtender) {
        ArrayList arrayList = new ArrayList();
        for (Action action : wearableExtender.getActions()) {
            if (!(action == null || action.getRemoteInputs() == null)) {
                arrayList.addAll(Arrays.asList(action.getRemoteInputs()));
            }
            this.action = action;
        }
        this.remoteInputs = new RemoteInput[arrayList.size()];
        arrayList.toArray(this.remoteInputs);
    }

    public RemoteInput[] getRemoteInputs() {
        return this.remoteInputs;
    }

    public Action getAction() {
        return this.action;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
