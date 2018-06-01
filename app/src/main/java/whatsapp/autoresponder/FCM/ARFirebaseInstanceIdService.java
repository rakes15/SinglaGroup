package whatsapp.autoresponder.FCM;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import whatsapp.autoresponder.Api.RestApi;
import whatsapp.autoresponder.Model.Rest.ListResponse;
import whatsapp.autoresponder.Utils.Global;
import whatsapp.autoresponder.Utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import whatsapp.autoresponder.Utils.Preferences;

/**
 * Created by iqor on 1/19/2018.
 */

public class ARFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = ARFirebaseInstanceIdService.class.getSimpleName();
    public static final String SERVER_KEY = "AIzaSyBAzjZtfXcF2xGjRAsciquV1bszjSOIP9s";//"AIzaSyATYtmYHFzovgFhIAqKtZzC0SCY30MV-aQ";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Preferences.setPreferenceString(getApplicationContext(), Preferences.FCM_TOKEN, refreshedToken);
        //sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (Global.isNetworkAvailable(getApplicationContext())) {

            RestApi api = Global.initRetrofit(getApplicationContext());

//            @Field("ServerKey") String ServerKey,
//            @Field("ModelNo") String ModelNo,
//            @Field("MacID") String MacID,
//            @Field("IMEINo") String IMEINo,
//            @Field("UniqueKey") String UniqueKey,
//            @Field("FCMID") String FCMID,
//            @Field("SerialNo") String SerialNo

            Call<ListResponse<String>> listResponseCall = api.storeFcmToken(RestApi.AKey,
                    RestApi.UserPwd,
                    SERVER_KEY,
                    getDeviceName(),
                    getMacAddress(),
                    getDeviceIMEI(),
                    "testUniqueKey",
                    refreshedToken,
                    "testSerialNumber");

            listResponseCall.enqueue(new Callback<ListResponse<String>>() {
                @Override
                public void onResponse(Call<ListResponse<String>> call, Response<ListResponse<String>> response) {
                    Log.e("NotiListenerService", "onResponse");
                }

                @Override
                public void onFailure(Call<ListResponse<String>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("NotiListenerService", "onFailure");
                }
            });
        }
    }
    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    private String getMacAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "testIMEINo";
            }
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

}
