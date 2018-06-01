package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.singlagroup.customwidgets.MessageDialog;
/**
 * Created by Rakesh on 02-Oct-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static NetworkListener networkListener;
    private static String TAG = NetworkChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                networkListener.networkReceived(status);
            } else {
                networkListener.networkReceived(status);
            }
        }catch (Exception e){
            Log.e(TAG,""+e.toString());
        }
    }
    public static void bindNetworkListener(NetworkListener listener) {
        networkListener = listener;
    }
}