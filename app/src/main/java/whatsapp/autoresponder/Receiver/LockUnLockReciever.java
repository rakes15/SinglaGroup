package whatsapp.autoresponder.Receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LockUnLockReciever extends DeviceAdminReceiver {

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onPasswordFailed(context, intent);
//        Toast.makeText(context, "onPasswordFailed", Toast.LENGTH_SHORT).show();
        Log.e("LockUnLockReciever", "onPasswordFailed: ");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onPasswordSucceeded(context, intent);
//        Toast.makeText(context, "onPasswordSucceeded", Toast.LENGTH_SHORT).show();
        Log.e("LockUnLockReciever", "onPasswordSucceeded: ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Toast.makeText(context, "onReceive " + intent.getExtras(), Toast.LENGTH_SHORT).show();
        Log.e("LockUnLockReciever", "onReceive: ");
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
//        Toast.makeText(context, "onReceive " + intent.getExtras(), Toast.LENGTH_SHORT).show();
        Log.e("LockUnLockReciever", "onReceive: ");
    }
}
