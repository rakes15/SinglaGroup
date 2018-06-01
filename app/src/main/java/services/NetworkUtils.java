package services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.singlagroup.MainActivity;

/**
 * Created by Rakesh on 12-Aug-16.
 */
public class NetworkUtils{

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_ETHERNET = 3;
    public static int TYPE_NOT_CONNECTED = 0;
    private static String TAG = NetworkUtils.class.getSimpleName();

    public static int getConnectivityStatus(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET)
                    return TYPE_ETHERNET;
            }
        }catch (Exception e){
            Log.e(TAG,""+e.toString());
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtils.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtils.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtils.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtils.TYPE_ETHERNET) {
            status = "Ethernet enabled";
        } else if (conn == NetworkUtils.TYPE_NOT_CONNECTED) {
            status = "No Internet Connection";
        }
        return status;
    }
}
